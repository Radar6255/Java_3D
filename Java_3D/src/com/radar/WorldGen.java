package com.radar;

import java.util.LinkedList;

public class WorldGen {
	int i,index,all,chunkX,chunkY,chunkZ,xOff,zOff,ix,iz = 0;
	boolean test = true;
	double x,y,z;
	Handler handler;
	Player thePlayer;
	Cube[] layer = new Cube[256];
	public Cube[] subchunk = new Cube[64];
	public LinkedList<LinkedList<LinkedList<Integer>>> world = new LinkedList<LinkedList<LinkedList<Integer>>>();
	LinkedList<LinkedList<Integer>> chunk = new LinkedList<LinkedList<Integer>>();
	
	public WorldGen(Handler handler, Player thePlayer){
		this.handler = handler;
		this.thePlayer = thePlayer;
		world.add(new LinkedList<LinkedList<Integer>>());
		world.get(0).add(new LinkedList<Integer>());
		i = 0;
		while (i < 257){
		//while (i < 2){
			world.get(0).get(0).add(1);
			i++;
		}
		//tempList = world.get(0).get(0);
		
	}
	public void tick(){
		x = thePlayer.getX();
		y = thePlayer.getY();
		z = thePlayer.getZ();
//		chunkX = (int) Math.floor(x/(double) 16);
//		chunkY = (int) y;
//		chunkZ = (int) Math.floor(z/(double) 16);
		chunkX = thePlayer.getChunkX();
		chunkY = thePlayer.getChunkY();
		chunkZ = thePlayer.getChunkZ();
		ix = -2;
		iz = -2;
		while(iz < 3){
			try{
				while((chunkX+xOff+ix) < 5){
					world.addFirst(new LinkedList<LinkedList<Integer>>());
					while(world.get(0).size() < zOff+ix){
						world.get(0).add(new LinkedList<Integer>());
					}
					xOff++;
				}
				while((chunkZ+zOff+iz) < 5){
					for (LinkedList<LinkedList<Integer>> zChunks:world){
						zChunks.addFirst(new LinkedList<Integer>());
					}
					zOff++;
				}
				
				while (world.size() <= chunkX+5+xOff+ix){
					world.add(new LinkedList<LinkedList<Integer>>());
					while(world.getLast().size() < zOff+iz){
						world.getLast().add(new LinkedList<Integer>());
					}
					//System.out.println("Adding to X... "+chunkX);
				}
				while (world.get(chunkX+xOff+ix).size() <= chunkZ+zOff+5+iz){
					world.get(chunkX+xOff+ix).add(new LinkedList<Integer>());
					//System.out.println("Adding to Z... "+chunkZ);
				}
				//System.out.println((chunkX+xOff)+" "+(chunkZ+zOff));
//				if (chunkX == 0 && chunkZ == 0 && test){
//					System.out.println(world.get(chunkX+xOff).get(chunkZ+zOff));
//					test = false;
//				}
				if (world.get(chunkX+xOff+ix).get(chunkZ+zOff+iz).isEmpty()){
					//world.get(chunkX+xOff).set(chunkZ+zOff,new LinkedList<Integer>());
					//System.out.println("Generating new chunk at X:"+chunkX+ix+" Z:"+chunkZ+iz);
					i = 0;
					while (i < 257){
						if (Math.round(Math.random()*(double) 2) == 1){
							world.get(chunkX+xOff+ix).get(chunkZ+zOff+iz).add(1);
						}else{
							world.get(chunkX+xOff+ix).get(chunkZ+zOff+iz).add(0);
						}
						i++;
//						System.out.println("Generating... "+i);
					}
				}
			}catch(Exception e){
				System.out.println("World gen error : ");
				e.printStackTrace();
				System.out.println(e.getCause());
			}
			ix++;
			if (ix == 3){
				iz++;
				ix = -2;
			}
		}
	}public LinkedList<LinkedList<LinkedList<Integer>>> getWorld(){
		return world;
	}public int getXOff(){
		return xOff;
	}public int getZOff(){
		return zOff;
	}public void printWorld(){
		System.out.println(world);
	}public void addBlock(int blockNum, int chunkX, int chunkZ){
		world.get(chunkX+xOff).get(chunkZ+zOff).add(blockNum);
		
	}
}
