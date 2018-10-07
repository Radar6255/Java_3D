package com.radar;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;

public class CubeGen extends Thread{
	LinkedList<LinkedList<Integer>> chunkQueue = new LinkedList<LinkedList<Integer>>();
	LinkedList<Integer> chunkPos = new LinkedList<Integer>();
	int index, i, i2 = 0;
	int chunkX, chunkZ;
	Chunk chunkCreating;
	LinkedList<Integer> currentChunk;
	LinkedList<Integer> tempChunk;
	boolean go = true;
	Player[] players;
	private Handler handler;
	Color[] faceColors = {Color.GREEN, Color.GREEN,Color.GREEN,Color.GREEN,Color.GREEN, new Color(0,132,0)};
	Color[] faceColorsS = {Color.gray, Color.gray,Color.gray,Color.gray,Color.gray, Color.darkGray};
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
	int width,tx = 0;
	public void run(){
		while(running){
//			System.out.println("Resumed");
			while (!chunkQueue.isEmpty()){
				if (!chunkQueue.get(0).isEmpty()){
					
					currentChunk = chunkQueue.get(0);
					i = 0;
					chunkX = chunkPos.get(0);
					chunkZ = chunkPos.get(1);
					chunkCreating = new Chunk(chunkX, chunkZ, players[0],handler, renderThread1);
					//TODO rewrite to use for loop instead of get i which is very slow
					if (combined){
						Iterator<Integer> iter = currentChunk.iterator();
						Integer block = iter.next();
						while(iter.hasNext()){
							if (block != 0){
								if (block == 1) {
									cubeColors = faceColors;
								}else {
									cubeColors = faceColorsS;
								}
								width = 1;
	//							System.out.println((int) (((i/16.0)-Math.floor(i/16.0))*16.0));
								try{
									if (block != 0) {
										tx = i;
										while (block == 1 && !((int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) != (int) Math.floor((((i+1)/256.0)-Math.floor((i+1)/256.0))*16.0)) && currentChunk.get(i+1) == 1){
											width++;
											i++;
											block = iter.next();
										}
									}
								}catch(Exception e){}
								new Cube(cubeColors, (int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) + 16*(chunkX), (int) Math.floor(i/256.0), (int) (((i/16.0)-Math.floor(i/16.0))*16.0) + 16*(chunkZ),1,1,-width+2,handler,i,chunkX,chunkZ,chunkCreating );
								//System.out.println((((i-256*Math.floor(i/(double) 256))%16)+16*chunkZ+1)+" "+i);
							}
							i++;
							block = iter.next();
						}
					}else{
						i = 0;
						for(int blockId : currentChunk){
							if (blockId == 1){
								new Cube(faceColors, (int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) + 16*(chunkX), (int) Math.floor(i/256.0), (int) (((i/16.0)-Math.floor(i/16.0))*16.0) + 16*(chunkZ),1,1,1,handler,i,chunkX,chunkZ,chunkCreating);
//								new TriCube( (int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) + 16*(chunkX), (int) Math.floor(i/256.0), (int) (((i/16.0)-Math.floor(i/16.0))*16.0) + 16*(chunkZ),1,1,1,handler,i,chunkX,chunkZ,chunkCreating);
							}else if (blockId == 2) {
								new Cube(faceColorsS, (int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) + 16*(chunkX), (int) Math.floor(i/256.0), (int) (((i/16.0)-Math.floor(i/16.0))*16.0) + 16*(chunkZ),1,1,1,handler,i,chunkX,chunkZ,chunkCreating);
							}
							i++;
						}
					}

					chunkPos.removeFirst();
					chunkPos.removeFirst();
					handler.addChunk(chunkCreating);
					chunkQueue.removeFirst();
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
	public void createChunk(LinkedList<Integer> chunk, int chunkX, int chunkZ){
			chunkPos.add(chunkX);
			chunkPos.add(chunkZ);
			index+=2;
			chunkQueue.add(chunk);
	}
}
