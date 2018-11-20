package com.troutmoon.mframe;

public interface IGameInteractionModel {

    public static int GO_LEFT      = 1;
    public static int GO_RIGHT     = 2;
    public static int GO_FORWARD   = 3;
    public static int GO_BACK      = 4;

    public static int GO_UP        = 5;
    public static int GO_DOWN      = 6;

    public static int ITEM_1       = 1;
    public static int ITEM_2       = 2;

    public GameCLConfig gameConfig = new GameCLConfig();
    public void setDirectionsOfMotion(GameCLConfig config);

    public String returnStateString();

    public boolean isWon();
    public boolean isLost();

    public boolean isInteractionEnabled();

    public void goDirection( int moveDirection);
    
    public String processInteraction( int item );

    //public void putdown( int item );


    public boolean setGreeting(String newGreeting);
    //public String  getGreeting();
    
    //public boolean setMainMenu(String mainMenu);
    public boolean setVictoryMessage(String victory);
    public String  getVictoryMessage();
    public boolean setLossMessage(String consolation);
    public String  getLossMessage();
    public boolean setFarewell(String farewell);
    public String  getFarewell();

    public boolean reset();

    public GameCLInteractive getTheGame();

}