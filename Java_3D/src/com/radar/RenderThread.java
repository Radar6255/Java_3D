package com.radar;

import java.awt.Graphics;
import java.util.ArrayList;

import com.radar.cube.CubeObject;

public class RenderThread extends Thread{
	
	ArrayList<CubeObject> visibleBlocks = new ArrayList<CubeObject>();
	String index;
	double px, py, pz, rotLat, rotVert = 0;
	public double sl,cl,sv,cv;
	public int size;
	float [] relativePos;
	public boolean running = true;
	volatile boolean ready = false;
	public Handler handler;
	RenderThread(String index, Handler handler){
		this.index = index;
		this.handler = handler;
	}
	public void run() {

		while (running) {
			try {
				if (ready && visibleBlocks.size() > 0) {
					int i = 0;
					for (CubeObject block: visibleBlocks) {
//						block.render(px, py, pz, rotLat, rotVert, sl, cl, sv, cv);
						block.render(relativePos[(size+i)*3], relativePos[((size+i)*3)+1], relativePos[((size+i)*3)+2], rotLat, rotVert, sl, cl, sv, cv);
//						block.render(relativePos[((size/2)+i+1)*3], relativePos[(((size/2)+i+1)*3)+1], relativePos[(((size/2)+i+1)*3)+2], rotLat, rotVert, sl, cl, sv, cv);
						i++;
					}
//					visibleBlocks = new LinkedList<CubeObject>();
					visibleBlocks.clear();
					ready = false;
					handler.moveOn();
				}
				synchronized (this) {
					notifyAll();
				}
			}catch(Exception e){
				System.out.println(e.getCause());
			}
			if (!ready) {
				synchronized(handler) {
					try {
						handler.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	public void render(Handler handler,ArrayList<CubeObject> visibleBlocks, float [] relativePos, double rotLat, double rotVert, double sl, double cl, double sv, double cv) {
		this.handler = handler;
		this.rotLat = rotLat;
		this.rotVert = rotVert;
		this.relativePos = relativePos;
		this.sl = sl;
		this.cl = cl;
		this.sv = sv;
		this.cv = cv;
		int i = visibleBlocks.size()/2;
		ready = false;
		if (1 > visibleBlocks.size()/2) {
			handler.moveOn();
		}
		size = visibleBlocks.size()/2;
//		for (CubeObject visibleBlock : visibleBlocks) {
//			if (i > visibleBlocks.size()/2) {
//				this.visibleBlocks.add(visibleBlock);
//			}
//			i++;
//		}
		while (i < visibleBlocks.size()) {
			this.visibleBlocks.add(visibleBlocks.get(i));
			i++;
		}
		ready = true;
	}
}
