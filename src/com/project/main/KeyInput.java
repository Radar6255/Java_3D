package com.project.main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.project.main.GameObject;

public class KeyInput implements KeyListener {
	
	private Handler handler;
	private boolean escape;
	
	public KeyInput(Handler handler){
		this.handler = handler;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		for(int i=0;i<handler.object.size();i++){
			GameObject tempObject = handler.object.get(i);
			if(tempObject.getId() == 0){
				if (key == KeyEvent.VK_W){
					tempObject.setUp(true);
				}if (key == KeyEvent.VK_S){
					tempObject.setDown(true);
				}if (key == KeyEvent.VK_A){
					tempObject.setLeft(true);
				}if (key == KeyEvent.VK_D){
					tempObject.setRight(true);
				}if (key == KeyEvent.VK_SPACE){
					tempObject.setSpace(true);
				}if (key == KeyEvent.VK_SHIFT){
					tempObject.setShift(true);
				}if (key == KeyEvent.VK_ESCAPE){
					if (!MainClass.pause && !escape){
						MainClass.pause = true;
					}else if(!escape){
						MainClass.pause = false;
					}
					escape = true;
				}
			}
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		for(int i=0;i<handler.object.size();i++){
			GameObject tempObject = handler.object.get(i);
			if(tempObject.getId() == 0){
				if (key == KeyEvent.VK_W){
					tempObject.setUp(false);
				}if (key == KeyEvent.VK_S){
					tempObject.setDown(false);
				}if (key == KeyEvent.VK_A){
					tempObject.setLeft(false);
				}if (key == KeyEvent.VK_D){
					tempObject.setRight(false);
				}if (key == KeyEvent.VK_SPACE){
					tempObject.setSpace(false);
				}if (key == KeyEvent.VK_SHIFT){
					tempObject.setShift(false);
				}if (key == KeyEvent.VK_ESCAPE){
					escape = false;
				}
			}
		}

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
