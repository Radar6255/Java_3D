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
import org.jocl.cl_queue_properties;

public class GpuHandler {
	
	cl_context context;
	cl_command_queue commandQueue;
	cl_program program;
	cl_kernel kernel;
	cl_program program2;
	cl_kernel kernel2;
	cl_program program3;
	cl_kernel kernel3;
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

        
        String subtractArrays =
    		    "__kernel void "+
    		    "subKernel(__global const float *a,"+
    		    "             __global const float *b,"+
    		    "             __global float *c)"+
    		    "{"+
    		    "    int gid = get_global_id(0);"+
    		    "    c[gid] = b[gid] - a[gid];"+
    		    "}";
        
        program = clCreateProgramWithSource(context,
                1, new String[]{ subtractArrays }, null, null);
        clBuildProgram(program, 0, null, null, null, null);
        
        kernel = clCreateKernel(program, "subKernel", null);
//        kernel2 = clCreateKernel(program,"addKernel",null);
        String addArrayC =
    		    "__kernel void "+
    		    "addKernel(__global const float *a,"+
    		    "             __global const float *b,"+
    		    "             __global float *c)"+
    		    "{"+
    		    "    int gid = get_global_id(0);"+
    		    "    c[gid] = b[gid] + a[gid];"+
    		    "}";
        
        program2 = clCreateProgramWithSource(context,
                1, new String[]{ addArrayC }, null, null);
        clBuildProgram(program2, 0, null, null, null, null);
        
        kernel2 = clCreateKernel(program2, "addKernel", null);
        
        String rotatePoints = 
        		"__kernel void "+
        		"rotateKernel(__global const float *x,"+
				"				__global const float *y,"+
        		"				__global const float *c,"+
				"				__global const float *s,"+
        		"				__global float *r,"+
				"				__global float *r2)"+
				"{"+
        		"	int gid = get_global_id(0);"+
				"	r[gid] = (x[gid]*c[0])-(y[gid]*s[0]);"+
        		"	r2[gid] = (y[gid]*c[0])+(x[gid]*s[0]);"+
				"}";
        program3 = clCreateProgramWithSource(context,
                1, new String[]{ rotatePoints }, null, null);
        clBuildProgram(program3, 0, null, null, null, null);
        
        kernel3 = clCreateKernel(program3, "rotateKernel", null);
		//private float[] rotate2D(float x, float y, double s,double c) {
		//return new float[] { (float) (x * c - y * s), (float) (y * c + x * s) };
	}
	Pointer srcA;
	Pointer srcB;
	Pointer srcC;
	Pointer srcD;
	Pointer dst;
	Pointer dst2;
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
    public float[] addArrays(float[] srcArrayA, float[] srcArrayB,int size) {
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
        
        clSetKernelArg(kernel2, 0, 
                Sizeof.cl_mem, Pointer.to(memObjects[0]));
        clSetKernelArg(kernel2, 1, 
                Sizeof.cl_mem, Pointer.to(memObjects[1]));
        clSetKernelArg(kernel2, 2, 
                Sizeof.cl_mem, Pointer.to(memObjects[2]));
        
        long global_work_size[] = new long[]{size};
        long local_work_size[] = new long[]{1};
        
        clEnqueueNDRangeKernel(commandQueue, kernel2, 1, null,
                global_work_size, local_work_size, 0, null, null);
        
        clEnqueueReadBuffer(commandQueue, memObjects[2], CL_TRUE, 0,
                size * Sizeof.cl_float, dst, 0, null, null);
        
        
        //Clears kernel, program, and memory objects
        clReleaseMemObject(memObjects[0]);
        clReleaseMemObject(memObjects[1]);
        clReleaseMemObject(memObjects[2]);
        
        return dstArray;
    }
    
    public float[][] rotatePoints(float[] srcArrayA, float[] srcArrayB, float[] srcArrayC, float[] srcArrayD,int size) {
        float dstArray[] = new float[size];
        float dstArray2[] = new float[size];
        
        srcA = Pointer.to(srcArrayA);
        srcB = Pointer.to(srcArrayB);
        srcC = Pointer.to(srcArrayC);
        srcD = Pointer.to(srcArrayD);
        dst = Pointer.to(dstArray);
        dst2 = Pointer.to(dstArray2);
        
        cl_mem memObjects[] = new cl_mem[6];
        memObjects[0] = clCreateBuffer(context, 
            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
            Sizeof.cl_float * size, srcA, null);
        memObjects[1] = clCreateBuffer(context, 
            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
            Sizeof.cl_float * size, srcB, null);
        memObjects[2] = clCreateBuffer(context, 
                CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_float, srcC, null);
        memObjects[3] = clCreateBuffer(context, 
                CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_float, srcD, null);
        memObjects[4] = clCreateBuffer(context, 
            CL_MEM_READ_WRITE, 
            Sizeof.cl_float * size, null, null);
        memObjects[5] = clCreateBuffer(context, 
            CL_MEM_READ_WRITE, 
            Sizeof.cl_float * size, null, null);
        
        
        
        clSetKernelArg(kernel3, 0, 
                Sizeof.cl_mem, Pointer.to(memObjects[0]));
        clSetKernelArg(kernel3, 1, 
                Sizeof.cl_mem, Pointer.to(memObjects[1]));
        clSetKernelArg(kernel3, 2, 
                Sizeof.cl_mem, Pointer.to(memObjects[2]));
        clSetKernelArg(kernel3, 3, 
                Sizeof.cl_mem, Pointer.to(memObjects[3]));
        clSetKernelArg(kernel3, 4, 
                Sizeof.cl_mem, Pointer.to(memObjects[4]));
        clSetKernelArg(kernel3, 5, 
                Sizeof.cl_mem, Pointer.to(memObjects[5]));
        
        long global_work_size[] = new long[]{size};
        long local_work_size[] = new long[]{1};
        
        clEnqueueNDRangeKernel(commandQueue, kernel3, 1, null,
                global_work_size, local_work_size, 0, null, null);
        
        clEnqueueReadBuffer(commandQueue, memObjects[4], CL_TRUE, 0,
                size * Sizeof.cl_float, dst, 0, null, null);
        
        
        
        clEnqueueReadBuffer(commandQueue, memObjects[5], CL_TRUE, 0,
                size * Sizeof.cl_float, dst2, 0, null, null);
        
        
        //Clears kernel, program, and memory objects
        clReleaseMemObject(memObjects[0]);
        clReleaseMemObject(memObjects[1]);
        clReleaseMemObject(memObjects[2]);
        clReleaseMemObject(memObjects[3]);
        clReleaseMemObject(memObjects[4]);
        clReleaseMemObject(memObjects[5]);
        
        return new float[][] {dstArray,dstArray2};
    }
}
