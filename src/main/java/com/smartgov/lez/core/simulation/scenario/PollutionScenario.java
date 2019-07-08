package com.smartgov.lez.core.simulation.scenario;

import com.smartgov.lez.core.environment.graph.PollutableOsmArcFactory;

import smartgov.urban.osm.environment.graph.OsmNode;
import smartgov.urban.osm.environment.graph.Road;
import smartgov.urban.osm.scenario.GenericOsmScenario;


public abstract class PollutionScenario extends GenericOsmScenario<OsmNode, Road> {
	
	public PollutionScenario() {
		super(OsmNode.class, Road.class, new PollutableOsmArcFactory());
	}
		
}
