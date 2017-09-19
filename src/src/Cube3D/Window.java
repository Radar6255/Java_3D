package src.Cube3D;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.BitSet;

public class Window { // Master window handler! handle all our technical needs here!

	public BitSet keys = new BitSet(256); // hold all key states
	
	Frame frame;
	Canvas canvas;
	
	Main main; // just to send events
	public Window(Main m) {
		main=m;
		
		frame = new Frame();
		canvas = new Canvas();
		
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){main.closeRequested();}
			public void windowDeactivated(WindowEvent e){main.lostFocus();}
			public void windowGainedFocus(WindowEvent e) {canvas.requestFocus();}
		});
		
		canvas.addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent e){resized();}
		});
		
		canvas.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(!keys.get(e.getKeyCode()))main.keyPressed(e.getKeyCode());
				keys.set(e.getKeyCode()); // sets this key (true)
			}
			public void keyReleased(KeyEvent e) {
				if(keys.get(e.getKeyCode()))main.keyReleased(e.getKeyCode());
				keys.clear(e.getKeyCode()); // clears this key (false)
			}
		});
		canvas.addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				main.mouseMoved(e.getX()-cx,e.getY()-cy);
				if(mouseLocked)centerMouse();
			}
		});
		
		frame.setBackground(new Color(0.5f,0.7f,1f,1f)); // not needed
		canvas.setPreferredSize(new Dimension(854,480)); // preferred size
		frame.add(canvas); frame.pack(); // auto size frame
		frame.setLocationRelativeTo(null); // center of desktop
		frame.setVisible(true);
		
		// so we don't have to click in window
		frame.requestFocus();
		canvas.requestFocus();
		
		screen = canvas.getGraphics();
//		resized();
		
		try{robot=new Robot(); // to teleport mouse
		}catch(AWTException e){}
		
		setMouseLocked(true);
		
	}
	
	public void resized() {
		w=canvas.getWidth(); h=canvas.getHeight(); cx=w/2; cy=h/2;
		image = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		g = image.createGraphics();
		main.resized(w,h);
	}

	int w,h,cx,cy;
	BufferedImage image;
	Graphics2D g; // buffered image graphics (using for double buffering)
	Graphics screen; // canvas graphics
	public void flip(){screen.drawImage(image,0,0,null);} // double buffered rendering
	
	public static final Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB),new Point(0,0),"blank");
	
	boolean mouseLocked;
	public void setMouseLocked(boolean state) {
		mouseLocked = state;
		frame.setCursor(state?blankCursor:Cursor.getDefaultCursor()); // set invisible / visible
		if(state)centerMouse(); // incase mouse is outside window, this will put it in
	}
	
	Robot robot; // so we can keep mouse centered
	public void centerMouse(){Point p=canvas.getLocationOnScreen(); robot.mouseMove(p.x+cx,p.y+cy);}
	
	
}
