/*
 * Chum Chum as an MFrame interactive game.
 */
import com.troutmoon.mframe.GameCLInteractive;

public class ChumChumMFGame extends GameCLInteractive {

    //private ChumChumGameModel gameModel = null;

    public static void main(String[] args) {
        
        // if debug we can output debug messages.
        boolean debug = true;
        
        ChumChumMFGame chumChumGame  = new ChumChumMFGame();
        ChumChumGameModel gameModel  = new ChumChumGameModel(chumChumGame);

        chumChumGame.setGameModelOnGame(gameModel);
        
        // Start the game:
        boolean gameStartSuccess = chumChumGame.start();
  
        if (debug) {
            if (gameStartSuccess) {
                System.out.println("DEBUG:  Game started successfully.");
            } else {
                System.out.println("DEBUG:  Game failed to start...");
            }
        }
        
    }    
}
