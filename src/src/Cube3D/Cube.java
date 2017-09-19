package src.Cube3D;

import java.util.Random;

public class Cube {
	
	// i would've used 0.5... using -1 to +1 means the cube is 2x2x2 (w,h,d)
	// that's why in my video, i divided by 2 when creating a clone array at different position
	public static final float[][] verts = {{-0.5f,-0.5f,-0.5f},{0.5f,-0.5f,-0.5f},{0.5f,0.5f,-0.5f},{-0.5f,0.5f,-0.5f},{-0.5f,-0.5f,0.5f},{0.5f,-0.5f,0.5f},{0.5f,0.5f,0.5f},{-0.5f,0.5f,0.5f}};
	public static final int[][] edges = {{0,1},{1,2},{2,3},{3,0},{4,5},{5,6},{6,7},{7,4},{0,4},{1,5},{2,6},{3,7}};
	public static final int[][] faces = {{0,1,2,3},{4,5,6,7},{0,1,5,4},{2,3,7,6},{0,3,7,4},{1,2,6,5}};
	public static final int[] colors = {0xff0000,0xff8000,0xffff00,0xffffff,0xff,0xff00}; // red, orange, yellow, white, blue, green
	
	public float x,y,z, rotX,rotY;
	public Cube(float xx,float yy,float zz){x=xx; y=yy; z=zz;} // im not creating a clone array. this is so we can rotate on spot

	Random rand = new Random(); // put a seed in here to watch them fly together :}
	float ry = rand.nextFloat() * 3.141593F * 2.0F;
	float rx = (rand.nextFloat()*2-1)*0.125f;
	float ryV=0,rxV=0;
	
	public static float sin(float rad){return (float)Math.sin(rad)*0.4f;} // slowing speed of bugs in here
	public static float cos(float rad){return (float)Math.cos(rad)*0.4f;}
	
	public void update(boolean flyLikeABug) {
		// rotation on the spot!
		rotX+=0.02f;
		rotY+=0.03f;
		// you could add rotZ also if you want...
		
		if(flyLikeABug){ // and even give it some movement updates :} (like so...)
			float rxRate = rand.nextInt(6)==0 ? 0.92F : 0.7F;
			
			float rxCos = cos(rx);
			x += cos(ry) * rxCos;
			y += sin(rx);
			z += sin(ry) * rxCos;
			
			ry += ryV * 0.1F; // slowly turn left/right
			ryV *= 0.75F;
			ryV += (rand.nextFloat()-rand.nextFloat()) * rand.nextFloat() * 4F;
			
			rx *= rxRate; // less amount of up/down
			rx += rxV * 0.1F; // turn up/down more often
			rxV *= 0.9F;
			rxV += (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 2.0F;
		}
		
	}
}
