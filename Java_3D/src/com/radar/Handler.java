package com.radar;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;

//Handler class makes worldGen into a reality
//Gets info from world then creates chunks and cubes from them
//Uses created chunks sorted to render all BlockFaces
public class Handler {
	ArrayList<BlockFace> facesToRender = new ArrayList<BlockFace>();
	ArrayList<BlockFace> facesToRender2 = new ArrayList<BlockFace>();
	LinkedList<Chunk> renderChunks = new LinkedList<Chunk>();
	volatile LinkedList<Chunk> renderQueue = new LinkedList<Chunk>();
	LinkedList<Chunk> objectsSorted = new LinkedList<Chunk>();
	ArrayList<Integer> currentChunk = new ArrayList<Integer>();
	LinkedList<CubeObject[]> visibleBlocks = new LinkedList<CubeObject[]>();
	volatile LinkedList<CubeObject[]> visibleBlocksTemp = new LinkedList<CubeObject[]>();
	public LinkedList<Integer[]> blockPos = new LinkedList<Integer[]>();
	volatile LinkedList<Integer[]> blockPosTemp = new LinkedList<Integer[]>();
	
	public static RenderThread renderThread1;
	GpuHandler gpuHandler;
	public CubeGen cubeGen;
	public Main main;
	
	public Handler(Main main){
		gpuHandler = new GpuHandler();
		cubeGen = new CubeGen(players, this, gpuHandler);
		cubeGen.start();
		if (SettingVars.multiThread) {
			renderThread1 = new RenderThread("1");
			renderThread1.setName("Render1");
			renderThread1.start();
		}
		this.main = main;
	}
	
	//Holds indices of chunks loaded into render and where they start in the objects linkedList
	LinkedList<Integer[]> renderedChunks = new LinkedList<Integer[]>();
	LinkedList<Integer> chunkSizes = new LinkedList<Integer>();
	
	int x,y,z, osize,xOff,zOff,chunkX,chunkZ,width,ix,iz,sCubeCount,chunkSize,cCubeCount;
	volatile int blockPosStart,visibleBlockStart = 0;
	volatile boolean renderWait = true;
	Player[] players = new Player[2];
	WorldGen gen;
	boolean looping,changed,out,loadChunk,debug;
	Chunk chunkCreating;
	ArrayList<ArrayList<ArrayList<Integer>>> chunks = new ArrayList<ArrayList<ArrayList<Integer>>>();
	int ci,pi,i,i2,chunkI = 0;
	public int getHeight() {
		return main.getHeight();
	}public int getWidth() {
		return main.getWidth();
	}public void fovChange() {
		for (Chunk chunk : renderChunks) {
			chunk.fovUpdate();
		}
	}
	public void addGeneration(WorldGen gen){
		this.gen = gen;
	}public ArrayList<ArrayList<ArrayList<Integer>>> getWorld(){
		return gen.getWorld();
	}public int getXOff(){
		return gen.getXOff();
	}public int getZOff(){
		return gen.getZOff();
	}public void addPlayer(Player player){
		players[pi] = player;
		pi++;
	}
	public void debugMode(boolean debug){
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
		renderChunks.clear();
		renderedChunks = null;
		renderedChunks = new LinkedList<Integer[]>();
		chunkI = 0;
	}public void addChunk(Chunk chunk){
		renderQueue.add(chunk);
		blockPosTemp.add(chunk.getBlockPos().toArray(new Integer[chunk.getBlockPos().size()]));
		visibleBlocksTemp.add(chunk.getVisibleBlocks().toArray(new CubeObject[chunk.getVisibleBlocks().size()]));
	}public void placeBlock() {
		for (Chunk chunk:renderChunks) {
			chunk.placeBlock();
		}
	}
	public void render(Graphics g){
		
		chunks = gen.getWorld();
		xOff = gen.getXOff();
		zOff = gen.getZOff();
		chunkX = players[0].getChunkX();
		chunkZ = players[0].getChunkZ();
		try{
			currentChunk = chunks.get(chunkX+xOff+ix).get(chunkZ+zOff+iz);
		}catch(Exception e){
			currentChunk = null;
		}
		
		//Checks if needs to load current chunk player is in or if it has already been put in rendering array
		if(currentChunk != null){
			loadChunk = true;
			for (Integer [] info:renderedChunks){
				if (info != null && info.length >3){
					if (info[0]!=null && info[1]!=null){
						if (info[0] == chunkX+ix && info[1] == chunkZ+iz){
							loadChunk = false;
						}
					}
				}
			}
		}else{
			loadChunk = false;
		}
		//Loads chunks from world making them into cube objects to be rendered and ticked
		if (loadChunk && !currentChunk.isEmpty()){
			cubeGen.createChunk(currentChunk,chunkX+ix,chunkZ+iz);
			synchronized(this) {
				this.notify();
			}
			Integer[] tempArray = {chunkX+ix,chunkZ+iz,osize,chunkSize};
			renderedChunks.add(chunkI,tempArray);
			chunkI++;
		}
		ix++;
		if (ix == SettingVars.viewDist+1){
			ix = -SettingVars.viewDist;
			iz++;
		}if (iz == SettingVars.viewDist+1){
			iz = -SettingVars.viewDist;
		}

//		for (Chunk chunk:renderChunks){
//			chunk.render(g);
//		}
		cubeRender(g);
		
		renderChunks.addAll(renderQueue);
		renderQueue.clear();
//		Threw Error twice
		visibleBlocks.addAll(visibleBlocksTemp);
		visibleBlocksTemp.clear();
//		Threw Error twice
		blockPos.addAll(blockPosTemp);
		blockPosTemp.clear();

		
		i = 0;
		while (i < renderChunks.size()){
			if (renderChunks.get(i).getDist() > (16*SettingVars.viewDist)+32){
				i2 = 0;
				for (Integer[] data: renderedChunks){
					if (data[0] == renderChunks.get(i).getChunkX() && data[1] == renderChunks.get(i).getChunkZ()){
						chunkI--;
						renderedChunks.remove(i2);
						blockPos.remove(i2);
						visibleBlocks.remove(i2);
						break;
					}
					i2++;
				}
				renderChunks.remove(i);
			}i++;
		}
		
		players[0].render(g);
		if (debug){
			g.drawString("Single Cubes:"+sCubeCount,10,70);
			g.drawString("Combined Cubes:"+cCubeCount,10,80);
		}
	}
	
	public void tick(){
		gen.tick();
//		for (Chunk chunk:renderChunks){
//			chunk.tick();
//		}
		players[0].tick();
	}
	Player player;
	float[] playerCoords = new float[3];
	float[] relativePos;
	double rotLat, rotVert,sl,cl,sv,cv;
	ArrayList<CubeObject> temp = new ArrayList<CubeObject>();
	public void cubeRender(Graphics g) {
		player = players[0];
		playerCoords[0] = (float) player.getX();
		playerCoords[1] = (float) player.getY();
		playerCoords[2] = (float) player.getZ();
		
		rotLat = player.getRotLat();
		rotVert = player.getRotVert();
		sl = player.getSineLat();
		cl = player.getCosineLat();
		sv = player.getSineVert();
		cv = player.getCosineVert();
		
		if (SettingVars.gpu || SettingVars.multiThread) {
			temp.clear();
			for (CubeObject[] blocks:visibleBlocks) {
				for (CubeObject block: blocks) {
					temp.add(block);
				}
			}
		}
		
		
		if (SettingVars.gpu) {
			i = 0;
			float [] temp2 = new float[temp.size()*3];
			while (i+2 < temp2.length) {
				temp2[i] = playerCoords[0];
				temp2[i+1] = playerCoords[1];
				temp2[i+2] = playerCoords[2];
				i+=3;
			}
			
//			relativePos = calcBlockPos(playerCoords);
			relativePos = calcBlockPos(temp2);
		}else {
			relativePos = new float[linkArraySize(blockPos)];
			i = 0;
			for (Integer[] chunkData:blockPos) {
				for (Integer coord: chunkData) {
					if (i % 3 == 0) {
						relativePos[i] = coord-playerCoords[0];
					}else if (i % 3 == 1) {
						relativePos[i] = coord-playerCoords[1];
					}else {
						relativePos[i] = coord-playerCoords[2];
					}
					i++;
				}
			}
		}
		
		if (SettingVars.multiThread) {
			renderWait = true;
			renderThread1.render(this,temp,g,relativePos,rotLat,rotVert,sl,cl,sv,cv);
		
			i = 0;
			for (CubeObject block: temp) {
				block.render(relativePos[(i*3)],relativePos[(i*3)+1],relativePos[(i*3)+2],rotLat,rotVert,sl,cl,sv,cv);
				i++;
				if (i > temp.size()/2) {
					break;
				}
			}
			while (renderWait) {}
			facesToRender.addAll(facesToRender2);
		}else {
			i = 0;
			for (CubeObject[] blocks:visibleBlocks) {
				for (CubeObject block: blocks) {
					block.render(relativePos[(i*3)],relativePos[(i*3)+1],relativePos[(i*3)+2],rotLat,rotVert,sl,cl,sv,cv);
					i++;
				}
			}
		}
		
		
		facesToRender.sort(new sortFaces());
		for (BlockFace face:facesToRender){
			if (face != null){
				g.setColor(face.getColor());
				g.fillPolygon(face.getXCoords(), face.getYCoords(), 4);
				if (debug){
					g.setColor(Color.BLACK);
					g.drawString(""+face.getCubeIndex(),face.getXCoords()[0],face.getYCoords()[0]);
				}
			}
		}
		facesToRender.clear();
		facesToRender2.clear();
	}
	
	public void addFace(BlockFace tempFace) {
		if(Thread.currentThread().getName() == "Render1") {
			facesToRender2.add(tempFace);
		}else {
			facesToRender.add(tempFace);
		}
	}
	public int linkArraySize(LinkedList<Integer[]> data) {
		i = 0;
		for (Integer[] chunkData:data) {
			i+=chunkData.length;
		}return i;
	}
	
	public float[] calcBlockPos(float[] srcArrayA) {
		int s = linkArraySize(blockPos);
		if (s == 0) {
			return null;
		}
		float[] srcArrayB = new float[s];
		int i = 0;
		for (Integer[] chunkData: blockPos) {
			for (Integer coord: chunkData) {
				srcArrayB[i] = coord;
				i++;
			}
		}
		return gpuHandler.findBlockPos(srcArrayA, srcArrayB, s);
	}
	public void moveOn() {
		renderWait = false;
	}
	public static void startMulti() {
		renderThread1 = new RenderThread("1");
		renderThread1.setName("Render1");
		renderThread1.start();
	}
	public static void stopMulti() {
		renderThread1.running = false;
		try {
			renderThread1.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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