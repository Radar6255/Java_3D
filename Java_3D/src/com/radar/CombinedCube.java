package com.radar;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

//Cube class is created on chunk reading from the handler
//Does all calculations for where BlockFaces should render and how far they are from the player

public class CombinedCube extends CubeObject{
	public int x, y, z, w, h, d, i, fov, far,pfov, index, cubeIndex,count,pcx,pcy,pcz,xOff,zOff,ir;
	BlockFace testFace;
//	public float p1x, p1y, p1z,p2x, p2y, p2z,p3x, p3y, p3z,p4x, p4y, p4z;
	public float distOff = 0.0f;
	public float tx, ty, tz, sum;
	public double f, f2, px, py, pz, rotLat, rotVert, dist, ldist, dist2,sl,cl,sv,cv, bound;
	public float[] point = new float[2];
	public boolean hasFar,debug,test2 = false;
	public int test = 0;
	public Color faceColor;
	public boolean visible, changed, looping, render,repeat,renderBlock,right,left,up,down,lside,rside,back,front;
	//ImageIcon img = new ImageIcon("./dirt.png");
	LinkedList<LinkedList<LinkedList<Integer>>> world = new LinkedList<LinkedList<LinkedList<Integer>>>();
	Player player;
	private Handler handler;
	Chunk chunk;
	
	public static float[][] verts = { { 0.5f, 0.5f, 0.5f }, { 0.5f, -0.5f, 0.5f }, { -0.5f, -0.5f, 0.5f },{ -0.5f, 0.5f, 0.5f }, { 0.5f, 0.5f, -0.5f }, { 0.5f, -0.5f, -0.5f }, { -0.5f, -0.5f, -0.5f },{ -0.5f, 0.5f, -0.5f } };
//	public static float[][] centerVerts = { { 0.0f, 0.0f, 0.5f }, { 0.0f, 0.0f, -0.5f }, { 0.5f, 0.0f, 0.0f },{ -0.5f, 0.0f, 0.0f }, { 0.0f, -0.5f, 0.0f }, { 0.0f, 0.5f, 0.0f } };
	private int[][] faces = { { 0, 1, 2, 3, 0, 1}, { 4, 5, 6, 7, 1, 1}, { 0, 4, 5, 1, 2, 1}, { 2, 6, 7, 3, 3, 1},{ 1, 5, 6, 2, 4, 1}, { 3, 7, 4, 0, 5, 1} };
	public float[][] points3D = new float[9][3];
	
	public float[] pointsX = new float[4];
	public float[] pointsY = new float[4];
	public float[] pointsZ = new float[4];
	
	public int[][] points = new int[8][3];
	public int[] pointsRemoved = new int[8];
	public double[] distances = new double[8];
	private int[] xCoords = new int[4];
	private int[] yCoords = new int[4];
	public BlockFace[] renderFaces = new BlockFace[3];
	public BlockFace tempFace;

	public double upperBound, lowerBound = 0;
	
	public CombinedCube(int x, int y, int z, int w, int h, int d, Handler handler, int cubeIndex,int chunkX,int chunkZ, Chunk chunk) {
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
//		repeat = false;
//		if (!renderUpdate()){
//			repeat = true;
//		}
		chunk.addCube(this);
	}
	
	public void render(Graphics g) {
//		if (!repeat && test < 10){
//			test++;
//			repeat = renderUpdate();
//			System.out.println("Repeating...");
//		}
		player = handler.getPlayer();
		px = player.getX();
		py = player.getY();
		pz = player.getZ();
		rotLat = player.getRotLat();
		rotVert = player.getRotVert();
		sl = player.getSineLat();
		cl = player.getCosineLat();
		sv = player.getSineVert();
		cv = player.getCosineVert();
		pfov = player.getFov();
		i = 0;
		ldist = 0;
		dist2 = 0;
		far = 0;
		
		//Goal: Find if cube is in FOV in a 3D manner
		//Need to get viewing angle and use it to make V in which cubes should be rendered
		renderBlock = true;
		renderBlock = false;
//		System.out.println("Start");
		lowerBound = (360 - rotLat) - 60;
		upperBound = (360 - rotLat) + 60;
		
		if ((x-px) == 0){
			bound = Math.toDegrees(Math.atan(z-pz));
		}else{
			bound = Math.toDegrees(Math.atan((z-pz)/(x-px)));
		}
		
		//2nd Quad
		if((x-px) < 0 && (z-pz) > 0){
			bound = 180 + bound;
		}//3rd Quad
		else if((x-px) < 0 && (z-pz) < 0){
			bound = 180 + bound;
		}//4th  Quad
		else if((x-px) > 0 && (z-pz) < 0){
			bound = 360 + bound;
		}else{//1st Quad
			
		}
		
		bound += 90.5;
		
		if ((bound > lowerBound && bound < upperBound) || (bound > lowerBound+360 && bound < upperBound+360) || (bound > lowerBound-360 && bound < upperBound-360)){
			renderBlock = true;
		}
		if (renderBlock){
			//Loop through all vertices to find which is farthest from player
			ir = 0;
			pointsRemoved = new int[8];
			for (float[] point : verts) {
				//Pulls x,y,z of points taking into account width, height, and depth
				if (point[0] > 0.0){
					tx = point[0]+(w-1);
				}else{
					tx = point[0];
				}
				if (point[1] > 0.0){
					ty = point[1]+(h-1);
				}else{
					ty = point[1];
				}
				if (point[2] < 0.0){
					tz = point[2]+(d-1);
				}else{
					tz = point[2];
				}
	
	//			points3D[i][0] = tx+x;
	//			points3D[i][1] = ty+y;
	//			points3D[i][2] = tz+z;
				
				tx = (float) (tx + (x - px));
				ty = (float) (ty + (y - py));
				tz = (float) (tz + (z - pz));
				
				points3D[i][0] = tx;
				points3D[i][1] = ty;
				points3D[i][2] = tz;
				
				//Calculates distance from player to vertex
				dist = Math.sqrt(Math.pow(tz, 2) + Math.pow(tx, 2) + Math.pow(ty, 2));
				//Rotates points so that player appears to look around
				point = rotate2D(tx, tz, sl,cl);
				tx = point[0];
				tz = point[1];
				
				point = rotate2D(ty, tz, sv,cv);
				ty = point[0];
				tz = point[1];
				
				//Calculates field of view taking into account divide by zero errors
				if (tz != 0) {
					f = fov / tz;
				} else {
					f = fov;
				}
				if (f > 0){
					renderBlock = false;
					//System.out.println("I did something");
				}
				
				//Corner points render mostly for debugging
				//g.fillOval((int) ((tx * f) + (Main.WIDTH / 2) - 2), (int) ((ty * f) + (Main.HEIGHT) - 2), 4, 4);
				
				//Puts 2D points into array for later when I need to render the polygons
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
			if (renderBlock){
			index = 0;
			count = 0;
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
						
						back = false;
						front = false;
						lside = false;
						rside = false;
						up = false;
						down = false;
						
						if (face[4] == 0) {
							faceColor = Color.BLUE;
							back = true;
						}if (face[4] == 1) {
							faceColor = Color.RED;
							front = true;
						}if (face[4] == 2) {
							faceColor = Color.GREEN;
							rside = true;
						}if (face[4] == 3) {
							faceColor = Color.ORANGE;
							lside = true;
						}if (face[4] == 4) {
							faceColor = Color.YELLOW;
							down = true;
						}if (face[4] == 5) {
							faceColor = Color.CYAN;
							up = true;
						}
//						g.fillOval((int) ((tx * f) + (Main.WIDTH / 2) - 2), (int) ((ty * f) + (Main.HEIGHT) - 2), 4, 4);
						//Calculates distance to the center point of face
						//Distance should be calculated to closest point on face not center of the face
						
						//This should be the 3D points of the face
						pointsX[0] = points3D[face[0]][0];
						pointsY[0] = points3D[face[0]][1];
						pointsZ[0] = points3D[face[0]][2];
						
						pointsX[1] = points3D[face[1]][0];
						pointsY[1] = points3D[face[1]][1];
						pointsZ[1] = points3D[face[1]][2];
						
						pointsX[2] = points3D[face[2]][0];
						pointsY[2] = points3D[face[2]][1];
						pointsZ[2] = points3D[face[2]][2];
						
						pointsX[3] = points3D[face[3]][0];
						pointsY[3] = points3D[face[3]][1];
						pointsZ[3] = points3D[face[3]][2];
						
						tx = pointsX[0]-distOff;
						ty = pointsY[0]-distOff;
						tz = pointsZ[0]-distOff;
						//Debug proof the values are of the 3D points
//						for(i=0;i < 4;i++){
//							System.out.println(pointsX[i]+" "+pointsY[i]+" "+pointsZ[i]);
//						}
						
						if (pointsX[0] < 0 || pointsX[1] < 0 || pointsX[2] < 0 || pointsX[3] < 0){ //If any point is left of the player
							for(i=0;i < 4;i++){
								if (tx < pointsX[i]+distOff){ //Find left most point in cube
									tx = pointsX[i]+distOff-(float)0;
								}
							}
//							System.out.println("right?");
						}
						if (pointsY[0] < 0 || pointsY[1] < 0 || pointsY[2] < 0 || pointsY[3] < 0){ //If any point is below the player
							for(i=0;i < 4;i++){
								if (ty < pointsY[i]-distOff){
									ty = pointsY[i]-distOff-(float)0;
								}
							}
						}
						if (pointsZ[0] < 0 || pointsZ[1] < 0 || pointsZ[2] < 0 || pointsZ[3] < 0){
							for(i=0;i < 4;i++){
								if (tz < pointsZ[i]-distOff){
									tz = pointsZ[i]-distOff-(float)0;
								}
							}
//							System.out.println("Left???");
						}
						
						//Right side of cube... These are very relative terms
						if (pointsX[0] > 0 || pointsX[1] > 0 || pointsX[2] > 0 || pointsX[3] > 0){
							for(i=0;i < 4;i++){
								if (tx > pointsX[i]-distOff){
									tx = pointsX[i]-distOff-(float)0;
								}
							}
						}
						if (pointsY[0] > 0 || pointsY[1] > 0 || pointsY[2] > 0 || pointsY[3] > 0){
							for(i=0;i < 4;i++){
								if (ty > pointsY[i]+distOff){
									ty = pointsY[i]+distOff-(float)0;
								}
							}
						}
						if (pointsZ[0] > 0 || pointsZ[1] > 0 || pointsZ[2] > 0 || pointsZ[3] > 0){
							for(i=0;i < 4;i++){
								if (tz > pointsZ[i]+distOff){
									tz = pointsZ[i]+distOff-(float)0;
								}
							}
							//System.out.println(px+" "+tz);
						}
						//TODO
						test2 = false;
						if (px > x && x+w > px && !lside && !rside){
							//System.out.println(tx);
							tx = 0;
							//System.out.println(tx+" "+ty+" "+tz);
							test2 = true;
						}if (py+1 > y && y+h > py+1 && !up && !down){
							//System.out.println(ty);
							ty = 0;
							//System.out.println(tx+" "+ty+" "+tz);
							test2 = true;
						}if (pz > z && z+d > pz && !back && !front){
							//System.out.println(tz);
							tz = 0;
							//System.out.println(tx+" "+ty+" "+tz);
							test2 = true;
						}
//						if (test2&&debug){
//							if (lside||rside){
//								System.out.println("Right/left side");
//							}if (up||down){
//								//System.out.println("Top/Bottom");
//							}if (front||back){
//								System.out.println("front/back");
//							}
//						}
						
						//Sending blockface to be rendered at the chunk
						dist = (float) Math.sqrt(Math.pow(tz, 2) + Math.pow(tx, 2) + Math.pow(ty, 2));
						tempFace = new BlockFace(xCoords, yCoords, face, dist, faceColor,cubeIndex);
						chunk.addFace(tempFace);

						/* visible = false;
						
						//Determines if cube face is on screen
						right = false;
						left = false;
						for (int xc : xCoords) {
//							if (xc > 0 && xc < Main.WIDTH) {
							if (xc > -200 && xc < Main.WIDTH+200) {
								visible = true;
							}
//							if (xc < 0){
//								left = true;
//							}if (xc > Main.WIDTH){
//								right = true;
//							}
						}if(left && right){
							visible = true;
							right = false;
							left = false;
						}
						if (visible) {
							visible = false;
							for (int yc : yCoords) {
								if (yc > 0 && yc < Main.HEIGHT) {
									visible = true;
								}
//								if (yc < 0){
//									left = true;
//								}if (yc > Main.HEIGHT){
//									right = true;
//								}
							}
						}
						if (left && right){
							visible = true;
						}
						if (visible){
							tempFace = new BlockFace(xCoords, yCoords, face, dist, faceColor,cubeIndex);
							chunk.addFace(tempFace);
						}
						//renderFaces[index] = new BlockFace(xCoords, yCoords, face, dist, faceColor, visible,cubeIndex);
						index++;
						sum = 0;*/
					}
				}
				count++;
			}
			}
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
			if (!visible && w < 2 && d < 2 && h < 2){
				face[5] = 0;
				System.out.println("Not visible");
			}else{
				face[5] = 1;
			}
			count++;
		}
		return true;
	}
	private float[] rotate2D(float x, float y, double s,double c) {
//		float s = (float) Math.sin(rad);
//		float c = (float) Math.cos(rad);
		return new float[] { (float) (x * c - y * s), (float) (y * c + x * s) };
	}
}