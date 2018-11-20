package com.troutmoon.games.flatspace.narrative;
/*  o  Load a story
 *  o  Issue activity events from initial story point
 *  o  Recieve activity events
 *  o  Advance the story when events meet requirements
 *  o  Issue activity events from story advances
 *  o  Handle THE END as/if anything special needed
 */
public class Engine implements StoryEventListener {
	
	private StoryEventListener listener;
	
	private Plot plot;
	

	public Engine(Plot plot, StoryEventListener listener) {
		super();
		this.plot 		= plot;
		this.listener 	= listener;
		
		// Begin the story (allow initial events to propagate to the listener)
		this.plot.tell(listener);
		
	}
	
	public void recieveEvent(StoryEvent event) {
		
		plot.advance(event);  // arms for the tell
		
		plot.tell(listener);  // tells if anything was triggered
	
	}

}
