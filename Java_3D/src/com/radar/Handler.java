package com.radar;

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
	
	LinkedList<float[]> modVerts = new LinkedList<float[]>();
	
	public LinkedList<Integer[]> blockPos = new LinkedList<Integer[]>();
	
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
	volatile boolean renderWait, chunkAddWait = true;
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
	}
	volatile float[] tempA;
	public void addChunk(Chunk chunk){
		while (chunkAddWait) {}
		renderQueue.add(chunk);
		
//		blockPosTemp.add(chunk.getBlockPos().toArray(new Integer[chunk.getBlockPos().size()]));
//		visibleBlocksTemp.add(chunk.getVisibleBlocks().toArray(new CubeObject[chunk.getVisibleBlocks().size()]));
		
//		tempA = new float[chunk.getModVerts().size()];
//		i = 0;
//		while (i < chunk.getModVerts().size()) {
//			tempA[i] = chunk.getModVerts().get(i);
//			i++;
//		}
//		modVertsTemp.add(tempA);
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
//		Threw error four times
		chunkAddWait = true;
		for (Chunk toAdd:renderQueue) {
			renderChunks.add(toAdd);
			blockPos.add(toAdd.getBlockPos().toArray(new Integer[toAdd.getBlockPos().size()]));
			visibleBlocks.add(toAdd.getVisibleBlocks().toArray(new CubeObject[toAdd.getVisibleBlocks().size()]));
			modVerts.add(toAdd.getModVerts());
		}renderQueue.clear();
		chunkAddWait = false;
//		renderChunks.addAll(renderQueue);
//		renderQueue.clear();
////		Threw Error twice
//		visibleBlocks.addAll(visibleBlocksTemp);
//		visibleBlocksTemp.clear();
////		Threw Error twice
//		blockPos.addAll(blockPosTemp);
//		blockPosTemp.clear();
//		modVerts.addAll(modVertsTemp);
//		modVertsTemp.clear();
		
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

		i = 0;
		while (i < renderChunks.size()){
//			Threw Error twice
			if (renderChunks.get(i).getDist() > (16*SettingVars.viewDist)+32){
				i2 = 0;
				for (Integer[] data: renderedChunks){
					if (data[0] == renderChunks.get(i).getChunkX() && data[1] == renderChunks.get(i).getChunkZ()){
						chunkI--;
						renderedChunks.remove(i2);
						blockPos.remove(i2);
						visibleBlocks.remove(i2);
						modVerts.remove(i2);
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
	volatile float[] relativePos;
	double rotLat, rotVert,sl,cl,sv,cv;
	float [][][] rotatedToRender;
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
			
			relativePos = calcBlockPos(temp2);
		}else {
			relativePos = new float[linkArraySize(blockPos)];
			int test = 0;
			for (Integer[] chunkData:blockPos) {
				for (Integer coord: chunkData) {
					if (test % 3 == 0) {
						relativePos[test] = coord-playerCoords[0];
					}else if (test % 3 == 1) {
						relativePos[test] = coord-playerCoords[1];
					}else {
						relativePos[test] = coord-playerCoords[2];
					}
					test++;
				}
			}
		}
		
		float [][] rotated = calcRotatedPoints(relativePos);
//		float [][] rotated = new float[1][1];
		
		
		if (rotated != null) {
//			System.out.println(rotated.length);
//			System.out.println(rotated[0].length);
			//TODO Check this
			rotatedToRender = new float[rotated[0].length/8][3][8];
			i = 0;
			float [] tempX = new float[8];
			float [] tempY = new float[8];
			float [] tempZ = new float[8];
			while (i < rotated[0].length) {
				tempX[i%8] = rotated[0][i];
				tempY[i%8] = rotated[1][i];
				tempZ[i%8] = rotated[2][i];
				if (i%8==7) {
					rotatedToRender[((i+1)/8)-1] = new float[][] {tempX,tempY,tempZ};
				}i++;
			}
		}

		
		if (SettingVars.multiThread) {
			renderWait = true;
			renderThread1.render(this,temp,g,relativePos,rotLat,rotVert,sl,cl,sv,cv);
		
			i = 0;
			for (CubeObject block: temp) {
//				block.render(relativePos[(i*3)],relativePos[(i*3)+1],relativePos[(i*3)+2],rotLat,rotVert,sl,cl,sv,cv);
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
//					block.render(relativePos[(i*3)],relativePos[(i*3)+1],relativePos[(i*3)+2],rotLat,rotVert,rotatedToRender[i][0],rotatedToRender[i][1],rotatedToRender[i][2]);
					block.render(playerCoords[0],playerCoords[1],playerCoords[2],rotLat,rotVert,rotatedToRender[i][0],rotatedToRender[i][1],rotatedToRender[i][2]);
					i++;
				}
			}
//			if (rotatedToRender != null)
//			System.out.println(rotatedToRender.length+" "+i);
		}
		
		
		
		facesToRender.sort(new sortFaces());
		for (BlockFace face:facesToRender){
			if (face != null){
				g.setColor(face.getColor());
				g.fillPolygon(face.getXCoords(), face.getYCoords(), 4);
//				if (debug){
//					g.setColor(Color.BLACK);
//					g.drawString(""+face.getCubeIndex(),face.getXCoords()[0],face.getYCoords()[0]);
//				}
			}
		}
		facesToRender.clear();
		facesToRender2.clear();
	}
	
	public float[][] calcRotatedPoints(float[] relativePos) {
		ArrayList<Float> xCoords = new ArrayList<Float>();
		ArrayList<Float> yCoords = new ArrayList<Float>();
		ArrayList<Float> zCoords = new ArrayList<Float>();
		i = 0;
		for (float[] coords:modVerts) {
			for (float coord: coords) {
				if (i%3==0) {
					xCoords.add(coord);
				}else if (i%3==1) {
					yCoords.add(coord);
				}else if (i%3==2) {
					zCoords.add(coord);
				}
				i++;
			}
		}
		float[] repeatedX = new float[(relativePos.length/3)*8];
		float[] repeatedY = new float[(relativePos.length/3)*8];
		float[] repeatedZ = new float[(relativePos.length/3)*8];
		i = 0;
		while (i < relativePos.length) {
			if (i%3==0) {
				for (int t=0;t<8;t++) {
					repeatedX[((i/3)*8)+t] = (float) relativePos[i];
				}
			}else if (i%3==1) {
				for (int t=0;t<8;t++) {
					repeatedY[(((i-1)/3)*8)+t] = (float) relativePos[i];
				}
			}else if (i%3==2){
				for (int t=0;t<8;t++) {
					repeatedZ[(((i-2)/3)*8)+t] = (float) relativePos[i];
				}
			}
			i++;
		}
		
		if (repeatedX.length == 0) {
			return null;
		}
		return new float[][]{repeatedX,repeatedY,repeatedZ};
//		float[] srcX = new float[xCoords.size()];
//		i = 0;
//		for (float num:xCoords) {
//			srcX[i] = (float) num;
//			i++;
//		}
//		repeatedX = gpuHandler.addArrays(repeatedX, srcX, repeatedX.length);
//		float[] srcY = new float[yCoords.size()];
//		i = 0;
//		for (float num:yCoords) {
//			srcY[i] = (float) num;
//			i++;
//		}
//		repeatedY = gpuHandler.addArrays(repeatedY, srcY, repeatedY.length);
//		
//		float[] srcZ = new float[zCoords.size()];
//		i = 0;
//		for (float num:zCoords) {
//			srcZ[i] = (float) num;
//			i++;
//		}
//		repeatedZ = gpuHandler.addArrays(repeatedZ, srcZ, repeatedZ.length);
//		
//		float[] s = {(float) sl};
//		float[] c = {(float) cl};
//		
//		return new float[][]{srcX,srcY,srcZ};
//		return new float[][]{repeatedX,repeatedY,repeatedZ};
		
//		float [][] rotated = gpuHandler.rotatePoints(repeatedX, repeatedZ, c, s, repeatedX.length);
//		
//		repeatedX = rotated[0];
//		repeatedZ = rotated[1];
//		
//		return new float[][]{repeatedX,repeatedY,repeatedZ};
		
//		s[0] = (float) sv;
//		c[0] = (float) cv;
//		
//		rotated = gpuHandler.rotatePoints(repeatedY, repeatedZ, c, s, repeatedX.length);
//		
//		repeatedY = rotated[0];
//		repeatedZ = rotated[1];
//		
//		return new float[][]{repeatedX,repeatedY,repeatedZ};
		
//		ArrayList<Float> tempCoords = new ArrayList<Float>();
//		for (float[] chunkData: modVerts) {
//			for (float coord:chunkData) {
//				tempCoords.add(coord);
//			}
//		}
//		float[] srcA = new float[tempCoords.size()];
//		i = 0;
//		for (float num:tempCoords) {
//			srcA[i] = num;
//			i++;
//		}
//		float[] repeatedPos = new float[relativePos.length*8];
//		if (repeatedPos.length == 0) {
//			return null;
//		}
//		i = 0;
//		while (i < relativePos.length/3) {
//			for (int t=0;t<8;t++) {
//				repeatedPos[(24*i)+(3*t)] = relativePos[(3*i)];
//				repeatedPos[(24*i)+(3*t)+1] = relativePos[(3*i)+1];
//				repeatedPos[(24*i)+(3*t)+2] = relativePos[(3*i)+2];
//			}
//			i++;
//		}
//	
//		return gpuHandler.addArrays(repeatedPos, srcA, repeatedPos.length);
		//private float[] rotate2D(float x, float y, double s,double c) {
		//return new float[] { (float) (x * c - y * s), (float) (y * c + x * s) };
		
	}
	
	public void addFace(BlockFace tempFace) {
		if(Thread.currentThread().getName() == "Render1") {
			facesToRender2.add(tempFace);
		}else {
			facesToRender.add(tempFace);
		}
	}
	public int linkArraySize(LinkedList<Integer[]> data) {
		int test = 0;
		for (Integer[] chunkData:data) {
			test+=chunkData.length;
		}return test;
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