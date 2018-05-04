package com.radar;

import java.awt.Graphics;
import java.util.Comparator;
import java.util.LinkedList;

//Handler class makes worldGen into a reality
//Gets info from world then creates chunks and cubes from them
//Uses created chunks sorted to render all BlockFaces
public class Handler {
	
	//Cube[] objects = new Cube[20];
	LinkedList<CubeObject> objects = new LinkedList<CubeObject>();
	LinkedList<Chunk> renderChunks = new LinkedList<Chunk>();
	LinkedList<Chunk> renderChunks2 = new LinkedList<Chunk>();
	LinkedList<Chunk> objectsSorted = new LinkedList<Chunk>();
	LinkedList<Integer> currentChunk = new LinkedList<Integer>();
	public CubeGen cubeGen;
	public Handler(){
		cubeGen = new CubeGen(players, this);
		cubeGen.start();
	}
	
	//Holds indices of chunks loaded into render and where they start in the objects linkedList
	LinkedList<Integer[]> renderedChunks = new LinkedList<Integer[]>();
	int x,y,z, osize,xOff,zOff,chunkX,chunkZ,width,ix,iz,sCubeCount,chunkSize,cCubeCount,tx;
	Player[] players = new Player[2];
	WorldGen gen;
	Cube tempCube;
	boolean looping,changed,out,loadChunk,debug;
	Chunk chunkCreating;
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
	}public void addCube(Cube object){
		objects.add(object);
		ci++;
	}public void debugMode(boolean debug){
		gen.setDebug(debug);
		for (Chunk chunk:renderChunks){
			chunk.setDebug(debug);
		}players[0].setDebug(debug);
		this.debug = debug;
	}public Player getPlayer(){
		return players[0];
	}public WorldGen getGen(){
		return gen;
	}public void reloadChunks(){
		objects = null;
		objects = new LinkedList<CubeObject>();
		renderedChunks = null;
		renderedChunks = new LinkedList<Integer[]>();
		chunkI = 0;
		sCubeCount = 0;
		cCubeCount = 0;
	}public void addChunk(Chunk chunk){
		renderChunks2.add(chunk);
		
		//Integer[] tempArray = {chunkX+ix,chunkZ+iz,osize,objects.size()-1};
		
	}
	@SuppressWarnings("unchecked")
	public void render(Graphics g){
		
		chunks = gen.getWorld();
		i = 0;
		xOff = gen.getXOff();
		zOff = gen.getZOff();
		chunkX = players[0].getChunkX();
		chunkZ = players[0].getChunkZ();
		try{
			currentChunk = chunks.get(chunkX+xOff+ix).get(chunkZ+zOff+iz);
//			if (objects == null){
//				currentChunk = chunks.get(xOff).get(zOff);
//				currentChunk = null;
//			}
		}catch(Exception e){
			//currentChunk = chunks.get(xOff).get(zOff);
//			System.out.println("Null chunk");
			currentChunk = null;
		}
		
//		objects = new LinkedList<Cube>();
		
		//Checks if needs to load current chunk player is in or if it has already been put in rendering array
		if(currentChunk != null){
			loadChunk = true;
			for (Integer [] info:renderedChunks){
				if (info != null && info.length >3){
					if (info[0]!=null && info[1]!=null){
						if (info[0] == chunkX+ix && info[1] == chunkZ+iz){
							loadChunk = false;
							//System.out.println(chunkI);
						}
					}
				}
			}
		}else{
			loadChunk = false;
		}
		//Loads chunks from world making them into cube objects to be rendered and ticked
		i = 0;
		if (loadChunk && currentChunk != null && !currentChunk.isEmpty()){
			
			if (cubeGen.createChunk(currentChunk,chunkX+ix,chunkZ+iz)){	
				Integer[] tempArray = {chunkX+ix,chunkZ+iz,osize,chunkSize};
				renderedChunks.add(chunkI,tempArray);
				chunkI++;
			}
			
//			for (Integer[] data:loadedChunks){
//				if (data[0] != null){
//					System.out.println(data[0]+" "+data[1]);
//				}
//			}
			//System.out.println((chunks.get(chunkX+xOff).get(chunkZ+zOff) == currentChunk)+" X Off "+xOff+" Z Off "+zOff);
			
		}
		ix++;
		if (ix == 3){
			ix = -2;
			iz++;
		}if (iz == 3){
			iz = -2;
		}
//		if (loadChunk){
//			objectsSorted = (LinkedList<Chunk>) renderChunks.clone();
//		}
		renderChunks.sort(new chunkCompare());
		
//		for (Cube object: objectsSorted){
//			if (object != null){
//				object.setDebug(debug);
//				object.render(g);
//			}
//		}
		for (Chunk chunk:renderChunks){
			chunk.render(g);
		}renderChunks = (LinkedList<Chunk>) renderChunks2.clone();
		players[0].render(g);
		if (debug){
			g.drawString("Single Cubes:"+sCubeCount,10,70);
			g.drawString("Combined Cubes:"+cCubeCount,10,80);
		}
	}
	public void tick(){
//		if (!debug){
//			gen.tick();
//		}
//		for (Cube object: objects){
//			if (object != null){
//				object.tick();
//			}
//		}
		for (Chunk chunk:renderChunks){
			chunk.tick();
		}
		players[0].tick();
	}
	
}

//Used for Cube sorting
class chunkCompare implements Comparator<Chunk>{
	@Override
	public int compare(Chunk c1, Chunk c2){
		if (c1.getDist() < c2.getDist()){
			return 1;
		}if (c1.getDist() > c2.getDist()){
			return -1;
		}return 0;
	}
}