package com.troutmoon.games.flatspace.narrative;

/**
 * @author wordsmith
 *
 * Story World
 * 
 * Thematic, illustrative, and plot terrain of the story's universe
 * of possibilities with all available paths and conclusions.
 */

/* Technical thoughts:
 * 
 * Load the fixed infrastructure from a property file (that can later be
 * developed and exposer to the player community for their creative use),
 * into a runtime data class.
 * 
 * This runtime storyworld dataclass will also contain dynamic state
 * information as play progresses that can itself be stored and restored
 * at players desire.  (Suspect an additional property file may not be
 * quite right, perhaps a generalized parameterized inner class that
 * can be serialized -- and later be developed to handle the standard
 * format dynamic state info. from player community story worlds as well!)
 * 
 * How it will work.
 * 
 * 1.  On creation, accept a string filepath to the properties;
 *     pass this into a load method to get the fixed structure.
 * 
 * 2.  If invoked, execute a method to load the dynamic state
 * 3.  If invoked, execute a method to store the dynamic state
 * 
 * 4.  Now for the fun stuff:  Playtime!
 * 
 *     a)  recieve an event
 *     b)  process event against the current state and static prerequisites
 *     c)  update progress of the story
 *     d)  log progress of the story
 *     e)  communicate progress of the story
 *     f)  issue any triggered events (and circle back to a,b,c,d,e,f.
 *     g)  wait for next event
 * 
 * 
 * OKAY -- SO NOW THE DATA STRUCTURES:
 * 
 * Static:
 * 
 * 	SceneList of Scenes
 *		Each Scene containing
 *			Required event list
 *			Triggered event list
 *			Characters whose presence is required list
 *			Prose:
 *				Scene setup
 *				The problem
 *			Player action in response to setup and problem
 *			Result of player action (whether played out our simulated)
 *			Scene status complete (when triggered events have been processed)
 *  
 * Dynamic:
 * 		Scene status (unplayed / triggerd / presented / action in process /
 *                    / result recieved (and what the result was) / 
 *                    triggered events issued (then complete?), scene complete)
 * 		
 * 
 * Wow, neat, I can do this!
 * 
 */
public class StoryWorld {

	public StoryWorld() {
		// TODO Auto-generated constructor stub
	}

}
