package com.troutmoon.games.flatspace.physics;

public class Physics {
	
	private static double gravitatorsx;
	private static double gravitatorsy;
	
	private static double gravity;

	public static void setGravitatorsCoordinates(double x, double y) {
		gravitatorsx = x;
		gravitatorsy = y;
	}

	public static void setGravitatorsGravity(double grav) {
		gravity = grav;
	}

	public static double getGravityVectorOnX(double shipx, double shipy) {

		//compute the distance from the ship to the gravitator:
		double xdelta = gravitatorsx - shipx;
		double ydelta = gravitatorsy - shipy;
		
		double radius = Math.sqrt((xdelta * xdelta) + (ydelta * ydelta)) ;
		double accell = (gravity / (radius * radius));

		//smoothing effort:
//		radius = radius;

		return (xdelta / radius) * accell;

	}

	public static double getGravityVectorOnY(double shipx, double shipy) {

		//compute the distance from the ship to the gravitator:
		double xdelta = gravitatorsx - shipx;
		double ydelta = gravitatorsy - shipy;
		
		double radius = Math.sqrt((xdelta * xdelta) + (ydelta * ydelta)) ;
		
		//smoothing effort:
//		radius = radius;
		
		double accell = (gravity / (radius * radius));
		
		return (ydelta / radius) * accell;

	}


}
