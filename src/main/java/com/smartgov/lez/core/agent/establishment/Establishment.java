package com.smartgov.lez.core.agent.establishment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;

import smartgov.core.agent.moving.MovingAgent;
import smartgov.core.agent.moving.ParkingArea;
import smartgov.urban.geo.utils.LatLon;
import smartgov.urban.osm.environment.graph.OsmNode;

public class Establishment implements ParkingArea {
	
	private String id;
	private String name;
	private ST8 activity;
	private LatLon location;
	private OsmNode closestOsmNode;

	private Map<VehicleCapacity, Collection<DeliveryVehicle>> fleet;
	private int fleetSize = 0;
	private Map<DeliveryVehicle, Round> rounds;
	
	
	public Establishment(String id, String name, ST8 activity, LatLon location) {
		this.id = id;
		this.name = name;
		this.activity = activity;
		this.location = location;
		fleet = new HashMap<>();
		rounds = new HashMap<>();
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
		if (!fleet.containsKey(capacity)) {
			fleet.put(capacity, new ArrayList<>());
		}
		fleet.get(capacity).add(vehicle);
		fleetSize++;
	}
	
	public void addRound(DeliveryVehicle initialVehicle, Round round) {
		rounds.put(initialVehicle, round);
	}

	public Map<VehicleCapacity, Collection<DeliveryVehicle>> getFleet() {
		return fleet;
	}
	
	public int getFleetSize() {
		return fleetSize;
	}

	public Map<DeliveryVehicle, Round> getRounds() {
		return rounds;
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
