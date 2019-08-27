package com.smartgov.lez.core.simulation.scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.smartgov.lez.core.agent.driver.DeliveryDriverBody;
import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;
import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicleFactory;
import com.smartgov.lez.core.copert.inputParser.CopertInputReader;
import com.smartgov.lez.core.copert.inputParser.CopertProfile;
import com.smartgov.lez.core.copert.tableParser.CopertParser;
import com.smartgov.lez.core.environment.lez.Lez;

import org.liris.smartgov.simulator.core.agent.core.Agent;
import org.liris.smartgov.simulator.core.environment.SmartGovContext;
import org.liris.smartgov.simulator.urban.osm.agent.OsmAgent;
import org.liris.smartgov.simulator.urban.osm.agent.OsmAgentBody;
import org.liris.smartgov.simulator.urban.osm.environment.OsmContext;
import org.liris.smartgov.simulator.urban.osm.scenario.lowLayer.RandomTrafficScenario;

public class RandomTrafficPollutionScenario extends PollutionScenario {

	public RandomTrafficPollutionScenario(Lez lez) {
		super(lez);
	}


	public static final String name = "Pollution";


	@Override
	public Collection<? extends Agent<?>> buildAgents(SmartGovContext context) {
		RandomTrafficScenario.generateSourceAndSinkNodes((OsmContext) context); 
		// Load the copert table
		CopertParser copertParser = new CopertParser(context.getFileLoader().load("copert_table"), new Random(1907190831l));
		
		// Load input profiles
		CopertProfile copertProfile = CopertInputReader.parseInputFile(context.getFileLoader().load("copert_profile"));
		
		// Create a vehicle factory
		DeliveryVehicleFactory vehicleFactory = new DeliveryVehicleFactory(copertProfile, copertParser);
		
		Queue<DeliveryVehicle> vehiclesStock = new LinkedList<>();
		
		// Feed the stock with delivery vehicles
		int vehicleNumber = Integer.parseInt((String) context.getConfig().get("AgentNumber"));
		vehiclesStock.addAll(vehicleFactory.create(vehicleNumber, new Random()));
		
		Collection<OsmAgent> drivers = new ArrayList<>();
		for(int i = 0; i < vehicleNumber; i++) {
			OsmAgentBody deliveryDriver = new DeliveryDriverBody(vehiclesStock.poll());
			drivers.add(OsmAgent.randomTrafficOsmAgent(String.valueOf(i), (OsmContext) context, deliveryDriver));
		}
		return drivers;
	}
}
