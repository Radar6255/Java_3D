package com.radar.windows;

import java.awt.Button;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import com.radar.Handler;
import com.radar.Main;
import com.radar.SettingVars;

public class MenuRender {
	Main main;
	JFrame frame;
	PopupMenu test;
	Button a = new Button("Resume");
	Button b = new Button("Toggle GPU");
	Button c = new Button("Toggle Multi-threading");
	Button d = new Button("Movement Rate");
	Button [] buttons = {a,b,c,d};
	
	ActionListener listener = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == a) {
				main.setVisible(true);
				Main.mode = 0;
				Main.pause = false;
				Main.changeMouse = true;
			}
			if (e.getSource() == b) {
				
				if (SettingVars.gpu) {
					System.out.println("GPU Stopped");
					SettingVars.gpu = false;
				}else {
					System.out.println("GPU Started");
					SettingVars.gpu = true;
				}
			}if (e.getSource() == c) {
				if (SettingVars.multiThread) {
					System.out.println("Stopped Multi-threading");
					SettingVars.multiThread = false;
					Handler.stopMulti();
				}else {
					System.out.println("Started Multi-threading");
					SettingVars.multiThread = true;
					Handler.startMulti();
				}
			}if (e.getSource() == d) {
				SettingVars.movementRate+=0.1;
			}
			
		}
		
	};
	public MenuRender(JFrame frame, Main main) {
		this.frame = frame;
		this.main = main;
		int w = 160;
		int h = 20;
		int x = 10;
		int y = 10;
		for (Button button: buttons) {
			button.setBounds(x,y,w,h);
			y += h;
			button.addActionListener(listener);
			frame.add(button);
		}
	}
}