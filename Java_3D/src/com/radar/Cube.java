package com.radar;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

public class Cube {
	public int x, y, z, w, h, d, i, fov, far, index, cubeIndex,count,pcx,pcy,pcz,xOff,zOff;
	public float tx, ty, tz, sum;
	public double f, f2, px, py, pz, rotLat, rotVert, dist, ldist, dist2;
	public float[] point = new float[2];
	public boolean hasFar,debug = false;
	public boolean test = true;
	public Color faceColor;
	public boolean visible, changed, looping, render,repeat;
	//ImageIcon img = new ImageIcon("./dirt.png");
	LinkedList<LinkedList<LinkedList<Integer>>> world = new LinkedList<LinkedList<LinkedList<Integer>>>();
	Player player;
	private Handler handler;
	Chunk chunk;
	
	public static float[][] verts = { { 0.5f, 0.5f, 0.5f }, { 0.5f, -0.5f, 0.5f }, { -0.5f, -0.5f, 0.5f },{ -0.5f, 0.5f, 0.5f }, { 0.5f, 0.5f, -0.5f }, { 0.5f, -0.5f, -0.5f }, { -0.5f, -0.5f, -0.5f },{ -0.5f, 0.5f, -0.5f } };
	public static float[][] centerVerts = { { 0.0f, 0.0f, 0.5f }, { 0.0f, 0.0f, -0.5f }, { 0.5f, 0.0f, 0.0f },{ -0.5f, 0.0f, 0.0f }, { 0.0f, -0.5f, 0.0f }, { 0.0f, 0.5f, 0.0f } };
	private int[][] faces = { { 0, 1, 2, 3, 0, 1}, { 4, 5, 6, 7, 1, 1}, { 0, 4, 5, 1, 2, 1}, { 2, 6, 7, 3, 3, 1},{ 1, 5, 6, 2, 4, 1}, { 3, 7, 4, 0, 5, 1} };
	public float[][] points3D = new float[9][3];
	public int[][] points = new int[8][2];
	public double[] distances = new double[8];
	private int[] xCoords = new int[4];
	private int[] yCoords = new int[4];
	public BlockFace[] renderFaces = new BlockFace[3];
	public BlockFace tempFace;

	public Cube(int x, int y, int z, int w, int h, int d, Handler handler, int cubeIndex,int chunkX,int chunkZ, Chunk chunk) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.h = h;
		this.d = d;
		this.chunk = chunk;
		pcx = chunkX;
		pcz = chunkZ;
//		if (x > 0){
//			pcx = (int) Math.floor(x/16.1);
//		}else{
//			pcx = (int) Math.floor((x-1)/16.1);
//		}
		pcy = (int) y;
//		if (z > 0){
//			pcz = (int) Math.floor(z/16.1);
//		}else{
//			pcz = (int) Math.floor((z-1)/16.1);
//		}
		
		this.cubeIndex = cubeIndex;
		this.handler = handler;
		if (Main.WIDTH < Main.HEIGHT) {
			fov = Main.WIDTH;
		} else {
			fov = Main.HEIGHT;
		}
//		repeat = true;
//		repeat = renderUpdate();
		repeat = false;
		if (!renderUpdate()){
			repeat = true;
		}
		chunk.addCube(this);
	}
	
	public void render(Graphics g) {
		if (repeat){
			repeat = renderUpdate();
			System.out.println("Repeating...");
		}
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

		//g.setColor(Color.BLACK);
		
		//Loop through all vertices to find which is farthest from player
		for (float[] point : verts) {
			//Pulls x,y,z of points taking into account width, height, and depth
			tx = point[0];
			ty = point[1];
			//If width is > 1
			if (point[2] > 0.0){
				tz = point[2]+(w-1);
			}else{
				tz = point[2];
			}
			points3D[i][0] = tx;
			points3D[i][1] = ty;
			points3D[i][2] = tz;
			
			tx = (float) (tx + (x - px));
			ty = (float) (ty + (y - py));
			tz = (float) (tz + (z - pz));
			
			//Calculates distance from player to vert
			dist = Math.sqrt(Math.pow(tz, 2) + Math.pow(tx, 2) + Math.pow(ty, 2));
			
			//Rotates points so that player appears to look around
			point = rotate2D(tx, tz, (float) Math.toRadians(rotLat));
			tx = point[0];
			tz = point[1];
			point = rotate2D(ty, tz, (float) Math.toRadians(rotVert));
			ty = point[0];
			tz = point[1];
			
			//Calculates field of view taking into account divide by zero errors
			if (tz != 0) {
				f = fov / tz;
			} else {
				f = fov;
			}
			
			
			//Corner points
			//g.fillOval((int) ((tx * f) + (Main.WIDTH / 2) - 2), (int) ((ty * f) + (Main.HEIGHT) - 2), 4, 4);
			
			//Puts 2D points into array for later when I need to render polygons
			points[i][0] = (int) ((tx * f) + (Main.WIDTH / 2));
			points[i][1] = (int) ((ty * f) + (Main.HEIGHT));
			
			//Finds point farthest from the player
			if (ldist < dist) {
				dist2 = far;
				ldist = dist;
				far = i;
			}
			i++;
		}
		
//		g.drawLine((int) points[0][0], (int) points[0][1], (int)
//				points[1][0], (int) points[1][1]);
//		g.drawLine((int) points[2][0], (int) points[2][1], (int)
//				points[1][0], (int) points[1][1]);
//		g.drawLine((int) points[2][0], (int) points[2][1], (int)
//				points[3][0], (int) points[3][1]);
//		g.drawLine((int) points[3][0], (int) points[3][1], (int)
//				points[0][0], (int) points[0][1]);
//		g.drawLine((int) points[0][0], (int) points[0][1], (int)
//				points[4][0], (int) points[4][1]);
//		g.drawLine((int) points[4][0], (int) points[4][1], (int)
//				points[5][0], (int) points[5][1]);
//		g.drawLine((int) points[5][0], (int) points[5][1], (int)
//				points[6][0], (int) points[6][1]);
//		g.drawLine((int) points[6][0], (int) points[6][1], (int)
//				points[7][0], (int) points[7][1]);
//		g.drawLine((int) points[1][0], (int) points[1][1], (int)
//				points[5][0], (int) points[5][1]);
//		g.drawLine((int) points[2][0], (int) points[2][1], (int)
//				points[6][0], (int) points[6][1]);
//		g.drawLine((int) points[3][0], (int) points[3][1], (int)
//				points[7][0], (int) points[7][1]);
//		g.drawLine((int) points[7][0], (int) points[7][1], (int)
//				points[4][0], (int) points[4][1]);
		
		index = 0;
		count = 0;
//		renderUpdate();
		renderFaces = new BlockFace[3];
		for (int[] face : faces) {
			if (face[5] == 0){}
			else{
				//Finds if the face has the point farthest from screen
				hasFar = false;
				i = 0;
				for (int point : face) {
					if (i < 4) {
						if (point == far) {
							hasFar = true;
							break;
						}
					}
					i++;
				}//If face doesn't have the farthest point then try to render it
				if (!hasFar) {
					//Add points to array to send with BlockFace for rendering
					xCoords[0] = points[face[0]][0];
					xCoords[1] = points[face[1]][0];
					xCoords[2] = points[face[2]][0];
					xCoords[3] = points[face[3]][0];
	
					yCoords[0] = points[face[0]][1];
					yCoords[1] = points[face[1]][1];
					yCoords[2] = points[face[2]][1];
					yCoords[3] = points[face[3]][1];
	
					dist = 0;
					
					//Finds which face it is and what color it should be by index
					//Also finds center point from that index-
					//in order to get accurate distance from player to center of face
					if (face[4] == 0) {
						point = centerVerts[0];
						faceColor = Color.BLUE;
					}
					if (face[4] == 1) {
						point = centerVerts[1];
						faceColor = Color.RED;
					}
					if (face[4] == 2) {
						point = centerVerts[2];
						faceColor = Color.GREEN;
					}
					if (face[4] == 3) {
						point = centerVerts[3];
						faceColor = Color.ORANGE;
					}
					if (face[4] == 4) {
						point = centerVerts[4];
						faceColor = Color.YELLOW;
					}
					if (face[4] == 5) {
						point = centerVerts[5];
						faceColor = Color.CYAN;
					}//Rotates center point to make sure of accuracy
					tx = point[0];
					ty = point[1];
					tz = point[2];
					tx = (float) (tx + (x - px));
					ty = (float) (ty + (y - py));
					tz = (float) (tz + (z - pz));
					point = rotate2D(tx, tz, (float) Math.toRadians(rotLat));
					tx = point[0];
					tz = point[1];
					point = rotate2D(ty, tz, (float) Math.toRadians(rotVert));
					ty = point[0];
					tz = point[1];
					//Calculates distance to the center point of face
					dist = (float) Math.sqrt(Math.pow(tz, 2) + Math.pow(tx, 2) + Math.pow(ty, 2));
					visible = false;
					
					//Determines if cube face is on screen
					for (int xc : xCoords) {
						if (xc > 0 && xc < Main.WIDTH) {
							visible = true;
						}
					}
					if (visible) {
						visible = false;
						for (int yc : yCoords) {
							if (yc > 0 && yc < Main.WIDTH) {
								visible = true;
							}
						}
					}
					if (visible){
						chunk.addFace(new BlockFace(xCoords, yCoords, face, dist, faceColor, visible,cubeIndex));
					}
					//renderFaces[index] = new BlockFace(xCoords, yCoords, face, dist, faceColor, visible,cubeIndex);
					index++;
					sum = 0;
				}
			}
			count++;
			
		}
	}
	
//	public double getDist() {
//		tx = (float) (x - px);
//		ty = (float) (y - py);
//		tz = (float) (z - pz);
//		return (Math.sqrt(Math.pow(tx, 2) + Math.pow(ty, 2) + Math.pow(tz, 2)));
//	}

	public int getIndex() {
		return cubeIndex;
	}public void setDebug(boolean debug){
		this.debug = debug;
	}

	public void tick() {
//		world = handler.getWorld();
//		zOff = handler.getZOff();
//		xOff = handler.getXOff();
	}
	public boolean renderUpdate(){
		world = handler.getWorld();
		xOff = handler.getXOff();
		zOff = handler.getZOff();
		count = 0;
		for (int[] face:faces){
			visible = true;
			try{
				if (visible && world.get(pcx+xOff).get(pcz+zOff)!=null){
					if (count == 0){
						//+x face direction
						if (world.get(pcx+xOff).get(pcz+zOff).size()-1 > cubeIndex+1){
							if (world.get(pcx+xOff).get(pcz+zOff).get(cubeIndex+1) != 0){
								visible = false;
							}
						}if (!visible && (cubeIndex+1)%16 == 0){
							//if (world.get(pcx+xOff).size()-1 > pcz+zOff+1){
								if (world.get(pcx+xOff).get(pcz+zOff+1) != null && !world.get(pcx+xOff).get(pcz+zOff+1).isEmpty()){
									if (0 <= cubeIndex-15 && world.get(pcx+xOff).get(pcz+zOff+1).size()-1 > cubeIndex-15){
										if (world.get(pcx+xOff).get(pcz+zOff+1).get(cubeIndex-15) == 0){
											visible = true;
										}
									}else{
										visible = true;
									}
								}else{
									visible = true;
								}
							//}else{
							//	visible = true;
							//}
//						if (cubeIndex == 17){
//							System.out.println("ChunkX:"+pcx+" ChunkZ:"+pcz+" Value:"+world.get(pcx+xOff).get(pcz+zOff).get(cubeIndex+16)+" Visble "+visible);
//						}
						}//if(!visible && (cubeIndex)/256){
							
						//}
					}else if (count == 1){
						//-x face direction
						if (cubeIndex-1 >= 0){
							//if (cubeIndex%16 != 0){
							if (world.get(pcx+xOff).get(pcz+zOff).size()-1>cubeIndex-1){
								if (world.get(pcx+xOff).get(pcz+zOff).get(cubeIndex-1) != 0){
									visible = false;
								}
							}
						}if (!visible && cubeIndex%16 == 0){
							if (pcz+zOff-1 >= 0){
								if (world.get(pcx+xOff).get(pcz+zOff-1) != null && !world.get(pcx+xOff).get(pcz+zOff-1).isEmpty()){
									if (world.get(pcx+xOff).get(pcz+zOff-1).size()-1 > cubeIndex+15){
										if (world.get(pcx+xOff).get(pcz+zOff-1).get(cubeIndex+15) == 0){
											visible = true;
										}
									}else{
										visible = true;
									}
								}else{
									visible = true;
								}
							}else{
								visible = true;
							}
						}
//					visible = true;
					}else if (count == 2){
						//-z face direction Green
						if (world.get(pcx+xOff).get(pcz+zOff).size()-1 > cubeIndex+16){
							if (world.get(pcx+xOff).get(pcz+zOff).get(cubeIndex+16) != 0){
								visible = false;
							}
//							if (cubeIndex == 0){
//								System.out.println("ChunkX:"+pcx+" ChunkZ:"+pcz+" Value:"+world.get(pcx+xOff).get(pcz+zOff).get(cubeIndex+16)+" Visble "+visible);
//							}
						}
					}else if (count == 3){
						//+z face direction
						if (cubeIndex - 16 >= 0 && world.get(pcx+xOff).get(pcz+zOff).size()-1 >= cubeIndex-16){
							if (world.get(pcx+xOff).get(pcz+zOff).get(cubeIndex-16) != 0){
								visible = false;
							}
						}
//						visible = true;
					}else if (count == 4){
						//-y face direction
//						visible = false;
						if (cubeIndex-256 >= 0){
							if (world.get(pcx+xOff).get(pcz+xOff).get(cubeIndex-256) != 0){
								visible = false;
							}
						}
						
					}else if (count == 5){
						//+y face direction
						if (world.get(pcx+xOff).get(pcz+zOff).size()-1 >= cubeIndex+257){
							if (world.get(pcx+xOff).get(pcz+zOff).get(cubeIndex+257) != 0){
								visible = false;
							}
						}
						//visible = false;
					}
				}
				//else if (visible){
				//	System.out.println(world.get(pcx+xOff).get(pcz+zOff));
					//System.out.println("Off X:"+xOff+" Off Z:"+zOff+" Chunk X:"+pcx+" Chunk Z:"+pcz+" Real Chunk X:"+(pcx+xOff)+" Chunk Z:"+(pcz+zOff));
				//}
			}catch(Exception e){
				e.printStackTrace();
				System.out.println(e.getCause());
				return false;
			}
			if (!visible){
				face[5] = 0;
			}else{
				face[5] = 1;
			}
			count++;
		}
		return true;
	}
	private float[] rotate2D(float x, float y, float rad) {
		float s = (float) Math.sin(rad);
		float c = (float) Math.cos(rad);
		return new float[] { x * c - y * s, y * c + x * s };
	}
}