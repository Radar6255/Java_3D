package com.radar;

import java.awt.Graphics;
import java.util.LinkedList;

public class Handler {
	//Cube[] objects = new Cube[20];
	LinkedList<Cube> objects = new LinkedList<Cube>();
	LinkedList<Integer> currentChunk = new LinkedList<Integer>();
	Integer [][] loadedChunks = new Integer[20][4];
	//Integer [] tempInts = new Integer[4];
	int x,y,z, osize;
	Player[] players = new Player[2];
	WorldGen gen;
	Cube tempCube;
	boolean looping,changed,out,loadChunk;
	LinkedList<LinkedList<LinkedList<Integer>>> chunks = new LinkedList<LinkedList<LinkedList<Integer>>>();
	int ci,pi,i,chunkI = 0;
	
	public void addGeneration(WorldGen gen){
		this.gen = gen;
	}
	
	public void addPlayer(Player player){
		players[pi] = player;
		pi++;
	}
	
	public void addCube(Cube object){
		objects.add(object);
		ci++;
	}
	public void render(Graphics g){
		chunks = gen.getWorld();
		i = 0;
		try{
			currentChunk = chunks.get(players[0].getChunkX()).get(players[0].getChunkZ());
			if (objects == null){
				currentChunk = chunks.get(0).get(0);
			}
		}catch(Exception e){
			currentChunk = chunks.get(0).get(0);
			//System.out.println("Null");
		}
		i = 0;
//		objects = new LinkedList<Cube>();
		loadChunk = true;
		for (Integer [] info:loadedChunks){
			if (info != null && info.length >3){
				if (info[0]!=null && info[1]!=null){
					if (info[0] == players[0].getChunkX() && info[1] == players[0].getChunkZ()){
						loadChunk = false;
						//System.out.println(chunkI);
					}
				}
			}
		}
		if (loadChunk){
			osize = objects.size()-1;
			while(i < currentChunk.size()-1){
				if (currentChunk.get(i) == 1){
					objects.add(new Cube((int) (i/16) + 16*players[0].getChunkX(),(int) Math.floor(i/256),(int) ((((double) i/16)-Math.floor(i/16))*16)+16*players[0].getChunkZ(),1,1,1,this,i));
					//System.out.println("x "+(i/16)+" y "+(int) Math.floor(i/256)+" z "+ (Math.floor(i/16))+" z2 "+ ((double) i/16));
				}i++;
			}
			Integer[] tempArray = {players[0].getChunkX(),players[0].getChunkZ(),osize,objects.size()-1};
			loadedChunks[chunkI] = tempArray;
			chunkI++;
		}
		
		
		looping = true;
		i = 0;
		while (looping){
			if (i >= objects.size()){
				i = 0;
				if (!changed){
					looping = false;
				}
			}
			changed = false;
			try{
				if (objects.get(i).getDist() <= objects.get(i+1).getDist()){
					tempCube = objects.get(i);
					objects.set(i, objects.get(i+1));
					objects.set(i+1, tempCube);
					changed = true;
				}
			}catch(Exception e){
				
			}
			i++;
			
		}
		
		for (Cube object: objects){
			if (object != null){
				object.render(g);
			}
		}
		players[0].render(g);
	}
	public void tick(){
		gen.tick();
		for (Cube object: objects){
			if (object != null){
				object.tick();
			}
		}
		players[0].tick();
	}
	public Player getPlayer(){
		return players[0];
	}
	
}
