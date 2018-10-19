package com.radar;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;

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
	Color[] faceColors = {Color.GREEN, Color.GREEN,Color.GREEN,Color.GREEN,Color.GREEN, new Color(0,132,0)};
	Color[] faceColorsS = {Color.gray, Color.gray,Color.gray,Color.gray,Color.gray, Color.darkGray};
	Color[] faceColorsS2 = {new Color(109, 100, 37), new Color(109, 100, 37),new Color(109, 100, 37),new Color(109, 100, 37),new Color(160, 147, 57), new Color(160, 147, 57)};
	Color[] cubeColors = new Color[6];
	RenderThread renderThread1;
	public CubeGen(Player[] players, Handler handler, RenderThread renderThread1){
		this.players = players;
		this.handler = handler;
		this.renderThread1 = renderThread1;
	}
	//TODO Switch cube type
	boolean combined = true;
	
	public volatile boolean running = true;
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
					chunkCreating = new Chunk(chunkX, chunkZ, players[0],handler, renderThread1);
					//TODO rewrite to use for loop instead of get i which is very slow
					if (combined){
						while(i < currentChunk.size()){
							if (currentChunk.get(i) != 0 && !valueIn(removedBlocks,i)) {
								width = 1;
								if (currentChunk.get(i) == 1) {
									cubeColors = faceColors;
								}else {
									cubeColors = faceColorsS;
								}
								depth = farthestDepth(i,16);
								while (depth > 1 && farthestDepth(i+1,depth) == depth && (int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) == (int) Math.floor((((i+1)/256.0)-Math.floor((i+1)/256.0))*16.0)) {
									width++;
									i++;
//									cubeColors = faceColorsS2;
								}
								while (depth == 1 && currentChunk.size() > i+1 && currentChunk.get(i+1) != 0 && !valueIn(removedBlocks,i+1) && (int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) == (int) Math.floor((((i+1)/256.0)-Math.floor((i+1)/256.0))*16.0)) {
									width++;
									i++;
//									cubeColors = faceColorsS2;
								}
//								if (depth > 1) {
//									cubeColors = faceColorsS2;
//								}
								new Cube(cubeColors, (int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) + 16*(chunkX), (int) Math.floor(i/256.0), (int) (((i/16.0)-Math.floor(i/16.0))*16.0) + 16*(chunkZ),depth,1,-width+2,handler,i,chunkX,chunkZ,chunkCreating );
							}
							i++;
						}
					}else{
						i = 0;
						for(int blockId : currentChunk){
							if (blockId == 1){
								new Cube(faceColors, (int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) + 16*(chunkX), (int) Math.floor(i/256.0), (int) (((i/16.0)-Math.floor(i/16.0))*16.0) + 16*(chunkZ),1,1,1,handler,i,chunkX,chunkZ,chunkCreating);
							}else if (blockId == 2) {
								new Cube(faceColorsS, (int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) + 16*(chunkX), (int) Math.floor(i/256.0), (int) (((i/16.0)-Math.floor(i/16.0))*16.0) + 16*(chunkZ),1,1,1,handler,i,chunkX,chunkZ,chunkCreating);
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
//				System.out.println("Paused");
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
	}public int farthestDepth(int i, int maxDepth) {
		if (currentChunk.get(i) == 0) {
			return 0;
		}
		int depth = 1;
		while (depth < maxDepth && currentChunk.size() > i+16 && currentChunk.get(i+16) != 0 && !valueIn(removedBlocks,i+1) && (int) Math.floor(i/256.0) == (int) Math.floor((i+16)/256.0) && (int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) == (int) Math.floor((((i+1)/256.0)-Math.floor((i+1)/256.0))*16.0)) {
			removedBlocks.add(i+16);
			i+=16;
			depth++;
		}return depth;
	}
}

