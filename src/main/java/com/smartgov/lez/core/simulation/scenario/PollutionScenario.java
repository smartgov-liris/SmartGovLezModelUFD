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
import com.smartgov.lez.core.environment.graph.PollutableOsmArc;
import com.smartgov.lez.core.environment.graph.PollutableOsmArcFactory;

import smartgov.core.agent.core.Agent;
import smartgov.core.environment.SmartGovContext;
import smartgov.urban.osm.agent.OsmAgent;
import smartgov.urban.osm.agent.OsmAgentBody;
import smartgov.urban.osm.environment.OsmContext;
import smartgov.urban.osm.environment.graph.OsmArc;
import smartgov.urban.osm.environment.graph.OsmNode;
import smartgov.urban.osm.environment.graph.Road;
import smartgov.urban.osm.simulation.scenario.BasicOsmScenario;
import smartgov.urban.osm.simulation.scenario.GenericOsmScenario;
import smartgov.urban.osm.simulation.scenario.lowLayer.RandomTrafficScenario;


public class PollutionScenario extends GenericOsmScenario<OsmNode, Road> {
	
	public PollutionScenario() {
		super(OsmNode.class, Road.class, new PollutableOsmArcFactory());
		// TODO Auto-generated constructor stub
	}


	public static final String name = "Pollution";


	/**
	 * Override the createArc method of the OsmScenario, so that PollutableOsmArcs will
	 * be created instead of normal OsmArcs by the OsmJSONReader.
	 * 
	 */
//	@Override
//	public OsmArc createArc(
//			String id,
//			Road road,
//			OsmNode startNode,
//			OsmNode targetNode,
//			int lanes,
//			String type) {
//		return new PollutableOsmArc(
//				id,
//				road,
//				startNode,
//				targetNode,
//				lanes,
//				type
//				);
//	}
	
//	@Override
//	public OsmAgentBody createAgentBody(
//			OsmContext environment) {
//		return new DeliveryDriver(
//				vehiclesStock.poll(),
//				(OsmContext) environment);
//	}

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
