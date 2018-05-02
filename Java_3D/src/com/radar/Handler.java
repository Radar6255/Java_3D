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
	LinkedList<Chunk> objectsSorted = new LinkedList<Chunk>();
	LinkedList<Integer> currentChunk = new LinkedList<Integer>();
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
		renderChunks.add(chunk);
	}
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
			chunkCreating = new Chunk(chunkX+ix, chunkZ+iz, this, players[0]);
//			osize = objects.size()-1;
			osize = chunkSize;
			while(i < currentChunk.size()){
				if (currentChunk.get(i) == 1){
//					width = 1;
////					objects.add( new CombinedCube( (int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) + 16*(chunkX+ix), (int) Math.floor(i/256.0), (int) (((i/16.0)-Math.floor(i/16.0))*16.0) + 16*(chunkZ+iz),1,1,width,this,i,chunkX+ix,chunkZ+iz,chunkCreating ) );
////					System.out.println((int) (((i/16.0)-Math.floor(i/16.0))*16.0));
//					try{
//						if (currentChunk.get(i) == 1 && currentChunk.get(i+1) == 1){
//							tx = i;
//						}
//						while (currentChunk.get(i) == 1 && currentChunk.get(i+1) == 1){
//							//System.out.println(width);
//							//if ((int) ((((i+1)-256*Math.floor((i+1)/(double) 256))/16) + 16*(chunkX+ix)+1) != (int) (((i-256*Math.floor(i/(double) 256))/16) + 16*(chunkX+ix)+1)){
//							if ((int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) != (int) Math.floor((((i+1)/256.0)-Math.floor((i+1)/256.0))*16.0)){
//								break;
//							}
//							width++;
//							i++;
////							System.out.println("Width:"+width+" i:"+i);
//						}
//					}catch(Exception e){}
//					
//					//new Cube((int) (((i-256*Math.floor(i/(double) 256))/16) + 16*(chunkX+ix)+1),(int) Math.floor(i/(double) 256),(int) (((i-256*Math.floor(i/(double) 256))%16)+16*(chunkZ+iz)+1),1,1,width,this,i,chunkX+ix,chunkZ+iz,chunkCreating);
//					if (width == 1){
//						sCubeCount++;
//						//objects.add(new Cube((int) (((i-256*Math.floor(i/(double) 256))/16) + 16*(chunkX+ix)+1),(int) Math.floor(i/(double) 256),(int) (((i-256*Math.floor(i/(double) 256))%16)+16*(chunkZ+iz)+1),1,1,width,this,i,chunkX+ix,chunkZ+iz,chunkCreating));
////						objects.add(new CombinedCube((int) (((i-256*Math.floor(i/(double) 256))/16) + 16*(chunkX+ix)),(int) Math.floor(i/(double) 256),(int) (((i-256*Math.floor(i/(double) 256))%16)+16*(chunkZ+iz)),1,1,width,this,i,chunkX+ix,chunkZ+iz,chunkCreating));
//						objects.add( new CombinedCube( (int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) + 16*(chunkX+ix), (int) Math.floor(i/256.0), (int) (((i/16.0)-Math.floor(i/16.0))*16.0) + 16*(chunkZ+iz),1,1,-width+2,this,i,chunkX+ix,chunkZ+iz,chunkCreating ) );
//					}else{
//						cCubeCount++;
//						//System.out.println((int) Math.floor(((tx/256.0)-Math.floor(tx/256.0))*16.0));
//						objects.add( new CombinedCube( (int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) + 16*(chunkX+ix), (int) Math.floor(i/256.0), (int) (((i/16.0)-Math.floor(i/16.0))*16.0) + 16*(chunkZ+iz),1,1,-width+2,this,i,chunkX+ix,chunkZ+iz,chunkCreating ) );
//						if ((int) Math.floor(i/256.0) == 3){
////							System.out.println(i);
//						}
//						//objects.add(new CombinedCube((int) (((i-256*Math.floor(i/(double) 256))/16) + 16*(chunkX+ix)+1),(int) Math.floor(i/(double) 256),(int) (((i-256*Math.floor(i/(double) 256))%16)+16*(chunkZ+iz)+1),-width,1,1,this,i,chunkX+ix,chunkZ+iz,chunkCreating));
//						//objects.add(new CombinedCube((int) (((tx-256*Math.floor(tx/256.0))/16) + 16*(chunkX+ix)),(int) Math.floor(tx/(double) 256),(int) (((tx-256*Math.floor(tx/256.0))%16)+16*(chunkZ+iz)),1,1,width+1,this,tx,chunkX+ix,chunkZ+iz,chunkCreating));
//						//System.out.println("Combined");
//					}
					objects.add( new CombinedCube( (int) Math.floor(((i/256.0)-Math.floor(i/256.0))*16.0) + 16*(chunkX+ix), (int) Math.floor(i/256.0), (int) (((i/16.0)-Math.floor(i/16.0))*16.0) + 16*(chunkZ+iz),1,1, 1 ,this,i,chunkX+ix,chunkZ+iz,chunkCreating ) );
					chunkSize++;
					//System.out.println((((i-256*Math.floor(i/(double) 256))%16)+16*chunkZ+1)+" "+i);
				}
				i++;
			}
			renderChunks.add(chunkCreating);
			//Integer[] tempArray = {chunkX+ix,chunkZ+iz,osize,objects.size()-1};
			Integer[] tempArray = {chunkX+ix,chunkZ+iz,osize,chunkSize};
			renderedChunks.add(chunkI,tempArray);
//			for (Integer[] data:loadedChunks){
//				if (data[0] != null){
//					System.out.println(data[0]+" "+data[1]);
//				}
//			}
			//System.out.println((chunks.get(chunkX+xOff).get(chunkZ+zOff) == currentChunk)+" X Off "+xOff+" Z Off "+zOff);
			chunkI++;
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
		}
		players[0].render(g);
		if (debug){
			g.drawString("Single Cubes:"+sCubeCount,10,70);
			g.drawString("Combined Cubes:"+cCubeCount,10,80);
		}
	}
	public void tick(){
		//TODO Make conversion from file to render better... Cubes extend in wrong direction
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