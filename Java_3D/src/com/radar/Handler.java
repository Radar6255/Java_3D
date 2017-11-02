package com.radar;

import java.awt.Graphics;
import java.util.Comparator;
import java.util.LinkedList;

public class Handler {
	//Cube[] objects = new Cube[20];
	LinkedList<Cube> objects = new LinkedList<Cube>();
	LinkedList<Integer> currentChunk = new LinkedList<Integer>();
	//Holds indices ofchunks loaded into render and where they start in the objects linkedList
	Integer [][] loadedChunks = new Integer[60][4];
	//Integer [] tempInts = new Integer[4];
	int x,y,z, osize,xOff,zOff;
	Player[] players = new Player[2];
	WorldGen gen;
	Cube tempCube;
	boolean looping,changed,out,loadChunk;
	LinkedList<LinkedList<LinkedList<Integer>>> chunks = new LinkedList<LinkedList<LinkedList<Integer>>>();
	int ci,pi,i,chunkI = 0;
	
	public void addGeneration(WorldGen gen){
		this.gen = gen;
	}public LinkedList<LinkedList<LinkedList<Integer>>> getWorld(){
		return gen.getWorld();
	}public int getXOff(){
		return gen.getXOff();
	}public int getZOff(){
		return gen.getZOff();
	}public void addPlayer(Player player){
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
		xOff = gen.getXOff();
		zOff = gen.getZOff();
		try{
			currentChunk = chunks.get(players[0].getChunkX()+xOff).get(players[0].getChunkZ()+zOff);
			if (objects == null){
				//currentChunk = chunks.get(xOff).get(zOff);
				currentChunk = null;
			}
		}catch(Exception e){
			//currentChunk = chunks.get(xOff).get(zOff);
			System.out.println("Null world");
			currentChunk = null;
		}
		i = 0;
//		objects = new LinkedList<Cube>();
		
		//Checks if needs to load current chunk player is in or if it has already been put in rendering array
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
		//Loads chunks from world making them into cube objects to be rendered
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
		
		objects.sort(new cubeCompare());
		
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
	}public WorldGen getGen(){
		return gen;
	}public void reloadChunks(){
		objects = new LinkedList<Cube>();
		loadedChunks = new Integer[60][4];
	}
}

//Used for Cube sorting
class cubeCompare implements Comparator<Cube>{
	@Override
	public int compare(Cube c1, Cube c2){
		if (c1.getDist() < c2.getDist()){
			return 1;
		}else{
			return -1;
		}
	}
}