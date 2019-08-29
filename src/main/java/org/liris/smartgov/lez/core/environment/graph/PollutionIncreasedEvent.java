package org.liris.smartgov.lez.core.environment.graph;

import org.liris.smartgov.simulator.core.agent.moving.events.arc.ArcEvent;

public class PollutionIncreasedEvent extends ArcEvent {

	public PollutionIncreasedEvent(PollutableOsmArc arc) {
		super(arc);
	}

}
