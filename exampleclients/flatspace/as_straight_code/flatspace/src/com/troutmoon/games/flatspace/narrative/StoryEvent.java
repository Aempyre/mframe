package com.troutmoon.games.flatspace.narrative;

import java.util.LinkedList;

/*  - The event (id, description, ?)
 *  - What inputs or combination of inputs will cause it
 *  - What outputs it generates
 */
public class StoryEvent implements StoryEventListener {
	
	private Integer id;
	private String  description;
	
	 LinkedList<StoryEvent> prerequisites;    	// precursor events
	 LinkedList<StoryEvent> consequences;  		// events to propagate
	
	
	public StoryEvent(Integer id, String description, LinkedList<StoryEvent> precursors, LinkedList<StoryEvent> consequences) {
		super();
		this.id = id;
		this.description = description;
		this.prerequisites = precursors;
		this.consequences = consequences;
	}

	public void recieveEvent(StoryEvent event) {
		// search for event in prerequisites, remove if found (prerequisite satisfied)
		if (prerequisites.contains(event)) {
			prerequisites.remove(event);
		}
	}
	
}
