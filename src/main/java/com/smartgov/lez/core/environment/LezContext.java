package com.smartgov.lez.core.environment;

import com.smartgov.lez.core.simulation.scenario.PollutionScenario;

import smartgov.core.simulation.Scenario;
import smartgov.urban.osm.environment.OsmContext;
import smartgov.urban.osm.environment.graph.OsmNode;

public class LezContext extends OsmContext {

	public LezContext(String configFile) {
		super(configFile);
	}

	@Override
	public Scenario loadScenario(String scenarioName) {
		Scenario superScenario = super.loadScenario(scenarioName);
		if (superScenario != null) {
			return superScenario;
		}
		switch(scenarioName){
			case PollutionScenario.name:
				return new PollutionScenario();
			default:
				return null;
		}
	}
}
