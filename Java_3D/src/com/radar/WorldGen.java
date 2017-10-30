package com.radar;

import java.util.LinkedList;

public class WorldGen {
	int i,index,all,chunkX,chunkY,chunkZ = 0;
	double x,y,z;
	Handler handler;
	Player thePlayer;
	Cube[] layer = new Cube[256];
	public Cube[] subchunk = new Cube[64];
	public LinkedList<LinkedList<LinkedList<Integer>>> world = new LinkedList<LinkedList<LinkedList<Integer>>>();
	LinkedList<LinkedList<Integer>> chunk = new LinkedList<LinkedList<Integer>>();
	LinkedList<Integer> tempList = new LinkedList<Integer>();
	
	WorldGen(Handler handler, Player thePlayer){
		this.handler = handler;
		this.thePlayer = thePlayer;
		
		world.add(new LinkedList<LinkedList<Integer>>());
		world.get(0).add(new LinkedList<Integer>());
		i = 0;
		index = 0;
		while (index < 16){
			if (i == 16){
				i = 0;
				index++;
			}
			world.get(0).get(0).add(1);
			i++;
		}
		tempList = world.get(0).get(0);
		
	}
	public void tick(){
		x = thePlayer.getX();
		y = thePlayer.getY();
		z = thePlayer.getZ();
		chunkX = (int) Math.floor(x/16);
		chunkY = (int) y;
		chunkZ = (int) Math.floor(z/16);
		try{
			while (world.size() <= chunkX+5){
				world.add(null);
				System.out.println("Adding to X... "+chunkX);
			}while (world.get(chunkX).size() <= chunkZ+5){
				world.get(chunkX).add(null);
				System.out.println("Adding to Z... "+chunkZ);
			}
			//System.out.println("X axis"+world.get(chunkX).toString());
			//System.out.println("Z axis"+world.get(chunkX).get(chunkZ).toString());
			
			//System.out.println("Z axis"+world.get(chunkX).get(chunkZ) == null);
			if (world.get(chunkX).get(chunkZ)==null){
				//world.set(chunkX, new LinkedList<LinkedList<Cube>>());
				//world.get(chunkX).set(chunkZ, (LinkedList<Cube>) tempList.clone());
				world.get(chunkX).set(chunkZ,new LinkedList<Integer>());
				i = 0;
				index = 0;
				while (index < 16){
					if (i == 16){
						i = 0;
						index++;
					}
					world.get(chunkX).get(chunkZ).add(1);
					i++;
					System.out.println("Generating... "+i);
				}
				//world.get(chunkX).get(chunkY).add()
			}
		}catch(Exception e){
			//e.printStackTrace();
			//System.out.println(e.getCause());
		}
		
	}public LinkedList<LinkedList<LinkedList<Integer>>> getWorld(){
		return world;
	}
}
