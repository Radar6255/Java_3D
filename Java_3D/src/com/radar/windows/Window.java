package com.radar.windows;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import com.radar.Main;

//Sets basis for all characteristics of the game window
public class Window {
	Frame frame;
	public Window(int width,int height, String title, Main game){
		frame = new Frame(title);
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		//frame.setUndecorated(true);
		frame.setPreferredSize(new Dimension(width,height));
		frame.setMaximumSize(new Dimension(width,height));
		frame.setMinimumSize(new Dimension(width,height));
//		frame.setC
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.add(game);
		frame.pack();
		frame.setVisible(true);
		frame.addComponentListener(new ComponentAdapter() { 
			public void componentResized(ComponentEvent e) {
				Dimension size = frame.getSize();
				game.setHeight((int) size.getHeight());
				game.setWidth((int) size.getWidth());
			}
		});
//		game.start();
	}
	public Frame getFrame() {
		return frame;
	}
	
}
