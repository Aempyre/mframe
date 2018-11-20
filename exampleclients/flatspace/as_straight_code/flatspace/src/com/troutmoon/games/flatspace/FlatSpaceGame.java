package com.troutmoon.games.flatspace;

/* 
   Full screen JFrame with no title bar or insets (borders).
   They are switched off with setUndecorated().

   No decoration means that only the JPanel is drawn, with active
   rendering in FlatSpacePanel, and so we can ignore repaint() events
   by calling setIgnoreRepaint(). 

   Pausing/Resuming/Quiting are controlled via on-screen pause 
   and quit buttons. We also monitor for a shutdown event.

*/

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;

import com.troutmoon.games.flatspace.engine.FlatSpacePanel;

@SuppressWarnings("serial")
public class FlatSpaceGame extends JFrame
{
  private static int DEFAULT_FPS = 51;


  public FlatSpaceGame(long stats_period_ns, String bg_filename)
  { super(" Flatspace: Legacy ");

    Container c = getContentPane(); 
    c.setLayout( new BorderLayout() );   

    FlatSpacePanel fs_panel = new FlatSpacePanel(stats_period_ns, bg_filename);
    c.add(fs_panel, "Center");

    setUndecorated(true);   // no borders or title bar
    setIgnoreRepaint(true);  // turn off all paint events since doing active rendering
    pack();
    setResizable(false);
    setVisible(true);
  } 

  
  public static void main(String args[])
  {  
    int fps = DEFAULT_FPS;
    if (args.length != 0) fps = Integer.parseInt(args[0]);
    
    long stats_period_ms = (long) 1000.0/fps;
    
    System.out.println("fps: " + fps + "; period: " + stats_period_ms + " ms -- bg = " + args[1]);

    new FlatSpaceGame( (stats_period_ms * 1000000L), args[1] );    // ms --> nanosecs 
  }  

}


