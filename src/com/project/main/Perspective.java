package com.project.main;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.Toolkit;

public class Perspective extends GameObject{
	double mx,my;
	PointerInfo mouseLoc;
	Point tempPoint;
	int centerX = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2;
	int centerY = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2;
	public Perspective(double x, double y, double z, int Width, int Height,int Depth,int id, Handler handler, double rotUp, double rotLat) {
		super(x, y, z, Width, Height, Depth, id, handler);
		this.rotUp = rotUp;
		this.rotLat = rotLat;
	}

	public void tick(){
		mouseLoc = MouseInfo.getPointerInfo();
		tempPoint = mouseLoc.getLocation();
		mx = tempPoint.getX();
		my = tempPoint.getY();
		//System.out.println("Mouse x:"+mx+" Mouse y:"+my);
		
		try {
			if (!MainClass.pause){
				rotLat = rotLat - (centerX-mx)/6;
				rotUp = rotUp + (centerY-my)/6;
				if (rotLat > 360){
					rotLat = rotLat - 360;
				}if (rotUp > 360){
					rotUp = rotUp - 360;
				}if (rotLat < 0){
					rotLat = rotLat + 360;
				}if(rotUp < 0){
					rotUp = rotUp + 360;
				}
				//System.out.println("Rotation Lateral:"+rotLat+" Rotation Vertical:"+rotUp+" Mouse X:"+mx+" Mouse Y:"+my);
				new Robot().mouseMove(centerX, centerY);
			}
		} catch (AWTException e) {
			e.printStackTrace();
		}
		if (getUp()){z++;}
		if (getDown()){z--;}
		if (getRight()){x++;}
		if (getLeft()){x--;}
		if (getSpace()){y++;}
		if (getShift()){y--;}
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawString(x+" "+y+" "+z, 200, 200);
		if(MainClass.pause){
			g.drawString("Paused", 200, 300);
		}else{
			g.drawString("Press Esc to Pause", 200, 300);
		}
		g.fillRect((MainClass.WIDTH/2)-5, (MainClass.HEIGHT/2)-1, 10, 2);
		g.fillRect((MainClass.WIDTH/2)-1, (MainClass.HEIGHT/2)-5, 2, 10);
		
		g.drawLine((int) ((MainClass.WIDTH/2)-x), 0,(int) ((MainClass.WIDTH/2)-x), MainClass.HEIGHT);
		g.drawLine(0, (int) ((MainClass.HEIGHT/2)+y),MainClass.WIDTH, (int) ((MainClass.HEIGHT/2)+y));
	}
	
}
