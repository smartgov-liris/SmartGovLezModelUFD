package com.smartgov.lez.core.agent.establishment;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.smartgov.lez.core.output.establishment.EstablishmentListIdSerializer;

import smartgov.urban.osm.environment.graph.OsmNode;

public class Round {
	
	@JsonIgnore
	private Establishment origin;
	@JsonSerialize(using = EstablishmentListIdSerializer.class)
	private List<Establishment> establishments;
	private double initialWeight;
	

	public Round(
			Establishment origin,
			List<Establishment> establishments,
			double initialWeight
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
	
	public double getInitialWeight() {
		return initialWeight;
	}

	@JsonIgnore
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
