package com.radar;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInput implements KeyListener {
	
	private Handler handler;
	private boolean escape;
	int i = 0;
	
	public KeyInput(Handler handler){
		this.handler = handler;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_P){
			i=0;
//			while (i <255){
//				handler.getGen().getWorld().get(0).get(0).add(0);
//				i++;
//			}
			handler.getGen().getWorld().get(0).get(0).add(1);
			System.out.println("Added block");
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
				Main.pause = true;
				Main.changeMouse = true;
			}else if(!escape){
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
