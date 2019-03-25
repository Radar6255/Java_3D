package com.radar.cube;

import com.radar.Handler;

public abstract class CubeObject {
	public static boolean debug = false;
	public static double fov;
	public static Handler handler;
	
	public CubeObject(Handler handler) {
		CubeObject.handler = handler;
	}
	public void tick(){
		
	}public void render(double px, double py, double pz, double rotLat, double rotVert, double sl, double cl, double sv, double cv){
		
	}public static void setDebug(boolean t){
		debug = t;
	}
	public abstract boolean isVisible();
	
	public void updateFov() {
		int width = handler.getWidth();
		int height = handler.getHeight();
		if (width < height) {
			fov = width;
		} else {
			fov = height;
		}
	}public void placeBlock() {
		
	}
}
