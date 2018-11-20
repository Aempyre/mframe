package com.troutmoon.games.flatspace.bodies;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.troutmoon.games.flatspace.physics.Engine;


public class StarryBackground {
		
	// Translation stuff
	@SuppressWarnings("unused")
	private double  xdelta  	= 0;
	@SuppressWarnings("unused")
	private double  ydelta  	= 0;
	@SuppressWarnings("unused")
	private double  xdist 	= 0;
	@SuppressWarnings("unused")
	private double  ydist 	= 0;
	AffineTransform xlationRatesAT = new AffineTransform();
	
	AffineTransform startAT = new AffineTransform();
	AffineTransform xlateAT = new AffineTransform();
	
    private BufferedImage starry_night;

	
	public StarryBackground(String file_name) {
		System.out.println("file_name = " + file_name);
	    startAT.translate(0, 0);
	    
	      if ((starry_night = loadImage(file_name)) == null) {
	        System.out.println("Unable to load the background immage null");
	      }
	}
	
	   private BufferedImage loadImage(String file_path) 
	   /* Load the image from <fnm>, returning it as a BufferedImage which is compatible with the graphics device being used.
	      Uses ImageIO.  */
	   {
	     try {
	    	 System.out.println("file_path = " + file_path);
	    	 File whatdir = new File(".");
	    	 System.out.println("what dir. = " + whatdir.getCanonicalPath());
	    	 String new_file_path =  whatdir.getCanonicalPath()
	    	 	+ "/" 
	    	 	+ "resource/image/"
	    	 	+ file_path;
	    	 System.out.println("new_file_path = " + new_file_path);
//	    	 System.out.println("getClass() = " + getClass());
	    	 File newfile = new File(new_file_path);
	         BufferedImage im =  ImageIO.read( newfile );
	       
	       return im;
	       
	       // An image returned from ImageIO in J2SE <= 1.4.2 is 
	       // _not_ a managed image, but is after copying!

//	       int transparency = im.getColorModel().getTransparency();
//	       BufferedImage copy =  gc.createCompatibleImage(
//	                                im.getWidth(), im.getHeight(),
//			                        transparency );
//	       // create a graphics context
//	       Graphics2D g2d = copy.createGraphics();
//	       // g2d.setComposite(AlphaComposite.Src);
//
//	       // reportTransparency(IMAGE_DIR + fnm, transparency);
//
//	       // copy image
//	       g2d.drawImage(im,0,0,null);
//	       g2d.dispose();
//	       return copy;
	     } 
	     catch(IOException e) {
	       System.out.println("Load Image error for " + file_path + ":\n" + e); 
	       return null;
	     }
	  } // end of loadImage() using ImageIO



	public void draw(Graphics g) {	
		
      Graphics2D g2d 			= (Graphics2D)g;
      
      AffineTransform origAT 	= g2d.getTransform();  
      
      // the following transform will let me move the planet around too (example:  to follow the ship as it leaves the area)
	  g2d.transform(startAT);   // This translates the planet to somehwere so the fillOval below can go to x=0, y=0
	  
	  g2d.drawImage(starry_night, 0, 0, null);
      
      g2d.setTransform(origAT);   // restore after my transform and write
	}  

} 

