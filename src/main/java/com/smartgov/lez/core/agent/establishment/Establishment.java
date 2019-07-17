package com.smartgov.lez.core.agent.establishment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.locationtech.jts.geom.Coordinate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;

import smartgov.core.agent.moving.MovingAgent;
import smartgov.core.agent.moving.ParkingArea;
import smartgov.urban.osm.environment.graph.OsmNode;

public class Establishment implements ParkingArea {
	
	private String id;
	private String name;
	private ST8 activity;
	private Coordinate location;
	private OsmNode closestOsmNode;

	private Map<VehicleCapacity, Collection<DeliveryVehicle>> fleet;
	private Map<VehicleCapacity, Round> rounds;
	
	
	public Establishment(String id, String name, ST8 activity, Coordinate location) {
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

	public Coordinate getLocation() {
		return location;
	}

	public OsmNode getClosestOsmNode() {
		return closestOsmNode;
	}
	
	public void addVehicleToFleet(DeliveryVehicle vehicle) {
		VehicleCapacity capacity = new VehicleCapacity(vehicle.getCategory(),  vehicle.getVehicleSegment());
		if (!fleet.containsKey(capacity)) {
			fleet.put(capacity, new ArrayList<>());
		}
		fleet.get(capacity).add(vehicle);
	}

	public Map<VehicleCapacity, Collection<DeliveryVehicle>> getFleet() {
		return fleet;
	}

	public Map<VehicleCapacity, Round> getRounds() {
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
