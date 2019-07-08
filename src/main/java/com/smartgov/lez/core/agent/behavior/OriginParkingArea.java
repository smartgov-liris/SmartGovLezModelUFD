package com.smartgov.lez.core.agent.behavior;

import java.util.Collection;

import smartgov.core.agent.moving.MovingAgent;
import smartgov.core.agent.moving.ParkingArea;
import smartgov.urban.osm.environment.graph.OsmNode;

public class OriginParkingArea implements ParkingArea {

	private Collection<MovingAgent> agents;
	private OsmNode node;
	
	public OriginParkingArea(OsmNode node) {
		this.node = node;
	}
	
	public OsmNode getNode() {
		return node;
	}
	
	@Override
	public void enter(MovingAgent agent) {
		this.agents.add(agent);
	}

	@Override
	public void leave(MovingAgent agent) {
		this.agents.remove(agent);
	}

	@Override
	public int spaceLeft() {
		return Integer.MAX_VALUE;
	}

}
