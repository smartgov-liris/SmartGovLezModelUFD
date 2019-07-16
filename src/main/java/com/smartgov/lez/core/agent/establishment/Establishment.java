package com.smartgov.lez.core.agent.establishment;

import java.util.Map;

import org.locationtech.jts.geom.Coordinates;

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
	private Coordinates coordinates;
	private OsmNode closestOsmNode;

	private Map<VehicleCapacity, DeliveryVehicle> fleet;
	private Map<VehicleCapacity, Round> rounds;
	
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public ST8 getActivity() {
		return activity;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}

	public OsmNode getClosestOsmNode() {
		return closestOsmNode;
	}

	public Map<VehicleCapacity, DeliveryVehicle> getFleet() {
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

}
