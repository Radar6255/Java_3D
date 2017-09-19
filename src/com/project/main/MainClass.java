package com.project.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class MainClass extends Canvas implements Runnable{
	
	private Handler handler;
	
	private static final long serialVersionUID = -2681032978929841738L;
	public static final int WIDTH = 1000, HEIGHT = (WIDTH / 12) * 9;
	public static boolean pause, prevPause = false;
	public static boolean first = true;
	private Thread thread;
	private boolean running = false;

	public enum STATE {
		Menu,
		pause,
		settings,
		help,
		game;
	}
	public MainClass(){
		handler = new Handler();
		handler.addObject(new Perspective(0, 0, 0, 20, 20, 20,0, handler,0,0));
		handler.addObject(new Cube2(0, 0, 0, 400, 400, 400,1, handler));
		//MouseMovement mouseMove = new MouseMovement();
		//this.addMouseListener(mouseMove);
		new Window(WIDTH,HEIGHT,"3D Testing", this);
		this.addKeyListener(new KeyInput(handler));
		
	}
	public synchronized void start(){
		thread = new Thread(this);
		thread.start();
		running = true;
	}
	public synchronized void stop(){
		try{
			thread.join();
			running = false;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void run(){
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
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
				System.out.println("FPS:  "+frames);
				frames = 0;
			}
		}
		stop();
	}
	private void tick(){
		if (pause && prevPause != pause){
			this.setCursor(Cursor.getDefaultCursor());
		}else if (prevPause != pause || first){
			first = false;
			BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
			Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
			    cursorImg, new Point(0, 0), "blank cursor");
			this.setCursor(blankCursor);
		}
		handler.tick();
		prevPause = pause;
	}
	private void render(){
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null){
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		handler.render(g);
		
		g.dispose();
		bs.show();
	}
	
	public static void main(String args[]){
		new MainClass();
	}
}
