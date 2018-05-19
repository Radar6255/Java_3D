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
	public static float[] verts = { 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f , -0.5f, -0.5f, 0.5f , -0.5f, 0.5f, 0.5f , 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f , -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f };
	
	LinkedList<Integer> blockPos = new LinkedList<Integer>();
	LinkedList<CubeObject> blocks = new LinkedList<CubeObject>();
	LinkedList<CubeObject> visibleBlocks = new LinkedList<CubeObject>();
	LinkedList<Cube> combinedBlocks = new LinkedList<Cube>();
	LinkedList<BlockFace> facesToRender = new LinkedList<BlockFace>();
	public int chunkX, chunkZ, xOff, zOff,off;
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
	double[] out = new double[24];
	public double[] allVerts;
	double px, py, pz, rotLat, rotVert, lowerBound,upperBound, bound = 0;
	public double sl,cl,sv,cv;
	boolean renderChunk = false;
	boolean first = true;
	float[] playerCoords = new float[3];
	public void render(Graphics g){
//		System.out.println(verts.length);
		if (first){
			@SuppressWarnings("unused")
			float[] allVerts = new float[visibleBlocks.size()*8*3];
			first = false;
		}
//		renderChunk = false;
//		//TODO Push the V backwards from players perspective, need to use sin + cos to find where the bottom of the v starts
//		lowerBound = (360 - rotLat) - 30;
//		upperBound = (360 - rotLat) + 30;
//		
//		if ((chunkX-(px/16)) == 0){
//			bound = Math.toDegrees(Math.atan(chunkZ-(pz/16)));
//		}else{
//			bound = Math.toDegrees(Math.atan((chunkZ-(pz/16))/(chunkX-(px/16))));
//		}
//		
//		//2nd Quad
//		if((chunkX-(px/16)) < 0 && (chunkZ-(pz/16)) > 0){
//			bound = 180 + bound;
//		}//3rd Quad
//		else if((chunkX-(px/16)) < 0 && (chunkZ-(pz/16)) < 0){
//			bound = 180 + bound;
//		}//4th  Quad
//		else if((chunkX-(px/16)) > 0 && (chunkZ-(pz/16)) < 0){
//			bound = 360 + bound;
//		}
//		
//		bound -= 90.5;
//		
//		if ((bound > lowerBound && bound < upperBound) || (bound > lowerBound+360 && bound < upperBound+360) || (bound > lowerBound-360 && bound < upperBound-360)){
//			renderChunk = true;
//		}
		if (!renderChunk){
//			blocks.sort(new blockSort());
			
			playerCoords[0] = (float) player.getX();
			playerCoords[1] = (float) player.getY();
			playerCoords[2] = (float) player.getZ();
			
			rotLat = player.getRotLat();
			rotVert = player.getRotVert();
			sl = player.getSineLat();
			cl = player.getCosineLat();
			sv = player.getSineVert();
			cv = player.getCosineVert();

//			allVerts = DoubleStream.iterate(0, i -> i + 1).parallel().limit(visibleBlocks.size()*8*3).map(i->verts[(int) Math.round(i%24)] + blockPos.get((int) ((i%3) + 3*(Math.floor(i/24))))-playerCoords[(int) (i%3)] ).toArray();
			for (CubeObject object:visibleBlocks){
				object.render(g,playerCoords[0],playerCoords[1],playerCoords[2],rotLat,rotVert,sl,cl,sv,cv);
			}
			facesToRender.sort(new sortFaces());
			for (BlockFace face:facesToRender){
				if (face != null){
					g.setColor(face.getColor());
					// Face polygon
					g.fillPolygon(face.getXCoords(), face.getYCoords(), 4);
					i++;
					if (debug){
						g.setColor(Color.BLACK);
						g.drawString(""+face.getDist(),face.getXCoords()[1],face.getYCoords()[1]);
						
	//					g.drawString(""+face.getX(),face.getXCoords()[0],face.getYCoords()[0]);
					}
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
		}
	}public int getChunkX(){
		return chunkX;
	}public int getChunkZ(){
		return chunkZ;
	}
	public void addFace(BlockFace face){
		facesToRender.add(face);
	}public void addCube(CubeObject cube,int x, int y, int z){
		blocks.add(cube);
		if (cube.isVisible()){
			visibleBlocks.add(cube);
		}
		blockPos.add(x);
		blockPos.add(y);
		blockPos.add(z);
	}
	public double getDist(){
		dist = Math.sqrt(Math.pow((16*chunkX)-player.getX()+8, 2)+Math.pow((16*chunkZ)-player.getZ()+8, 2));
		return dist;
	}public void setDebug(boolean debug){
		this.debug = debug;
		for (CubeObject cube:blocks){
			cube.setDebug(debug);
		}for (Cube cube:combinedBlocks){
			cube.setDebug(debug);
		}
	}
}

class blockSort implements Comparator<CubeObject>{

	public int compare(CubeObject o1, CubeObject o2) {
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