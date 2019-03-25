package com.radar.world;

import java.awt.Graphics;
import java.util.LinkedList;

import com.radar.GpuHandler;
import com.radar.Handler;
import com.radar.Player;
import com.radar.RenderThread;
import com.radar.cube.BlockFace;
import com.radar.cube.CubeObject;

//This class is created when a chunk is loaded
//It is given to each cube created in the chunk
//The cubes then add themselves to the chunk list and are "rendered" and ticked
//When cubes are rendered they pass in the BlockFaces to sort and render

//These chunks are sorted in the handler using the getDist class

public class Chunk {
	int size;
	LinkedList<Integer> blockPos = new LinkedList<Integer>();
	LinkedList<CubeObject> blocks = new LinkedList<CubeObject>();
	LinkedList<CubeObject> visibleBlocks = new LinkedList<CubeObject>();
	volatile LinkedList<BlockFace> facesToRender = new LinkedList<BlockFace>();
	volatile LinkedList<BlockFace> facesToRender2 = new LinkedList<BlockFace>();
	RenderThread renderThread1;
	public int chunkX, chunkZ, xOff, zOff,off, i;
	public volatile boolean threadReady = false;
	public boolean doubleRender = false;
	public double dist;

	GpuHandler gpuHandler;
	Handler handler;
	Player player;
	public Chunk(int chunkX, int chunkZ, Player player, Handler handler, GpuHandler gpuHandler){
		this.gpuHandler = gpuHandler;
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		this.player = player;
		this.handler = handler;
	}
	
	public void tick(){
		for (CubeObject object: blocks){
			object.tick();
		}
	}
	double[] out = new double[24];
	public double[] allVerts;
	double rotLat, rotVert, lowerBound,upperBound, bound = 0;
	public double sl,cl,sv,cv;
	boolean renderChunk = false;
	boolean first = true;
	float[] playerCoords = new float[3];
	public void moveOn () {
		threadReady = true;
	}
	float [] relativePos;
	public void render(Graphics g){
		
//		renderChunk = false;
//		lowerBound = (360 - rotLat) - 30;
//		upperBound = (360 - rotLat) + 30;
//		
//		if ((chunkX-(playerCoords[0]/16)) == 0){
//			bound = Math.toDegrees(Math.atan(chunkZ-(playerCoords[2] = playerCoords[2]/16)));
//		}else{
//			bound = Math.toDegrees(Math.atan((chunkZ-(playerCoords[2] = playerCoords[2]/16))/(chunkX-(playerCoords[0]/16))));
//		}
//		
//		//2nd Quad
//		if((chunkX-(playerCoords[0]/16)) < 0 && (chunkZ-(playerCoords[2] = playerCoords[2]/16)) > 0){
//			bound = 180 + bound;
//		}//3rd Quad
//		else if((chunkX-(playerCoords[0]/16)) < 0 && (chunkZ-(playerCoords[2] = playerCoords[2]/16)) < 0){
//			bound = 180 + bound;
//		}//4th  Quad
//		else if((chunkX-(playerCoords[0]/16)) > 0 && (chunkZ-(playerCoords[2] = playerCoords[2]/16)) < 0){
//			bound = 360 + bound;
//		}
//		
//		bound -= 90.5;
//		
//		if ((bound > lowerBound && bound < upperBound) || (bound > lowerBound+360 && bound < upperBound+360) || (bound > lowerBound-360 && bound < upperBound-360)){
//			renderChunk = true;
//		}
	}public int getChunkX(){
		return chunkX;
	}public int getChunkZ(){
		return chunkZ;
	}public void fovUpdate() {
		for (CubeObject cube : visibleBlocks) {
			cube.updateFov();
		}
	}
	
	public float[] calcBlockPos(float[] srcArrayA) {
		int s = blockPos.size();
		float[] srcArrayB = new float[s];
		int i = 0;
		for (Integer coord: blockPos) {
			srcArrayB[i] = coord;
			i++;
		}
		return gpuHandler.findBlockPos(srcArrayA, srcArrayB, s);
	}
	
	public void addFace(BlockFace face){
		if (Thread.currentThread().getName() == "Render1") {
			facesToRender.add(face);
		}else if (Thread.currentThread().getName() == "Render2") {
			facesToRender2.add(face);
		}else {
			facesToRender2.add(face);
		}
	}public void addCube(CubeObject cube,int x, int y, int z){
		blocks.add(cube);
		if (cube.isVisible()){
			visibleBlocks.add(cube);
			blockPos.add(x);
			blockPos.add(y);
			blockPos.add(z);
		}
	}
	public double getDist(){
		dist = Math.sqrt(Math.pow((16*chunkX)-player.getX()+8, 2)+Math.pow((16*chunkZ)-player.getZ()+8, 2));
		return dist;
	}
	public void placeBlock() {
		for (CubeObject cube:blocks) {
			cube.placeBlock();
		}
	}public LinkedList<CubeObject> getVisibleBlocks() {
		return visibleBlocks;
	}public LinkedList<Integer> getBlockPos(){
		return blockPos;
	}
}

