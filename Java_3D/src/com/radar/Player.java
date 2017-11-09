package com.radar;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.Toolkit;

public class Player {
	double x,y,z,mx,my;
	int chunkX,chunkY,chunkZ;
	double rotLat,rotVert,s,c;
	public boolean up,down,left,right,space,shift;
	
	int centerX = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2;
	int centerY = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2;
	
	public PointerInfo mouseLoc;
	public Point tempPoint;
	
	public double rate = 0.25;
	
	public Player(double x,double y,double z,double rotLat,double rotVert){
		this.x = x;
		this.y = y;
		this.z = z;
		this.rotLat = rotLat;
		this.rotVert = rotVert;
	}
	public void render(Graphics g){
		
		g.setColor(Color.BLACK);
		g.drawString(x+" "+y+" "+z, 20, 20);
		g.drawString(chunkX+" "+chunkY+" "+chunkZ, 20, 40);
	}
	public void tick(){
		chunkX = (int) Math.floor(x/16);
		chunkY = (int) y;
		chunkZ = (int) Math.floor(z/16);
		if (!Main.pause){
			mouseLoc = MouseInfo.getPointerInfo();
			tempPoint = mouseLoc.getLocation();
			mx = tempPoint.getX();
			my = tempPoint.getY();
			rotLat = rotLat + ((mx-centerX)/6);
			rotVert = rotVert + ((my-centerY)/6);
			if (rotVert > 90){
				rotVert = 90;
			}if (rotVert < -90){
				rotVert = -90;
			}
			if (rotLat > 360){
				rotLat -= 360;
			}if (rotLat < 0){
				rotLat += 360;
			}
			try {
				new Robot().mouseMove(centerX, centerY);
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
		
		s = Math.sin(Math.toRadians(rotLat));
		c = Math.cos(Math.toRadians(rotLat));
		
		 if (space){y+=rate/2;}
		 if (shift){y-=rate/2;}
		 if (up){z-=c*rate; x-=s*rate;}
		 if (down){z+=c*rate; x+=s*rate;}
		 //if(up){z-=rate;}
		 //if(down){z+=rate;}
		 if (left){z-=s*rate;x+=c*rate;}
		 if (right){z+=s*rate;x-=c*rate;}
		 
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
	}public double getX(){
		return x;
	}public double getY(){
		return y;
	}public double getZ(){
		return z;
	}public double getRotLat(){
		return rotLat;
	}public double getRotVert(){
		return rotVert;
	}public int getChunkX(){
		return chunkX;
	}public int getChunkY(){
		return chunkY;
	}public int getChunkZ(){
		return chunkZ;
	}
}
