package com.radar;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.ImageIcon;

public class Cube {
	public int x,y,z,w,h,d,i,fov,far,index;
	public float tx,ty,tz,sum;
	public double f,f2,px,py,pz,rotLat,rotVert,dist,ldist,dist2;
	public float[] point = new float[2];
	public boolean hasFar = false;
	public Color faceColor;
	public boolean visible,changed,looping,render;
	ImageIcon img = new ImageIcon("./dirt.png");
	
	Player player;
	private Handler handler;
	
	public float[][] verts = {{0.5f,0.5f,0.5f},{0.5f,-0.5f,0.5f},{-0.5f,-0.5f,0.5f},{-0.5f,0.5f,0.5f},{0.5f,0.5f,-0.5f},{0.5f,-0.5f,-0.5f},{-0.5f,-0.5f,-0.5f},{-0.5f,0.5f,-0.5f}};
	public int[][] faces = {{0,1,2,3,0},{4,5,6,7,1},{0,4,5,1,2},{2,6,7,3,3},{1,5,6,2,4},{3,7,4,0,5}}; 
	public float[][] points3D = new float[9][3];
	public int[][] points = new int[8][2];
	public double[] distances = new double[8];
	private int[] xCoords = new int[4];
	private int[] yCoords = new int[4];
	public BlockFace[] renderFaces = new BlockFace[3];
	public BlockFace tempFace;
	
	public Cube(int x,int y,int z,int w,int h,int d,Handler handler){
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.h = h;
		this.d = d;
		this.handler = handler;
		if (Main.WIDTH < Main.HEIGHT){
			fov = Main.WIDTH;
		}else{
			fov = Main.HEIGHT;
		}
		
	}
	
	private float[] rotate2D(float x,float y,float rad) {
		float s = (float)Math.sin(rad);
		float c = (float)Math.cos(rad);
		return new float[]{x*c-y*s,y*c+x*s};
	}
	
	public void render(Graphics g){
		player = handler.getPlayer();
		px = player.getX();
		py = player.getY();
		pz = player.getZ();
		rotLat = player.getRotLat();
		rotVert = player.getRotVert();
		
		i = 0;
		ldist = 0;
		dist2 = 0;
		far = 0;
		
		g.setColor(Color.BLACK);
		for(float[] point:verts){
			tx = point[0];
			ty = point[1];
			tz = point[2];
			
			tx = (float) (tx+(x-px));
			ty = (float) (ty+(y-py));
			tz = (float) (tz+(z-pz));
			
//			points3D[i][0] = tx;
//			points3D[i][1] = ty;
//			points3D[i][2] = tz;
			
			dist = Math.sqrt(Math.pow(tz, 2)+Math.pow(tx, 2)+Math.pow(ty, 2));
			
			point = rotate2D(tx,tz,(float) Math.toRadians(rotLat));
			tx = point[0];
			tz = point[1];
			point = rotate2D(ty,tz,(float) Math.toRadians(rotVert));
			ty = point[0];
			tz = point[1];

			points3D[i][0] = tx;
			points3D[i][1] = ty;
			points3D[i][2] = tz;
			
			if (tz != 0){
				f = fov / tz;
			}else{
				f = fov;
			}
			
			g.fillOval((int) ((tx*f)+(Main.WIDTH/2)-2), (int) ((ty*f)+(Main.HEIGHT)-2), 4, 4);
			points[i][0] = (int) ((tx*f)+(Main.WIDTH/2));
			points[i][1] = (int) (ty*f)+(Main.HEIGHT);
			if (ldist < dist){
				dist2 = far;
				ldist = dist;
				far = i;
			}
			i++;
		}
		
//		g.drawLine((int) points[0][0], (int) points[0][1], (int) points[1][0], (int) points[1][1]);
//		g.drawLine((int) points[2][0], (int) points[2][1], (int) points[1][0], (int) points[1][1]);
//		g.drawLine((int) points[2][0], (int) points[2][1], (int) points[3][0], (int) points[3][1]);
//		g.drawLine((int) points[3][0], (int) points[3][1], (int) points[0][0], (int) points[0][1]);
//		g.drawLine((int) points[0][0], (int) points[0][1], (int) points[4][0], (int) points[4][1]);
//		g.drawLine((int) points[4][0], (int) points[4][1], (int) points[5][0], (int) points[5][1]);
//		g.drawLine((int) points[5][0], (int) points[5][1], (int) points[6][0], (int) points[6][1]);
//		g.drawLine((int) points[6][0], (int) points[6][1], (int) points[7][0], (int) points[7][1]);
//		g.drawLine((int) points[1][0], (int) points[1][1], (int) points[5][0], (int) points[5][1]);
//		g.drawLine((int) points[2][0], (int) points[2][1], (int) points[6][0], (int) points[6][1]);
//		g.drawLine((int) points[3][0], (int) points[3][1], (int) points[7][0], (int) points[7][1]);
//		g.drawLine((int) points[7][0], (int) points[7][1], (int) points[4][0], (int) points[4][1]);
		
		index = 0;
		for (int[] face : faces){
			hasFar = false;
			i = 0;
			for(int point : face){
				if (i != 4){
					if (point == far){
						hasFar = true;
						break;
					}
				}
				i++;
			}if (!hasFar){
				xCoords[0] = points[face[0]][0];
				xCoords[1] = points[face[1]][0];
				xCoords[2] = points[face[2]][0];
				xCoords[3] = points[face[3]][0];
				
				yCoords[0] = points[face[0]][1];
				yCoords[1] = points[face[1]][1];
				yCoords[2] = points[face[2]][1];
				yCoords[3] = points[face[3]][1];
				
				float dist=0;
//				for(int i=0;i<3;i++){
//					float sum=0;
//					for(int j:face)sum+=points3D[j][i];
//					dist+=sum*sum;
//				}
//				for (int j:face){
//					dist += (float) Math.sqrt(Math.pow(points3D[j][0],2)+Math.pow(points3D[j][1],2)+Math.pow(points3D[j][2],2));
//				}dist = dist/4;
				
				for (int i=0;i<3;i++){
					float sum=0;
					for(int j:face){
						sum += points3D[j][i];
					}points3D[8][i] = sum/4;
					dist+=sum/4;
				}dist = (float) Math.sqrt(dist);
				
				if (tz != 0){
					f = fov / points3D[8][2];
				}else{
					f = fov;
				}
				g.setColor(Color.BLACK);
				g.fillOval((int) ((points3D[8][0]-2)*f)+(Main.WIDTH/2),(int) ((points3D[8][1]-2)*f)+(Main.HEIGHT/2), 2, 2);
				
				if(face[4] == 0){
					faceColor = Color.BLUE;
				}if(face[4] == 1){
					faceColor = Color.RED;
				}if(face[4] == 2){
					faceColor = Color.GREEN;
				}if(face[4] == 3){
					faceColor = Color.ORANGE;
				}if(face[4] == 4){
					faceColor = Color.YELLOW;
				}if(face[4] == 5){
					faceColor = Color.CYAN;
				}
				visible = false;
				for (int xc : xCoords){
					if (xc>0&&xc<Main.WIDTH){
						visible = true;
					}
				}if (visible){
					visible = false;
					for (int yc : yCoords){
						if (yc>0&&yc<Main.WIDTH){
							visible = true;
						}
					}
				}
				renderFaces[index] = new BlockFace(xCoords,yCoords,face,dist,faceColor,visible);
				
				index++;
				sum = 0;
			}
		}

		i = 0;
		looping = true;
		while (looping){
			
			if (renderFaces[i+1].getDist() > renderFaces[i].getDist()){
				tempFace = renderFaces[i];
				renderFaces[i] = renderFaces[i+1];
				renderFaces[i+1] = tempFace;
				changed = true;
			}
			i++;
			if (i == 2 && changed){
				i = 0;
				changed = false;
			}else{
				if (i==2){
					looping = false;
				}
			}
		}
		i = 0;
		for (BlockFace face:renderFaces){
			if (i+1<3){
				if (renderFaces[i+1].getDist() >= renderFaces[i].getDist()){
					System.out.println("Not fully sorted");
				}
			}
			if (face.getVisible()){
				g.setColor(face.getColor());
				g.fillPolygon(face.getXCoords(), face.getYCoords(),4);
				//g.drawImage(img.getImage(), Main.WIDTH/2, Main.WIDTH/2, null);
			}
			i++;
		}
	}
	public double getDist(){
		try{
			tx = points3D[0][0];
			ty = points3D[0][1];
			tz = points3D[0][2];
		}catch(Exception e){
			return 0;
		}
		return (Math.sqrt(Math.pow(tx, 2)+Math.pow(ty, 2)+Math.pow(tz, 2)));
	}
	public void tick(){
		
	}
}
