//Made by Riley Adams
//Credit to RealTutsGML who taught me how to program games in java and gave me a base platform that this is designed off of.
//and DLC ENERGY who helped me understand 3D rendering from scratch, borrowed his 2D rotation class from his java example.

package com.radar;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
//Main class initializes all classes and runs the game loop
public class Main extends Canvas implements Runnable{
	public String version = "1.1.1";
	public int frames,fps;
	private static final long serialVersionUID = 1L;
	private Thread thread;
	private boolean running;
	static boolean pause;
	static boolean changeMouse = true;
	private Handler handler;
	static int WIDTH = 1000;
	static int HEIGHT = 700;
	int i = 0;
	int iz = -50;
	ImageIcon img = new ImageIcon("./dirt.png");
	
	public Main(){
		handler = new Handler();
		Player thePlayer = new Player(1,3,2,180,-30);
		handler.addPlayer(thePlayer);
		WorldGen gen = new WorldGen(handler,thePlayer);
		handler.addGeneration(gen);
		this.addKeyListener(new KeyInput(handler));
		new Window(WIDTH,HEIGHT,"3D Stuff",this);
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
		while (running){
			long now = System.nanoTime();
			delta += (now- lastTime) / ns;
			lastTime = now;
			while (delta >= 1){
				tick();
				delta--;
			}
			if (running){
				render();
			}
			frames++;
			if (System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				fps = frames;
				frames = 0;
			}
		}
		stop();
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
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null){
			this.createBufferStrategy(2);
			return;
		}
		//Help my sorting of cubes in handler
		//System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		final Graphics g = bs.getDrawGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		handler.render(g);
		g.setColor(Color.BLACK);
		g.drawString("FPS:"+Integer.toString(fps), WIDTH-55, 20);
//		g.drawString(version, 10, HEIGHT-40);
		g.dispose();
		bs.show();
	}
	
	public static void main(String[] args){
		new Main();
	}
}