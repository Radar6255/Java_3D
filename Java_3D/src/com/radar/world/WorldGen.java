package com.radar.world;

import java.util.ArrayList;
import java.util.LinkedList;

import com.radar.Handler;
import com.radar.Player;
import com.radar.SettingVars;

public class WorldGen{
	int i,index,all,chunkX,chunkY,chunkZ,xOff,zOff,ix,iz,h,tx,ty,ty2,tz = 0;
	boolean debug = false;
	int genSize = SettingVars.genSize;
	double x,y,z, yRange, yRange2;
	Handler handler;
	Player thePlayer;
	public ArrayList<ArrayList<ArrayList<Integer>>> world2 = new ArrayList<ArrayList<ArrayList<Integer>>>();
	LinkedList<LinkedList<Integer>> chunk = new LinkedList<LinkedList<Integer>>();
	public WorldGen(Handler handler, Player thePlayer){
		this.handler = handler;
		this.thePlayer = thePlayer;
		world2.add(new ArrayList<ArrayList<Integer>>());
		world2.get(0).add(new ArrayList<Integer>());
		i = 0;
//		Debug flat world
//		while(i < 256){
//			world2.get(0).get(0).add(1);
//			i++;
//		}
//		i = 0;
	}
	public void tick(){
		x = thePlayer.getX();
		y = thePlayer.getY();
		z = thePlayer.getZ();
		chunkX = thePlayer.getChunkX();
		chunkY = thePlayer.getChunkY();
		chunkZ = thePlayer.getChunkZ();
		ix = -genSize;
		iz = -genSize;
		while(iz < genSize+1){
			try{
				while((chunkX+xOff+ix) < genSize+1){
					
					world2.add(0,new ArrayList<ArrayList<Integer>>());
					while (world2.get(0).size() < zOff+ix) {
						world2.get(0).add(new ArrayList<Integer>());
					}
					xOff++;
				}
				while((chunkZ+zOff+iz) < genSize+1){
					for (ArrayList<ArrayList<Integer>> zChunks:world2){
						zChunks.add(0, new ArrayList<Integer>());
					}
					zOff++;
				}
				while (world2.size() <= chunkX+genSize+1+xOff+ix){
					world2.add(new ArrayList<ArrayList<Integer>>());
					while(world2.get(world2.size()-1).size() < zOff+iz){
						world2.get(world2.size()-1).add(new ArrayList<Integer>());
					}
				}
				while (world2.get(chunkX+xOff+ix).size() <= chunkZ+zOff+5+iz){
					world2.get(chunkX+xOff+ix).add(new ArrayList<Integer>());
				}
				if (world2.get(chunkX+xOff+ix).get(chunkZ+zOff+iz).isEmpty()){
					h = 0;
					i = 0;
					tx = 0;
					tz = 0;
					//Normally 60
					while (h < 60){
						while (i < 256){
//								ty  = (int) Math.round(Math.sqrt(5/(Math.pow(((16-tx)+(16*(chunkX+ix)))*0.1,2)*Math.pow(((16-tz)+(16*(chunkZ+iz)))*0.1, 2))));
							ty = 0;
							ty2 = 0;
//								Rotate ty = (int) Math.round(0.4*(Math.pow(((16-tx)+(16*(chunkX+ix)))*0.2, 2)+Math.pow(((tz)+(16*(chunkZ+iz)))*0.2, 2))); 90 degrees
//								ty = (int) Math.round(0.4*(Math.pow(((tz)+(16*(chunkX+ix)))*0.2, 2)+Math.pow(((tx)+(16*(chunkZ+iz)))*0.2, 2)));
//								ty = (int) Math.round(Math.pow((0.16-Math.pow((0.6-Math.pow((Math.pow(((tz)+(16*(chunkX+ix)))*0.04,2)+Math.pow(((tx)+(16*(chunkZ+iz)))*0.04,2)),0.5)),2)),0.5)*20);
//								ty = (int) ((Math.sin(((tz)+(16*(chunkX+ix)))*0.2)*Math.cos(((tx)+(16*(chunkZ+iz)))*0.2))*5.0)+10;
							if (SettingVars.noise) {
								ty = (int) (20*(noise(((tx)+(16*(chunkZ+iz)))/100.0,(((tz)+(16*(chunkX+ix)))/100.0))+1));
							}else {
								yRange = 0.4*(Math.pow(((tz)+(16*(chunkX+ix)))*0.3, 2)+Math.pow(((tx)+(16*(chunkZ+iz)))*0.3, 2));
								z = ((tx)+(16*(chunkZ+iz)));
								x = ((tz)+(16*(chunkX+ix)));
//								yRange = 0.6*Math.sqrt((1+Math.pow((z*0.8),2)) + (Math.pow((x*0.8), 2)))+7;
//								yRange2 = 0.6*-Math.sqrt((1+Math.pow((z*0.8),2)) + (Math.pow((x*0.8), 2)))+7;
							}
//								System.out.println((10*(Value2D(((tx)+(16*(chunkZ+iz))),((tz)+(16*(chunkX+ix))),8)+1)));
//								if ((((tz)+(16*(chunkX+ix))) + 3) != 0){
//									ty = (int) ( (((tx)+(16*(chunkZ+iz))) + 2) / (((tz)+(16*(chunkX+ix))) + 3) );
//								}
//								ty = (int) Math.sqrt(Math.pow((tx)+(16*(chunkZ+iz)),2) + Math.pow((tz)+(16*(chunkX+ix)),2));
							if (SettingVars.noise) {
								if (ty == h){
									if (ty < 10) {
										world2.get(chunkX+xOff+ix).get(chunkZ+zOff+iz).add(2);
									}else {
										world2.get(chunkX+xOff+ix).get(chunkZ+zOff+iz).add(1);
									}
								}else{
									world2.get(chunkX+xOff+ix).get(chunkZ+zOff+iz).add(0);
								}
							}else {
								if ((yRange < h+0.5 && yRange > h-0.5) || (yRange2 < h+0.5 && yRange2 > h-0.5)){
									if (ty < 10) {
										world2.get(chunkX+xOff+ix).get(chunkZ+zOff+iz).add(2);
									}else {
										world2.get(chunkX+xOff+ix).get(chunkZ+zOff+iz).add(1);
									}
								}else{
									world2.get(chunkX+xOff+ix).get(chunkZ+zOff+iz).add(0);
								}
							}
							i++;
							tx++;
							if (tx > 15){
								tx = 0;
								tz++;
							}
						}
						h++;
						i = 0;
						tz = 0;
						tx = 0;
					}
				}
			}catch(Exception e){
				System.out.println("World gen error : ");
				e.printStackTrace();
				System.out.println(e.getCause());
			}
			ix++;
			if (ix == genSize+1){
				iz++;
				ix = -genSize;
			}
		}
	}public ArrayList<ArrayList<ArrayList<Integer>>> getWorld(){
		return world2;
	}public int getXOff(){
		return xOff;
	}public int getZOff(){
		return zOff;
	}public void printWorld(){
		System.out.println(world2);
	}public void setDebug(boolean debug){
		this.debug = debug;
	}public void addBlock(int blockNum, int chunkX, int chunkZ){
		world2.get(chunkX+xOff).get(chunkZ+zOff).add(blockNum);
	}
	//Credit to Stefan Gustavson's paper on simplex noise at http://staffwww.itn.liu.se/~stegu/simplexnoise/simplexnoise.pdf
	//Code taken from end of paper to generate 2d simplex noise
	  // This method is a *lot* faster than using (int)Math.floor(x)
	  private static int fastfloor(double x) {
	    return x>0 ? (int)x : (int)x-1;
	  }
	  private static double dot(int g[], double x, double y) {
	    return g[0]*x + g[1]*y; }
	  private static int grad3[][] = {{1,1,0},{-1,1,0},{1,-1,0},{-1,-1,0},
	          {1,0,1},{-1,0,1},{1,0,-1},{-1,0,-1},
	          {0,1,1},{0,-1,1},{0,1,-1},{0,-1,-1}};
	  private static int p[] = {151,160,137,91,90,15,
			  131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
			  190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
			  88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,
			  77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
			  102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,
			  135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,
			  5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
			  223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,
			  129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
			  251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
			  49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
			  138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180};
	  private static int perm[] = new int[512];
	  static { for(int i=0; i<512; i++) perm[i]=p[i & 255]; }
	  // 2D simplex noise
	  public static double noise (double xin, double yin){
	    double n0, n1, n2; 
	// Noise contributions from the three corners
	    // Skew the input space to determine which simplex cell we're in
	    final double F2 = 0.5*(Math.sqrt(3.0)-1.0);
	    double s = (xin+yin)*F2; 
	// Hairy factor for 2D
	    int i = fastfloor(xin+s);
	    int j = fastfloor(yin+s);
	    final double G2 = (3.0-Math.sqrt(3.0))/6.0;
	    double t = (i+j)*G2;
	    double X0 = i-t; 
	// Unskew the cell origin back to (x,y) space
	    double Y0 = j-t;
	    double x0 = xin-X0; 
	// The x,y distances from the cell origin
	    double y0 = yin-Y0;
	    // For the 2D case, the simplex shape is an equilateral triangle.
	    // Determine which simplex we are in.
	    int i1, j1; 
	// Offsets for second (middle) corner of simplex in (i,j) coords
	    if(x0>y0) {i1=1; j1=0;} 
	// lower triangle, XY order: (0,0)->(1,0)->(1,1)
	    else {i1=0; j1=1;}      
	// upper triangle, YX order: (0,0)->(0,1)->(1,1)
	    // A step of (1,0) in (i,j) means a step of (1-c,-c) in (x,y), and
	    // a step of (0,1) in (i,j) means a step of (-c,1-c) in (x,y), where
	    // c = (3-sqrt(3))/6
	    double x1 = x0 - i1 + G2; 
	// Offsets for middle corner in (x,y) unskewed coords
	    double y1 = y0 - j1 + G2;
	    double x2 = x0 - 1.0 + 2.0 * G2; 
	// Offsets for last corner in (x,y) unskewed coords
	    double y2 = y0 - 1.0 + 2.0 * G2;
	    // Work out the hashed gradient indices of the three simplex corners
	    int ii = i & 255;
	    int jj = j & 255;
	    int gi0 = perm[ii+perm[jj]] % 12;
	    int gi1 = perm[ii+i1+perm[jj+j1]] % 12;
	    int gi2 = perm[ii+1+perm[jj+1]] % 12;
	    // Calculate the contribution from the three corners
	    double t0 = 0.5 - x0*x0-y0*y0;
	    if(t0<0) n0 = 0.0;
	    else {
	      t0 *= t0;
	      n0 = t0 * t0 * dot(grad3[gi0], x0, y0);  
	// (x,y) of grad3 used for 2D gradient
	    }
	    double t1 = 0.5 - x1*x1-y1*y1;
	    if(t1<0) n1 = 0.0;
	    else {
	      t1 *= t1;
	      n1 = t1 * t1 * dot(grad3[gi1], x1, y1);
	    }
	    double t2 = 0.5 - x2*x2-y2*y2;
	    if(t2<0) n2 = 0.0;
	    else {
	      t2 *= t2;
	      n2 = t2 * t2 * dot(grad3[gi2], x2, y2);
	    }
	    // Add contributions from each corner to get the final noise value.
	    // The result is scaled to return values in the interval [-1,1].
	    return 70.0 * (n0 + n1 + n2);
	  }
}
