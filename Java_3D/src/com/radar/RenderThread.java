package com.radar;

import java.awt.Graphics;
import java.util.LinkedList;

public class RenderThread extends Thread{
	
	LinkedList<CubeObject> visibleBlocks = new LinkedList<CubeObject>();
	String index;
	Graphics g;
	double px, py, pz, rotLat, rotVert = 0;
	public double sl,cl,sv,cv;
	public boolean running = true;
	volatile boolean ready = false;
	public Chunk chunk;
	RenderThread(String index){
		this.index = index;
	}
	
	public void run() {

		while (running) {
			
//			try {// TODO Find alternative to sleep function possibly
//				Thread.sleep(1);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			} 3540
			try {

				if (ready && visibleBlocks.size() > 0) {
					for (CubeObject block: visibleBlocks) {
						block.render(g, px, py, pz, rotLat, rotVert, sl, cl, sv, cv);
					}
					visibleBlocks = new LinkedList<CubeObject>();
					ready = false;
					if (index == "1") {
						chunk.moveOn();
					}else {
						chunk.moveOn2();
					}
				}
				//running = false;
			}catch(Exception e){
				System.out.println(e);
			}
		}
	}
	public void render(Chunk chunk,LinkedList<CubeObject> visibleBlocks, Graphics g, double px, double py, double pz, double rotLat, double rotVert, double sl, double cl, double sv, double cv) {
		this.chunk = chunk;
		this.g = g;
		this.px = px;
		this.py = py;
		this.pz = pz;
		this.rotLat = rotLat;
		this.rotVert = rotVert;
		this.sl = sl;
		this.cl = cl;
		this.sv = sv;
		this.cv = cv;
		int i = 0;
		ready = false;
		while (i < visibleBlocks.size()/2) {
			if (index == "2") {
				this.visibleBlocks.add(visibleBlocks.get(i));
				//visibleBlocks.get(i).render(g,px,py,pz,rotLat,rotVert,sl,cl,sv,cv);
			}else {
				this.visibleBlocks.add(visibleBlocks.get(visibleBlocks.size()-i-1));
				//visibleBlocks.get(visibleBlocks.size()-i-1).render(g,px,py,pz,rotLat,rotVert,sl,cl,sv,cv);
			}
			i++;
		}
		ready = true;
		//for (CubeObject object:visibleBlocks){
		//	object.render(g,px,py,pz,rotLat,rotVert,sl,cl,sv,cv);
		//}
	}
}
