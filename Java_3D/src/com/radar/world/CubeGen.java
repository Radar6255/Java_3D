package com.radar.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;

import com.radar.GpuHandler;
import com.radar.Handler;
import com.radar.Player;
import com.radar.cube.Cube;

public class CubeGen extends Thread{
	ArrayList<ArrayList<Integer>> chunkQueue = new ArrayList<ArrayList<Integer>>();
	LinkedList<Integer> chunkPos = new LinkedList<Integer>();
	int index, i, i2 = 0;
	int chunkX, chunkZ;
	Chunk chunkCreating;
	ArrayList<Integer> currentChunk;
	LinkedList<Integer> tempChunk;
	LinkedList<Integer> removedBlocks = new LinkedList<Integer>();
	boolean go = true;
	Player[] players;
	private Handler handler;
	Color[] faceColors = {new Color(0,162,0), new Color(0,162,0),new Color(0,162,0),new Color(0,162,0),Color.GREEN, new Color(0,132,0)};
	Color[] faceColorsS = {Color.gray, Color.gray,Color.gray,Color.gray,Color.gray, Color.darkGray};
	Color[] faceColorsS2 = {new Color(109, 100, 37), new Color(109, 100, 37),new Color(109, 100, 37),new Color(109, 100, 37),new Color(160, 147, 57), new Color(160, 147, 57)};
	Color[] testColors = {Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE, Color.YELLOW,Color.CYAN};
	Color[] cubeColors = new Color[6];
	GpuHandler gpuHandler;
	public CubeGen(Player[] players, Handler handler, GpuHandler gpuHandler){
		this.gpuHandler = gpuHandler;
		this.players = players;
		this.handler = handler;
	}
	//Way to change back to only single cubes
	boolean combined = true;
	
	public volatile boolean running = true;
//	int width,depth,tx,left,right = 0;
	int width,depth,tx = 0;
	public void run(){
		while(running){
//			System.out.println("Resumed");
			while (!chunkQueue.isEmpty()){
				if (!chunkQueue.get(0).isEmpty()){
					removedBlocks.clear();
					currentChunk = chunkQueue.get(0);
					i = 0;
					chunkX = chunkPos.get(0);
					chunkZ = chunkPos.get(1);
					chunkCreating = new Chunk(chunkX, chunkZ, players[0],handler, gpuHandler);
					if (combined){
						while(i < currentChunk.size()){
							if (currentChunk.get(i) != 0 && !valueIn(removedBlocks,i)) {
								if (currentChunk.get(i) == 1) {
									cubeColors = faceColors;
								}else {
									cubeColors = faceColorsS;
								}
//								cubeColors = faceColorsS;
								width = 1;
								depth = farthestDepth(i,16);
								removedDepth(depth, i);
//								if(depth > 1) {
//									System.out.println(depth);
//								}
								// && (int) Math.floor((((i+1)/256.0)-Math.floor((i+1)/256.0))*16.0) == (int) Math.floor((((i+2)/256.0)-Math.floor((i+2)/256.0))*16.0)
								while (depth > 1 && farthestDepth(i+1,depth) == depth) {
									removedDepth(depth,i+1);
									width++;
									i++;
//									cubeColors = faceColorsS2;
								}
//								if (depth == 1 && currentChunk.size() > i+16 && i > 16) {
//									left = currentChunk.get(i+16);
//									right = currentChunk.get(i-16);
//								}
//								while (depth == 1 && i > 16 && currentChunk.size() > i+16 && currentChunk.get(i+16) == left && currentChunk.get(i+16) == right && currentChunk.get(i+1) != 0 && !valueIn(removedBlocks,i+1) && (int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) == (int) Math.floor((((i+1)/256.0)-Math.floor((i+1)/256.0))*16.0)) {
								while (depth == 1 && currentChunk.size() > i+1 && currentChunk.get(i+1) != 0 && !valueIn(removedBlocks,i+1) && (int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) == (int) Math.floor((((i+1)/256.0)-Math.floor((i+1)/256.0))*16.0)) {
									width++;
									i++;
//									cubeColors = faceColors;
//									cubeColors = faceColorsS2;
								}
//								System.out.println(i);
//								cubeColors = testColors;
								new Cube(cubeColors.clone(), (int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) + 16*(chunkX), (int) Math.floor(i/256.0), (int) (((i/16.0)-Math.floor(i/16.0))*16.0) + 16*(chunkZ),depth,1,-width+2,handler,i,chunkX,chunkZ,chunkCreating );
							}
							i++;
						}
					}else{
						i = 0;
						for(int blockId : currentChunk){
							if (blockId == 1){
								new Cube(faceColors.clone(), (int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) + 16*(chunkX), (int) Math.floor(i/256.0), (int) (((i/16.0)-Math.floor(i/16.0))*16.0) + 16*(chunkZ),1,1,1,handler,i,chunkX,chunkZ,chunkCreating);
							}else if (blockId == 2) {
								new Cube(faceColorsS.clone(), (int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) + 16*(chunkX), (int) Math.floor(i/256.0), (int) (((i/16.0)-Math.floor(i/16.0))*16.0) + 16*(chunkZ),1,1,1,handler,i,chunkX,chunkZ,chunkCreating);
							}
							i++;
						}
					}

					chunkPos.removeFirst();
					chunkPos.removeFirst();
					handler.addChunk(chunkCreating);
					chunkQueue.remove(0);
					index-=2;
				}
			}
			try {
				synchronized(handler) {
					handler.wait();
				}
			}catch (InterruptedException e) {e.printStackTrace();}
		}
	}
	public void createChunk(ArrayList<Integer> chunk, int chunkX, int chunkZ){
			chunkPos.add(chunkX);
			chunkPos.add(chunkZ);
			index+=2;
			chunkQueue.add(chunk);
	}public boolean valueIn(LinkedList<Integer> removedBlocks, int value) {
		for (int i: removedBlocks) {
			if (value == i) {
				return true;
			}
		}return false;
	}
	public void removedDepth(int depth, int i) {
		while (depth > 0) {
			removedBlocks.add(i);
			i+=16;
			depth--;
		}
	}
	public int farthestDepth(int i, int maxDepth) {
		int orig = i;
		int depth = 0;
//		while (depth < maxDepth && i > 0 && currentChunk.size() > i+16 && currentChunk.get(i+16) != 0 && currentChunk.get(i+1) == left && currentChunk.get(i-1) == right && !valueIn(removedBlocks,i+1) && (int) Math.floor(i/256.0) == (int) Math.floor((i+16)/256.0) && (int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) == (int) Math.floor((((i+16)/256.0)-Math.floor((i+16)/256.0))*16.0)) {
//		while (depth <= maxDepth && currentChunk.size() > i+16 && currentChunk.get(i+16) != 0 && (int) (((i/16.0)-Math.floor(i/16.0))*16.0) == (int) ((((i+16)/16.0)-Math.floor((i+16)/16.0))*16.0) && (int) Math.floor(i/256.0) == (int) Math.floor((i+16)/256.0)) {
		while (depth <= maxDepth && currentChunk.size() > i && currentChunk.get(i) != 0 && !valueIn(removedBlocks,i) && (int) (((i/16.0)-Math.floor(i/16.0))*16.0) == (int) ((((i-16)/16.0)-Math.floor((i-16)/16.0))*16.0) && ((int) Math.floor(i/256.0) == (int) Math.floor((i-16)/256.0) || i == orig)) {
//			removedBlocks.add(i);
			i+=16;
			depth++;
		}
//		if (depth > 1) {
//			System.out.println(depth);
//		}
		return depth;
	}
}

