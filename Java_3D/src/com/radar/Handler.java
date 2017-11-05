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
	int x,y,z, osize,xOff,zOff,chunkX,chunkZ,width;
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
		chunkX = players[0].getChunkX();
		chunkZ = players[0].getChunkZ();
		try{
			currentChunk = chunks.get(chunkX+xOff).get(chunkZ+zOff);
//			if (objects == null){
//				currentChunk = chunks.get(xOff).get(zOff);
//				currentChunk = null;
//			}
		}catch(Exception e){
			//currentChunk = chunks.get(xOff).get(zOff);
			System.out.println("Null chunk");
			currentChunk = null;
		}
		
//		objects = new LinkedList<Cube>();
		
		//Checks if needs to load current chunk player is in or if it has already been put in rendering array
		if(currentChunk != null){
			loadChunk = true;
			for (Integer [] info:loadedChunks){
				if (info != null && info.length >3){
					if (info[0]!=null && info[1]!=null){
						if (info[0] == chunkX && info[1] == chunkZ){
							loadChunk = false;
							//System.out.println(chunkI);
						}
					}
				}
			}
		}
		//Loads chunks from world making them into cube objects to be rendered and ticked
		i = 0;
		if (loadChunk && currentChunk != null && !currentChunk.isEmpty()){
			osize = objects.size()-1;
			while(i < currentChunk.size()-1){
				if (currentChunk.get(i) == 1){
					width = 1;
//					try{
//						while (currentChunk.get(i) == 1 && currentChunk.get(i+1) == 1){
//							width++;
//							i++;
//						}
//					}catch(Exception e){System.out.println("Hi");}
					
					objects.add(new Cube((int) (((i-256*Math.floor(i/(double) 256))/16) + 16*chunkX+1),(int) Math.floor(i/(double) 256),(int) (((i-256*Math.floor(i/(double) 256))%16)+16*chunkZ+1),1,1,1,this,i,chunkX,chunkZ));
					//System.out.println((((i-256*Math.floor(i/(double) 256))%16)+16*chunkZ+1)+" "+i);
				}i++;
			}
			Integer[] tempArray = {chunkX,chunkZ,osize,objects.size()-1};
			loadedChunks[chunkI] = tempArray;
//			for (Integer[] data:loadedChunks){
//				if (data[0] != null){
//					System.out.println(data[0]+" "+data[1]);
//				}
//			}
			//System.out.println((chunks.get(chunkX+xOff).get(chunkZ+zOff) == currentChunk)+" X Off "+xOff+" Z Off "+zOff);
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
		objects = null;
		objects = new LinkedList<Cube>();
		loadedChunks = null;
		loadedChunks = new Integer[60][4];
		chunkI = 0;
	}
}

//Used for Cube sorting
class cubeCompare implements Comparator<Cube>{
	@Override
	public int compare(Cube c1, Cube c2){
		if (c1.getDist() < c2.getDist()){
			return 1;
		}if (c1.getDist() > c2.getDist()){
			return -1;
		}return 0;
	}
}