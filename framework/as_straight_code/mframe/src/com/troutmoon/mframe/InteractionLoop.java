package com.troutmoon.mframe;

import java.util.Scanner;

public class InteractionLoop {

    // if debug we can output debug messages.
    private boolean debug = true;


    private boolean running = false; 

    // boolean hasGreeting     = false;
    // boolean hasFarewell     = false;
    // String greetingString   = "";         
    // String farewellString   = "";

    // // Scanner inputScanner    = new Scanner(System.in);

    // // Menu commands to be setup from game configuration properties or left as defaults
    String quitCommand              = "q";
    // String goLeftCommand            = "a";
    // String goRightCommand           = "d";
    // String goForwardCommand         = "w";
    // String goBackCommand            = "s";
    // String selectionFirstCommand    = "1";
    // String selectionSecondCommand   = "2";

    int userInputCommand    = 0;
    String userInputString  = "";    

    IGameInteractionModel gameModel;
    

    public InteractionLoop(IGameInteractionModel model) {
        gameModel = model;
        // Constructor for default command keys.
    };

    // public InteractionLoop(
    //     String quit,
    //     String goLeft,
    //     String goRight,
    //     String goForward,
    //     String goBack,
    //     String selectionFirst,
    //     String selectionSecond ) {
        
    //     quitCommand              = quit;
    //     goLeftCommand            = goLeft;
    //     goRightCommand           = goRight;
    //     goForwardCommand         = goForward;
    //     goBackCommand            = goBack;
    //     selectionFirstCommand    = selectionFirst;
    //     selectionSecondCommand   = selectionSecond;
    // }


    public void play() {

        running = true;

        while(running) {
          
            GameCLView.display(gameModel.returnStateString());

            if (gameModel.isWon()) {
                GameCLView.display(gameModel.getVictoryMessage());
                running = false;
                continue;
            } else if (gameModel.isLost()) {
                GameCLView.display(gameModel.getLossMessage());
                running = false;
                continue;
            }
            
            if (gameModel.isInteractionEnabled()) {
                userInputCommand = Integer.parseInt(gameModel.getTheGame().getInputScanner().nextLine());
                String whichWay = "";

                if ( userInputCommand == IGameInteractionModel.ITEM_1 ) {
                    whichWay = gameModel.processInteraction(IGameInteractionModel.ITEM_1);
                } else 
                if ( userInputCommand == IGameInteractionModel.ITEM_2 ) {
                    whichWay = gameModel.processInteraction(IGameInteractionModel.ITEM_2);
                } else {
                    // invalid input message and continue around the loop.
                }
                GameCLView.display(whichWay);
            }
            
            userInputString = gameModel.getTheGame().getInputScanner().nextLine(); 

            if (userInputString.equalsIgnoreCase(quitCommand)) {
                 running = false;
            } else {
                userInputCommand = Integer.parseInt(userInputString);
                if (debug) GameCLView.display(userInputString + " / " + userInputCommand);
                gameModel.goDirection(userInputCommand);
            }
            
        }
        GameCLView.display(gameModel.getFarewell());
       
    }

}