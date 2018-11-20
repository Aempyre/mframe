package com.troutmoon.mframe;

import java.util.Scanner;

public class GameCLInteractive {

    IGameInteractionModel interActiveGameModel;

    Scanner inputScanner;

    /**
     * @param gameConfig (in the future)
     */
    public GameCLInteractive() {

        /* For interactive games the framework only
           supports command line input scanner for now
           so it creates its own scanner here.
           In Future could support a custom input class
           passed in from the client if desireable
        */

        // Create the input scanner that will be used here.
        inputScanner = new Scanner(System.in);

        // Also could create a random number generator here (next version)

    }

    public void setGameModelOnGame(IGameInteractionModel model) {
        interActiveGameModel = model;
    }

    /*  Below are operational    */

    public boolean start() {
        interActiveGameModel.reset();

        InteractionLoop gameLoop = new InteractionLoop(interActiveGameModel);
        gameLoop.play();

        return true;
    }

    public void quit() {

    }

    public Scanner getInputScanner() {
        return inputScanner;
    }

}