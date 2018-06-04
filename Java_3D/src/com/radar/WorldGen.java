package com.radar;

import java.util.LinkedList;

public class WorldGen{
	int i,index,all,chunkX,chunkY,chunkZ,xOff,zOff,ix,iz,h,tx,ty,ty2,tz = 0;
	boolean debug = false;
	double x,y,z;
	Handler handler;
	Player thePlayer;
	public LinkedList<LinkedList<LinkedList<Integer>>> world = new LinkedList<LinkedList<LinkedList<Integer>>>();
	LinkedList<LinkedList<Integer>> chunk = new LinkedList<LinkedList<Integer>>();
	
	public WorldGen(Handler handler, Player thePlayer){
		this.handler = handler;
		this.thePlayer = thePlayer;
		world.add(new LinkedList<LinkedList<Integer>>());
		world.get(0).add(new LinkedList<Integer>());
//		world.get(0).get(0).add(1);
		i = 0;
	}
	public void tick(){
//		while (!debug){
			x = thePlayer.getX();
			y = thePlayer.getY();
			z = thePlayer.getZ();
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
					}
					while (world.get(chunkX+xOff+ix).size() <= chunkZ+zOff+5+iz){
						world.get(chunkX+xOff+ix).add(new LinkedList<Integer>());
					}
					if (world.get(chunkX+xOff+ix).get(chunkZ+zOff+iz).isEmpty()){
						//Return to 0
						h = -9;
						i = 0;
						tx = 0;
						tz = 0;
						//TODO Fix to revive world gen
						//Normally 60
						while (h < 15){
//						while (h < 0){
							while (i < 256){
//								ty  = (int) Math.round(Math.sqrt(5/(Math.pow(((16-tx)+(16*(chunkX+ix)))*0.1,2)*Math.pow(((16-tz)+(16*(chunkZ+iz)))*0.1, 2))));
								ty = 0;
								ty2 = 0;
//								Rotate ty = (int) Math.round(0.4*(Math.pow(((16-tx)+(16*(chunkX+ix)))*0.2, 2)+Math.pow(((tz)+(16*(chunkZ+iz)))*0.2, 2))); 90 degrees
//								ty = (int) Math.round(0.4*(Math.pow(((tz)+(16*(chunkX+ix)))*0.2, 2)+Math.pow(((tx)+(16*(chunkZ+iz)))*0.2, 2)));
//								ty = (int) Math.round(Math.pow((0.16-Math.pow((0.6-Math.pow((Math.pow(((tz)+(16*(chunkX+ix)))*0.04,2)+Math.pow(((tx)+(16*(chunkZ+iz)))*0.04,2)),0.5)),2)),0.5)*20);
								//TODO
//								ty = (int) ((Math.sin(((tz)+(16*(chunkX+ix)))*0.2)*Math.cos(((tx)+(16*(chunkZ+iz)))*0.2))*5.0)+10;
//								if ((((tz)+(16*(chunkX+ix))) + 3) != 0){
//									ty = (int) ( (((tx)+(16*(chunkZ+iz))) + 2) / (((tz)+(16*(chunkX+ix))) + 3) );
//								}
								ty = (int) Math.sqrt(Math.pow((tx)+(16*(chunkZ+iz)),2) + Math.pow((tz)+(16*(chunkX+ix)),2));
//								if (h!=0 && ty == h){
								if ( ty == (int) (4.0*((h*0.15) * Math.sin((h*0.15))))+4){
									world.get(chunkX+xOff+ix).get(chunkZ+zOff+iz).add(1);
								}else{
									world.get(chunkX+xOff+ix).get(chunkZ+zOff+iz).add(0);
								}
								i++;
								tx++;
								if (tx > 15){
									tx = 0;
									tz++;
								}
							}
							h++;
							i = 0;
							tz = 0;
							tx = 0;
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
//		}
	}public LinkedList<LinkedList<LinkedList<Integer>>> getWorld(){
		return world;
	}public int getXOff(){
		return xOff;
	}public int getZOff(){
		return zOff;
	}public void printWorld(){
		System.out.println(world);
	}public void setDebug(boolean debug){
		this.debug = debug;
	}public void addBlock(int blockNum, int chunkX, int chunkZ){
		world.get(chunkX+xOff).get(chunkZ+zOff).add(blockNum);
		
	}
}
