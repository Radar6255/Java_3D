//Made by Riley Adams
//Started around September of 2017
//Credit to RealTutsGML who taught me how to program games in Java and gave me a base platform that this is designed off of
//and DLC ENERGY who helped me understand 3D rendering from scratch, borrowed his 2D rotation class from his java example.
//Credit to Stefan Gustavson's paper on simplex noise at http://staffwww.itn.liu.se/~stegu/simplexnoise/simplexnoise.pdf

package com.radar;

import static com.jogamp.opengl.GL2ES3.*;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import com.radar.cube.Cube;
import com.radar.windows.KeyInput;
import com.radar.windows.MenuRender;
import com.radar.windows.MouseInput;
import com.radar.windows.Window;
import com.radar.world.WorldGen;
//Main class initializes all classes and runs the game loop
public class Main extends GLCanvas implements Runnable, GLEventListener{
	public String version = "1.1.3";
	public int frames,fps;
	long renderTime;
	
	public int totalFrames, seconds = 0;
	public static int mode = 0;
	long totalCubeTime, totalCubeRenderTime, totalLoops, totalRenderTime, totalTestTime = 0;
	
	private static final long serialVersionUID = 1L;
	private Thread thread;
	private boolean running;
	public static boolean pause;
	public static boolean changeMouse = true;
	private Handler handler;
	MenuRender menuRender;
	int WIDTH = 1200;
	int HEIGHT = 800;
	int i = 0;
	int iz = -50;
	Frame frame;
	FPSAnimator animator;
	long last, start;
	public Main(){
		handler = new Handler(this);
		Player thePlayer = new Player(1.1,40.1,2.1,180,120,handler,this);
		handler.addPlayer(thePlayer);
		WorldGen gen = new WorldGen(handler,thePlayer);
		handler.addGeneration(gen);
		this.addMouseListener(new MouseInput(thePlayer));
		this.addKeyListener(new KeyInput(handler));
		frame = new Window(WIDTH, HEIGHT, "3D Stuff", this).getFrame();
		menuRender = new MenuRender(frame,this);
		animator = new FPSAnimator(this, 200);
		this.addGLEventListener(this);
		animator.start();
		this.start();
		last = System.currentTimeMillis();
//		start = animator.getFPSStartTime();
	}
	long startTime, endTime;
	
	@Override
	public void run() {
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 20.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		frames = 0;
//		running = false;
		while (running){
			long now = System.nanoTime();
			delta += (now- lastTime) / ns;
			lastTime = now;
			while (delta >= 1){
				tick();
				delta--;
			}
			if (running){
				Cube.msLag = 0;
				renderTime = System.currentTimeMillis();
//				render();
			}
			frames++;
			if (System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				fps = frames;
				frames = 0;
				if (!pause) {
					totalFrames += fps;
					seconds+=1;
				}
			}
		}
		stop();
	}
	@Override
	public void display(GLAutoDrawable drawable) {
//		render();
//		animator.resetFPSCounter();
//		if (System.currentTimeMillis()-last > 1000) {
//			last = System.currentTimeMillis();
//			animator.resetFPSCounter();
//			System.out.println(animator.getLastFPSUpdateTime());
//		}
		GL2 gl = drawable.getGL().getGL2();
		gl.glBegin(GL_QUADS);
		gl.glColor3f(1,1,1);
		gl.glVertex2f(-1, -1);
		gl.glVertex2f(1, -1);
		gl.glVertex2f(1, 1);
		gl.glVertex2f(-1, 1);
		gl.glEnd();
		gl.glBegin(GL_LINES);
		gl.glColor3f(0, 0, 0);
//		System.out.println(System.currentTimeMillis()/100);
//		if (animator.getLastFPSUpdateTime()-start != 0) {
//			System.out.println((animator.getTotalFPSFrames()*1000)/(animator.getLastFPSUpdateTime()-start));
//		}
		if (last - System.currentTimeMillis() != 0) {
			System.out.println((1000*animator.getTotalFPSFrames())/(System.currentTimeMillis()-last));
		}
		
		
//		System.out.println(animator.getTotalFPSFrames());
//		if (animator.getLastFPS() > 150) {
//			gl.glVertex2f(-0.03f, -0.03f);
//			gl.glVertex2f(-0.03f, 0.03f);
//		}
		gl.glEnd();
		handler.render(gl);
//		gl.glEnd();
	}

	public synchronized void start(){
		thread = new Thread(this);
		thread.start();
		running = true;
	}public synchronized void stop(){
		try{
			thread.join();
			running = false;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void tick(){
		if (pause && changeMouse){
			this.setCursor(Cursor.getDefaultCursor());
			changeMouse = false;
		}else if(!pause && changeMouse){
			BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
			Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
			this.setCursor(blankCursor);
			changeMouse = false;
		}
		handler.tick();
	}
	public void render(){
//		System.out.println("Rendering right");
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null){
			this.createBufferStrategy(2);
			return;
		}
		final Graphics2D g = (Graphics2D) bs.getDrawGraphics();
//		Graphics2D gGraph = (Graphics2D) getGraphics();
//		VolatileImage vImg = createVolatileImage(WIDTH, HEIGHT);
//		final Graphics2D g = (Graphics2D) vImg.getGraphics();
		
		g.setColor(new Color(114, 154, 219));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		if (mode == 0) {
//			handler.render(g);
		}
		renderTime = System.currentTimeMillis() - renderTime;
		g.setColor(Color.BLACK);
		g.drawString("FPS:"+Integer.toString(fps), WIDTH-75, 20);
		if (seconds > 0) {
			g.drawString("Avg FPS:"+Integer.toString(totalFrames/seconds), WIDTH-85, 40);
		}if (totalLoops != 0) {
			g.drawString("Cube ms:"+Long.toString(totalCubeTime/totalLoops), WIDTH-105, 60);
			g.drawString("Cube render ms:"+Long.toString(totalCubeRenderTime/totalLoops), WIDTH-135, 80);
			g.drawString("Handler Test ms:"+Long.toString(totalTestTime/totalLoops), WIDTH-135, 100);
//			g.drawString("%Cube:"+Long.toString((100*totalCubeTime/totalLoops)/(totalRenderTime/totalLoops)), WIDTH-105, 60);
//			g.drawString("%Cube render:"+Long.toString((100*totalCubeRenderTime/totalLoops)/(totalRenderTime/totalLoops)), WIDTH-135, 80);
			g.drawString("Total render ms:"+Long.toString(totalRenderTime/totalLoops), WIDTH-135, 120);
		}
		totalCubeTime += Cube.msLag;
		totalCubeRenderTime += handler.cubeRenderMs;
		totalRenderTime += renderTime;
		totalTestTime += handler.testMs;
		totalLoops += 1;
		
//		if (renderTime != 0) {
//			g.drawString("%Cube:"+Long.toString(100*Cube.msLag/renderTime), WIDTH-105, 60);
//			g.drawString("%Cube render:"+Long.toString(100*Handler.cubeRenderMs/renderTime), WIDTH-135, 80);
//		}
		
//		g.drawString(version, 10, HEIGHT-40);
		g.dispose();
//		gGraph.drawImage(vImg, 0, 0, this);
		bs.show();
	}
	
	
	public int getHeight(){
		return HEIGHT;
	}public int getWidth(){
		return WIDTH;
	}public void setHeight(int height) {
		this.HEIGHT = height;
		handler.fovChange();
	}public void setWidth(int width) {
		this.WIDTH = width;
	}
	
	public static void main(String[] args){
		new Main();
	}
	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void init(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		
	}
}