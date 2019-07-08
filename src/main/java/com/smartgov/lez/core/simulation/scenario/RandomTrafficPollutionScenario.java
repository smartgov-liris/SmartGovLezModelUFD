package com.smartgov.lez.core.simulation.scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import com.smartgov.lez.core.agent.DeliveryDriver;
import com.smartgov.lez.core.agent.vehicle.DeliveryVehicle;
import com.smartgov.lez.core.agent.vehicle.DeliveryVehicleFactory;
import com.smartgov.lez.core.copert.inputParser.CopertInputReader;
import com.smartgov.lez.core.copert.inputParser.CopertProfile;
import com.smartgov.lez.core.copert.tableParser.CopertParser;

import smartgov.core.agent.core.Agent;
import smartgov.core.environment.SmartGovContext;
import smartgov.urban.osm.agent.OsmAgent;
import smartgov.urban.osm.agent.OsmAgentBody;
import smartgov.urban.osm.environment.OsmContext;
import smartgov.urban.osm.scenario.lowLayer.RandomTrafficScenario;

public class RandomTrafficPollutionScenario extends PollutionScenario {

	public static final String name = "Pollution";


	@Override
	public Collection<? extends Agent<?>> buildAgents(SmartGovContext context) {
		RandomTrafficScenario.generateSourceAndSinkNodes((OsmContext) context); 
		// Load the copert table
		CopertParser copertParser = new CopertParser(context.getFileLoader().load("copert_table"));
		
		// Load input profiles
		CopertInputReader reader = new CopertInputReader();
		CopertProfile copertProfile = reader.parseInputFile(context.getFileLoader().load("copert_profile"));
		
		// Create a vehicle factory
		DeliveryVehicleFactory vehicleFactory = new DeliveryVehicleFactory(copertProfile, copertParser);
		
		Queue<DeliveryVehicle> vehiclesStock = new LinkedList<>();
		
		// Feed the stock with delivery vehicles
		int vehicleNumber = Integer.parseInt((String) context.getConfig().get("AgentNumber"));
		vehiclesStock.addAll(vehicleFactory.create(vehicleNumber));
		
		Collection<OsmAgent> drivers = new ArrayList<>();
		for(int i = 0; i < vehicleNumber; i++) {
			OsmAgentBody deliveryDriver = new DeliveryDriver(vehiclesStock.poll(), (OsmContext) context);
			drivers.add(OsmAgent.randomTrafficOsmAgent(String.valueOf(i), (OsmContext) context, deliveryDriver));
		}
		return drivers;
	}
}
