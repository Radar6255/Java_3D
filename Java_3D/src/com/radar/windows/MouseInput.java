package com.radar.windows;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.radar.Player;

public class MouseInput implements MouseListener{
	Player player;
	public MouseInput(Player player) {
		this.player = player;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		int key = e.getButton();
		if (key == MouseEvent.BUTTON3) {
			player.placeBlock();
		}else if (key == MouseEvent.BUTTON1) {
			player.destroyBlock();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	
	
}
