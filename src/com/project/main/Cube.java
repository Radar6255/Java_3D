package com.project.main;

import java.awt.Color;
import java.awt.Graphics;

public class Cube extends GameObject{
	private Handler handler;
	double px,py,pz,rotLat, rotUp,dist,dist2,dist3,dist4;
	double distAdd,distAdd2,distAdd3,distAdd4;
	double lookDist, lookDist2, lookDist3, lookDist4;
	int dx, dy;
	int x1, x2, x3, x4;
	int y1, y2, y3, y4;
	public Cube(double x, double y, double z, int Width, int Height, int Depth,int id, Handler handler) {
		super(x, y, z, Width, Height, Depth, id, handler);
		this.handler = handler;
	}

	public void render (Graphics g){
		if (handler != null){
			for (int i=0;i<handler.object.size();i++){
				if (handler.object.get(i).getId() == 0){
					GameObject tempObject = handler.object.get(i);
					px = tempObject.getX();
					py = tempObject.getY();
					pz = tempObject.getZ();
					rotLat = tempObject.getRotLat();
					rotUp = tempObject.getRotUp();
				}
			}
		}
		dx = (int) x;
		dy = (int) y;
		
		
		//Dist3 is from the left middle of the plane
		dist3 = Math.sqrt(Math.pow((x-distAdd-px),2)+Math.pow(((y-(Height/2))-py),2)+Math.pow((z-pz),2))+100;
		//Dist4 is from the right middle of the plane
		dist4 = Math.sqrt(Math.pow((x+Width+distAdd2-px),2)+Math.pow(((y-(Height/2))-py),2)+Math.pow((z-pz),2))+100;
		//Dist1 is from the top middle of the plane
		dist = Math.sqrt(Math.pow(((x+(Width/2))-px),2)+Math.pow((y+distAdd3-py),2)+Math.pow((z-pz),2))+100;
		//Dist2 is from the bottom middle of the plane
		dist2 = Math.sqrt(Math.pow(((x+(Width/2))-px),2)+Math.pow((y-distAdd4-Height-py),2)+Math.pow((z-pz),2))+100;
		
		
		
		//dist = ((Width/2)*(Math.pow(dist-20, 2))/(Math.pow(dist, 2)));
		//dist2 = ((Width/2)*(Math.pow(dist2-20, 2))/(Math.pow(dist2, 2)));
		//dist3 = ((Width/2)*(Math.pow(dist3-20, 2))/(Math.pow(dist3, 2)));
		//dist4 = ((Width/2)*(Math.pow(dist4-20, 2))/(Math.pow(dist4, 2)));
		
		distAdd = ((Width/2)*(Math.pow(dist-20, 2))/(Math.pow(dist, 2)));
		distAdd2 = ((Width/2)*(Math.pow(dist2-20, 2))/(Math.pow(dist2, 2)));
		distAdd3 = ((Width/2)*(Math.pow(dist3-20, 2))/(Math.pow(dist3, 2)));
		distAdd4 = ((Width/2)*(Math.pow(dist4-20, 2))/(Math.pow(dist4, 2)));
		
		//x1 = (int) (((MainClass.WIDTH/2)-px+x +rotLat+ ((Width/2)*(Math.pow(dist-20, 2))/(Math.pow(dist, 2)))) - (Math.pow(dist,-1)*300));
		//x2 = (int) (((MainClass.WIDTH/2)-px+x +rotLat+ ((Width/2)*(Math.pow(dist2-20, 2))/(Math.pow(dist2, 2)))) - (Math.pow(dist2, -1)*300));
		//x3 = (int) (((MainClass.WIDTH/2)-px+x+Width +rotLat - ((Width/2)*(Math.pow(dist-20, 2))/(Math.pow(dist, 2)))) + (Math.pow(dist,-1)*300));
		//x4 = (int) (((MainClass.WIDTH/2)-px+x+Width +rotLat - ((Width/2)*(Math.pow(dist2-20, 2))/(Math.pow(dist2, 2)))) + (Math.pow(dist2, -1)*300));
		
		//y1 = (int) (((MainClass.HEIGHT/2)+py+y + ((Width/2)*(Math.pow(dist3-20, 2))/(Math.pow(dist3, 2)))) - (Math.pow(dist3, -1)*300));
		//y2 = (int) (((MainClass.HEIGHT/2)+py+y + ((Width/2)*(Math.pow(dist4-20, 2))/(Math.pow(dist4, 2)))) - (Math.pow(dist4, -1)*300));
		//y3 = (int) (((MainClass.HEIGHT/2)+py+y+Height - ((Width/2)*(Math.pow(dist3-20, 2))/(Math.pow(dist3, 2)))) + (Math.pow(dist3, -1)*300));
		//y4 = (int) (((MainClass.HEIGHT/2)+py+y+Height - ((Width/2)*(Math.pow(dist4-20, 2))/(Math.pow(dist4, 2)))) + (Math.pow(dist4, -1)*300));
		//New distance perspective points
		//x1 = (int) (((MainClass.WIDTH/2)-px+x - (((3*rotLat/360)*MainClass.WIDTH)-(3*MainClass.WIDTH/2)) + dist));
		//x2 = (int) (((MainClass.WIDTH/2)-px+x - (((3*rotLat/360)*MainClass.WIDTH)-(3*MainClass.WIDTH/2)) + dist2));
		//x3 = (int) (((MainClass.WIDTH/2)-px+x+Width - (((3*rotLat/360)*MainClass.WIDTH)-(3*MainClass.WIDTH/2)) - dist));
		//x4 = (int) (((MainClass.WIDTH/2)-px+x+Width - (((3*rotLat/360)*MainClass.WIDTH)-(3*MainClass.WIDTH/2)) - dist2));
		
		x1 = (int) (((MainClass.WIDTH/2)-px+x + distAdd));
		x2 = (int) (((MainClass.WIDTH/2)-px+x + distAdd2));
		x3 = (int) (((MainClass.WIDTH/2)-px+x+Width - distAdd));
		x4 = (int) (((MainClass.WIDTH/2)-px+x+Width - distAdd2));
		
		y1 = (int) (((MainClass.HEIGHT/2)+py+y + distAdd3));
		y2 = (int) (((MainClass.HEIGHT/2)+py+y + distAdd4));
		y3 = (int) (((MainClass.HEIGHT/2)+py+y+Height - distAdd3));
		y4 = (int) (((MainClass.HEIGHT/2)+py+y+Height - distAdd4));
				
		//System.out.println((Math.pow(dist4, -1)*300));
		
		g.drawLine(x1,y1, x3, y2);
		g.setColor(Color.RED);
		g.drawLine(x1,y1, x2, y3);
		g.setColor(Color.BLUE);
		g.drawLine(x3,y2, x4, y4);
		g.setColor(Color.GREEN);
		g.drawLine(x2,y3, x4, y4);

	}

	@Override
	public void tick() {
		
	}
	
}

