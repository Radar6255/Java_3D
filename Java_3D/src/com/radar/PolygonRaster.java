package com.radar;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.LinkedList;

//Goal of this class is to turn the 2d points of the polygons into pixels to render on screen while also accounting for depth
public class PolygonRaster {
	BlockFace blockFace;
	LinkedList<BlockFace> faces = new LinkedList<BlockFace>();
	float [][] pixels;
	int i = 0;
	BufferedImage canvas;
	int [] xCoords,yCoords,zCoords;
	PolygonRaster(){
		//For each pixel the first value is the color and second is the depth
		pixels = new float[Main.WIDTH*Main.HEIGHT][2];
	}
	public void addFace(BlockFace blockFace){
		faces.add(blockFace);
	}
	public void render(Graphics g){
		
		for (BlockFace face : faces){
			xCoords = face.getXCoords();
			yCoords = face.getYCoords();
			zCoords = face.getZCoords();
			//Need to make line between every point
			i = 0;
			while (i < 4){
				//Need to make equation for a line
				
				i++;
			}
		}
		
		canvas = new BufferedImage(Main.WIDTH, Main.HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		WritableRaster raster = canvas.getRaster();
		int[] color = {255,255,255};
		int[] color2 = {0,0,0};
		i = 0;
		for (float[] pixel: pixels){
			if (pixel[0] > 0.5){
				raster.setPixel(0, 0, color);
			}else{
				raster.setPixel(0, 0, color2);
			}
			
			i++;
		}
	    Graphics2D g2 = (Graphics2D) g;
	    g2.drawImage(canvas, null, null);
	}
}
