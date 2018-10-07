package com.radar;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

//Sets basis for all characteristics of the game window
public class Window extends Canvas{
	private static final long serialVersionUID = 7288512704324421631L;
	
	public Window(int width,int height, String title, Main game){
		JFrame frame = new JFrame(title);
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		//frame.setUndecorated(true);
		frame.setPreferredSize(new Dimension(width,height));
		frame.setMaximumSize(new Dimension(width,height));
		frame.setMinimumSize(new Dimension(width,height));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.add(game);
		frame.setVisible(true);
		frame.addComponentListener(new ComponentAdapter() { 
			public void componentResized(ComponentEvent e) {
				Dimension size = frame.getSize();
				game.setHeight((int) size.getHeight());
				game.setWidth((int) size.getWidth());
			}
		});
		game.start();
	}
	
}
