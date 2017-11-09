package com.radar;

import java.awt.Color;

public class BlockFace {
	private int[] xCoords;
	private int[] yCoords;
	private int[] xCoordsC;
	private int[] yCoordsC;
	private int[] face;
	private Color color;
	private boolean visible, looping;
	private boolean hasFar = false;
	private double dist;
	public int i,tempInt,cubeIndex;
	
	public BlockFace(int[] xCoords,int[] yCoords,int[] face,double dist, Color color, boolean visible, int cubeIndex){
		this.xCoords = xCoords.clone();
		this.yCoords = yCoords.clone();
		this.xCoordsC = xCoords.clone();
		this.yCoordsC = yCoords.clone();
		this.face = face;
		this.color = color;
		this.visible = visible;
		this.dist = dist;
		this.cubeIndex = cubeIndex;
	}public int getCubeIndex(){
		return cubeIndex;
	}public int[] getFace(){
		return face;
	}public int[] getXCoords(){
		return xCoords;
	}public int[] getYCoords(){
		return yCoords;
	}public Color getColor(){
		return color;
	}public boolean getVisible(){
		return visible;
	}public void setVisible(boolean visible){
		this.visible = visible;
	}public void setHasFar(boolean hasFar){
		this.hasFar = hasFar;
	}public boolean getHasFar(){
		return hasFar;
	}public double getDist(){
		return dist;
	}public void setDist(double dist){
		this.dist = dist;
	}public void sortPointsX(){
		looping = true;
		i = 0;
		while (looping){
			looping = false;
			if (i > 2){
				i = 0;
			}
			if (xCoordsC[i] < xCoordsC[i+1]){
				tempInt = xCoordsC[i];
				xCoordsC[i] = xCoordsC[i+1];
				xCoordsC[i+1] = tempInt;
				
				tempInt = yCoordsC[i];
				yCoordsC[i] = yCoordsC[i+1];
				yCoordsC[i+1] = tempInt;
				looping = true;
			}
			i++;
		}
	}public void sortPointsY(){
		looping = true;
		i = 0;
		while (looping){
			looping = false;
			if (i > 2){
				i = 0;
			}
			if (yCoordsC[i] < yCoordsC[i+1]){
				tempInt = yCoordsC[i];
				yCoordsC[i] = yCoordsC[i+1];
				yCoordsC[i+1] = tempInt;
				
				tempInt = xCoordsC[i];
				xCoordsC[i] = xCoordsC[i+1];
				xCoordsC[i+1] = tempInt;
				looping = true;
			}
			i++;
		}
	}public int[] getXCoordsC(){
		return xCoordsC;
	}public int[] getYCoordsC(){
		return yCoordsC;
	}
}
