package com.troutmoon.games.flatspace.physics;

import com.troutmoon.games.flatspace.engine.FlatSpacePanel;

public class Engine {
	
	private FlatSpacePanel parent_panel;
	
	// TODO this engine cannot have static variables / cannot be static as the gravity and radius of the gravitator change
	//      with each gravitating body in question.
	
	
	//TODO next:  Bring in the parent panel and adjust the xdelta and ydelta by the scaling factor
	//            yes this is a hack as am using screen coordinates for the gravity computation,
	//            even when ship coordinates are being scaled.  
	//TODO later but soon:  create x, y ship space coordinates for such use, and keep the seperate screen coordinates.
	//            its not much difference now but real x,y object coordinates will be helpful in ascertaining ship arrivals, dockings
	//             close passages, etc.
	
	private double gravitatorsx;
	private double gravitatorsy;
	
	private double gravity;
	private double surface_radius;

	public Engine(FlatSpacePanel panel) {
		
		this.parent_panel = panel;
		
	}

	public void setGravitatorsCoordinates(double x, double y) {
		gravitatorsx = x;
		gravitatorsy = y;
	}

	public void setGravitatorsGravity(double grav) {
		gravity = grav;
	}

	public void setGravitatorsSurfaceRadius(double surfaceRadius) {
		surface_radius = surfaceRadius;
	}

	public double getGravityVectorOnX(double shipx, double shipy) {

		//compute the distance from the ship to the gravitator:
		double xdelta = gravitatorsx - shipx;
		double ydelta = gravitatorsy - shipy;
				
		double sfac = parent_panel.getScale_factor();
		double ship_radius = Math.sqrt(((xdelta*sfac) * (xdelta*sfac)) + ((ydelta*sfac) * (ydelta*sfac))) ;
		double surfrad = surface_radius / sfac;

		double accell;
		
		if (ship_radius > 0) {
			if (ship_radius <= surfrad) {
				/* apply gravity as if you were flying inside the planet! */
				double inverse_square_subsurface_proportion_difference = ((surfrad * surfrad) / (ship_radius * ship_radius));
				accell = gravity / ((surfrad * surfrad) * inverse_square_subsurface_proportion_difference);
			} else {
				/* apply gravity as if you were flying outside the body */
				 accell = gravity / (ship_radius * ship_radius);
			}
		} else {
			return 0;	// sitting in the center of the gravitating body so acceleration is zero
		}

		return (xdelta / ship_radius) * accell;  // parse out the part that applies to the x-axis

	}

	
	public double getGravityVectorOnY(double shipx, double shipy) {

		//compute the distance from the ship to the gravitator:
		double xdelta = gravitatorsx - shipx;
		double ydelta = gravitatorsy - shipy;
		
		double sfac = parent_panel.getScale_factor();
		double ship_radius = Math.sqrt(((xdelta*sfac) * (xdelta*sfac)) + ((ydelta*sfac) * (ydelta*sfac))) ;
		double surfrad = surface_radius / sfac;

		double accell;		
		
		if (ship_radius > 0) {
			if (ship_radius <= surfrad) {
				/* apply gravity as if you were flying inside the planet! */
				double inverse_square_subsurface_proportion_difference = ((surfrad * surfrad) / (ship_radius * ship_radius));
				accell = gravity / ((surfrad * surfrad) * inverse_square_subsurface_proportion_difference);
			} else {
				/* apply gravity as if you were flying outside the body */
				 accell = gravity / (ship_radius * ship_radius);
			}
		} else {
			return 0;	// sitting in the center of the gravitating body so acceleration is zero
		}

		return (ydelta / ship_radius) * accell;  // parse out the part that applies to the y-axis

	}
//	//Scaleing experiment
//	public double getGravityVectorOnX(double shipx, double shipy, double scaler) {
//
//		//compute the distance from the ship to the gravitator:
//		double xdelta = gravitatorsx - shipx;
//		double ydelta = gravitatorsy - shipy;
//		
//		xdelta = xdelta * scaler;  //NOTE scaling experiment
//		ydelta = ydelta * scaler;  //NOTE scaleing experiment
//		
//		double ship_radius = Math.sqrt((xdelta * xdelta) + (ydelta * ydelta)) ;
//
//		double accell;
//		
//		if (ship_radius > 0) {
//			if (ship_radius <= surface_radius) {
//				/* apply gravity as if you were flying inside the planet! */
//				double inverse_square_subsurface_proportion_difference = ((surface_radius * surface_radius) / (ship_radius * ship_radius));
//				accell = gravity / ((surface_radius * surface_radius) * inverse_square_subsurface_proportion_difference);
//			} else {
//				/* apply gravity as if you were flying outside the body */
//				 accell = gravity / (ship_radius * ship_radius);
//			}
//		} else {
//			return 0;	// sitting in the center of the gravitating body so acceleration is zero
//		}
//
////		double accell = gravity / (ship_radius * ship_radius);
////
////		if (ship_radius <= surface_radius) {
////			/* quick effort to eliminate subsurface slingshot to super speed situations:
////			   by applying gravity as if ship was flying through the planet's interior gravitational field */
////			double inverse_square_subsurface_proportion_difference = ((surface_radius * surface_radius) / (ship_radius * ship_radius));
////			accell = gravity / ((surface_radius * surface_radius) * inverse_square_subsurface_proportion_difference);
////		}
//
//		return (xdelta / ship_radius) * accell;  // parse out the part that applies to the x-axis
//
//	}


//	//end of scaling experiment
//	
//	public double getGravityVectorOnY(double shipx, double shipy) {
//
//		//compute the distance from the ship to the gravitator:
//		double xdelta = gravitatorsx - shipx;
//		double ydelta = gravitatorsy - shipy;
//		
//		double ship_radius = Math.sqrt((xdelta * xdelta) + (ydelta * ydelta)) ;
//		
//		double accell;		
//		
//		if (ship_radius > 0) {
//			if (ship_radius <= surface_radius) {
//				/* apply gravity as if you were flying through the planet! */
//				double inverse_square_subsurface_proportion_difference = ((surface_radius * surface_radius) / (ship_radius * ship_radius));
//				accell = gravity / ((surface_radius * surface_radius) * inverse_square_subsurface_proportion_difference);
//			} else {
//				/* apply gravity as if you were flying above the body */
//				 accell = gravity / (ship_radius * ship_radius);
//			}
//		} else {
//			return 0;	// sitting in the center of the gravitating body so acceleration is zero
//		}
//
//		return (ydelta / ship_radius) * accell;  // parse out the part that applies to the y-axis
//
//	}


}
