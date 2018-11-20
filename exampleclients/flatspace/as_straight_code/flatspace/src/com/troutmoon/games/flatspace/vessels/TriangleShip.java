package com.troutmoon.games.flatspace.vessels;
import java.awt.*;
import java.awt.geom.*;

import com.troutmoon.games.flatspace.engine.FlatSpacePanel;
import com.troutmoon.games.flatspace.physics.Engine;


public class TriangleShip {
	
	private FlatSpacePanel parent_panel;
	
	// Translation stuff
	private double  xdelta  	= 0;
	private double  ydelta  	= 0;
	private double  xmovement 	= 0;
	private double  ymovement 	= 0;
	AffineTransform xlationCombinedAT 	= new AffineTransform();
	
	AffineTransform startPositionAT 	= new AffineTransform();
	AffineTransform xlateFromRotateAT 	= new AffineTransform();
	
//	boolean time_to_scale 				= true;  //TODO complete scaling experiment
	
	// Rotation stuff
	private double  rotation 	= 0;		// Maintained in radians
    AffineTransform rotationAT	= new AffineTransform();
	    
    // TODO s/b from the ship heirachy above or in some associated config class but for now...
    private double  rotation_neg_null = -0.14;
    private double  rotation_pos_null = 0.14;
	    
    // Ship stuff
	private Polygon ship_shape;
	private Polygon exhaust_plume_shape;
	private boolean exhaust_plume_on  		= false;
	private int     exhaust_plume_ctr 		= 0;
	private int     exhaust_plume_duration	= 7;
	
	private int screen_width	= 1024;  //TODO default here for now -- set from graphics context?
	private int screen_height	= 768;
	private Engine gravityEngine;
	

	public TriangleShip(int h_len, int h_wid, double ix, double iy, Engine gravity_engine, FlatSpacePanel parentPanel) {
		
		this.parent_panel = parentPanel;
		this.screen_width = parentPanel.getSW();
		this.screen_height = parentPanel.getSH();
		
		this.gravityEngine = gravity_engine;
		
		int[] xpoints 	= { (-h_len -2), (h_len -2), (h_len -2) };
		int[] ypoints 	= {          0 ,    -h_wid ,  h_wid     };
		int   numpoints = 3;
		ship_shape 		= new Polygon(xpoints, ypoints, numpoints);
		
		int[] xp2 = { (h_len -1), (h_len +3), (h_len -1), (h_len +6), (h_len -1), (h_len +3), (h_len -1) };
		int[] yp2 = {(-h_wid +1),(-h_wid +2),(-h_wid +3), (0)       , (h_wid -3), (h_wid -1), (h_wid -1) };
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
		
//		xdelta = axial_accel_change / FlatSpacePanel.scalar;
//		ydelta = lateral_accel_change / FlatSpacePanel.scalar;
		xdelta = axial_accel_change;
		ydelta = lateral_accel_change;
		
	}
		
	public void update_position() {
		
		//TODO build on this scaling experiment
//		xdelta = xdelta / FlatSpacePanel.scalar;
//		ydelta = ydelta / FlatSpacePanel.scalar;
		
//		xmovement = xmovement / FlatSpacePanel.scalar;
//		ymovement = ymovement / FlatSpacePanel.scalar;
		
		
		//---------------------------------------
		
		if ((xdelta == 0) && (ydelta == 0)) {
		} else {
			double[] matemp = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
			rotationAT.getMatrix(matemp);
			
			xlateFromRotateAT.setToTranslation((xdelta * matemp[0]) * matemp[0], (xdelta * matemp[1]));
			
			xmovement = xmovement + (xdelta * matemp[0]);
			ymovement = ymovement + (xdelta * matemp[1]); 
			
			//TODO someday ad in lateral accels -- all the above was longditudinal
			
		}	
		
		xlationCombinedAT.translate(xmovement, ymovement);

		// TODO Wrap around the screen (as a supplement to explicitly commanded scaling?) 
		
		double margin = 5;
		
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
		
//		double old_scale_value;

		if ((xTrans + xStart) >= screen_width - margin) {
			if (parent_panel.getScale_factor() < parent_panel.maxScale()) {
				parent_panel.setScale_factor(parent_panel.getScale_factor() + parent_panel.scaleIncrement());
				xTrans = xTrans / parent_panel.getScale_factor();
				
				xmovement = xmovement / parent_panel.getScale_factor();

				parent_panel.scaleTheGame();
			}
//		} else { // zoom in if we can (TODO:  move to parent moving object class so it can check this for a list of objects before acting)
//			if ((xTrans + xStart) <= screen_width/4 - margin) {
//				if ( parent_panel.getScale_factor() > parent_panel.minScale()) {
//					old_scale_value = parent_panel.getScale_factor();
//					parent_panel.setScale_factor(old_scale_value - parent_panel.scaleIncrment());
//					xTrans = xTrans * (old_scale_value / parent_panel.getScale_factor());
//					parent_panel.scaleTheGame();
//				}
//			}
		}
		if ((xTrans + xStart) <= 0 + margin ) {
			if (parent_panel.getScale_factor() < parent_panel.maxScale()) {
				parent_panel.setScale_factor(parent_panel.getScale_factor() + parent_panel.scaleIncrement());
				xTrans = xTrans / parent_panel.getScale_factor();
				
				xmovement = xmovement / parent_panel.getScale_factor();
				
				parent_panel.scaleTheGame();
			}
//		} else { // zoom in if we can (TODO:  move to parent moving object class so it can check this for a list of objects before acting)
//			if ((xTrans + xStart) >= screen_width/4 + margin) {
//				if ( parent_panel.getScale_factor() > parent_panel.minScale()) {
//					old_scale_value = parent_panel.getScale_factor();
//					parent_panel.setScale_factor(old_scale_value - parent_panel.scaleIncrment());
//					xTrans = xTrans * (old_scale_value / parent_panel.getScale_factor());
//					parent_panel.scaleTheGame();
//				}
//			}
		}

		if ((yTrans + yStart) >= screen_height - margin) {
			if (parent_panel.getScale_factor() < parent_panel.maxScale()) {
				parent_panel.setScale_factor(parent_panel.getScale_factor() + parent_panel.scaleIncrement());
				yTrans = yTrans / parent_panel.getScale_factor();
				
				ymovement = ymovement / parent_panel.getScale_factor();

				parent_panel.scaleTheGame();
			}
//		} else { // zoom in if we can (TODO:  move to parent moving object class so it can check this for a list of objects before acting)
//			if ((yTrans + yStart) <= screen_height/4 - margin) {
//				if ( parent_panel.getScale_factor() > parent_panel.minScale()) {
//					old_scale_value = parent_panel.getScale_factor();
//					parent_panel.setScale_factor(old_scale_value - parent_panel.scaleIncrment());
//					yTrans = yTrans * (old_scale_value / parent_panel.getScale_factor());
//					parent_panel.scaleTheGame();
//				}
//			}
		}
		if ((yTrans + yStart) <= 0 + margin ) {
			if (parent_panel.getScale_factor() < parent_panel.maxScale()) {
				parent_panel.setScale_factor(parent_panel.getScale_factor() + parent_panel.scaleIncrement());
				yTrans = yTrans / parent_panel.getScale_factor();
				
				ymovement = ymovement / parent_panel.getScale_factor();

				parent_panel.scaleTheGame();
			}
		} else { // zoom in if we can (TODO:  move to parent moving object class so it can check this for a list of objects before acting)
//			if ((yTrans + yStart) >= screen_height/4 + margin) {
//				if ( parent_panel.getScale_factor() > parent_panel.minScale()) {
//					old_scale_value = parent_panel.getScale_factor();
//					parent_panel.setScale_factor(old_scale_value - parent_panel.scaleIncrment());
//					yTrans = yTrans * (old_scale_value / parent_panel.getScale_factor());
//					parent_panel.scaleTheGame();
//				}
//			}
		}

		
//		if ((xTrans + xStart) >= screen_width) xTrans = xTrans - screen_width;
//		if ((xTrans + xStart) <= 0           ) xTrans = xTrans + screen_width;
//
//		if ((yTrans + yStart) >= screen_height) yTrans = yTrans - screen_height;
//		if ((yTrans + yStart) <= 0            ) yTrans = yTrans + screen_height;
		
		
//		System.out.println(" ---------------- ");
//		System.out.println("xTrans " + xTrans);
//		System.out.println("xStart " + xStart);
//		System.out.println("yTrans " + yTrans);
//		System.out.println("yStart " + yStart);

		// Plug in the gravity influence here, before the following translate...
//		xmovement = xmovement + gravityEngine.getGravityVectorOnX((xTrans + xStart), (yTrans + yStart), FlatSpacePanel.scalar);
//		ymovement = ymovement + gravityEngine.getGravityVectorOnY((xTrans + xStart), (yTrans + yStart), FlatSpacePanel.scalar);

		//		double xincrement = gravityEngine.getGravityVectorOnX((xTrans + xStart), (yTrans + yStart));  //scaling experiment
//		double yincrement = gravityEngine.getGravityVectorOnY((xTrans + xStart), (yTrans + yStart));  //scaling experiment
		
//		xmovement = xmovement + xincrement / (FlatSpacePanel.scalar * FlatSpacePanel.scalar);
//		ymovement = ymovement + yincrement / (FlatSpacePanel.scalar * FlatSpacePanel.scalar);

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
	    
//	    if (time_to_scale) {
//		    scalingAT.scale((1/FlatSpacePanel.scalar), (1/FlatSpacePanel.scalar));
//		    time_to_scale = false;
//	    }
	}

	public void draw(Graphics g) {	
		
      Graphics2D g2d = (Graphics2D)g;
      
      AffineTransform origAT = g2d.getTransform();  
      
	  g2d.transform(startPositionAT);
	  
      g2d.transform(xlationCombinedAT);
      
      g2d.transform(rotationAT);
      
      g2d.transform(parent_panel.getScalingAT());
      
      if (exhaust_plume_on == true) { 
          g2d.setColor(Color.YELLOW);
    	  g2d.draw(exhaust_plume_shape);
    	  exhaust_plume_ctr--;
    	  if (exhaust_plume_ctr < 0) {
    		  exhaust_plume_on = false;
    	  }
      }
     
      g2d.setColor(Color.ORANGE);

      g2d.fillPolygon(ship_shape);

      g2d.setTransform(origAT);
	}  

} 

