package com.radar;

import java.util.LinkedList;

public class WorldGen {
	int i = 0;
	public Cube[] subchunk = new Cube[64];
	LinkedList<LinkedList<Cube[]>> chunks = new LinkedList<LinkedList<Cube[]>>();
	LinkedList<Cube[]> tempList = new LinkedList<Cube[]>();
	
	WorldGen(Handler handler){
		while (i < 31){
			tempList.add(new Cube[32]);
			chunks.add(tempList);
			chunks.get(0).get(0)[i] = new Cube(i,0,0,1,1,1,handler,i);
			i++;
		}
	}
	public void tick(){
		
	}public LinkedList<LinkedList<Cube[]>> getWorld(){
		return chunks;
	}
}
