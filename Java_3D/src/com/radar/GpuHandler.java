package com.radar;

import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_TYPE_ALL;
import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateCommandQueue;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadBuffer;
import static org.jocl.CL.clGetDeviceIDs;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clReleaseMemObject;
import static org.jocl.CL.clSetKernelArg;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;

public class GpuHandler {
	
	cl_context context;
	cl_command_queue commandQueue;
	cl_program program;
	cl_kernel kernel;
	public GpuHandler() {
        final int platformIndex = 0;
        final long deviceType = CL_DEVICE_TYPE_ALL;
        final int deviceIndex = 0;
        
        CL.setExceptionsEnabled(true);
        
        int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];
        
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];
        
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);
        
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];
        
        cl_device_id devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];
        
        context = clCreateContext(
                contextProperties, 1, new cl_device_id[]{device}, 
                null, null, null);
        
        commandQueue = 
                clCreateCommandQueue(context, device, 0, null);
        
        String programSource =
    		    "__kernel void "+
    		    "sampleKernel(__global const float *a,"+
    		    "             __global const float *b,"+
    		    "             __global float *c)"+
    		    "{"+
    		    "    int gid = get_global_id(0);"+
    		    "    c[gid] = b[gid] - a[gid%3];"+
    		    "}";
        
        program = clCreateProgramWithSource(context,
                1, new String[]{ programSource }, null, null);
        clBuildProgram(program, 0, null, null, null, null);
        
        kernel = clCreateKernel(program, "sampleKernel", null);
	}
	Pointer srcA;
	Pointer srcB;
	Pointer dst;
    public float[] findBlockPos(float[] srcArrayA, float[] srcArrayB,int size) {
        float dstArray[] = new float[size];
        
        srcA = Pointer.to(srcArrayA);
        srcB = Pointer.to(srcArrayB);
        dst = Pointer.to(dstArray);
        
        cl_mem memObjects[] = new cl_mem[3];
        memObjects[0] = clCreateBuffer(context, 
            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
            Sizeof.cl_float * size, srcA, null);
        memObjects[1] = clCreateBuffer(context, 
            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
            Sizeof.cl_float * size, srcB, null);
        memObjects[2] = clCreateBuffer(context, 
            CL_MEM_READ_WRITE, 
            Sizeof.cl_float * size, null, null);
        
        clSetKernelArg(kernel, 0, 
                Sizeof.cl_mem, Pointer.to(memObjects[0]));
        clSetKernelArg(kernel, 1, 
                Sizeof.cl_mem, Pointer.to(memObjects[1]));
        clSetKernelArg(kernel, 2, 
                Sizeof.cl_mem, Pointer.to(memObjects[2]));
        
        long global_work_size[] = new long[]{size};
        long local_work_size[] = new long[]{1};
        
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
                global_work_size, local_work_size, 0, null, null);
        
        clEnqueueReadBuffer(commandQueue, memObjects[2], CL_TRUE, 0,
                size * Sizeof.cl_float, dst, 0, null, null);
        
        
        
        //Clears kernel, program, and memory objects
        clReleaseMemObject(memObjects[0]);
        clReleaseMemObject(memObjects[1]);
        clReleaseMemObject(memObjects[2]);
        
        return dstArray;
    }
}
