package com.radar;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;
public class Cube extends CubeObject{
	public int x, y, z, w, h, d, i, fov, far,pfov, cubeIndex,pcx,pcy,pcz,xOff,zOff, chunkMax;
	public float tx, ty, tz;
	public double f, f2, px, py, pz, rotLat, rotVert, dist, ldist, dist2,sl,cl,sv,cv, bound;
	public boolean hasFar,debug = false;
	public Color[] faceColors = new Color[6];
	public boolean visible, changed, looping, render,repeat,renderBlock,right,left,up,down,lside,rside,back,front;
	LinkedList<Integer> chunkData = new LinkedList<Integer>();
	LinkedList<Integer> chunkPosX = new LinkedList<Integer>();
	LinkedList<Integer> chunkNegX = new LinkedList<Integer>();
	LinkedList<Integer> chunkPosY = new LinkedList<Integer>();
	LinkedList<Integer> chunkNegY = new LinkedList<Integer>();
	private Handler handler;
	Chunk chunk;
	
	public static float[][] verts = { { 0.5f, 0.5f, 0.5f }, { 0.5f, -0.5f, 0.5f }, { -0.5f, -0.5f, 0.5f },{ -0.5f, 0.5f, 0.5f }, { 0.5f, 0.5f, -0.5f }, { 0.5f, -0.5f, -0.5f }, { -0.5f, -0.5f, -0.5f },{ -0.5f, 0.5f, -0.5f } };
//	public static float[][] centerVerts = { { 0.0f, 0.0f, 0.5f }, { 0.0f, 0.0f, -0.5f }, { 0.5f, 0.0f, 0.0f },{ -0.5f, 0.0f, 0.0f }, { 0.0f, -0.5f, 0.0f }, { 0.0f, 0.5f, 0.0f } };
	private int[][] faces = { { 0, 1, 2, 3, 0, 1}, { 4, 5, 6, 7, 1, 1}, { 0, 4, 5, 1, 2, 1}, { 2, 6, 7, 3, 3, 1},{ 1, 5, 6, 2, 4, 1}, { 3, 7, 4, 0, 5, 1} };
	public float[][] points3D = new float[9][3];
	
	public float[] pointsX = new float[4];
	public float[] pointsY = new float[4];
	public float[] pointsZ = new float[4];
	boolean first = true;
	public int[][] points = new int[8][3];
	public double[] distances = new double[8];
	private int[] xCoords = new int[4];
	private int[] yCoords = new int[4];
	private int[] zCoords = new int[4];
	public BlockFace tempFace;

	public double upperBound, lowerBound = 0;
	
	public Cube(Color[] faceColors,int x, int y, int z, int w, int h, int d,Handler handler, int cubeIndex,int chunkX,int chunkZ, Chunk chunk) {
		this.faceColors = faceColors;
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.h = h;
		this.d = d;
		this.handler = handler;
		this.chunk = chunk;
		pcx = chunkX;
		pcz = chunkZ;
		pcy = (int) y;
		
		this.cubeIndex = cubeIndex;
		if (Main.WIDTH < Main.HEIGHT) {
			fov = Main.WIDTH;
		} else {
			fov = Main.HEIGHT;
		}
		chunk.addCube(this,x,y,z);
//		renderUpdate();
		
	}
	public void render(Graphics g, double px, double py, double pz, double rotLat, double rotVert, double sl, double cl, double sv, double cv) {
		
		//Uncomment to run on main thread, else it runs on the cubeGen thread
//		if (first){
//			renderUpdate();
//			first = false;
//		}
		//Consider grabbing in the render from chunk
		
		//Need to get viewing angle and use it to make V in which cubes should be rendered
//		renderBlock = true;
		renderBlock = false;
//		System.out.println("Start");
		//TODO Push the V backwards from players perspective, need to use sin + cos to find where the bottom of the v starts
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
		}
		
		bound += 90.5;
		
		if ((bound > lowerBound && bound < upperBound) || (bound > lowerBound+360 && bound < upperBound+360) || (bound > lowerBound-360 && bound < upperBound-360)){
			renderBlock = true;
		}
		if (renderBlock){
			//Loop through all vertices to find which is farthest from player
			//pointsRemoved = new int[8];
			i = 0;
			ldist = 0;
			dist2 = 0;
			far = 0;
			
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
				
				tx = (float) (tx + (x - px));
				ty = (float) (ty + (y - py));
				tz = (float) (tz + (z - pz));
				
				points3D[i][0] = tx;
				points3D[i][1] = ty;
				points3D[i][2] = tz;
				
				
				//Calculates distance from player to vertex
				//TODO This may lower performance so find a way to remove nicely
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
				}
				
				//Corner points render mostly for debugging
				//g.fillOval((int) ((tx * f) + (Main.WIDTH / 2) - 2), (int) ((ty * f) + (Main.HEIGHT) - 2), 4, 4);
				
				//Puts 2D points into array for later when I need to render the polygons

				points[i][0] = (int) ((tx * f) + (Main.WIDTH / 2));
				points[i][1] = (int) ((ty * f) + (Main.HEIGHT));
				points[i][2] = (int) (tz);
				
				//Finds point farthest from the player
				if (ldist < dist) {
					dist2 = far;
					ldist = dist;
					far = i;
				}
				i++;
			}//System.out.println(i);
			if (renderBlock){
//			renderFaces = new LinkedList<BlockFace>();
			for (int[] face : faces) {
				if (face[5] != 0){
					//Finds if the face has the point farthest from screen
					hasFar = false;
					for (int i = 0;i < face.length;i++) {
						if (i < 4) {
							if (face[i] == far) {
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
								if (yc > 0 && yc < Main.HEIGHT) {
									visible = true;
								}
							}
						}
						zCoords[0] = points[face[0]][2];
						zCoords[1] = points[face[1]][2];
						zCoords[2] = points[face[2]][2];
						zCoords[3] = points[face[3]][2];
						if (visible){
						//Finds which face it is and what color it should be by index
							back = false;
							front = false;
							lside = false;
							rside = false;
							up = false;
							down = false;
							
							if (face[4] == 0) {
								back = true;
							}else if (face[4] == 1) {
								front = true;
							}else if (face[4] == 2) {
								rside = true;
							}else if (face[4] == 3) {
								lside = true;
							}else if (face[4] == 4) {
								down = true;
							}else if (face[4] == 5) {
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
							
							tx = pointsX[0];
							ty = pointsY[0];
							tz = pointsZ[0];
							//Debug proof the values are of the 3D points
	//						for(i=0;i < 4;i++){
	//							System.out.println(pointsX[i]+" "+pointsY[i]+" "+pointsZ[i]);
	//						}
							
							if (pointsX[0] < 0 || pointsX[1] < 0 || pointsX[2] < 0 || pointsX[3] < 0){ //If any point is left of the player
								for(i=0;i < 4;i++){
									if (tx < pointsX[i]){ //Find left most point in cube
										tx = pointsX[i];
									}
								}
							}
							if (pointsY[0] < 0 || pointsY[1] < 0 || pointsY[2] < 0 || pointsY[3] < 0){ //If any point is below the player
								for(i=0;i < 4;i++){
									if (ty < pointsY[i]){
										ty = pointsY[i];
									}
								}
							}
							if (pointsZ[0] < 0 || pointsZ[1] < 0 || pointsZ[2] < 0 || pointsZ[3] < 0){
								for(i=0;i < 4;i++){
									if (tz < pointsZ[i]){
										tz = pointsZ[i];
									}
								}
							}
							
							//Right side of cube... These are very relative terms
							if (pointsX[0] > 0 || pointsX[1] > 0 || pointsX[2] > 0 || pointsX[3] > 0){
								for(i=0;i < 4;i++){
									if (tx > pointsX[i]){
										tx = pointsX[i];
									}
								}
							}
							if (pointsY[0] > 0 || pointsY[1] > 0 || pointsY[2] > 0 || pointsY[3] > 0){
								for(i=0;i < 4;i++){
									if (ty > pointsY[i]){
										ty = pointsY[i];
									}
								}
							}
							if (pointsZ[0] > 0 || pointsZ[1] > 0 || pointsZ[2] > 0 || pointsZ[3] > 0){
								for(i=0;i < 4;i++){
									if (tz > pointsZ[i]){
										tz = pointsZ[i];
									}
								}
							}
							if (px > x && x+w > px && !lside && !rside){
								tx = 0;
							}if (py+1 > y && y+h > py+1 && !up && !down){
								ty = 0;
							}if (pz > z && z+d > pz && !back && !front){
								tz = 0;
							}
							//Sending blockface to be rendered at the chunk
							dist = (float) Math.pow(tz, 2) + Math.pow(tx, 2) + Math.pow(ty, 2);
							//Making a block face for each cube doesn't seem to affect performance much so I'll keep it to use for the raster
							tempFace = new BlockFace(xCoords, yCoords, zCoords, face, dist, faceColors[face[4]],cubeIndex);
							chunk.addFace(tempFace);
							
//							renderFaces.add(tempFace);
//							facesRender[count] = face;
//							colorFace[count] = faceColor;
//							count++;
							
//							g.setColor(faceColor);
//							g.fillPolygon(xCoords, yCoords, 4);
							
						}
						//renderFaces[index] = new BlockFace(xCoords, yCoords, face, dist, faceColor, visible,cubeIndex);
					}
				}
			}
			//Code for rendering by faces then cubes
//			renderFaces.sort(new sortFaces());
//			i = 0;
////			for (int[] face:facesRender){
//			while(i < count){
////				face = facesRender[count];
//				if (facesRender[count] != null){
//					xCoords[0] = points[facesRender[count][0]][0];
//					xCoords[1] = points[facesRender[count][1]][0];
//					xCoords[2] = points[facesRender[count][2]][0];
//					xCoords[3] = points[facesRender[count][3]][0];
//	
//					yCoords[0] = points[facesRender[count][0]][1];
//					yCoords[1] = points[facesRender[count][1]][1];
//					yCoords[2] = points[facesRender[count][2]][1];
//					yCoords[3] = points[facesRender[count][3]][1];
//					g.setColor(colorFace[count]);
//					// Face polygon
//					g.fillPolygon(xCoords, yCoords, 4);
//				}
//				i++;
//			}
			
			}
		}
	}
	
	public double getDist() {
		tx = (float) (x - px);
		ty = (float) (y - py);
		tz = (float) (z - pz);
		return (Math.sqrt(Math.pow(tx, 2) + Math.pow(ty, 2) + Math.pow(tz, 2)));
	}

	public int getIndex() {
		return cubeIndex;
	}public void setDebug(boolean debug){
		this.debug = debug;
	}

	public void tick() {}
	public boolean isVisible(){
		for (int[] face:faces){
			if (face[5] == 1){
				return true;
			}
		}
		return false;
	}
	//Code to cull the faces next to each other that would be overwritten anyways
	public void renderUpdate(){
		xOff= handler.getXOff();
		zOff= handler.getZOff();
		chunkData = handler.getWorld().get(pcx+xOff).get(pcz+zOff);
//		System.out.println(pcx +" "+ pcz);
//		chunkPosX = handler.getWorld().get(pcx+xOff+1).get(pcz+zOff);
//		chunkNegX = handler.getWorld().get(pcx+xOff-1).get(pcz+zOff);
//		chunkPosY = handler.getWorld().get(pcx+xOff).get(pcz+zOff+1);
//		chunkNegY = handler.getWorld().get(pcx+xOff).get(pcz+zOff-1);
		chunkMax = chunkData.size();
		
		int count = 0;
		for (int[] face : faces){
			
			visible = true;
			//Blue Side
			if (count == 0 && cubeIndex+1 < chunkMax && (cubeIndex+1) % 16 != 0 && chunkData.get(cubeIndex+1) == 1){
				visible = false;
			}
			//Red Side
			else if (count == 1 && cubeIndex-1 >= 0 && cubeIndex% 16 != 0 && chunkData.get(cubeIndex-1) == 1){
				visible = false;
			}
			
			//Green Side
			else if (count == 2 && cubeIndex+16 < chunkMax && ((cubeIndex+16) % 256 < 0 || (cubeIndex+16) % 256 > 15) && chunkData.get(cubeIndex+16) == 1){
				visible = false;
			}
			//Orange Side
			else if (count == 3 && cubeIndex-16 >= 0 && (cubeIndex % 256 < 0 || cubeIndex % 256 > 15) && chunkData.get(cubeIndex-16) == 1){
				visible = false;
			}
			
			//Yellow Side
			else if (count == 4 && cubeIndex-256 >= 0 && chunkData.get(cubeIndex-256) == 1){
				visible = false;
			}
			//Light Blue Side
			else if (count == 5 && cubeIndex+256 < chunkMax && chunkData.get(cubeIndex+256) == 1){
				visible = false;
			}
			//Adjacent Chunk Blue Side
//			else if (count == 0 && chunkPosX != null) {// && cubeIndex < handler.getWorld().get(pcx+xOff+1).get(pcz+zOff).size() //&& handler.getWorld().get(pcx+xOff+1).get(pcz+zOff).get(cubeIndex) == 1) {
//				visible = false;
//			}
			
			
			if (visible){
				face[5] = 1;
			}else{
				face[5] = 0;
			}
			count++;
		}chunkData.clear();
	}
	//Code used from DLC Energy now changed a bit
	private float[] rotate2D(float x, float y, double s,double c) {
		return new float[] { (float) (x * c - y * s), (float) (y * c + x * s) };
	}
}
