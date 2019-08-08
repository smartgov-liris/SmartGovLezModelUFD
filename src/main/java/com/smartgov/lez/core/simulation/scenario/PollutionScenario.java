package com.smartgov.lez.core.simulation.scenario;

import com.smartgov.lez.core.environment.graph.PollutableOsmArcFactory;
import com.smartgov.lez.core.environment.lez.Lez;

import smartgov.urban.osm.environment.graph.OsmNode;
import smartgov.urban.osm.environment.graph.Road;
import smartgov.urban.osm.scenario.GenericOsmScenario;


public abstract class PollutionScenario extends GenericOsmScenario<OsmNode, Road> {
	
	private Lez lez;
	
	public PollutionScenario(Lez lez) {
		super(OsmNode.class, Road.class, new PollutableOsmArcFactory(lez));
		this.lez = lez;
	}
	
	public PollutionScenario() {
		this(Lez.none());
	}
	
	public Lez getLez() {
		return lez;
	}
		
}
