package com.smartgov.lez.core.environment;

import com.smartgov.lez.core.simulation.scenario.RandomTrafficPollutionScenario;

import smartgov.core.scenario.Scenario;
import smartgov.urban.osm.environment.OsmContext;

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
			case RandomTrafficPollutionScenario.name:
				return new RandomTrafficPollutionScenario();
			default:
				return null;
		}
	}
}
