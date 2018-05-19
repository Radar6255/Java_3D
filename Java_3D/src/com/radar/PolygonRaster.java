package com.radar;

//Goal of this class is to turn the 2d points of the polygons into pixels to render on screen while also accounting for depth
public class PolygonRaster {
	PolygonRaster(){
		System.out.println(Main.WIDTH);
		System.out.println(Main.HEIGHT);
		//For each pixel the first value is the color and second is the depth
		Float [][] pixels = new Float[Main.WIDTH*Main.HEIGHT][2];
	}
}
