package com.project.main;

import java.awt.Graphics;
public abstract class GameObject {
	protected double x,y,z;
	protected int Width,Height, Depth, id;
	protected boolean up,down,left,right,space,shift = false;
	protected double rotUp, rotLat;
	public GameObject (double x,double y, double z,int Width, int Height,int Depth,int id, Handler handler){
		this.x = x;
		this.y = y;
		this.Width = Width;
		this.Height = Height;
		this.Depth = Depth;
		this.id = id;
	}
	public abstract void tick();
	public abstract void render(Graphics g);
	
	public int getId(){
		return id;
	}
	
	public void setUp(boolean up){
		this.up = up;
	}public void setDown(boolean down){
		this.down = down;
	}public void setLeft(boolean left){
		this.left = left;
	}public void setRight(boolean right){
		this.right = right;
	}public void setSpace(boolean space){
		this.space = space;
	}public void setShift(boolean shift){
		this.shift = shift;
	}
	
	public boolean getUp(){
		return up;
	}public boolean getRight(){
		return right;
	}public boolean getLeft(){
		return left;
	}public boolean getDown(){
		return down;
	}public boolean getSpace(){
		return space;
	}public boolean getShift(){
		return shift;
	}public double getX(){
		return x;
	}public double getY(){
		return y;
	}public double getZ(){
		return z;
	}
	
	public double getRotUp(){
		return rotUp;
	}public double getRotLat(){
		return rotLat;
	}
	
}