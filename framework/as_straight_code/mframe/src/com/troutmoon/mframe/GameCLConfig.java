// Config controls degrees of freedom of the game:
// ie. Left-right, L, R, Forward, Back, or 
//     L, R, F, B, Up, Down.

package com.troutmoon.mframe;

public class GameCLConfig {

    public static int LEFT_RIGHT                       = 1;
    public static int LEFT_RIGHT_FORWARD_BACK          = 2;
    public static int LEFT_RIGHT_FORWARD_BACK_UP_DOWN  = 3;
    
    public int DIRECTIONS_OF_MOTION = LEFT_RIGHT;   // Defaults to L/R only.

}