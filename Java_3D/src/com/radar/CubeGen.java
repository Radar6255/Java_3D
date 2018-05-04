package com.radar;

import java.util.LinkedList;

public class CubeGen extends Thread{
	LinkedList<LinkedList<Integer>> chunkQueue = new LinkedList<LinkedList<Integer>>();
	int[] chunkPos = new int[50];
	int index, i, i2 = 0;
	int chunkX, chunkZ;
	Chunk chunkCreating;
	LinkedList<Integer> currentChunk;
	LinkedList<Integer> tempChunk;
	boolean go = true;
	Player[] players;
	private Handler handler;
	public CubeGen(Player[] players, Handler handler){
		this.players = players;
		this.handler = handler;
	}
	
	
	public boolean running = true;
	public void run(){
		while(running){
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!chunkQueue.isEmpty()){
				if (!chunkQueue.get(0).isEmpty()){
					currentChunk = chunkQueue.get(0);
					i = 0;
					chunkX = chunkPos[0];
					chunkZ = chunkPos[1];
					chunkCreating = new Chunk(chunkX, chunkZ, players[0]);
					
					while(i < currentChunk.size()){
						if (currentChunk.get(i) == 1){
							new CombinedCube( (int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) + 16*(chunkX), (int) Math.floor(i/256.0), (int) (((i/16.0)-Math.floor(i/16.0))*16.0) + 16*(chunkZ),1,1,1,handler,i,chunkX,chunkZ,chunkCreating);
						}
						i++;
					}
					handler.addChunk(chunkCreating);
					chunkQueue.removeFirst();
					index-=2;
				}
			}
		}
	}
	public boolean createChunk(LinkedList<Integer> chunk, int chunkX, int chunkZ){
//		this.chunkX = chunkX;
//		this.chunkZ = chunkZ;
		if (index < 2){
			chunkPos[index] = chunkX;
			chunkPos[index+1] = chunkZ;
			index+=2;
			chunkQueue.add(chunk);
			return true;
		}else{
			return false;
		}
	}
}
