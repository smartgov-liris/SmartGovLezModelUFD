package com.smartgov.lez.core.simulation.scenario;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.smartgov.lez.SmartgovLezApplication;
import com.smartgov.lez.core.agent.driver.DeliveryDriverAgent;
import com.smartgov.lez.core.agent.driver.DeliveryDriverBody;
import com.smartgov.lez.core.agent.driver.behavior.DeliveryDriverBehavior;
import com.smartgov.lez.core.agent.establishment.Establishment;
import com.smartgov.lez.core.copert.tableParser.CopertParser;
import com.smartgov.lez.core.environment.LezContext;
import com.smartgov.lez.core.environment.graph.PollutableOsmArcFactory;
import com.smartgov.lez.input.establishment.EstablishmentLoader;

import smartgov.SmartGov;
import smartgov.core.agent.core.Agent;
import smartgov.core.environment.SmartGovContext;
import smartgov.core.environment.graph.Node;
import smartgov.urban.geo.environment.graph.GeoKdTree;
import smartgov.urban.geo.utils.lonLat.LonLat;
import smartgov.urban.osm.agent.OsmAgent;
import smartgov.urban.osm.environment.graph.OsmNode;
import smartgov.urban.osm.environment.graph.Road;
import smartgov.urban.osm.environment.graph.tags.Highway;
import smartgov.urban.osm.utils.OsmArcsBuilder;

public class DeliveriesScenario extends PollutionScenario {
	
	public static final String name = "Deliveries";
	public static final Highway[] forbiddenClosestNodeHighways = {
			Highway.MOTORWAY,
			Highway.MOTORWAY_LINK,
			Highway.TRUNK,
			Highway.TRUNK_LINK
	};

	@Override
	public Collection<? extends Agent<?>> buildAgents(SmartGovContext context) {
		int deadEnds = 0;
		for(Node node : context.nodes.values()) {
			if(node.getOutgoingArcs().isEmpty() || node.getIncomingArcs().isEmpty()) {
				deadEnds++;
				Road road = ((OsmNode) node).getRoad();
				SmartgovLezApplication.logger.info("Dead end found on node " + node.getId() + ", road " + road.getId());
			}
		}
		SmartgovLezApplication.logger.info(deadEnds + " dead ends found.");
		
		OsmArcsBuilder.fixDeadEnds((LezContext) context, new PollutableOsmArcFactory());

		// All the vehicles will belong to the loaded copert table
		CopertParser parser = new CopertParser(context.getFileLoader().load("copert_table"), new Random(240720191835l));
		Map<String, Establishment> establishments = null;
		try {
			establishments = 
					EstablishmentLoader.loadEstablishments(
							context.getFileLoader().load("establishments"),
							context.getFileLoader().load("fleet_profiles"),
							parser
							);
		} catch (IOException e) {
			e.printStackTrace();
		}
		((LezContext) context).setEstablishments(establishments);
		
		Map<String, OsmNode> geoNodes = new HashMap<>();
		for (String id : context.nodes.keySet()) {
			OsmNode node = (OsmNode) context.nodes.get(id);
			if(!Arrays.asList(forbiddenClosestNodeHighways).contains(node.getRoad().getHighway()))
					geoNodes.put(id, node);
		}
		GeoKdTree kdTree = new GeoKdTree(geoNodes);
		
		for (Establishment establishment : establishments.values()) {
			establishment.setClosestOsmNode((OsmNode) kdTree.getNearestNodeFrom(
					new LonLat().project(establishment.getLocation())
					)
				);
		}
		
		int agentId = 0;
		Collection<OsmAgent> agents = new ArrayList<>();
		Collection<BuildAgentThread> threads = new ArrayList<>();
		
		for (Establishment establishment : establishments.values()) {
			for(String vehicleId : establishment.getRounds().keySet()) {
//				DeliveryDriverBody driver = new DeliveryDriverBody(establishment.getFleet().get(vehicleId));
//				DeliveryDriverBehavior behavior
//					= new DeliveryDriverBehavior(
//							driver,
//							establishment.getRounds().get(vehicleId),
//							context
//							);
//				
//				DeliveryDriverAgent agent = new DeliveryDriverAgent(String.valueOf(agentId++), driver, behavior);
//				agents.add(agent);
				BuildAgentThread thread = new BuildAgentThread(agentId++, vehicleId, establishment, context);
				threads.add(thread);
				thread.start();
			}
		}
		for(BuildAgentThread thread : threads) {
			try {
				thread.join();
				agents.add(thread.getBuiltAgent());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		return agents;
	}
	
	private static class BuildAgentThread extends Thread {
		
		private int agentId;
		private String vehicleId;
		private Establishment establishment;
		private SmartGovContext context;
		
		private DeliveryDriverAgent builtAgent;
		private DeliveryDriverBehavior builtBehavior;
		
		public BuildAgentThread(int agentId, String vehicleId, Establishment establishment, SmartGovContext context) {
			super();
			this.agentId = agentId;
			this.vehicleId = vehicleId;
			this.establishment = establishment;
			this.context = context;
		}

		public void run() {
			DeliveryDriverBody driver = new DeliveryDriverBody(establishment.getFleet().get(vehicleId));
			builtBehavior
				= new DeliveryDriverBehavior(
						driver,
						establishment.getRounds().get(vehicleId),
						context
						);
			
			builtAgent = new DeliveryDriverAgent(String.valueOf(agentId), driver, builtBehavior);
			builtBehavior.addRoundEndListener((event) ->
				SmartgovLezApplication.logger.info("Agent " + builtAgent.getId()
					+ " ended its round for " + establishment.getName() + " at "
					+ SmartGov.getRuntime().getClock().getHour() + ":" + SmartGov.getRuntime().getClock().getMinutes())
				);
		}
		
		/*
		 * Listeners are initialized there from the main thread to avoid
		 * concurrent modifications errors.
		 */
		public DeliveryDriverAgent getBuiltAgent() {
			builtBehavior.setUpListeners();
			return builtAgent;
		}
		
	}

}
