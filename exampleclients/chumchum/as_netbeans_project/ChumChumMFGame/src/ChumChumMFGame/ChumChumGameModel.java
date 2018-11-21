package ChumChumMFGame;

/*
 * Chum Chum as an MFrame interactive game -- ChumChumGameModel.
 */

import com.troutmoon.mframe.IGameInteractionModel;
import com.troutmoon.mframe.GameCLInteractive;
import com.troutmoon.mframe.GameCLConfig;

import java.util.Scanner;


public class ChumChumGameModel implements IGameInteractionModel {

    // if debug we can output debug messages.
    private boolean debug = true;

    /* This represents a simple model for a simple game,
       one is free to get much more complex as long as
       your internal implementation is correct an you 
       serve the implemented interface appropriately.  */

    private static final int STATE_INITIAL            = 00;

    // First level - possible states.
    private static final int STATE_CHUMCHUM           = 10;
    private static final int STATE_DOWN_THE_MOUNTAIN  = 20;

    // Second level -- possible states.
    private static final int STATE_CANNIBALS          = 11;
    private static final int STATE_CLIFF              = 12;
    private static final int STATE_ELEPHANTS          = 21;
    private static final int STATE_CHIMPANZEES        = 22;

    // Final Outcome -- possible states.
    private static final int STATE_VICTORY            = 14;
    private static final int STATE_LOST               = 67;

    // -----------------------------------------------
    private int gameState = STATE_INITIAL;


    private static int NOTHING   = 00;
    // -----------------------------------------------
    private int playerHas = NOTHING;

    private static final int CHUMCHUM  = 10;
    private static final int PARACHUTE = 20;
    private static final int PEANUTS   = 30;
    private static final int MOUSE     = 40;

    private boolean interactionEnabled = false;


    private String greeting = 
            "\nYou are stranded on top of a mountain.";

    private String whichWay = 
            "\nYou can take the left or right path."
        +   "\nEnter 1 for left or 2 for right";

    private String encounterChumChumAndTakeAction = 
            "\nYou have just run afoul of ChumChum,"
        +   "\n the evil extinctor of elephants, lions, and pandas (oh my)."
        +   "\n\nThere is also a parachute near ChumChum."
        +   "\n\nYou must chose to take either ChumChum or the parachute."
        +   "\nEnter 1 to take ChumChum"
        +   "\nEnter 2 to take the parachute";


    private String encounterDownTheMountainAndTakeAction = 
            "\nYou went Right down the mountain."
        +   "\nYou found:"
        +   "\na bag of peanuts,"
        +   "\n and a mouse."
        +   "\n\nyou must chose to take either the peanuts or the mouse."
        +   "\nEnter 1 to take the peanuts. "
        +   "\nEnter 2 to take the mousee.";


        private String victoryString     = "\n\nYou win!";
        private String consolationString = "\n\nSorry you lose.";
        private String farewellString    = "\n\nPlease play again sometime...";


        private GameCLConfig gameConfig = null;

        private GameCLInteractive theGame = null;


    public ChumChumGameModel(ChumChumMFGame game) {
        gameConfig = new GameCLConfig();
        gameConfig.DIRECTIONS_OF_MOTION = GameCLConfig.LEFT_RIGHT;
        theGame = game;
    };

    public void setDirectionsOfMotion(GameCLConfig config) {
        gameConfig = config;
    }


    public String returnStateString() {

        switch (gameState) {
            case STATE_INITIAL: 
                return greeting + whichWay;
                //break;

            case STATE_CHUMCHUM: 
                interactionEnabled = true;
                return encounterChumChumAndTakeAction;
                //break;

            case STATE_DOWN_THE_MOUNTAIN: 
                interactionEnabled = true;
                return encounterDownTheMountainAndTakeAction;
                //break;

            case STATE_CANNIBALS: 
                if (playerHas == CHUMCHUM) {
                    gameState = STATE_VICTORY;
                    return "\nYou have run into cannibals."
                         + "\nGive ChumChum to the cannibals and you are free.";
                } else
                if (playerHas == PARACHUTE) {
                    gameState = STATE_LOST;
                    return "\nYou have run into a cliff."
                         + "\nYou are captured by the cannibals.";
                }
                return "error 1";
                //break;

            case STATE_CLIFF: 
                if (playerHas == CHUMCHUM) {
                    gameState = STATE_LOST;
                    return "\nYou have run into a cliff."
                        + "\nYou are captured by the cannibals.";
                } else
                if (playerHas == PARACHUTE) {
                    gameState = STATE_VICTORY;
                    return "\nYou have run into a cliff."
                        + "\nYou parachute to safety.";
                }
                return "error 1";
                //break;

            case STATE_ELEPHANTS: 
                if (playerHas == PEANUTS) {
                    gameState = STATE_LOST;
                    return "\nYou have run into elephants."
                        + "\nYou are stomped by the elephants.";
                } else
                if (playerHas == MOUSE) {
                    gameState = STATE_VICTORY;
                    return "\nYou have run into elephants."
                        + "\nYour mouse scares the elephants.";
                }
                return "error 1";
                //break;

            case STATE_CHIMPANZEES: 
                if (playerHas == PEANUTS) {
                    gameState = STATE_VICTORY;
                    return "\nYou have run into fanged chimpanzees."
                         + "\nYou throw the peanuts at the chimpanzees."
                         + "\nThe chimpanzees scatter to find the peanuts.";
                } else
                if (playerHas == MOUSE) {
                    gameState = STATE_LOST;
                    return "\nYou have run into fanged chimpanzees."
                    + "\nSorry, they hate mice and hate you too."
                    + "\nThe chimpanzees tear you to pieces.";
                }
                return "error 1";
                //break;

            default:  
                return "error 2";       
                //break;            
        }

    }


    public boolean isInteractionEnabled() {
        return interactionEnabled;
    }


    public String processInteraction( int item ) {

        if (debug) System.out.println("processInteraction( " + item + " )");

        switch (gameState) {

            case STATE_CHUMCHUM: 
                if ( item == IGameInteractionModel.ITEM_1 ) {
                    playerHas = CHUMCHUM;
                } else 
                if ( item == IGameInteractionModel.ITEM_2 ) {
                    playerHas = PARACHUTE;
                }  
                interactionEnabled = false;
                //return whichWay;
                break;

            case STATE_DOWN_THE_MOUNTAIN: 
                if ( item == IGameInteractionModel.ITEM_1 ) {
                    playerHas = PEANUTS;
                } else 
                if ( item == IGameInteractionModel.ITEM_2 ) {
                    playerHas = MOUSE;
                }  
                interactionEnabled = false;
                //return whichWay;
                break;

            default:   
                //return "error a";      
                break;
                        
        }

        if (debug) {
            System.out.println("player has  the ");
            if ( playerHas == CHUMCHUM ) { System.out.println("CHUMCHUM."); }
            else  if ( playerHas == PARACHUTE ) { System.out.println("PARACHUTE."); }
            else  if ( playerHas == PEANUTS ) { System.out.println("PEANUTS."); }
            else  if ( playerHas == MOUSE ) { System.out.println("MOUSE."); }
        }

        return whichWay;

    }

    public void putdown( int item ) {
        playerHas = NOTHING;
    }


    public boolean isWon() {
        return (gameState == STATE_VICTORY);
    }
    public boolean isLost() {
        return (gameState == STATE_LOST);
    }


    public void goDirection( int moveDirection) {

        if (debug) System.out.println("goDirection( " + moveDirection + " )");

        switch (gameState) {
            case STATE_INITIAL: 
                if ( moveDirection == IGameInteractionModel.GO_LEFT ) {
                    gameState = STATE_CHUMCHUM;
                } else 
                if ( moveDirection == IGameInteractionModel.GO_RIGHT ) {
                    gameState = STATE_DOWN_THE_MOUNTAIN;
                }  
                break;

            case STATE_CHUMCHUM: 
                if ( moveDirection == IGameInteractionModel.GO_LEFT ) {
                    gameState = STATE_CANNIBALS;
                } else 
                if ( moveDirection == IGameInteractionModel.GO_RIGHT ) {
                    gameState = STATE_CLIFF;
                }  
                break;
            case STATE_DOWN_THE_MOUNTAIN: 
                if ( moveDirection == IGameInteractionModel.GO_LEFT ) {
                    gameState = STATE_CHIMPANZEES;
                } else 
                if ( moveDirection == IGameInteractionModel.GO_RIGHT ) {
                    gameState = STATE_ELEPHANTS;
                }  
                break;

            default:         
                break;
                        
        }

    }

    public boolean setGreeting(String newGreeting) {
        greeting = newGreeting;
        return true;
    }

    public boolean setVictoryMessage(String newVictory) {
        victoryString = newVictory;
        return true;
    }
    public String  getVictoryMessage() {
        return victoryString;
    }

    public boolean setLossMessage(String newConsolation) {
        consolationString = newConsolation;
        return true;
    }
    public String  getLossMessage() {
        return consolationString;
    }

    public boolean setFarewell(String newFarewell) {
        farewellString = newFarewell;
        return true;
    }
    public String  getFarewell() {
        return farewellString;
    }

    public GameCLInteractive getTheGame() {
        return theGame;
    }

    public boolean reset() {
        gameState = STATE_INITIAL;
        playerHas = NOTHING;
        return true;
    }

    
}