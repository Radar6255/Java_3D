package com.radar;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Comparator;
import java.util.LinkedList;

//This class is created when a chunk is loaded
//It is given to each cube created in the chunk
//The cubes then add themselves to the chunk list and are "rendered" and ticked
//When cubes are rendered they pass in the BlockFaces to sort and render

//These chunks are sorted in the handler using the getDist class

public class Chunk {
	LinkedList<Cube> chunk = new LinkedList<Cube>();
	LinkedList<BlockFace> facesToRender = new LinkedList<BlockFace>();
	public int chunkX, chunkZ, xOff, zOff;
	public boolean debug = false;
	public double dist;
	Handler handler;
	Player player;
	public Chunk(int chunkX, int chunkZ, Handler handler, Player player){
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		this.handler = handler;
		this.player = player;
	}
	
	public void tick(){
		for (Cube object: chunk){
			object.tick();
		}
	}
	
	public void render(Graphics g){
		for (Cube object:chunk){
			object.render(g);
		}
		facesToRender.sort(new sortFaces());
		for (BlockFace face:facesToRender){
			if (face != null){
				if (face.getVisible()) {
					g.setColor(face.getColor());
				// Face polygon
					g.fillPolygon(face.getXCoords(), face.getYCoords(), 4);
					if (debug){
						g.setColor(Color.BLACK);
						g.drawString(""+face.getCubeIndex(),face.getXCoords()[0],face.getYCoords()[0]);
					}
//					g.draw(at.createTransformedShape(g)); // Draw the transformed shape
//					PerspectiveTransform test = new PerspectiveTransform();
//					g.drawImage(img.getImage(), Main.WIDTH/2, Main.WIDTH/2, null);
				}
				//i++;
			}
		}
		facesToRender = new LinkedList<BlockFace>();
	}
	
	public void addFace(BlockFace face){
		facesToRender.add(face);
	}public void addCube(Cube cube){
		chunk.add(cube);
	}public double getDist(){
		dist = Math.sqrt(Math.pow((16*chunkX)-player.getX()+8, 2)+Math.pow((16*chunkZ)-player.getZ()+8, 2));
		return dist;
	}
}class sortFaces implements Comparator<BlockFace>{

	public int compare(BlockFace o1, BlockFace o2) {
		//return Double.compare(o1.getDist(), o2.getDist());
//		if (o1 == null || o2 == null){
//			return 0;
//		}
		if (o1.getDist() < o2.getDist()){
			return 1;
		}if (o1.getDist() > o2.getDist()){
			return -1;
		}return 0;
	}
	
}