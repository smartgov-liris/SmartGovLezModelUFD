package com.smartgov.lez.core.environment.graph;

import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.smartgov.lez.core.copert.fields.Pollutant;
import com.smartgov.lez.core.environment.pollution.Pollution;
import com.smartgov.lez.core.output.PollutionSerializer;

import smartgov.core.events.EventHandler;
import smartgov.urban.osm.environment.graph.OsmArc;
import smartgov.urban.osm.environment.graph.OsmNode;
import smartgov.urban.osm.environment.graph.Road;

/**
 * An OsmArc that can be polluted with some particles.
 * 
 * @author pbreugnot
 *
 */
public class PollutableOsmArc extends OsmArc {

	// @JsonSerialize(using=PollutionSerializer.class)
	private Pollution pollution;
	
	private Collection<EventHandler<PollutionIncreasedEvent>> pollutionIncreasedListeners;
	
	public PollutableOsmArc(
			String id,
			OsmNode startNode,
			OsmNode targetNode,
			Road road,
			RoadDirection roadDirection) {
		super(id, startNode, targetNode, road, roadDirection);
		pollution = new Pollution();
		pollutionIncreasedListeners = new ArrayList<>();
	}
	
	public void increasePollution(Pollutant pollutant, double increment) {
		pollution.get(pollutant).increasePollution(increment);
	}
	
	public Pollution getPollution() {
		return pollution;
	}
	
	public void addPollutionIncreasedListener(EventHandler<PollutionIncreasedEvent> pollutionIncreasedListener) {
		this.pollutionIncreasedListeners.add(pollutionIncreasedListener);
	}
	
	public void triggerPollutionIncreasedListeners(PollutionIncreasedEvent event) {
		for(EventHandler<PollutionIncreasedEvent> listener : pollutionIncreasedListeners) {
			listener.handle(event);
		}
	}
}
