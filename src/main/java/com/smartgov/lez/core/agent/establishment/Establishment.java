package com.smartgov.lez.core.agent.establishment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.smartgov.lez.core.agent.driver.DeliveryDriverAgent;
import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;
import com.smartgov.lez.core.output.establishment.FleetSerializer;

import smartgov.core.agent.moving.MovingAgent;
import smartgov.core.agent.moving.ParkingArea;
import smartgov.core.output.node.NodeIdSerializer;
import smartgov.urban.geo.utils.LatLon;
import smartgov.urban.osm.environment.graph.OsmNode;

public class Establishment implements ParkingArea {
	
	private String id;
	private String name;
	private ST8 activity;
	private LatLon location;
	@JsonSerialize(using = NodeIdSerializer.class)
	private OsmNode closestOsmNode;
	
	private Map<String, DeliveryVehicle> fleet;
	
	@JsonIgnore
	private Map<VehicleCapacity, Collection<DeliveryVehicle>> fleetByCapacity;
	
	private Map<String, Round> rounds;
	@JsonIgnore
	private Collection<DeliveryDriverAgent> agents;
	
	
	public Establishment(String id, String name, ST8 activity, LatLon location) {
		this.id = id;
		this.name = name;
		this.activity = activity;
		this.location = location;
		fleet = new HashMap<>();
		fleetByCapacity = new HashMap<>();
		rounds = new HashMap<>();
		agents = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public ST8 getActivity() {
		return activity;
	}

	public LatLon getLocation() {
		return location;
	}

	public void setClosestOsmNode(OsmNode closestOsmNode) {
		this.closestOsmNode = closestOsmNode;
	}

	public OsmNode getClosestOsmNode() {
		return closestOsmNode;
	}
	
	public void addVehicleToFleet(DeliveryVehicle vehicle) {
		VehicleCapacity capacity = new VehicleCapacity(vehicle.getCategory(),  vehicle.getSegment());
		if (!fleetByCapacity.containsKey(capacity)) {
			fleetByCapacity.put(capacity, new ArrayList<>());
		}
		fleetByCapacity.get(capacity).add(vehicle);
		fleet.put(vehicle.getId(), vehicle);
	}
	
	public void addRound(DeliveryVehicle initialVehicle, Round round) {
		rounds.put(initialVehicle.getId(), round);
	}

	public Map<String, DeliveryVehicle> getFleet() {
		return fleet;
	}

	public Map<VehicleCapacity, Collection<DeliveryVehicle>> getFleetByCapacity() {
		return fleetByCapacity;
	}

	public Map<String, Round> getRounds() {
		return rounds;
	}
	
	public void addAgent(DeliveryDriverAgent agent) {
		agents.add(agent);
	}
	
	public Collection<DeliveryDriverAgent> getAgents() {
		return agents;
	}

	@Override
	public void enter(MovingAgent agent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void leave(MovingAgent agent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int spaceLeft() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		return "Establishment [id=" + id + ", name=" + name + ", activity=" + activity + ", location=" + location
				+ ", fleet=" + fleet + ", rounds=" + rounds + "]";
	}

	
}
