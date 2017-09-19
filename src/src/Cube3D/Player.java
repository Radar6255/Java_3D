package src.Cube3D;

import java.awt.event.KeyEvent;
import java.util.BitSet;

public class Player {
	
	public static float rad(float deg){return (float)Math.toRadians(deg);}
	
	public float x,y,z, rotX,rotY;
	
	public Player(float xx,float yy,float zz,float rx,float ry){
		x=xx; y=yy; z=zz; rotX=rad(rx); rotY=rad(ry); // passed degrees in, but we use radians
	}
	
	private static final float RAD90 = rad(90); // using this to clamp our pitch
	public void mouse_motion(float dx,float dy) {
		dx/=200; dy/=200; rotX+=dy; rotY+=dx;
		if(rotX>RAD90)rotX=RAD90;
		else if(rotX<-RAD90)rotX=-RAD90;
	}

	public void update(BitSet keys) {
		float s = 10/Timer.updatesPerSec; // 10 units of movement per second
		
		if(keys.get(KeyEvent.VK_SPACE)) y-=s;
		if(keys.get(KeyEvent.VK_SHIFT)) y+=s;
		
		double dx = s*Math.sin(rotY), dz = s*Math.cos(rotY);
		if(keys.get(KeyEvent.VK_W)){ x+=dx; z+=dz;}
		if(keys.get(KeyEvent.VK_S)){ x-=dx; z-=dz;}
		if(keys.get(KeyEvent.VK_A)){ x-=dz; z+=dx;}
		if(keys.get(KeyEvent.VK_D)){ x+=dz; z-=dx;}
		
	}
	
}
