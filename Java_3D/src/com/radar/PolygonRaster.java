package com.radar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.LinkedList;

//Goal of this class is to turn the 2d points of the polygons into pixels to render on screen while also accounting for depth
public class PolygonRaster {
	BlockFace blockFace;
	LinkedList<BlockFace> faces = new LinkedList<BlockFace>();
	//Pixels array is 3d to include x,y and depth with color
	float [][][] pixels;
	int i, i2 = 0;
	BufferedImage canvas;
	int [] xCoords,yCoords,zCoords;
	int xMin, xMax, yMin, yMax, x, y, colorI, ySum, xSum;
	float depth = 0;
	boolean gy1, gy2, gy3, gy4, ly1, ly2, ly3, ly4;
	boolean gx1, gx2, gx3, gx4, lx1, lx2, lx3, lx4;
	
	PolygonRaster(){
		//For each pixel the first value is the color and second is the depth
		pixels = new float[Main.HEIGHT][Main.WIDTH][2];
	}
	public void addFace(BlockFace blockFace){
		faces.add(blockFace);
	}
	public void render(Graphics g){
		
		for (BlockFace face : faces){
			xCoords = face.getXCoords();
			yCoords = face.getYCoords();
			zCoords = face.getZCoords();
//			colorI = 5;
			if (face.getColor() == Color.YELLOW){
				colorI = 0;
			}if (face.getColor() == Color.BLUE){
				colorI = 1;
			}if (face.getColor() == Color.RED){
				colorI = 2;
			}if (face.getColor() == Color.ORANGE){
				colorI = 3;
			}if (face.getColor() == Color.CYAN){
				colorI = 4;
			}if (face.getColor() == Color.GREEN){
				colorI = 5;
			}
			xSum = 0;
			ySum = 0;
			xMin = xCoords[0];
			xMax = xCoords[0];
			yMin = yCoords[0];
			yMax = yCoords[0];
			for (int x: xCoords){
				if (x > xMax){
					xMax = x;
				}if (x < xMin){
					xMin = x;
				}
				xSum += x;
			}for (int y: yCoords){
				if (y > yMax){
					yMax = y;
				}if (y < yMin){
					yMin = y;
				}
				ySum += y;
			}
			i = 0;
			
			x = xMin;
			y = yMin;
			
			if (x < 0){
				x = 0;
			}if (y < 0){
				y = 0;
			}
			//Somewhere the functions aren't quite right might have messed up the slope calculations
			
			gy1 = false; gy2 = false; gy3 = false; gy4 = false;
			ly1 = false; ly2 = false; ly3 = false; ly4 = false;
			
			gx1 = false; gx2 = false; gx3 = false; gx4 = false;
			lx1 = false; lx2 = false; lx3 = false; lx4 = false;
			
			if (xCoords[1]-xCoords[0] != 0 && (ySum/4) > (((yCoords[1]-yCoords[0])/(xCoords[1]-xCoords[0]))*((xSum/4)-xCoords[0]))+yCoords[0]){
				gy1 = true;
			}else if(xCoords[1]-xCoords[0] != 0){
				ly1 = true;
			}else if (yCoords[1]-yCoords[0] != 0 && (xSum/4) > (((xCoords[1]-xCoords[0])/(yCoords[1]-yCoords[0]))*((ySum/4)-yCoords[0]))+xCoords[0]){
				gx1 = true;
			}else if (yCoords[1]-yCoords[0] != 0){
				lx1 = true;
			}
			
			if (xCoords[2]-xCoords[1] != 0 && (ySum/4) > (((yCoords[2]-yCoords[1])/(xCoords[2]-xCoords[1]))*((xSum/4)-xCoords[1]))+yCoords[1]){
				gy2 = true;
			}else if (xCoords[2]-xCoords[1] != 0){
				ly2 = true;
			}else if (yCoords[2]-yCoords[1] != 0 && (xSum/4) > (((xCoords[2]-xCoords[1])/(yCoords[2]-yCoords[1]))*((ySum/4)-yCoords[1]))+xCoords[1]){
				gx2 = true;
			}else if (yCoords[2]-yCoords[1] != 0){
				lx2 = true;
			}
			
			if (xCoords[3]-xCoords[2] != 0 && (ySum/4) > (((yCoords[3]-yCoords[2])/(xCoords[3]-xCoords[2]))*((xSum/4)-xCoords[2]))+yCoords[2]){
				gy3 = true;
			}else if (xCoords[3]-xCoords[2] != 0){
				ly3 = true;
			}else if (yCoords[3]-yCoords[2] != 0 && (xSum/4) > (((xCoords[3]-xCoords[2])/(yCoords[3]-yCoords[2]))*((ySum/4)-yCoords[2]))+xCoords[2]){
				gx3 = true;
			}else if (yCoords[3]-yCoords[2] != 0){
				lx3 = true;
			}
			
			if (xCoords[3]-xCoords[0] != 0 && (ySum/4) > (((yCoords[3]-yCoords[0])/(xCoords[3]-xCoords[0]))*((xSum/4)-xCoords[0]))+yCoords[0]){
				gy4 = true;
			}else if (xCoords[3]-xCoords[0] != 0){
				ly4 = true;
			}else if (yCoords[3]-yCoords[0] != 0 && (xSum/4) > (((xCoords[3]-xCoords[0])/(yCoords[3]-yCoords[0]))*((ySum/4)-yCoords[0]))+xCoords[0]){
				gx4 = true;
			}else if (yCoords[3]-yCoords[0] != 0){
				lx4 = true;
			}
			while (y < yMax && y < Main.HEIGHT){
				while (x < xMax && x < Main.WIDTH){
					//0,1 1,2 2,3 3,0 Bounding line indices
					
//					if ((xCoords[2]-xCoords[1] != 0 && y == (int) ((((yCoords[1]-yCoords[2])/(xCoords[1]-xCoords[2]))*(x-xCoords[2]))+yCoords[2])) || (yCoords[1]-yCoords[2] != 0 && x == ((xCoords[1]-xCoords[2])/(yCoords[1]-yCoords[2]))*(y-yCoords[1])+yCoords[1])){
					
//					if (((xCoords[1]-xCoords[0] != 0 && (((yCoords[0] == yMin || yCoords[1] == yMin) && y > ((yCoords[1]-yCoords[0])/(xCoords[1]-xCoords[0]))*(x-xCoords[0])+yCoords[0]) || ((yCoords[0] == yMax || yCoords[1] == yMax) && y < ((yCoords[1]-yCoords[0])/(xCoords[1]-xCoords[0]))*(x-xCoords[0])+yCoords[0])))
//						|| (yCoords[1]-yCoords[0] != 0 && (((xCoords[0] == xMin || xCoords[1] == xMin) && x > ((xCoords[1]-xCoords[0])/(yCoords[1]-yCoords[0]))*(y-yCoords[0])+xCoords[0]) || ((xCoords[0] == xMax || xCoords[1] == xMax) && x < ((xCoords[1]-xCoords[0])/(yCoords[1]-yCoords[0]))*(y-yCoords[0])+xCoords[0]))))
//							
//						&& ((xCoords[2]-xCoords[1] != 0 && (((yCoords[2] == yMin || yCoords[1] == yMin) && y > ((yCoords[2]-yCoords[1])/(xCoords[2]-xCoords[1]))*(x-xCoords[1])+yCoords[1]) || ((yCoords[2] == yMax || yCoords[1] == yMax) && y < ((yCoords[2]-yCoords[1])/(xCoords[2]-xCoords[1]))*(x-xCoords[1])+yCoords[1])))
//						|| (yCoords[2]-yCoords[1] != 0 && (((xCoords[2] == xMin || xCoords[1] == xMin) && x > ((xCoords[2]-xCoords[1])/(yCoords[2]-yCoords[1]))*(y-yCoords[1])+xCoords[1]) || ((xCoords[2] == xMax || xCoords[1] == xMax) && x < ((xCoords[2]-xCoords[1])/(yCoords[2]-yCoords[1]))*(y-yCoords[1])+xCoords[1]))))
//						
//						&& ((xCoords[3]-xCoords[2] != 0 && (((yCoords[2] == yMin || yCoords[3] == yMin) && y > ((yCoords[3]-yCoords[2])/(xCoords[3]-xCoords[2]))*(x-xCoords[2])+yCoords[2]) || ((yCoords[2] == yMax || yCoords[3] == yMax) && y < ((yCoords[3]-yCoords[2])/(xCoords[3]-xCoords[2]))*(x-xCoords[2])+yCoords[2])))
//						|| (yCoords[3]-yCoords[2] != 0 && (((xCoords[2] == xMin || xCoords[3] == xMin) && x > ((xCoords[3]-xCoords[2])/(yCoords[3]-yCoords[2]))*(y-yCoords[2])+xCoords[2]) || ((xCoords[2] == xMax || xCoords[3] == xMax) && x < ((xCoords[3]-xCoords[2])/(yCoords[3]-yCoords[2]))*(y-yCoords[2])+xCoords[2]))))
//						
//						&& ((xCoords[3]-xCoords[0] != 0 && (((yCoords[0] == yMin || yCoords[3] == yMin) && y > ((yCoords[3]-yCoords[0])/(xCoords[3]-xCoords[0]))*(x-xCoords[0])+yCoords[0]) || ((yCoords[0] == yMax || yCoords[3] == yMax) && y < ((yCoords[3]-yCoords[0])/(xCoords[3]-xCoords[0]))*(x-xCoords[0])+yCoords[0])))
//						|| (yCoords[3]-yCoords[0] != 0 && (((xCoords[0] == xMin || xCoords[3] == xMin) && x > ((xCoords[3]-xCoords[0])/(yCoords[3]-yCoords[0]))*(y-yCoords[0])+xCoords[0]) || ((xCoords[0] == xMax || xCoords[3] == xMax) && x < ((xCoords[3]-xCoords[0])/(yCoords[3]-yCoords[0]))*(y-yCoords[0])+xCoords[0]))))){
					if ( ((gy1 && y >= (((yCoords[1]-yCoords[0])/(xCoords[1]-xCoords[0]))*(x-xCoords[0]))+yCoords[0]) || (ly1 && y <= (((yCoords[1]-yCoords[0])/(xCoords[1]-xCoords[0]))*(x-xCoords[0]))+yCoords[0])
							|| (gx1 && x >= (((xCoords[1]-xCoords[0])/(yCoords[1]-yCoords[0]))*(y-yCoords[0]))+xCoords[0]) || (lx1 && x <= (((xCoords[1]-xCoords[0])/(yCoords[1]-yCoords[0]))*(y-yCoords[0]))+xCoords[0]))
							
							&& ((gy2 && y >= (((yCoords[2]-yCoords[1])/(xCoords[2]-xCoords[1]))*(x-xCoords[1]))+yCoords[1]) || (ly2 && y <= (((yCoords[2]-yCoords[1])/(xCoords[2]-xCoords[1]))*(x-xCoords[1]))+yCoords[1])
							|| (gx2 && x >= (((xCoords[2]-xCoords[1])/(yCoords[2]-yCoords[1]))*(y-yCoords[1]))+xCoords[1]) || (lx2 && x <= (((xCoords[2]-xCoords[1])/(yCoords[2]-yCoords[1]))*(y-yCoords[1]))+xCoords[1]))
							
							&& ((gy3 && y >= (((yCoords[3]-yCoords[2])/(xCoords[3]-xCoords[2]))*(x-xCoords[2]))+yCoords[2]) || (ly3 && y <= (((yCoords[3]-yCoords[2])/(xCoords[3]-xCoords[2]))*(x-xCoords[2]))+yCoords[2])
							|| (gx3 && x >= (((xCoords[3]-xCoords[2])/(yCoords[3]-yCoords[2]))*(y-yCoords[2]))+xCoords[2]) || (lx3 && x <= (((xCoords[3]-xCoords[2])/(yCoords[3]-yCoords[2]))*(y-yCoords[2]))+xCoords[2]))
							
							&& ((gy4 && y >= (((yCoords[3]-yCoords[0])/(xCoords[3]-xCoords[0]))*(x-xCoords[0]))+yCoords[0]) || (ly4 && y <= (((yCoords[3]-yCoords[0])/(xCoords[3]-xCoords[0]))*(x-xCoords[0]))+yCoords[0])
							|| (gx4 && x >= (((xCoords[3]-xCoords[0])/(yCoords[3]-yCoords[0]))*(y-yCoords[0]))+xCoords[0]) || (lx4 && x <= (((xCoords[3]-xCoords[0])/(yCoords[3]-yCoords[0]))*(y-yCoords[0]))+xCoords[0]))
							){
						if (xCoords[i]-xCoords[i+1] != 0 && yCoords[i]-yCoords[i+1] != 0){
							depth = (((zCoords[i]-zCoords[i+1])/(xCoords[i]-xCoords[i+1]))*(x-xCoords[i])) + (((zCoords[i]-zCoords[i+1])/(yCoords[i]-yCoords[i+1]))*(y-yCoords[i])) + zCoords[i];
						}else if (xCoords[i]-xCoords[i+1] == 0){
							depth = ((zCoords[i]-zCoords[i+1])/(yCoords[i]-yCoords[i+1]))*(y-yCoords[i]) + zCoords[i];
						}else if (yCoords[i]-yCoords[i+1] == 0){
							depth = ((zCoords[i]-zCoords[i+1])/(xCoords[i]-xCoords[i+1]))*(x-xCoords[i]) + zCoords[i];
						}
						
//						if (((gy1 && y >= (((yCoords[1]-yCoords[0])/(xCoords[1]-xCoords[0]))*(x-xCoords[0]))+yCoords[0]))) {
//							colorI = 1;
//						}
//						if (((gy2 && y >= (((yCoords[2]-yCoords[1])/(xCoords[2]-xCoords[1]))*(x-xCoords[1]))+yCoords[1]) || (ly2 && y <= (((yCoords[2]-yCoords[1])/(xCoords[2]-xCoords[1]))*(x-xCoords[1]))+yCoords[1])
//								|| (gx2 && x >= (((xCoords[2]-xCoords[1])/(yCoords[2]-yCoords[1]))*(y-yCoords[1]))+xCoords[1]) || (lx2 && x <= (((xCoords[2]-xCoords[1])/(yCoords[2]-yCoords[1]))*(y-yCoords[1]))+xCoords[1]))) {
//							colorI = 2;
//						}

//						pixels[y][x][0] = colorI;
//						pixels[y][x][1] = depth;
						try{
							if (depth < pixels[y][x][1]){
								pixels[y][x][0] = colorI;
								pixels[y][x][1] = depth;
							}
						}catch(Exception e){
							pixels[y][x][0] = colorI;
							pixels[y][x][1] = depth;
							System.out.println("Tried");
						}
					}
					x++;
				}
				if (xMin < 0){
					x = 0;
				}else{
					x = xMin;
				}
				y++;
			}
		}
		faces = new LinkedList<BlockFace>();
		canvas = new BufferedImage(Main.WIDTH, Main.HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		WritableRaster raster = canvas.getRaster();
		int[] color0 = {128,128,128};
		int[] color1 = {0,0,255};
		int[] color2 = {255,0,0};
		int[] color3 = {255,128,128};
		int[] color4 = {128,128,255};
		int[] color5 = {0,255,0};
		i = 0;
		i2 = 0;
		while (i < Main.HEIGHT){
			while (i2 < Main.WIDTH){
				if (pixels[i][i2] != null){
					if (pixels[i][i2][0] == 0){
						raster.setPixel(i2, i, color0);
					}if (pixels[i][i2][0] == 1){
						raster.setPixel(i2, i, color1);
					}if (pixels[i][i2][0] == 2){
						raster.setPixel(i2, i, color2);
					}if (pixels[i][i2][0] == 3){
						raster.setPixel(i2, i, color3);
					}if (pixels[i][i2][0] == 4){
						raster.setPixel(i2, i, color4);
					}if (pixels[i][i2][0] == 5){
						raster.setPixel(i2, i, color5);
					}
				}
				i2++;
			}
			i++;
			i2 = 0;
		}
		pixels = new float[Main.HEIGHT][Main.WIDTH][2];
	    Graphics2D g2 = (Graphics2D) g;
	    g2.drawImage(canvas, null, null);
	}
}
