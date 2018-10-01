package com.radar;

import java.awt.Graphics;
import java.util.Comparator;
import java.util.LinkedList;

//Handler class makes worldGen into a reality
//Gets info from world then creates chunks and cubes from them
//Uses created chunks sorted to render all BlockFaces
public class Handler {
	
	LinkedList<CubeObject> objects = new LinkedList<CubeObject>();
	LinkedList<Chunk> renderChunks = new LinkedList<Chunk>();
	LinkedList<Chunk> renderQueue = new LinkedList<Chunk>();
	LinkedList<Chunk> objectsSorted = new LinkedList<Chunk>();
	LinkedList<Integer> currentChunk = new LinkedList<Integer>();
	public RenderThread renderThread1;
//	public RenderThread renderThread2;
	public CubeGen cubeGen;
	public PolygonRaster raster;
	public Handler(){
		raster = new PolygonRaster();
		cubeGen = new CubeGen(players, this);
		cubeGen.start();
		renderThread1 = new RenderThread("1");
//		renderThread2 = new RenderThread("2");
		renderThread1.setName("Render1");
//		renderThread2.setName("Render2");
		renderThread1.start();
//		renderThread2.start();
	}
	
	//Holds indices of chunks loaded into render and where they start in the objects linkedList
	LinkedList<Integer[]> renderedChunks = new LinkedList<Integer[]>();
	int x,y,z, osize,xOff,zOff,chunkX,chunkZ,width,ix,iz,sCubeCount,chunkSize,cCubeCount,tx;
	Player[] players = new Player[2];
	WorldGen gen;
	boolean looping,changed,out,loadChunk,debug;
	Chunk chunkCreating;
	LinkedList<LinkedList<LinkedList<Integer>>> chunks = new LinkedList<LinkedList<LinkedList<Integer>>>();
	int ci,pi,i,i2,chunkI = 0;
	
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
	}public PolygonRaster getRaster(){
		return raster;
	}
	public void debugMode(boolean debug){
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
		renderQueue.add(chunk);
	}
	public void render(Graphics g){
		
		chunks = gen.getWorld();
		xOff = gen.getXOff();
		zOff = gen.getZOff();
		chunkX = players[0].getChunkX();
		chunkZ = players[0].getChunkZ();
		try{
			currentChunk = chunks.get(chunkX+xOff+ix).get(chunkZ+zOff+iz);
		}catch(Exception e){
			currentChunk = null;
		}
		
		//Checks if needs to load current chunk player is in or if it has already been put in rendering array
		if(currentChunk != null){
			loadChunk = true;
			for (Integer [] info:renderedChunks){
				if (info != null && info.length >3){
					if (info[0]!=null && info[1]!=null){
						if (info[0] == chunkX+ix && info[1] == chunkZ+iz){
							loadChunk = false;
						}
					}
				}
			}
		}else{
			loadChunk = false;
		}
		//Loads chunks from world making them into cube objects to be rendered and ticked
		if (loadChunk && currentChunk != null && !currentChunk.isEmpty()){
			cubeGen.createChunk(currentChunk,chunkX+ix,chunkZ+iz);
			synchronized(this) {
				this.notify();
			}
			Integer[] tempArray = {chunkX+ix,chunkZ+iz,osize,chunkSize};
			renderedChunks.add(chunkI,tempArray);
			chunkI++;
			
		}
		ix++;
		if (ix == 3){
			ix = -2;
			iz++;
		}if (iz == 3){
			iz = -2;
		}
		renderChunks.sort(new chunkCompare());

		for (Chunk chunk:renderChunks){
			chunk.render(g, renderThread1);
		}

		//TODO The has errored once
		for (Chunk toAdd:renderQueue){
			renderChunks.add(toAdd);
		}renderQueue.clear();
//		raster.render(g);
		
//		for (Chunk chunk:renderChunks){
//			chunk.render(g);
//		}
		
		i = 0;
		while (i < renderChunks.size()){
			//TODO Tie this with render distance setting
			if (renderChunks.get(i).getDist() > 80){
				i2 = 0;
				for (Integer[] data: renderedChunks){
					if (data[0] == renderChunks.get(i).getChunkX() && data[1] == renderChunks.get(i).getChunkZ()){
						chunkI--;
						renderedChunks.remove(i2);
						break;
					}
					i2++;
				}
				renderChunks.remove(i);
			}
			i++;
		}
		
		players[0].render(g);
		if (debug){
			g.drawString("Single Cubes:"+sCubeCount,10,70);
			g.drawString("Combined Cubes:"+cCubeCount,10,80);
		}
	}
	
	public void tick(){
		gen.tick();
//		for (Chunk chunk:renderChunks){
//			chunk.tick();
//		}
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