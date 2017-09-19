package src.Cube3D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class Main implements Runnable { // the actual game!
	
	public static void main(String[] args){
		new Thread(new Main("3D Graphics 1")).start(); // start new thread with game
//		new Thread(new Main("3D Graphics 2")).start(); // could open another
	}
	
	public boolean running=true;
	
	public void run(){
		setup();
		
		Timer.reset();

		while(running){
			
			Timer.update();
			while(Timer.updates-->0)update();

			render();

			if(Timer.nextFPS())window.frame.setTitle(title+" : "+Timer.fps+" fps");
		}
		
		window.frame.dispose(); // end of thread, destroy window
	}
	
	public void closeRequested(){running=false;}
	public void lostFocus(){window.setMouseLocked(false);}
	
	public void mouseMoved(int dx,int dy) {
		if(window.mouseLocked){
			player.mouse_motion(dx,dy);
			window.centerMouse();
		}
	}
	
	public void keyPressed(int key) {
		if(key==KeyEvent.VK_E)window.setMouseLocked(!window.mouseLocked);
	}

	public void keyReleased(int key) {
//		if(key==KeyEvent.VK_E)main.setMouseLocked(!window.mouseLocked);
	}

	
	String title;
	Window window;
	public Main(String t) {title = t;}
	
	ArrayList<Cube> cubes = new ArrayList<>();
	
	public void setup() {
		window = new Window(this);
		window.frame.setTitle(title);
		for(int i=0; i<10; i++) cubes.add(new Cube(-10+i*2,0,0));
	}
	
	Player player = new Player(-1.5f,-0.5f,-1.5f,0,0);
	
	public void update() {
		player.update(window.keys);
		//for(Cube cube : cubes)cube.update(false); // put false to keep it normal
	}
	
	
	private float[] rotate2D(float x,float y,float rad) {
		float s = (float)Math.sin(rad);
		float c = (float)Math.cos(rad);
		return new float[]{x*c-y*s,y*c+x*s};
	}
	
	class Face { // just a 2d poly
		int[][] coords; int color; float depth;
		public Face(int[][] c,int rgb,float d){coords = c; color = rgb; depth = d;}
	}
	
	class FaceSorter implements Comparator<Face> {
		public int compare(Face a, Face b) {
			if(a.depth>b.depth) return -1;
			if(a.depth<b.depth) return 1;
			return 0;
			// without correct logic, sorting becomes non transitive, (like rock, paper, scissors) and will cause an error
		}
	}
	
	int fov;
	
	public void resized(int w,int h) {
		fov = w<h ? w : h;
	}
	
	public void render() {

		Graphics2D g = window.g;
		g.setColor(new Color(0x80b2ff));
		g.fillRect(0,0,window.w,window.h);

		ArrayList<Face> face_list = new ArrayList<>(); // all faces to render

		for(Cube cube : cubes){
			// the specific objects static data
			float[][] verts = Cube.verts; // vertices
			int[][] faces = Cube.faces; // indices for faces
			int[] colors = Cube.colors; // color for faces
			
			ArrayList<float[]> vert_list = new ArrayList<>(); // translated 3d points
			ArrayList<int[]> screen_coords = new ArrayList<>(); // projected 2d points
			
			float x,y,z; float[] rot;
			
			for(float[] v : verts){
				x=v[0]; y=v[1]; z=v[2];
				rot=rotate2D(x,z,cube.rotY); x=rot[0]; z=rot[1];
				rot=rotate2D(y,z,cube.rotX); y=rot[0]; z=rot[1];
				
				x+=cube.x-player.x; y+=cube.y-player.y; z+=cube.z-player.z;
				
				rot=rotate2D(x,z,player.rotY); x=rot[0]; z=rot[1];
				rot=rotate2D(y,z,player.rotX); y=rot[0]; z=rot[1];
				vert_list.add(new float[]{x,y,z}); // 3d position
				float f = z==0 ? fov : fov/z;
				//System.out.println(x+" "+y+" "+fov);
				screen_coords.add(new int[]{(int)(window.cx+x*f),(int)(window.cy+y*f)}); // 2d position
			}
			
			for(int f=0; f<faces.length; f++){
				int[] face = faces[f];
				
				boolean on_screen = false;
				for(int i : face){
					x = screen_coords.get(i)[0];
					y = screen_coords.get(i)[1];
					if(vert_list.get(i)[2]>0 && x>0 && x<window.w && y>0 && y<window.h){on_screen=true; break;}
				}
				
				if(on_screen){
					
					int[][] coords = new int[face.length][2];
					for(int i=0; i<face.length; i++) coords[i]=screen_coords.get(face[i]);
					
					float depth=0;
					for(int i=0;i<3;i++){
						float sum=0;
						for(int j:face)sum+=vert_list.get(j)[i];
						depth+=sum*sum;
					}
					
					face_list.add(new Face(coords,colors[f],depth));
				}
				
			}
		
		}
		
		Collections.sort(face_list, new FaceSorter());
		
		for(Face f:face_list){
			g.setColor(new Color(f.color));
			int[] xs = new int[f.coords.length],ys = new int[f.coords.length];
			for(int i=0;i<f.coords.length;i++){xs[i]=f.coords[i][0]; ys[i]=f.coords[i][1];}
			g.fillPolygon(xs, ys, f.coords.length);
		}
		
		// YOUR OLD EDGES (fixed)
//		float x,y,z; float[] rot;
//		
//		g.setStroke(new BasicStroke(5));
//		g.setColor(Color.black);
//		for (int[] edge : Cube.edges) {
//			int[][] points = new int[2][2];
//
//			// calculate the edge points
//			for (int i=0; i<edge.length; i++) {
//				float[] v = Cube.verts[edge[i]];
//
//				x=v[0]-player.x; y=v[1]-player.y; z=v[2]-player.z;
//				rot=rotate2D(x,z,player.rotY); x=rot[0]; z=rot[1];
//				rot=rotate2D(y,z,player.rotX); y=rot[0]; z=rot[1];
//
//				float f = z==0 ? 200 : 200/z;
//				points[i][0] = (int) (main.cx + x*f);
//				points[i][1] = (int) (main.cy + y*f);
//			}
//
//			g.drawLine((int) points[0][0],points[0][1],points[1][0],points[1][1]);
//		}

		window.flip(); // like pygame.display.flip()

	}
	
	
}
