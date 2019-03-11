package com.radar;

import java.awt.Color;
import java.util.ArrayList;
public class Cube extends CubeObject{
	public int x, y, z, w, h, d, i, fov, far, cubeIndex,pcx,pcy,pcz,xOff,zOff, chunkMax, height, width;
	public float tx, ty, tz;
	
	public static volatile long msLag;
	
	//px, py, pz
	public double f, f2, rotLat, rotVert, dist, ldist, dist2, bound;
	public boolean hasFar,place = false;
	public Color[] faceColors = new Color[6];
	public boolean visible, changed, looping, render,repeat,renderBlock,right,left,up,down,lside,rside,back,front;
	ArrayList<Integer> chunkData = new ArrayList<Integer>();
//	LinkedList<Integer> chunkPosX = new LinkedList<Integer>();
//	LinkedList<Integer> chunkNegX = new LinkedList<Integer>();
//	LinkedList<Integer> chunkPosY = new LinkedList<Integer>();
//	LinkedList<Integer> chunkNegY = new LinkedList<Integer>();
	Chunk chunk;
	
	public static float[][] verts = { { 0.5f, 0.5f, 0.5f }, { 0.5f, -0.5f, 0.5f }, { -0.5f, -0.5f, 0.5f },{ -0.5f, 0.5f, 0.5f }, { 0.5f, 0.5f, -0.5f }, { 0.5f, -0.5f, -0.5f }, { -0.5f, -0.5f, -0.5f },{ -0.5f, 0.5f, -0.5f } };

	public float[][] vertsMod = { { 0.5f, 0.5f, 0.5f }, { 0.5f, -0.5f, 0.5f }, { -0.5f, -0.5f, 0.5f },{ -0.5f, 0.5f, 0.5f }, { 0.5f, 0.5f, -0.5f }, { 0.5f, -0.5f, -0.5f }, { -0.5f, -0.5f, -0.5f },{ -0.5f, 0.5f, -0.5f } };

	private int[][] faces = { { 0, 1, 2, 3, 0, 1}, { 4, 5, 6, 7, 1, 1}, { 0, 4, 5, 1, 2, 1}, { 2, 6, 7, 3, 3, 1},{ 1, 5, 6, 2, 4, 1}, { 3, 7, 4, 0, 5, 1} };
	public float[][] points3D = new float[9][3];
	
	public float[] pointsZ = new float[4];
	
	public int[][] points = new int[8][3];
	public double[] distances = new double[8];
	private int[] xCoords = new int[4];
	private int[] yCoords = new int[4];
	private int[] zCoords = new int[4];
	public BlockFace tempFace;

	public double upperBound, lowerBound = 0;
	
	public Cube(Color[] faceColors,int x, int y, int z, int w, int h, int d,Handler handler, int cubeIndex,int chunkX,int chunkZ, Chunk chunk) {
		super(handler);
		this.faceColors = faceColors;
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.h = h;
		this.d = d;
//		this.handler = handler;
		this.chunk = chunk;
		pcx = chunkX;
		pcz = chunkZ;
		pcy = (int) y;
		this.cubeIndex = cubeIndex;
		
		width = handler.getWidth();
		height = handler.getHeight();
		if (width < height) {
			fov = width;
		} else {
			fov = height;
		}
		
		chunk.addCube(this,x,y,z);
		if (w == 1 && d == 1) {
			renderUpdate();
		}
		else {
//			combinedCubeUpdate();
		}
		updateModVerts();
		
	}
	public void render(double relx, double rely, double relz, double rotLat, double rotVert, double sl, double cl, double sv, double cv) {
		long startRender = System.currentTimeMillis();
		//Need to get viewing angle and use it to make V in which cubes should be rendered
		//Saves a few fps
		renderBlock = false;
		//TODO Push the V backwards from players perspective, need to use sin + cos to find where the bottom of the v starts
		lowerBound = (360 - rotLat) - 60;
		upperBound = (360 - rotLat) + 60;
		
		if ((relx) == 0){
			bound = Math.toDegrees(Math.atan(relz));
			//System.out.println(z-pz);
		}else{
			bound = Math.toDegrees(Math.atan((relz)/(relx)));
			//System.out.println((z-pz)/(x-px));
		}
		
		//2nd Quad
		if((relx) < 0 && (relz) > 0){
			bound = 180 + bound;
		}//3rd Quad
		else if((relx) < 0 && (relz) < 0){
			bound = 180 + bound;
		}//4th  Quad
		else if((relx) > 0 && (relz) < 0){
			bound = 360 + bound;
		}
		
		bound += 90.5;
		
		if ((bound > lowerBound && bound < upperBound) || (bound > lowerBound+360 && bound < upperBound+360) || (bound > lowerBound-360 && bound < upperBound-360)){
			renderBlock = true;
		}
		if (renderBlock){
			//Loop through all vertices to find which is farthest from player
			i = 0;
			far = 0;
			
			for (float[] point : verts) {
				tx = vertsMod[i][0];
				ty = vertsMod[i][1];
				tz = vertsMod[i][2];
				
				tx = (float) (tx + (relx));
				ty = (float) (ty + (rely));
				tz = (float) (tz + (relz));
				
				points3D[i][0] = tx;
				points3D[i][1] = ty;
				
				//Rotates points so that player appears to look around
				point = rotate2D(tx, tz, sl,cl);
				tx = point[0];
				tz = point[1];
				
				point = rotate2D(ty, tz, sv,cv);
				ty = point[0];
				tz = point[1];
				dist = -tz;
//				if (tx > 0) {
//					dist = -tz+(tx*0.2);
//					faceColors[5] = Color.RED;
//				}else {
//					dist = -tz;
//					faceColors[5] = Color.BLUE;
//				}
				
				points3D[i][2] = -tz;
				//TODO Double check visual bugs
				if (i == 0) {
					ldist = dist;
				}
				//Calculates field of view
				if (tz != 0) {
					f = fov / tz;
				} else {
					f = fov;
				}
				if (f > 0){
					renderBlock = false;
				}
				
				//Puts 2D points into array for later when I need to render the polygons

				points[i][0] = (int) ((tx * f) + (width / 2));
				points[i][1] = (int) ((ty * f) + (height));
				points[i][2] = (int) (tz);
				
				//Finds point farthest from the player
				if (ldist < dist) {
					ldist = dist;
					far = i;
				}
				i++;
			}
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
					//TODO add ||true to render all faces instead of just 3
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
							if (xc > 0 && xc < width) {
								visible = true;
							}
						}if (visible) {
							visible = false;
							for (int yc : yCoords) {
								if (yc > 0 && yc < height) {
									visible = true;
								}
							}
						}
						zCoords[0] = points[face[0]][2];
						zCoords[1] = points[face[1]][2];
						zCoords[2] = points[face[2]][2];
						zCoords[3] = points[face[3]][2];
						if (visible){
							//Distance calculated when getting 2d points
							
							pointsZ[0] = points3D[face[0]][2];
							pointsZ[1] = points3D[face[1]][2];
							pointsZ[2] = points3D[face[2]][2];
							pointsZ[3] = points3D[face[3]][2];
							
							//Sending block face to be rendered at the chunk
							//Making a block face objects for each cube doesn't seem to affect performance much
							dist = 0;
							for (float testz:pointsZ) {
								dist+=testz;
							}
							if (containsPoint(width/2,height/2,xCoords,yCoords)) {
								if (place) {
									if (face[4] == 0) {
										z+=1;
										d-=1;
									}else if (face[4]==1) {
										d-=1;
									}else if (face[4]==2) {
										w+=1;
									}else if (face[4]==3) {
										w+=1;
										x-=1;
									}else if (face[4]==4) {
										h+=1;
										y-=1;
									}else {
										h+=1;
									}
									updateModVerts();
									tempFace = new BlockFace(xCoords, yCoords, zCoords, face, dist, Color.red,cubeIndex);
//									chunk.addFace(tempFace);
									handler.addFace(tempFace);
								}else {
									tempFace = new BlockFace(xCoords, yCoords, zCoords, face, dist, Color.RED,cubeIndex);
									handler.addFace(tempFace);
//									chunk.addFace(tempFace);
								}
							}else if (!place) {
								tempFace = new BlockFace(xCoords, yCoords, zCoords, face, dist, faceColors[face[4]],cubeIndex);
//								chunk.addFace(tempFace);
								handler.addFace(tempFace);
							}
						}
					}
				}
			}
			
			}
		}
		place = false;
		msLag += System.currentTimeMillis() - startRender;
	}
	
	public void updateModVerts() {
		i = 0;
		while (i < vertsMod.length) {
			//Pulls x,y,z of points taking into account width, height, and depth
			if (vertsMod[i][0] > 0.0){
				vertsMod[i][0] = verts[i][0] + (w-1);
			}else {
				vertsMod[i][0] = verts[i][0];
			}
			if (vertsMod[i][1] > 0.0){
				vertsMod[i][1] = verts[i][1] + (h-1);
			}else {
				vertsMod[i][1] = verts[i][1];
			}
			if (vertsMod[i][2] < 0.0){
				vertsMod[i][2] = verts[i][2] + (d-1);
			}else {
				vertsMod[i][2] = verts[i][2];
			}
			i++;
		}
	}
	
	public boolean containsPoint(int x, int y, int[] xCoords, int[] yCoords) {
		boolean leftS = false;
		boolean rightS = false;
		boolean cont = false;
		int xSum = 0;
		int ySum = 0;
		for (int point : xCoords){
			xSum += point;
			if (point > x) {
				leftS = true;
			}else {
				rightS = true;
			}
		}
		if(leftS && rightS) {
			
			leftS = false;
			rightS = false;
			for (int point : yCoords){
				ySum += point;
				if (point > y) {
					leftS = true;
				}else {
					rightS = true;
				}
			}if (leftS && rightS) {
				cont = true;
			}
		}
		
		if (!cont) {
			return false;
		}
		boolean prt1,prt2,prt3,prt4;
		prt1 = prt2 = prt3 = prt4 = false;
		if (xCoords[0]-xCoords[1] != 0 && ySum/4 >= (((yCoords[0]-yCoords[1])/(xCoords[0]-xCoords[1]))*((xSum/4)-xCoords[0])) + yCoords[0]) {
			if (y > yCoords[0]+(((yCoords[0]-yCoords[1])/(xCoords[0]-xCoords[1]))*(x-xCoords[0]))) {
				prt1 = true;
			}
		}else if (xCoords[0]-xCoords[1] == 0 && yCoords[0]-yCoords[1] != 0 && xSum/4 >= (((xCoords[0]-xCoords[1])/(yCoords[0]-yCoords[1]))*((ySum/4)-yCoords[0])) + xCoords[0]) {
			if (x > xCoords[0]+(((xCoords[0]-xCoords[1])/(yCoords[0]-yCoords[1]))*(y-yCoords[0]))) {
				prt1 = true;
			}
		}
		if (xCoords[1]-xCoords[2] != 0 && ySum/4 >= (((yCoords[1]-yCoords[2])/(xCoords[1]-xCoords[2]))*((xSum/4)-xCoords[1])) + yCoords[1]) {
			if (y > yCoords[1]+(((yCoords[1]-yCoords[2])/(xCoords[1]-xCoords[2]))*(x-xCoords[1]))) {
				prt2 = true;
			}
		}else if (xCoords[1]-xCoords[2] == 0 && yCoords[1]-yCoords[2] != 0 && xSum/4 >= (((xCoords[1]-xCoords[2])/(yCoords[1]-yCoords[2]))*((ySum/4)-yCoords[1])) + xCoords[1]) {
			if (x > xCoords[1]+(((xCoords[1]-xCoords[2])/(yCoords[1]-yCoords[2]))*(y-yCoords[1]))) {
				prt2 = true;
			}
		}
		if (xCoords[2]-xCoords[3] != 0 && ySum/4 >= (((yCoords[2]-yCoords[3])/(xCoords[2]-xCoords[3]))*((xSum/4)-xCoords[2])) + yCoords[2]) {
			if (y > yCoords[2]+(((yCoords[2]-yCoords[3])/(xCoords[2]-xCoords[3]))*(x-xCoords[2]))) {
				prt3 = true;
			}
		}else if (xCoords[2]-xCoords[3] == 0 && yCoords[2]-yCoords[3] != 0 && xSum/4 >= (((xCoords[2]-xCoords[3])/(yCoords[2]-yCoords[3]))*((ySum/4)-yCoords[2])) + xCoords[2]) {
			if (x > xCoords[2]+(((xCoords[2]-xCoords[3])/(yCoords[2]-yCoords[3]))*(y-yCoords[2]))) {
				prt3 = true;
			}
		}
		if (xCoords[0]-xCoords[3] != 0 && ySum/4 >= (((yCoords[0]-yCoords[3])/(xCoords[0]-xCoords[3]))*((xSum/4)-xCoords[0])) + yCoords[0]) {
			if (y > yCoords[0]+(((yCoords[0]-yCoords[3])/(xCoords[0]-xCoords[3]))*(x-xCoords[0]))) {
				prt4 = true;
			}
		}else if (xCoords[0]-xCoords[3] == 0 && yCoords[0]-yCoords[3] != 0 && xSum/4 >= (((xCoords[0]-xCoords[3])/(yCoords[0]-yCoords[3]))*((ySum/4)-yCoords[0])) + xCoords[0]) {
			if (x > xCoords[0]+(((xCoords[0]-xCoords[3])/(yCoords[0]-yCoords[3]))*(y-yCoords[0]))) {
				prt4 = true;
			}
		}
		
		if (prt1 && prt2 && prt3 && prt4) {
			return true;
		}
		
		if (xCoords[0]-xCoords[1] != 0 && ySum/4 < (((yCoords[0]-yCoords[1])/(xCoords[0]-xCoords[1]))*((xSum/4)-xCoords[0])) + yCoords[0]) {
			if (y < yCoords[0]+(((yCoords[0]-yCoords[1])/(xCoords[0]-xCoords[1]))*(x-xCoords[0]))) {
				prt1 = true;
			}
		}else if (xCoords[0]-xCoords[1]==0 && yCoords[0]-yCoords[1] != 0 && xSum/4 < (((xCoords[0]-xCoords[1])/(yCoords[0]-yCoords[1]))*((ySum/4)-yCoords[0])) + xCoords[0]) {
			if (x < xCoords[0]+(((xCoords[0]-xCoords[1])/(yCoords[0]-yCoords[1]))*(y-yCoords[0]))) {
				prt1 = true;
			}
		}
		if (xCoords[1]-xCoords[2] != 0 && ySum/4 < (((yCoords[1]-yCoords[2])/(xCoords[1]-xCoords[2]))*((xSum/4)-xCoords[1])) + yCoords[1]) {
			if (y < yCoords[1]+(((yCoords[1]-yCoords[2])/(xCoords[1]-xCoords[2]))*(x-xCoords[1]))) {
				prt2 = true;
			}
		}else if (xCoords[1]-xCoords[2]==0 && yCoords[1]-yCoords[2] != 0 && xSum/4 < (((xCoords[1]-xCoords[2])/(yCoords[1]-yCoords[2]))*((ySum/4)-yCoords[1])) + xCoords[1]) {
			if (x < xCoords[1]+(((xCoords[1]-xCoords[2])/(yCoords[1]-yCoords[2]))*(y-yCoords[1]))) {
				prt2 = true;
			}
		}
		if (xCoords[2]-xCoords[3] != 0 && ySum/4 < (((yCoords[2]-yCoords[3])/(xCoords[2]-xCoords[3]))*((xSum/4)-xCoords[2])) + yCoords[2]) {
			if (y < yCoords[2]+(((yCoords[2]-yCoords[3])/(xCoords[2]-xCoords[3]))*(x-xCoords[2]))) {
				prt3 = true;
			}
		}else if (xCoords[2]-xCoords[3]==0 && yCoords[2]-yCoords[3] != 0 && xSum/4 < (((xCoords[2]-xCoords[3])/(yCoords[2]-yCoords[3]))*((ySum/4)-yCoords[2])) + xCoords[2]) {
			if (x < xCoords[2]+(((xCoords[2]-xCoords[3])/(yCoords[2]-yCoords[3]))*(y-yCoords[2]))) {
				prt3 = true;
			}
		}
		if (xCoords[0]-xCoords[3] != 0 && ySum/4 < (((yCoords[0]-yCoords[3])/(xCoords[0]-xCoords[3]))*((xSum/4)-xCoords[0])) + yCoords[0]) {
			if (y < yCoords[0]+(((yCoords[0]-yCoords[3])/(xCoords[0]-xCoords[3]))*(x-xCoords[0]))) {
				prt4 = true;
			}
		}else if (xCoords[0]-xCoords[3]==0 && yCoords[0]-yCoords[3] != 0 && xSum/4 < (((xCoords[0]-xCoords[3])/(yCoords[0]-yCoords[3]))*((ySum/4)-yCoords[0])) + xCoords[0]) {
			if (x < xCoords[0]+(((xCoords[0]-xCoords[3])/(yCoords[0]-yCoords[3]))*(y-yCoords[0]))) {
				prt4 = true;
			}
		}
		
		if (prt1 && prt2 && prt3 && prt4) {
			return true;
		}
		return false;
	}

	public int getIndex() {
		return cubeIndex;
	}

	public void tick() {}
	
	public boolean isVisible(){
		for (int[] face:faces){
			if (face[5] == 1){
				return true;
			}
		}
		return false;
	}public void combinedCubeUpdate() {
		
		xOff= handler.getXOff();
		zOff= handler.getZOff();
		chunkData = handler.getWorld().get(pcx+xOff).get(pcz+zOff);
		chunkMax = chunkData.size();
		//axis x == width
		int xPos = 0;
		int yPos = d-1;
		for (int[] face : faces){
			face[5] = 0;
		}
//		System.out.println("w: "+w+" d: "+d);
		while (xPos <= w) {
			while (yPos <= 1) {
				int count = 0;
//				System.out.println("xPos:"+xPos+" yPos:"+yPos);
				int testPos = cubeIndex - yPos + (16*(xPos+2));
//				System.out.println(testPos);
				for (int[] face : faces){
					visible = true;
					//Blue Side
					if (face[5] == 1) {continue;}
					else if (count == 0 && testPos+1 < chunkMax && (testPos+1) % 16 != 0 && chunkData.get(testPos+1) != 0){
						visible = false;
					}
					//Red Side
					else if (count == 1 && testPos-1 >= 0 && testPos% 16 != 0 && chunkData.get(testPos-1) != 0){
						visible = false;
					}
					//Green Side
					else if (count == 2 && testPos+16 < chunkMax && ((testPos+16) % 256 < 0 || (testPos+16) % 256 > 15) && chunkData.get(testPos+16) != 0){
						visible = false;
					}
					//Orange Side
					else if (count == 3 && testPos-16 >= 0 && (testPos % 256 < 0 || testPos % 256 > 15) && chunkData.get(testPos-16) != 0){
						visible = false;
					}
					//Yellow Side
					else if (count == 4 && testPos-256 >= 0 && chunkData.get(testPos-256) != 0){
						visible = false;
					}
					//Light Blue Side
					else if (count == 5 && testPos+256 < chunkMax && chunkData.get(testPos+256) != 0){
						visible = false;
					}
					else {
						visible = true;
					}
					//Adjacent Chunk Blue Side
//					else if (count == 0 && chunkPosX != null) {// && cubeIndex < handler.getWorld().get(pcx+xOff+1).get(pcz+zOff).size() //&& handler.getWorld().get(pcx+xOff+1).get(pcz+zOff).get(cubeIndex) == 1) {
//						visible = false;
//					}
					
					
					if (visible){
						face[5] = 1;
					}else{
						face[5] = 0;
					}
					count++;
				}
				yPos++;
			}
			yPos = d-1;
			xPos++;
		}
	}
	public void renderUpdate(){
		/**
		 * Code to cull the faces next to each other that would be overwritten anyways
		 */
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
			if (count == 0 && cubeIndex+1 < chunkMax && (cubeIndex+1) % 16 != 0 && chunkData.get(cubeIndex+1) != 0 && d == 1){
				visible = false;
			}
			//Red Side
			else if (count == 1 && cubeIndex-1 >= 0 && cubeIndex% 16 != 0 && chunkData.get(cubeIndex-1) != 0 && d == 1){
				visible = false;
			}
			
			//Green Side
			else if (count == 2 && cubeIndex+16 < chunkMax && ((cubeIndex+16) % 256 < 0 || (cubeIndex+16) % 256 > 15) && chunkData.get(cubeIndex+16) != 0 && w == 1){
				visible = false;
			}
			//Orange Side
			else if (count == 3 && cubeIndex-16 >= 0 && (cubeIndex % 256 < 0 || cubeIndex % 256 > 15) && chunkData.get(cubeIndex-16) != 0){
				visible = false;
			}
			
			//Yellow Side
			else if (count == 4 && cubeIndex-256 >= 0 && chunkData.get(cubeIndex-256) != 0){
				visible = false;
			}
			//Light Blue Side
			else if (count == 5 && cubeIndex+256 < chunkMax && chunkData.get(cubeIndex+256) != 0){
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
		}
	}public void placeBlock() {
		place = true;
		Player player = handler.getPlayer();
		
		double sl = player.getSineLat();
		double cl = player.getCosineLat();
		double sv = player.getSineVert();
		double cv = player.getCosineVert();
		render(player.getX(),player.getY(),player.getZ(),player.getRotLat(),player.getRotVert(),sl,cl,sv,cv);
	}
	//Code used from DLC Energy now changed a bit
	private float[] rotate2D(float x, float y, double s,double c) {
		return new float[] { (float) (x * c - y * s), (float) (y * c + x * s) };
	}
}
