package com.radar;

import java.awt.Graphics;

public class Handler {
	Cube[] objects = new Cube[30];
	Player[] players = new Player[2];
	Cube tempCube;
	boolean looping,changed;
	int ci,pi,i = 0;
	
	public void addPlayer(Player player){
		players[pi] = player;
		pi++;
	}
	
	public void addCube(Cube object){
		objects[ci] = object;
		ci++;
	}
	public void render(Graphics g){
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
			//System.out.println(changed);
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
