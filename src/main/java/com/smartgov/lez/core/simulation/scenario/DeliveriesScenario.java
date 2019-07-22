package com.smartgov.lez.core.simulation.scenario;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.smartgov.lez.core.agent.driver.DeliveryDriverAgent;
import com.smartgov.lez.core.agent.driver.DeliveryDriverBody;
import com.smartgov.lez.core.agent.driver.behavior.DeliveryDriverBehavior;
import com.smartgov.lez.core.agent.establishment.Establishment;
import com.smartgov.lez.core.environment.LezContext;
import com.smartgov.lez.input.establishment.EstablishmentLoader;

import smartgov.core.agent.core.Agent;
import smartgov.core.environment.SmartGovContext;
import smartgov.core.simulation.time.Date;
import smartgov.core.simulation.time.WeekDay;
import smartgov.urban.geo.environment.graph.GeoKdTree;
import smartgov.urban.geo.utils.lonLat.LonLat;
import smartgov.urban.osm.agent.OsmAgent;
import smartgov.urban.osm.environment.graph.OsmNode;

public class DeliveriesScenario extends PollutionScenario {
	
	public static final String name = "Deliveries";

	@Override
	public Collection<? extends Agent<?>> buildAgents(SmartGovContext context) {
		Map<String, Establishment> establishments = null;
		try {
			establishments = 
					EstablishmentLoader.loadEstablishments(
							context.getFileLoader().load("establishments"),
							context.getFileLoader().load("fleet_profiles"),
							context.getFileLoader().load("copert_table")
							);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		((LezContext) context).setEstablishments(establishments);
		
		Map<String, OsmNode> geoNodes = new HashMap<>();
		for (String id : context.nodes.keySet()) {
			geoNodes.put(id, (OsmNode) context.nodes.get(id));
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
		int i = 0;
		for (Establishment establishment : establishments.values()) {
			for(String vehicleId : establishment.getRounds().keySet()) {
				DeliveryDriverBody driver = new DeliveryDriverBody(establishment.getFleet().get(vehicleId));
				DeliveryDriverBehavior behavior
					= new DeliveryDriverBehavior(
							driver,
							establishment.getRounds().get(vehicleId),
							new Date(0, WeekDay.MONDAY, 0, i++),
							context
							);
				
				DeliveryDriverAgent agent = new DeliveryDriverAgent(String.valueOf(agentId++), driver, behavior);
				agents.add(agent);
			}
		}

		return agents;
	}

}
