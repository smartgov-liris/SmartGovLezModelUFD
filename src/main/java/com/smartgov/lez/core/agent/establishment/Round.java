package com.smartgov.lez.core.agent.establishment;

import java.util.ArrayList;
import java.util.List;

import smartgov.urban.osm.environment.graph.OsmNode;

public class Round {
	
	private Establishment origin;
	private List<Establishment> establishments;
	private float initialWeight;
	

	public Round(
			Establishment origin,
			List<Establishment> establishments,
			int initialWeight
			) {
		super();
		this.origin = origin;
		this.establishments = establishments;
		this.initialWeight = initialWeight;
	}

	public Establishment getOrigin() {
		return origin;
	}

	public List<Establishment> getEstablishments() {
		return establishments;
	}

	public List<OsmNode> getNodes() {
		List<OsmNode> nodes = new ArrayList<>();
		
		nodes.add(origin.getClosestOsmNode());
		for (Establishment establishment : establishments) {
			nodes.add(establishment.getClosestOsmNode());
		}
		nodes.add(origin.getClosestOsmNode());
		
		return nodes;
	}

}
