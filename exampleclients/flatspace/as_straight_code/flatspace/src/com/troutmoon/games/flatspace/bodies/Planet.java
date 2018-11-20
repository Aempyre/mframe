package com.troutmoon.games.flatspace.bodies;
import java.awt.*;
import java.awt.geom.*;

import com.troutmoon.games.flatspace.engine.FlatSpacePanel;
import com.troutmoon.games.flatspace.physics.Engine;


public class Planet {
	
	private FlatSpacePanel parent_panel;
	
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
	
	// Rotation stuff
	@SuppressWarnings("unused")
	private double  rotation 	= 0;		// Maintained in radians
    AffineTransform rotationAT	= new AffineTransform();


	private Engine gravityEngine;

	private double upperLeftX;

	private double upperLeftY;

	private double centerOfMassX;

	private double centerOfMassY;
	    
    // Shape stuff
    public static int PLANET_DIAMETER = 100;
    public static int PLANET_RADIUS   =  50;

	public static boolean TRANSPARENT = false;  // by default we are not a gas giant
	
	public static Color PLANET_COLOR = Color.BLUE;   // default to blue

//	private Polygon planet_shape;
//	private Polygon exhaust_plume_shape;
//	private boolean exhaust_plume_on  = false;
//	private int     exhaust_plume_ctr = 0;
//	private int     exhaust_plume_duration	= 1500;
	
	
	public Planet(double ix, double iy, Engine gravity_engine, FlatSpacePanel parentPanel) {
		
		this.parent_panel = parentPanel;
		
		centerOfMassX = ix;
		centerOfMassY = iy;
		
	    upperLeftX = centerOfMassX - PLANET_RADIUS;  // ensure center of planet as drawn equals the center of gravity
	    upperLeftY = centerOfMassY - PLANET_RADIUS;  // needed as the drawing takes place from upper left of bounding box
	    											   // and NOT from the center of the drawing itself!
	    
	    this.gravityEngine = gravity_engine;

	    startAT.translate(upperLeftX, upperLeftY);
	    
	      if (TRANSPARENT) {
	    	  int red 	= PLANET_COLOR.getRed();
	    	  int green = PLANET_COLOR.getGreen();
	    	  int blue  = PLANET_COLOR.getBlue();
	    	  
	    	  int alpha_factor = 195;				// 0-255
	    	  
	    	  PLANET_COLOR = new Color(red, green, blue, alpha_factor);
	      }

	}

	public void draw(Graphics g) {	
		
      Graphics2D g2d 			= (Graphics2D)g;
      
      AffineTransform origAT 	= g2d.getTransform();  
      
      // the following 'startAT' transform will let me move the planet around too (example:  to follow the ship as it leaves the area)
      // or for keeping it in the center of the screen as we scale (considering it draws from upper left not center of mass)
      
      if (timeToScale()) {
    	  
    	  upperLeftX = centerOfMassX - PLANET_RADIUS / parent_panel.getScale_factor();  // ensure center of planet as drawn equals the center of gravity
    	  upperLeftY = centerOfMassY - PLANET_RADIUS / parent_panel.getScale_factor();  // needed as the drawing takes place from upper left of bounding box
    	    											   // and NOT from the center of the drawing itself!
          startAT = new AffineTransform();
          
          startAT.translate(upperLeftX, upperLeftY);

      }
      
	  g2d.transform(startAT);   // This translates the planet to somehwere so the fillOval below can go to x=0, y=0
	  
      g2d.transform(parent_panel.getScalingAT());
	  
      
      g2d.setColor(PLANET_COLOR);
      
      g2d.fillOval( 0, 0, PLANET_DIAMETER, PLANET_DIAMETER);

//      g2d.draw(planet_shape);  // s/b a sprite with a nice graphic
      
      g2d.setTransform(origAT);
	}

	double saved_scaling_factor = 1;
	
	private boolean timeToScale() {
		
		double current_game_scaling_factor = parent_panel.getScale_factor();

		if (current_game_scaling_factor == saved_scaling_factor) {
			return false;
		} else {
			saved_scaling_factor = current_game_scaling_factor;
			return true;
		}
	}  

} 

