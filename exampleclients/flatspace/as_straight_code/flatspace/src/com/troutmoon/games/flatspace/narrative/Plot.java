package com.troutmoon.games.flatspace.narrative;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/*  - Entry point event or events
 *  - For each entrypoint event:
 *    . possible next event or events
 *    . for each next event:
 *      - possible next event or events...
 * 
 */
public class Plot {
	
	private Engine engine;
	private LinkedHashMap<Integer, StoryEvent> events;  // key must be same as it's data's id!

	
	public Plot(Engine engine, LinkedHashMap<Integer, StoryEvent> events) {
		super();
		this.engine = engine;
		this.events = events;
	}

	public void advance(StoryEvent recieved_event) {
		Iterator<Entry<Integer, StoryEvent>> itr = events.entrySet().iterator();
		while (itr.hasNext()) {
			StoryEvent plot_event = itr.next().getValue();
			plot_event.recieveEvent(recieved_event);
		}
	}
	
	public void tell(StoryEventListener listener) {
		
		Iterator<Entry<Integer, StoryEvent>> event_itr = events.entrySet().iterator();
		
		while (event_itr.hasNext()) {
			
			StoryEvent plot_event = event_itr.next().getValue();
			if (plot_event.prerequisites.size() == 0) {
				
				// prerequisites satisfied; tell of this event!
				listener.recieveEvent(plot_event);
				
				// are there any consequences to this event?
				if (plot_event.consequences.size() > 0) {
					
					Iterator<StoryEvent> conseq_itr = plot_event.consequences.iterator();
					
					while (conseq_itr.hasNext()) {
						
						StoryEvent conseq_plot_event = conseq_itr.next();
						
						engine.recieveEvent(conseq_plot_event);
						
					}
					
				}
			}
		}
	}


}
