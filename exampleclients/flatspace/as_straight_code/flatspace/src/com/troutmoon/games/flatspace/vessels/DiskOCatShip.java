package com.troutmoon.games.flatspace.vessels;

import java.awt.*;
import java.awt.geom.*;

import com.troutmoon.games.flatspace.engine.FlatSpacePanel;
import com.troutmoon.games.flatspace.physics.Engine;


public class DiskOCatShip {
	
	private FlatSpacePanel parent_panel;
	
	// Translation stuff
	private double  xdelta  	= 0;
	private double  ydelta  	= 0;
	private double  xmovement 	= 0;
	private double  ymovement 	= 0;
	AffineTransform xlationCombinedAT 	= new AffineTransform();
	
	AffineTransform startPositionAT 	= new AffineTransform();
	AffineTransform xlateFromRotateAT 	= new AffineTransform();
	
	// Rotation stuff
	private double  rotation 	= 0;		// Maintained in radians
    AffineTransform rotationAT	= new AffineTransform();
    
    // TODO s/b from the ship heirachy above or in some associated config class but for now...
    private double  rotation_neg_null = -0.11;
    private double  rotation_pos_null = 0.11;
	    
    // Ship stuff
//	private Ellipse2D.Double 		ship_shape;
//	private RoundRectangle2D.Double	ship_shape2;
//	private RoundRectangle2D.Double	ship_shape3;
	
	private Polygon 		ship_shape;
	
	private Polygon exhaust_plume_shape;
	private boolean exhaust_plume_on  		= false;
	private int     exhaust_plume_ctr 		= 0;
	private int     exhaust_plume_duration	= 7;
	
	
	private int screen_width	= 1024;  //TODO default here for now -- set from graphics context?
	private int screen_height	= 768;
	private Engine gravityEngine;
	
	
	public DiskOCatShip(double ix, double iy, Engine gravity_engine, FlatSpacePanel parentPanel) {

		this.parent_panel = parentPanel;
		this.screen_width = parentPanel.getSW();
		this.screen_height = parentPanel.getSH();
		
		this.gravityEngine = gravity_engine;
		
		int[] xpoints = { -6,-3, 0, 1, 4, 8, 5, 5, 8, 4, 1, 0,-3,-6 };
		int[] ypoints = {  1, 4, 5, 5, 5, 7, 2,-2,-7,-5,-5,-5,-4,-1 };
		int   numpoints = 14;
		ship_shape 		= new Polygon(xpoints, ypoints, numpoints);
				
//		ship_shape 		= new Ellipse2D.Double(ix, iy, 9, 9);
//		ship_shape2     = new RoundRectangle2D.Double(ix-3, iy+1, 2, 2, 1, 1);
//		ship_shape3     = new RoundRectangle2D.Double(ix-3, iy-1, 2, 2, 1, 1);
		
		int[] xp2 = { 10, 14, 10, 18, 10, 14, 10 };
		int[] yp2 = {-3, -2, -1, 0, 1,  2, 3 };
		int   np2 = 7;
		exhaust_plume_shape = new Polygon(xp2, yp2, np2);
		
	    startPositionAT.translate(ix, iy);
	    rotationAT.setToRotation(0);
	    xlationCombinedAT.setToTranslation(0,0);
	}

	public void turn(double rotation_change) {
		rotation = rotation + rotation_change;
	}

	public void go(double axial_accel_change, double lateral_accel_change) {
		
		if (axial_accel_change < 0) {
			exhaust_plume_on = true;
			exhaust_plume_ctr = exhaust_plume_duration;
		}
		
		xdelta = axial_accel_change;
		ydelta = lateral_accel_change;
		
	}
		
	public void update_position() {
		
		if ((xdelta == 0) && (ydelta == 0)) {
		} else {
			double[] matemp = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
			rotationAT.getMatrix(matemp);
			
			xlateFromRotateAT.setToTranslation((xdelta * matemp[0]) * matemp[0], (xdelta * matemp[1]));
			
			xmovement = xmovement + (xdelta * matemp[0]);
			ymovement = ymovement + (xdelta * matemp[1]);
			
//			// wraparound screen	TODO
			// xmovement and ymovement are rates, not displacements.
			// >> need to get and add up the actual x, y displaceents
			// >> from the initial position and the ship movement transform,
			// >> and check those for screen edges...
			// >> get the screen edges values from the graphics system!
			// >> ALSO:
			// >> Set up a scaling transform over the whole game display.
			// ...use this to scale down as edges approached, to a certain point
			//    then beyond that point we transition to a whole new plot, whether
			//    it is the cislunar nav plot, or the solar system nav plot (or 
			//    interstellar! -- or on the other side:  earth, moon, or other planetary
			//    entry, descent, and landing mode(s).
			
//			if (xmovement < 0)    xmovement = 1024;
//			if (xmovement > 1024) xmovement = 1;
//			if (ymovement < 0)    ymovement = 768;
//			if (ymovement > 768)  ymovement = 1;
//			
			
			
/*			// near zero damping  TODO
 * >> this impl wrong, instead of a constant to test for close to 
 *    center use a constant modified in the same way as the displacement
 *    itself (ie. use the sine and cosine as above)
			if (( xmovement > -0.005) && ( xmovement < 0.005)) xmovement = 0;
			if (( ymovement > -0.005) && ( ymovement < 0.005)) ymovement = 0;
*/			
		}	
		
		xlationCombinedAT.translate(xmovement, ymovement);

		// TODO Wrap around the screen (as a supplement to explicitly commanded scaling?) 
		
		double xStart =   startPositionAT.getTranslateX();
		double yStart =   startPositionAT.getTranslateY();
		
		double xTrans = xlationCombinedAT.getTranslateX();
		double yTrans = xlationCombinedAT.getTranslateY();
		
//		System.out.println(" ================ ");
//		System.out.println("xTrans " + xTrans);
//		System.out.println("xStart " + xStart);
//		System.out.println("yTrans " + yTrans);
//		System.out.println("yStart " + yStart);

		//		double xNew   = xTrans;
//		double yNew   = yTrans;
		
		if ((xTrans + xStart) >= screen_width) xTrans = xTrans - screen_width;
		if ((xTrans + xStart) <= 0           ) xTrans = xTrans + screen_width;

		if ((yTrans + yStart) >= screen_height) yTrans = yTrans - screen_height;
		if ((yTrans + yStart) <= 0            ) yTrans = yTrans + screen_height;
		
//		System.out.println(" ---------------- ");
//		System.out.println("xTrans " + xTrans);
//		System.out.println("xStart " + xStart);
//		System.out.println("yTrans " + yTrans);
//		System.out.println("yStart " + yStart);

		// Plug in the gravity influence here, before the following translate...
		xmovement = xmovement + gravityEngine.getGravityVectorOnX((xTrans + xStart), (yTrans + yStart));
		ymovement = ymovement + gravityEngine.getGravityVectorOnY((xTrans + xStart), (yTrans + yStart));
		
		xlationCombinedAT.setToTranslation(xTrans, yTrans);

		// End of wrap around -- 
		
		
		xdelta = 0;
		ydelta = 0;
//		exhaust_plume_on = false;
		
		// near zero damping
		if (( rotation > rotation_neg_null) && ( rotation < rotation_pos_null)) rotation = 0;
		
	    rotationAT.rotate( Math.toRadians(rotation));
		
	}

	public void draw(Graphics g) {	
		
      Graphics2D g2d = (Graphics2D)g;
      
      AffineTransform origAT = g2d.getTransform();  
      
	  g2d.transform(startPositionAT);
	  
      g2d.transform(xlationCombinedAT);
      
      g2d.transform(rotationAT);
      
      if (exhaust_plume_on == true) { 
          g2d.setColor(Color.YELLOW);
    	  g2d.draw(exhaust_plume_shape);
    	  exhaust_plume_ctr--;
    	  if (exhaust_plume_ctr < 0) {
    		  exhaust_plume_on = false;
    	  }
      }
     
   //   g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

   //   g2d.setStroke(new BasicStroke(5.0f));
      g2d.setPaint(Color.LIGHT_GRAY);
      
//      g2d.fill(ship_shape);
//      g2d.setPaint(Color.WHITE);
          
      g2d.fillPolygon(ship_shape);
      
//      g2d.setPaint(Color.MAGENTA);
//      g2d.fill(ship_shape2);
//      g2d.setPaint(Color.YELLOW);
//      g2d.draw(ship_shape2);
//      
//      g2d.setPaint(Color.MAGENTA);
//      g2d.fill(ship_shape3);
//      g2d.setPaint(Color.YELLOW);
//      g2d.draw(ship_shape3);
      
      g2d.setTransform(origAT);
	}  

} 

