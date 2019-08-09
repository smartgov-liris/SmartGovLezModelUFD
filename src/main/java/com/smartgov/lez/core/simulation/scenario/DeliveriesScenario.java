package com.smartgov.lez.core.simulation.scenario;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.smartgov.lez.SmartgovLezApplication;
import com.smartgov.lez.core.agent.driver.DeliveryDriverAgent;
import com.smartgov.lez.core.agent.driver.DeliveryDriverBody;
import com.smartgov.lez.core.agent.driver.behavior.DeliveryDriverBehavior;
import com.smartgov.lez.core.agent.establishment.Establishment;
import com.smartgov.lez.core.agent.establishment.preprocess.LezPreprocessor;
import com.smartgov.lez.core.copert.tableParser.CopertParser;
import com.smartgov.lez.core.environment.LezContext;
import com.smartgov.lez.core.environment.graph.PollutableOsmArcFactory;
import com.smartgov.lez.core.environment.lez.Lez;
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


	public static final String name = "LezDeliveries";
	public static final Highway[] forbiddenClosestNodeHighways = {
			Highway.MOTORWAY,
			Highway.MOTORWAY_LINK,
			Highway.TRUNK,
			Highway.TRUNK_LINK,
			Highway.LIVING_STREET,
			Highway.SERVICE
	};
	
	public DeliveriesScenario(Lez lez) {
		super(lez);
	}
	
	@Override
	public Collection<? extends Agent<?>> buildAgents(SmartGovContext context) {
		int deadEnds = 0;
		for(Node node : context.nodes.values()) {
			if(node.getOutgoingArcs().isEmpty() || node.getIncomingArcs().isEmpty()) {
				deadEnds++;
				Road road = ((OsmNode) node).getRoad();
				// SmartgovLezApplication.logger.debug("Dead end found on node " + node.getId() + ", road " + road.getId());
			}
		}
		SmartgovLezApplication.logger.info(deadEnds + " dead ends found.");
		
		OsmArcsBuilder.fixDeadEnds((LezContext) context, new PollutableOsmArcFactory(getLez()));

		// All the vehicles will belong to the loaded copert table
		CopertParser parser = loadParser(context);
		
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
		
		SmartgovLezApplication.logger.info("Applying lez...");
		LezPreprocessor preprocessor = new LezPreprocessor(getLez(), parser);

		int establishmentsInLez = 0;
		int totalVehiclesReplaced = 0;
		for(Establishment establishment : establishments.values()) {
			if(getLez().contains(establishment.getClosestOsmNode())) {
				SmartgovLezApplication.logger.info("[LEZ] " + establishment.getId() + " - " + establishment.getName());
				int replacedVehiclesCount = preprocessor.preprocess(establishment);
				totalVehiclesReplaced += replacedVehiclesCount;
				SmartgovLezApplication.logger.info("[LEZ] Number of vehicles replaced : " + replacedVehiclesCount);
				establishmentsInLez++;
			}
		}
		SmartgovLezApplication.logger.info("[LEZ] Number of estbalishments in lez : " + establishmentsInLez);
		SmartgovLezApplication.logger.info("[LEZ] Total number of vehicles replaced : " + totalVehiclesReplaced);
		
		int agentId = 0;
		Collection<OsmAgent> agents = new ArrayList<>();
		Collection<BuildAgentThread> threads = new ArrayList<>();
		
		for (Establishment establishment : establishments.values()) {
			for(String vehicleId : establishment.getRounds().keySet()) {
				BuildAgentThread thread = new BuildAgentThread(agentId++, vehicleId, establishment, (LezContext) context);
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
		private LezContext context;
		
		private DeliveryDriverAgent builtAgent;
		private DeliveryDriverBehavior builtBehavior;
		
		public BuildAgentThread(int agentId, String vehicleId, Establishment establishment, LezContext context) {
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

			builtBehavior.addRoundDepartureListener((event) -> {
				SmartgovLezApplication.logger.info(
				"[" + SmartGov.getRuntime().getClock().getHour()
				+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
				+ "Agent " + builtAgent.getId()
				+ " begins round for [" + establishment.getId() + "] "
				+ establishment.getName());
			});
				
			builtBehavior.addRoundEndListener((event) -> {
				SmartgovLezApplication.logger.info(
				"[" + SmartGov.getRuntime().getClock().getHour()
				+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
				+ "Agent " + builtAgent.getId()
				+ " ended round for [" + establishment.getId() + "] "
				+ establishment.getName());
				context.ongoingRounds.remove(builtAgent.getId());
				SmartgovLezApplication.logger.info("Rounds still ongoing : " + context.ongoingRounds.size());
				if(context.ongoingRounds.isEmpty()) {
					SmartGov.getRuntime().stop();
				}
			});
			SmartgovLezApplication.logger.info("Agent " + builtAgent.getId() + " : " + builtBehavior.getRound());
		}
		
		/*
		 * Listeners are initialized there from the main thread to avoid
		 * concurrent modifications errors.
		 * Rounds are also added to the context there, for the same reasons.
		 */
		public DeliveryDriverAgent getBuiltAgent() {
			builtBehavior.setUpListeners();
			context.ongoingRounds.put(builtAgent.getId(), builtBehavior.getRound());
			return builtAgent;
		}
		
	}
	
	public static class NoLezDeliveries extends DeliveriesScenario {
		
		public static final String name = "Deliveries";

		public NoLezDeliveries() {
			super(Lez.none());
		}
		
	}

}
