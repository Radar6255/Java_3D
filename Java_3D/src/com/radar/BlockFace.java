package com.radar;

import java.awt.Color;

//BlockFace class is object to hold data about the BlockFace to be rendered

public class BlockFace {
	private int[] xCoords;
	private int[] yCoords;
	private int[] zCoords;
	private int[] face;
	private Color color;
	private boolean hasFar = false;
	private double dist;
	public int cubeIndex,x;
	
	public BlockFace(int[] xCoords,int[] yCoords,int[] zCoords,int[] face,double dist, Color color, int cubeIndex){
		this.xCoords = xCoords.clone();
		this.yCoords = yCoords.clone();
		this.zCoords = zCoords.clone();
		this.face = face;
		this.color = color;
		this.dist = dist;
		this.cubeIndex = cubeIndex;
	}public void setX(int x){
		this.x = x;
	}public int getX(){
		return x;
	}public int getCubeIndex(){
		return cubeIndex;
	}public int[] getFace(){
		return face;
	}public int[] getXCoords(){
		return xCoords;
	}public int[] getYCoords(){
		return yCoords;
	}public int[] getZCoords(){
		return zCoords;
	}
	
	public Color getColor(){
		return color;
	}public void setHasFar(boolean hasFar){
		this.hasFar = hasFar;
	}public boolean getHasFar(){
		return hasFar;
	}public double getDist(){
		return dist;
	}public void setDist(double dist){
		this.dist = dist;
	}
}
