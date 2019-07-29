package com.smartgov.lez.core.environment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.smartgov.lez.core.agent.establishment.Establishment;
import com.smartgov.lez.core.agent.establishment.Round;
import com.smartgov.lez.core.simulation.scenario.DeliveriesScenario;
import com.smartgov.lez.core.simulation.scenario.RandomTrafficPollutionScenario;

import smartgov.core.scenario.Scenario;
import smartgov.urban.osm.environment.OsmContext;

public class LezContext extends OsmContext {
	
	private Map<String, Establishment> establishments;
	public Collection<Round> ongoingRounds;

	public LezContext(String configFile) {
		super(configFile);
		this.establishments = new HashMap<>();
		this.ongoingRounds = new ArrayList<>();
	}

	public Map<String, Establishment> getEstablishments() {
		return establishments;
	}

	public void setEstablishments(Map<String, Establishment> establishments) {
		this.establishments = establishments;
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
			case DeliveriesScenario.name:
				return new DeliveriesScenario();
			default:
				return null;
		}
	}
}
