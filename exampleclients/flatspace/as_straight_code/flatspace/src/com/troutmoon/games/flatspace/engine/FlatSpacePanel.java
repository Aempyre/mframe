package com.troutmoon.games.flatspace.engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;  		//TODO implement an extension of this noop skeleton to make somthing happen
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;  //TODO implement an extenstion of this noop skeleton to make something happen
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;

import javax.swing.JPanel;

import com.sun.j3d.utils.timer.J3DTimer;
import com.troutmoon.games.flatspace.bodies.Planet;
import com.troutmoon.games.flatspace.bodies.StarryBackground;
import com.troutmoon.games.flatspace.physics.Engine;
import com.troutmoon.games.flatspace.vessels.DiskOCatShip;
import com.troutmoon.games.flatspace.vessels.MultiLineShip;
import com.troutmoon.games.flatspace.vessels.TriangleShip;

@SuppressWarnings("serial")
public class FlatSpacePanel extends JPanel implements Runnable {
		
	private static long MAX_STATS_INTERVAL = 1000000000L;

	/* How many frames with 0 ms delay before animation thread yields */
	private static final int NO_DELAYS_PER_YIELD = 16;

	/* how many frames that may be skipped in any one animation loop -- games state is updated but not rendered */
	private static int MAX_FRAME_SKIPS = 5;  

	private static int NUM_FPS = 10;			// how many FPS values stored for denominator in average computation

	private double scale_factor = 1;   		// start off viewing 'close' orbit, scaling times 1, neutral/natural screen scale
	private AffineTransform scalingAT			= new AffineTransform();

	//private int    panelWidth, panelHeight; 	// panel dimensions
	private static int SWIDTH = 1024;
	private static int SHEIGHT = 768;

	// used for gathering statistics
	private long statsInterval = 0L; // in ns
	private long prevStatsTime;
	private long totalElapsedTime = 0L;
	private long gameStartTime;
	private int timeSpentInGame = 0; // in seconds

	private long frameCount = 0;
	private double fpsStore[];
	private long statsCount = 0;
	private double averageFPS = 0.0;

	private long framesSkipped = 0L;
	private long totalFramesSkipped = 0L;
	private double upsStore[];
	private double averageUPS = 0.0;

	private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp
	private DecimalFormat timedf = new DecimalFormat("0.####"); // 4 dp

	private Thread animator; // the thread that performs the animation
	private volatile boolean running = false; // used to stop the animation thread
	private volatile boolean isPaused = false;

	private long statistics_period; // statistics_period between drawing in _nanosecs_

//	private String spaceBGFilename;

	private TriangleShip triangleShip;      // NOTE on the left controls -- should be configurable w/o code changes not like now
	private MultiLineShip ericARedShip;	// ditto the above
	private DiskOCatShip diskOCatShip;		// ditto the above

	private Planet planet;
	
	private StarryBackground starry_background;

	
	private volatile boolean gameOver = false;		// used at game termination
	private Font font;
	private FontMetrics metrics;

	private boolean finishedUp = false;

	// used by quit 'button'
	private volatile boolean isOverQuitButton = false;

	private Rectangle quitArea;

	// used by the pause 'button'
	private volatile boolean isOverPauseButton = false;

	private Rectangle pauseArea;

	// off-screen rendering
	private Graphics dbg;

	private Image dbImage = null;
	

	public FlatSpacePanel(long stats_period, String bg_filename) {
		
		this.statistics_period = stats_period;

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension scrDim = tk.getScreenSize();
		SWIDTH  = scrDim.width;
		SHEIGHT = scrDim.height;
		
//		System.out.println("scrDim.width  = " + scrDim.width);
//		System.out.println("scrDim.height = " + scrDim.height);
		
		//TODO I'm getting back the screen dimensions for the combined double monitor, but later only the opoosite is used so the os must cut it down later
		
		setPreferredSize(scrDim);

		setFocusable(true);
		requestFocus(); // the JPanel now has focus, so receives key events
		setUpKeyListener();

		// create game components
		
		starry_background = new StarryBackground(bg_filename);

		
		Engine gravity_engine = new Engine(this);
		
		double planetaryX = SWIDTH/4;
		double planetaryY = SHEIGHT/2;
		double planetaryG = 222;   //NOTE gravity set here
		
		Planet.PLANET_DIAMETER = 60;
		Planet.PLANET_RADIUS   = 30;
		Planet.PLANET_COLOR    = new Color(255, 0, 0);
		Planet.PLANET_COLOR.darker();
		Planet.TRANSPARENT     = true;
		
		gravity_engine.setGravitatorsCoordinates(planetaryX, planetaryY);
		gravity_engine.setGravitatorsGravity(planetaryG);
		gravity_engine.setGravitatorsSurfaceRadius(Planet.PLANET_RADIUS);
		
		triangleShip = new TriangleShip(9, 4, (SWIDTH/2), (SHEIGHT/2 - 100), gravity_engine, this);
		ericARedShip = new MultiLineShip( (SWIDTH - 100), (SHEIGHT - 50),    gravity_engine, this);
		diskOCatShip = new DiskOCatShip( (SWIDTH  - 300), (SHEIGHT/2),       gravity_engine, this);

		
		planet = new Planet(planetaryX, planetaryY, gravity_engine, this);
		
		// give a little initial velocity to each ship
		triangleShip.go(-0.7, 0.0);
		ericARedShip.go(-0.2, 0.0);
		diskOCatShip.go( 0.0, 0.0);

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				testPress(e.getX(), e.getY());
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				testMove(e.getX(), e.getY());
			}
		});

		// set up message font
		font = new Font("SansSerif", Font.BOLD, 14);
		metrics = this.getFontMetrics(font);

		// specify screen areas for the buttons
//		pauseArea = new Rectangle(panelWidth - 100, panelHeight - 45, 70, 15);
//		quitArea  = new Rectangle(panelWidth - 100, panelHeight - 20, 70, 15);
		pauseArea = new Rectangle(SWIDTH - 75, SHEIGHT - 40, 70, 15);  //TODO get around the double monitor issue
		quitArea  = new Rectangle(SWIDTH - 75, SHEIGHT - 20, 70, 15);  //     for now just hard code to make the buttons show

		// initialise timing elements
		fpsStore = new double[NUM_FPS];
		upsStore = new double[NUM_FPS];
		for (int i = 0; i < NUM_FPS; i++) {
			fpsStore[i] = 0.0;
			upsStore[i] = 0.0;
		}
	} // end of FlatSpacePanel()
	

	private void setUpKeyListener() {

		addKeyListener(new KeyAdapter() {

			private double left_controls_turn = 0.0;
			private double left_controls_fwd = 0.0;

			private double right_controls_turn = 0.0;
			private double right_controls_fwd = 0.0;

			private double center_controls_turn = 0.0;
			private double center_controls_fwd = 0.0;
			
			//TODO:  Should be from the ship inheritance heirarchy NOT from the control mechanism itself prolly, but...?
			private double ship_turn_factor = 0.19;
			private double ship_fwd_factor  = 0.05;
			private double ship_aft_factor  = 0.025;


			public void keyPressed(KeyEvent e) {

				int keyCode = e.getKeyCode();
				// System.out.println("...keyCode = " + keyCode);

				left_controls_turn 	= 0.0;
				left_controls_fwd 	= 0.0;
				
				right_controls_turn = 0.0;
				right_controls_fwd 	= 0.0;
				
				center_controls_turn= 0.0;
				center_controls_fwd = 0.0;

				
				if (keyCode == KeyEvent.VK_LEFT) {
					right_controls_turn = right_controls_turn - ship_turn_factor;
				}
				if (keyCode == KeyEvent.VK_RIGHT) {
					right_controls_turn = right_controls_turn + ship_turn_factor;
				}
				if (keyCode == KeyEvent.VK_UP) {
					right_controls_fwd = right_controls_fwd - ship_fwd_factor;
				}
				if (keyCode == KeyEvent.VK_DOWN) {
					right_controls_fwd = right_controls_fwd + ship_aft_factor;
				}

				if (keyCode == KeyEvent.VK_A) {
					left_controls_turn = left_controls_turn - ship_turn_factor;
				}
				if (keyCode == KeyEvent.VK_D) {
					left_controls_turn = left_controls_turn + ship_turn_factor;
				}
				if (keyCode == KeyEvent.VK_W) {
					left_controls_fwd = left_controls_fwd - ship_fwd_factor;
				}
				if (keyCode == KeyEvent.VK_S) {
					left_controls_fwd = left_controls_fwd + ship_aft_factor;
				}

				if (keyCode == KeyEvent.VK_V) {
					center_controls_turn = center_controls_turn - ship_turn_factor;
				}
				if (keyCode == KeyEvent.VK_N) {
					center_controls_turn = center_controls_turn + ship_turn_factor;
				}
				if (keyCode == KeyEvent.VK_B) {
					center_controls_fwd = center_controls_fwd - ship_fwd_factor;
				}
				if (keyCode == KeyEvent.VK_SPACE) {
					center_controls_fwd = center_controls_fwd + ship_aft_factor;
				}
				
				if (keyCode == KeyEvent.VK_EQUALS) {
					System.out.println("VK_EQUALS");
					setScale_factor( getScale_factor() + scaleIncrement() );
					scaleTheGame();
				}
				if (keyCode == KeyEvent.VK_MINUS) {
					System.out.println("VK_MINUS");
					setScale_factor( getScale_factor() - scaleIncrement() );
					scaleTheGame();
				}
				

				if (!(right_controls_turn == 0.0))
					ericARedShip.turn(right_controls_turn);
				if (!(right_controls_fwd == 0.0))
					ericARedShip.go(right_controls_fwd, 0.0);

				if (!(left_controls_turn == 0.0))
					triangleShip.turn(left_controls_turn);
				if (!(left_controls_fwd == 0.0))
					triangleShip.go(left_controls_fwd, 0.0);
//				    triangleShip.go(left_controls_fwd, 0.0);

				if (!(center_controls_turn == 0.0))
					diskOCatShip.turn(center_controls_turn);
				if (!(center_controls_fwd == 0.0))
					diskOCatShip.go(center_controls_fwd, 0.0);

				if (keyCode == KeyEvent.VK_ESCAPE) {
					running = false;
				}

			}
		});

		// for shutdown tasks
		// a shutdown may not only come from the program
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				running = false;
				// System.out.println("Shutdown hook executed");
				finishUp();
			}
		});
	} 

	public void addNotify()
	// wait for the JPanel to be added to the JFrame before starting
	{
		super.addNotify(); // creates the peer

		if (animator == null || !running) { // start the thread
			animator = new Thread(this);
			animator.start();
		}
	} 

	 // ------------- game life cycle methods ------------
	  // called by the JFrame's window listener methods


	  public void resumeGame()
	  // called when the JFrame is activated / deiconified
	  {  isPaused = false;  } 


	  public void pauseGame()
	  // called when the JFrame is deactivated / iconified
	  { isPaused = true;   } 


	  public void stopGame() 
	  // called when the JFrame is closing
	  {  running = false;   }

	  // ----------------------------------------------

	
	/* Handle pause and quit buttons. */
	private void testPress(int x, int y) {
		if (isOverPauseButton) {
			isPaused = !isPaused; // toggle the pause state
		} else {
			if (isOverQuitButton) {
				running = false;
			}
		}
	}

	private void testMove(int x, int y) {
//		System.out.println("test move with x = " + x + ", and y = " + y + ".");

		if (running) { // stops problems with a rapid move after pressing quit
//			System.out.println("isOverPauseButton = " + pauseArea.contains(x, y));
//			System.out.println("isOverPauseButton = " + quitArea.contains(x, y));
			
//			System.out.println("pauseArea = " + pauseArea.toString());
//			System.out.println("quitArea = " + quitArea.toString());
			
			isOverPauseButton = pauseArea.contains(x, y) ? true : false;
			isOverQuitButton = quitArea.contains(x, y) ? true : false;
			
//			System.out.println("isOverPauseButton set to " + (pauseArea.contains(x, y) ? true : false));
//			System.out.println("isOverPauseButton set to " + (quitArea.contains(x, y) ? true : false));
			
		}
	}

	@SuppressWarnings("deprecation")
	public void run() {
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int numDelays = 0;
		int passesCount = 0;
		long excess = 0L;
//		@SuppressWarnings("unused")
//		Graphics g;

		gameStartTime = J3DTimer.getValue();
		prevStatsTime = gameStartTime;
		beforeTime = gameStartTime;

		running = true;

		while (running) {
			gameUpdate();
			gameRender(); // render the game to a buffer
			paintScreen(); // draw the buffer on-screen

			afterTime = J3DTimer.getValue();
			timeDiff = afterTime - beforeTime;
			
			sleepTime = (statistics_period - timeDiff) - overSleepTime;

			if (sleepTime > 0) { // some time left in this cycle
				try {
					Thread.sleep(sleepTime / 1000000L); // nano -> ms
				} catch (InterruptedException ex) {
				}
				overSleepTime = (J3DTimer.getValue() - afterTime) - sleepTime;
			} else { // sleepTime <= 0; the frame took longer than the statistics_period
				excess -= sleepTime; // store excess time value
				overSleepTime = 0L;

				if (++numDelays >= NO_DELAYS_PER_YIELD) {
					Thread.yield(); // give another thread a chance to run
					numDelays = 0;
				}
			}
			
			if (++passesCount >= MAX_FRAME_SKIPS) {
				Thread.yield(); // give another thread a chance to run
				passesCount = 0;
			}

			beforeTime = J3DTimer.getValue();

			/*
			 * If frame animation is taking too long, update the game state
			 * without rendering it, to get the updates/sec nearer to the
			 * required FPS.
			 */
			int skips = 0;
			while ((excess > statistics_period) && (skips < MAX_FRAME_SKIPS)) {
				excess -= statistics_period;
				gameUpdate(); // update state but don't render
				skips++;
			}
			framesSkipped += skips;
			excess = excess % statistics_period;

			storeStats();
		}
		finishUp();
	}

	private void gameUpdate() {
		if (!isPaused && !gameOver) {

			// TODO: put in the overall game narrative stuff here
			// where the delegation to internet comm routines?

			triangleShip.update_position();
			ericARedShip.update_position();
			diskOCatShip.update_position();

		}
	} // end of gameUpdate()

	private void gameRender() {
		if (dbImage == null) {
			dbImage = createImage(SWIDTH, SHEIGHT);
			if (dbImage == null) {
				System.out.println("dbImage is null");
				return;
			} else
				dbg = dbImage.getGraphics();
		}

		// clear the background
		dbg.setColor(Color.black);
		dbg.fillRect(0, 0, SWIDTH, SHEIGHT);
		
		starry_background.draw(dbg);

		dbg.setColor(Color.blue);
		dbg.setFont(font);

		// report frame count & average FPS and UPS at top left
		dbg.drawString("Frame Count " + frameCount, 10, 20);
		dbg.drawString("Average FPS/UPS: " + df.format(averageFPS) + ", " + df.format(averageUPS), 10, 40);

		// report time used and bosex used at bottom left
		dbg.drawString("Time Spent: " + timeSpentInGame + " secs", 10, SHEIGHT-20);
		
		// draw the pause and quit 'buttons'
		drawButtons(dbg);

		//TODO make this a list of things to draw, iterate them calling their draw function
		triangleShip.draw(dbg);
		ericARedShip.draw(dbg);
		diskOCatShip.draw(dbg);
		planet.draw(dbg);

		// if (gameOver)
		// gameOverMessage(dbg);

	} // end of gameRender()

	private void drawButtons(Graphics g) {
//		g.setColor(Color.blue);
		
		// draw the pause 'button'
		if (isOverPauseButton) {
//			System.out.println("isOverPauseButton");
			g.setColor(Color.green);
			g.drawOval(pauseArea.x, pauseArea.y, pauseArea.width, pauseArea.height);
			
			if (isPaused)
				g.drawString("paused", pauseArea.x + 14, pauseArea.y + 11);
			else
				g.drawString("pause", pauseArea.x + 15, pauseArea.y + 11);
		}

//
//		if (isOverPauseButton)
//			g.setColor(Color.black);
//
		
		// draw the quit 'button'
		if (isOverQuitButton) {
			g.setColor(Color.green);

			g.drawOval(quitArea.x, quitArea.y, quitArea.width, quitArea.height);
			g.drawString("quit", quitArea.x + 20, quitArea.y + 11);

			
		}
		
//		if (isOverQuitButton)
//			g.setColor(Color.black);
		
	} 

	// private void gameOverMessage(Graphics g)
	// // center the game-over message in the panel
	// {
	// String msg = "Game Over. Your Score: " + score;
	// int x = (panelWidth - metrics.stringWidth(msg))/2;
	// int y = (panelHeight - metrics.getHeight())/2;
	// g.setColor(Color.red);
	// g.setFont(font);
	// g.drawString(msg, x, y);
	// } // end of gameOverMessage()

	private void paintScreen()
	// use active rendering to put the buffered image on-screen
	{
		Graphics g;
		try {
			g = this.getGraphics();
			if ((g != null) && (dbImage != null))
				g.drawImage(dbImage, 0, 0, null);
			// Sync the display on some systems.
			// (on Linux, this fixes event queue problems)
			Toolkit.getDefaultToolkit().sync();
			g.dispose();
		} catch (Exception e) // quite commonly seen at applet destruction
		{
			System.out.println("Graphics error: " + e);
		}
	} // end of paintScreen()

	@SuppressWarnings("deprecation")
	private void storeStats()
	/*
	 * The statistics: - the summed periods for all the iterations in this
	 * interval (statistics_period is the amount of time a single frame iteration should
	 * take), the actual elapsed time in this interval, the error between these
	 * two numbers;
	 *  - the total frame count, which is the total number of calls to run();
	 *  - the frames skipped in this interval, the total number of frames
	 * skipped. A frame skip is a game update without a corresponding render;
	 *  - the FPS (frames/sec) and UPS (updates/sec) for this interval, the
	 * average FPS & UPS over the last NUM_FPSs intervals.
	 * 
	 * The data is collected every MAX_STATS_INTERVAL (1 sec).
	 */
	{
		frameCount++;
		statsInterval += statistics_period;

		if (statsInterval >= MAX_STATS_INTERVAL) { // record stats every
													// MAX_STATS_INTERVAL
			long timeNow = J3DTimer.getValue();
			timeSpentInGame = (int) ((timeNow - gameStartTime) / 1000000000L); // ns
																				// -->
																				// secs

			long realElapsedTime = timeNow - prevStatsTime; // time since last
															// stats collection
			totalElapsedTime += realElapsedTime;

			@SuppressWarnings("unused")
			double timingError = ((double) (realElapsedTime - statsInterval) / statsInterval) * 100.0;

			totalFramesSkipped += framesSkipped;

			double actualFPS = 0; // calculate the latest FPS and UPS
			double actualUPS = 0;
			if (totalElapsedTime > 0) {
				actualFPS = (((double) frameCount / totalElapsedTime) * 1000000000L);
				actualUPS = (((double) (frameCount + totalFramesSkipped) / totalElapsedTime) * 1000000000L);
			}

			// store the latest FPS and UPS
			fpsStore[(int) statsCount % NUM_FPS] = actualFPS;
			upsStore[(int) statsCount % NUM_FPS] = actualUPS;
			statsCount = statsCount + 1;

			double totalFPS = 0.0; // total the stored FPSs and UPSs
			double totalUPS = 0.0;
			for (int i = 0; i < NUM_FPS; i++) {
				totalFPS += fpsStore[i];
				totalUPS += upsStore[i];
			}

			if (statsCount < NUM_FPS) { // obtain the average FPS and UPS
				averageFPS = totalFPS / statsCount;
				averageUPS = totalUPS / statsCount;
			} else {
				averageFPS = totalFPS / NUM_FPS;
				averageUPS = totalUPS / NUM_FPS;
			}
			/*
			 * System.out.println(timedf.format( (double)
			 * statsInterval/1000000000L) + " " + timedf.format((double)
			 * realElapsedTime/1000000000L) + "s " + df.format(timingError) + "% " +
			 * frameCount + "c " + framesSkipped + "/" + totalFramesSkipped + "
			 * skip; " + df.format(actualFPS) + " " + df.format(averageFPS) + "
			 * afps; " + df.format(actualUPS) + " " + df.format(averageUPS) + "
			 * aups" );
			 */
			framesSkipped = 0;
			prevStatsTime = timeNow;
			statsInterval = 0L; // reset
		}
	} // end of storeStats()

	private void finishUp()
	/*
	 * Get ready to halt.  
	 * Should be called at end of run() and via the shutdown hook in readyForTermination().
	 * 
	 * Call at the end of run() not really necessary but included for safety. Flag prevents this code being called twice.
	 */
	{
		if (!finishedUp) {
			finishedUp = true;
			printStats();
			System.exit(0);
		}
	} 

	private void printStats() {
		System.out.println("Frame Count/Loss: " + frameCount + " / "
				+ totalFramesSkipped);
		System.out.println("Average FPS: " + df.format(averageFPS));
		System.out.println("Average UPS: " + df.format(averageUPS));
		System.out.println("Time Spent: " + timeSpentInGame + " secs");
	}


	public double getScale_factor() {
		return scale_factor;
	}


	public void setScale_factor(double scale_factor) {
//		System.out.println("Requested new scale factor " + scale_factor) ;
		
		if (scale_factor <= maxScale() && scale_factor >= minScale() ) {
			this.scale_factor = scale_factor;
			
//			System.out.println(" Accepted new scale factor " + this.scale_factor);
		} 
	}

	private double maximum_scaling = 4.0;
	public double maxScale() {
		// TODO Auto-generated method stub
		return maximum_scaling;
	}
	
	private double sincre = 0.5;
	public double scaleIncrement() {
		// TODO Auto-generated method stub
		return sincre;
	}
	
	private double minimum_scaling = 0.5;
	public double minScale() {
		// TODO Auto-generated method stub
		return minimum_scaling;
	}


	public AffineTransform getScalingAT() {
		return scalingAT;
	}


	public void scaleTheGame() {
//		System.out.println("A scalingAT    = " + scalingAT.toString());
//		System.out.println("B scale_factor = " + scale_factor);
	    scalingAT.scale((1/scale_factor), (1/scale_factor));
//		System.out.println("C scalingAT    = " + scalingAT.toString());
	}


	public int getSW() {
		// TODO change class variable to instance variable?
		return SWIDTH;
	}


	public int getSH() {
		return SHEIGHT;
	}


//	public void setSpaceBGFilename(String spaceBGFilename) {
//		this.spaceBGFilename = spaceBGFilename;
//	}

} // end of FlatSpacePanel class

