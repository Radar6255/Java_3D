package src.Cube3D;

public class Timer { // to handle our game loop

	public static final float updatesPerSec=60; // design your physics, knowing the universe updates this many times per second
	private static long now,last,nextFPS;
	private static double seconds,updateCount;
	private static int frameCount;
	
	public static int updates,fps;
	public static float renderFloat;
	
	public static void reset(){
		last=System.nanoTime();nextFPS=System.currentTimeMillis()+1000;
		updateCount=0;updates=0;frameCount=0;
	}
	
	public static void update(){
		now=System.nanoTime(); // current nano time
		seconds=(now-last)/1000000000d; // nano difference, in seconds
		if(seconds>0.25)seconds=0.25; // if taking 0.25s to update, reduce updates (4fps is bad enough)
		last=now; // last is now (well, for the next loop)
		updateCount+=seconds*updatesPerSec; // add our updates to the pile
		updates=(int)updateCount; // the whole amount of updates we can do in a loop
		updateCount-=updates; // take them off the pile (the fractions add up)
		renderFloat=(float)updateCount; // very accurate left over, which can be used as a fraction to render between the updates
		frameCount++; // another fps
	}
	
	public static boolean nextFPS(){
		boolean a = System.currentTimeMillis()>=nextFPS; // has it been a second?
		if(a){fps=frameCount;frameCount=0;nextFPS+=1000;} // get the fps, reset the count, move interval +1 second
		return a; // return whether fps was updated
	}
	
}