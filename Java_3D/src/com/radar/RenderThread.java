package com.radar;

import java.awt.Graphics;
import java.util.LinkedList;

public class RenderThread extends Thread{
	
	LinkedList<CubeObject> visibleBlocks = new LinkedList<CubeObject>();
	String index;
	Graphics g;
	double px, py, pz, rotLat, rotVert = 0;
	public double sl,cl,sv,cv;
	public int size;
	float [] relativePos;
	public boolean running = true;
	volatile boolean ready = false;
	public Handler handler;
	RenderThread(String index){
		this.index = index;
	}
	public void run() {

		while (running) {
			try {
				if (ready && visibleBlocks.size() > 0) {
					int i = 0;
					for (CubeObject block: visibleBlocks) {
//						block.render(px, py, pz, rotLat, rotVert, sl, cl, sv, cv);
//						block.render(relativePos[(size-i-1)*3], relativePos[((size-i-1)*3)+1], relativePos[((size-i-1)*3)+2], rotLat, rotVert, sl, cl, sv, cv);
						block.render(relativePos[((size/2)+i+1)*3], relativePos[(((size/2)+i+1)*3)+1], relativePos[(((size/2)+i+1)*3)+2], rotLat, rotVert, sl, cl, sv, cv);
						i++;
					}
//					visibleBlocks = new LinkedList<CubeObject>();
					visibleBlocks.clear();
					ready = false;
					handler.moveOn();
				}
			}catch(Exception e){
				System.out.println(e.getCause());
			}
		}
	}
	public void render(Handler handler,LinkedList<CubeObject> visibleBlocks, Graphics g, float [] relativePos, double rotLat, double rotVert, double sl, double cl, double sv, double cv) {
		this.handler = handler;
		this.g = g;
		this.rotLat = rotLat;
		this.rotVert = rotVert;
		this.relativePos = relativePos;
		this.sl = sl;
		this.cl = cl;
		this.sv = sv;
		this.cv = cv;
		int i = 0;
		ready = false;
		if (1 > visibleBlocks.size()/2) {
			handler.moveOn();
		}
		size = visibleBlocks.size();
		for (CubeObject visibleBlock : visibleBlocks) {
			if (i > visibleBlocks.size()/2) {
				this.visibleBlocks.add(visibleBlock);
			}
			i++;
		}
		ready = true;
	}
}
