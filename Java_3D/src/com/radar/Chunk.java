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
	LinkedList<CubeObject> blocks = new LinkedList<CubeObject>();
	LinkedList<CombinedCube> combinedBlocks = new LinkedList<CombinedCube>();
	LinkedList<BlockFace> facesToRender = new LinkedList<BlockFace>();
	public int chunkX, chunkZ, xOff, zOff;
	public boolean debug = false;
	public boolean doubleRender = false;
	public double dist;
	Handler handler;
	Player player;
	int i = 0;
	public Chunk(int chunkX, int chunkZ, Player player){
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		this.player = player;
	}
	
	public void tick(){
		for (CubeObject object: blocks){
			object.tick();
		}
	}
	
	public void render(Graphics g){
		
//		blocks.sort(new blockSort());
		for (CubeObject object:blocks){
			object.render(g);
		}
		facesToRender.sort(new sortFaces());
		for (BlockFace face:facesToRender){
			if (face != null){
				g.setColor(face.getColor());
				// Face polygon
				g.fillPolygon(face.getXCoords(), face.getYCoords(), 4);
				i++;
				if (debug){
//					g.setColor(Color.BLACK);
//					g.drawString(""+face.getDist(),face.getXCoords()[1],face.getYCoords()[1]);
					
//					g.drawString(""+face.getX(),face.getXCoords()[0],face.getYCoords()[0]);
				}
//					g.draw(at.createTransformedShape(g)); // Draw the transformed shape
//					PerspectiveTransform test = new PerspectiveTransform();
//					g.drawImage(img.getImage(), Main.WIDTH/2, Main.WIDTH/2, null);
				i++;
			}
		}
		g.setColor(Color.BLACK);
		g.drawString("Faces Rendering:"+Integer.toString(i), 10, 60);
		i = 0;
		facesToRender = new LinkedList<BlockFace>();
		//TODO Double check if this could ever work
//		if (doubleRender){
//			for (CombinedCube object:combinedBlocks){
//				object.render(g);
//			}
//		}
	}public int getChunkX(){
		return chunkX;
	}public int getChunkZ(){
		return chunkZ;
	}
	public void addFace(BlockFace face){
		facesToRender.add(face);
	}public void addCube(CubeObject cube){
		blocks.add(cube);
	}public double getDist(){
		dist = Math.sqrt(Math.pow((16*chunkX)-player.getX()+8, 2)+Math.pow((16*chunkZ)-player.getZ()+8, 2));
		return dist;
	}public void setDebug(boolean debug){
		this.debug = debug;
		for (CubeObject cube:blocks){
			cube.setDebug(debug);
		}for (CombinedCube cube:combinedBlocks){
			cube.setDebug(debug);
		}
	}
}
class blockSort implements Comparator<CubeObject>{

	public int compare(CubeObject o1, CubeObject o2) {
		//return Double.compare(o1.getDist(), o2.getDist());
//		if (o1 == null || o2 == null){
//			return 0;
//		}
		if (o1.getDist() < o2.getDist()){
			return 1;
		}else if (o1.getDist() > o2.getDist()){
			return -1;
		}else{return 0;}
	}
	
}
class sortFaces implements Comparator<BlockFace>{

	public int compare(BlockFace o1, BlockFace o2) {
		if (o1.getDist() < o2.getDist()){
			return 1;
		}else if (o1.getDist() > o2.getDist()){
			return -1;
		}else{return 0;}
	}
	
}