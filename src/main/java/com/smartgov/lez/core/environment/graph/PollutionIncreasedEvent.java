package com.smartgov.lez.core.environment.graph;

import smartgov.core.agent.moving.events.arc.ArcEvent;

public class PollutionIncreasedEvent extends ArcEvent {

	public PollutionIncreasedEvent(PollutableOsmArc arc) {
		super(arc);
	}

}
