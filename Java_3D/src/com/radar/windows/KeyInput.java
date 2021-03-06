package com.radar.windows;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.radar.Handler;
import com.radar.Main;
import com.radar.cube.CubeObject;

//Handles all key input from the user
//Sends the data through setter methods

public class KeyInput implements KeyListener {
	
	private Handler handler;
	private boolean escape;
	boolean debug = false;
	int i = 0;
	
	public KeyInput(Handler handler){
		this.handler = handler;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_F3){
			if (!debug){
				debug = true;
			}else{
				debug = false;
			}handler.debugMode(debug);
			CubeObject.setDebug(debug);
		}
		if (key == KeyEvent.VK_R){
			handler.reloadChunks();
		}if (key == KeyEvent.VK_T){
			handler.getGen().printWorld();
		}if (key == KeyEvent.VK_W){
			handler.getPlayer().setUp(true);
		}if (key == KeyEvent.VK_S){
			handler.getPlayer().setDown(true);
		}if (key == KeyEvent.VK_A){
			handler.getPlayer().setLeft(true);
		}if (key == KeyEvent.VK_D){
			handler.getPlayer().setRight(true);
		}if (key == KeyEvent.VK_SPACE){
			handler.getPlayer().setSpace(true);
		}if (key == KeyEvent.VK_SHIFT){
			handler.getPlayer().setShift(true);
		}if (key == KeyEvent.VK_ESCAPE){
			if (!Main.pause && !escape){
				Main.mode = 1;
				handler.main.setVisible(false);
				Main.pause = true;
				Main.changeMouse = true;
			}else if(!escape){
				Main.mode = 0;
				handler.main.setVisible(true);
				Main.pause = false;
				Main.changeMouse = true;
			}
			escape = true;
		}
	}


	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();

		if (key == KeyEvent.VK_W){
			handler.getPlayer().setUp(false);
		}if (key == KeyEvent.VK_S){
			handler.getPlayer().setDown(false);
		}if (key == KeyEvent.VK_A){
			handler.getPlayer().setLeft(false);
		}if (key == KeyEvent.VK_D){
			handler.getPlayer().setRight(false);
		}if (key == KeyEvent.VK_SPACE){
			handler.getPlayer().setSpace(false);
		}if (key == KeyEvent.VK_SHIFT){
			handler.getPlayer().setShift(false);
		}if (key == KeyEvent.VK_ESCAPE){
			escape = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
