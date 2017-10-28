package com.radar;

import java.awt.Graphics;
import java.util.LinkedList;

public class Handler {
	Cube[] objects;
	Player[] players = new Player[2];
	WorldGen gen;
	Cube tempCube;
	boolean looping,changed,out;
	LinkedList<LinkedList<Cube[]>> chunks = new LinkedList<LinkedList<Cube[]>>();
	int ci,pi,i = 0;
	
	public void addGeneration(WorldGen gen){
		this.gen = gen;
	}
	
	public void addPlayer(Player player){
		players[pi] = player;
		pi++;
	}
	
	public void addCube(Cube object){
		objects[ci] = object;
		ci++;
	}
	public void render(Graphics g){
		chunks = gen.getWorld();
		objects = chunks.get(0).get(0);
		looping = true;
		i = 0;
		while (looping){
			if (i >= objects.length){
				i = 0;
				if (!changed){
					looping = false;
				}
			}
			changed = false;
			if (objects[i] != null && objects[i+1] != null){
				if (objects[i].getDist() <= objects[i+1].getDist()){
					tempCube = objects[i];
					objects[i] = objects[i+1];
					objects[i+1] = tempCube;
					changed = true;
				}
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
