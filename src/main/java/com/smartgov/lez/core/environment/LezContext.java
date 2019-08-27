package com.smartgov.lez.core.environment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.smartgov.lez.core.agent.establishment.Establishment;
import com.smartgov.lez.core.agent.establishment.Round;
import com.smartgov.lez.core.environment.lez.Lez;
import com.smartgov.lez.core.simulation.scenario.DeliveriesScenario;
import com.smartgov.lez.core.simulation.scenario.RandomTrafficPollutionScenario;
import com.smartgov.lez.input.lez.CritAirLezDeserializer;

import org.liris.smartgov.simulator.core.scenario.Scenario;
import org.liris.smartgov.simulator.urban.osm.environment.OsmContext;

public class LezContext extends OsmContext {
	
	private Map<String, Establishment> establishments;
	public Map<String, Round> ongoingRounds;

	public LezContext(String configFile) {
		super(configFile);
		this.establishments = new HashMap<>();
		this.ongoingRounds = new TreeMap<>();
	}

	public Map<String, Establishment> getEstablishments() {
		return establishments;
	}

	public void setEstablishments(Map<String, Establishment> establishments) {
		this.establishments = establishments;
	}

	
	@Override
	protected Scenario loadScenario(String scenarioName) {
		Scenario superScenario = super.loadScenario(scenarioName);
		if (superScenario != null) {
			return superScenario;
		}
		switch(scenarioName){
			case RandomTrafficPollutionScenario.name:
				return new RandomTrafficPollutionScenario(Lez.none());
			case DeliveriesScenario.NoLezDeliveries.name:
				return new DeliveriesScenario.NoLezDeliveries();
			case DeliveriesScenario.name:
				try {
					return new DeliveriesScenario(
							CritAirLezDeserializer.load(
									this.getFileLoader().load("lez")
									)
							);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			default:
				return null;
		}
	}
}
