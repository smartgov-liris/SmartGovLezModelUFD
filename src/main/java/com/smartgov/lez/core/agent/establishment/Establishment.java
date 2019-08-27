package com.smartgov.lez.core.agent.establishment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.smartgov.lez.core.agent.driver.DeliveryDriverAgent;
import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;

import org.liris.smartgov.simulator.core.agent.moving.MovingAgent;
import org.liris.smartgov.simulator.core.agent.moving.ParkingArea;
import org.liris.smartgov.simulator.core.output.node.NodeIdSerializer;
import org.liris.smartgov.simulator.urban.geo.utils.LatLon;
import org.liris.smartgov.simulator.urban.osm.environment.graph.OsmNode;

/**
 * Represents establishments that can receive and perform deliveries.
 *
 * <p>
 * This class implements the ParkingArea interface, to represent the fact
 * that vehicle are waiting outside of the road graph between each delivery.
 * </p>
 */
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
	
	
	/**
	 * Establishment constructor.
	 *
	 * @param id establishment id
	 * @param name name
	 * @param activity ST8 category
	 * @param location geographic location
	 */
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

	/**
	 * Returns establishment's id.
	 *
	 * @return establishment's id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns establishment's name.
	 *
	 * @return establishment's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns establishment's activity.
	 *
	 * @return establishment's activity
	 */
	public ST8 getActivity() {
		return activity;
	}

	/**
	 * Returns establishment's geographical location.
	 *
	 * @return establishment's geographical location
	 */
	public LatLon getLocation() {
		return location;
	}

	/**
	 * Sets the closest available osm node of this establishment, that
	 * belongs to a road.
	 *
	 * @param closestOsmNode closest available osm node
	 */
	public void setClosestOsmNode(OsmNode closestOsmNode) {
		this.closestOsmNode = closestOsmNode;
	}

	/**
	 * Returns this establishment's closest osm node, that can be used to
	 * perform deliveries.
	 *
	 * @return closest osm node
	 */
	public OsmNode getClosestOsmNode() {
		return closestOsmNode;
	}
	
	/**
	 * Adds a vehicle to the establishment's fleet.
	 *
	 * <p>
	 * Vehicles are indexed by their internal DeliveryVehicle ids.
	 * </p>
	 * <p>
	 * In the current context, the vehicle's id does not have to be unique
	 * in the complete simulation, and only need to be unique by
	 * establishment, and so does the {@link
	 * com.smartgov.lez.input.establishment.EstablishmentLoader}.
	 * </p>
	 *
	 * @param vehicle vehicle to add to the fleet
	 */
	public void addVehicleToFleet(DeliveryVehicle vehicle) {
		VehicleCapacity capacity = new VehicleCapacity(vehicle.getCategory(),  vehicle.getSegment());
		if (!fleetByCapacity.containsKey(capacity)) {
			fleetByCapacity.put(capacity, new ArrayList<>());
		}
		fleetByCapacity.get(capacity).add(vehicle);
		fleet.put(vehicle.getId(), vehicle);
	}
	
	/**
	 * Adds a round to be performed by the specified vehicle.
	 *
	 * <p>
	 * Rounds are indexed by vehicle ids.
	 * </p>
	 *
	 * @param vehicle vehicle that can perform this round
	 * @param round round to perform
	 */
	public void addRound(DeliveryVehicle vehicle, Round round) {
		rounds.put(vehicle.getId(), round);
	}

	/**
	 * Returns the establishment vehicles, indexed by their ids.
	 *
	 * @return estalishment's fleet
	 */
	public Map<String, DeliveryVehicle> getFleet() {
		return fleet;
	}

	/**
	 * Returns a convenient fleet representation, indexed by vehicles
	 * capacities.
	 *
	 * <p>
	 * Can be used to easily associate vehicles to rounds according to
	 * their respective capacities and weight for example.
	 * </p>
	 *
	 * @return fleet representation, indexed by vehicle capacities
	 */
	public Map<VehicleCapacity, Collection<DeliveryVehicle>> getFleetByCapacity() {
		return fleetByCapacity;
	}

	/**
	 * Returns rounds that must be performed by this establishment,
	 * <b>indexed by delivery vehicle ids that must perform each round.</b>
	 *
	 * @return establishment's rounds
	 */
	public Map<String, Round> getRounds() {
		return rounds;
	}
	
	/**
	 * Adds an agent to the establishment.
	 *
	 * <p>
	 * Such agents are not direcly used by the establishments,
	 * but represents a nice way to monitor agents and establishments
	 * behavior when agents added are agents corresponding to
	 * DeliveryDriverBodies that perform establishment's rounds (as it's
	 * done currently).
	 * </p>
	 *
	 * @param agent agent to add to this establishment
	 */
	public void addAgent(DeliveryDriverAgent agent) {
		agents.add(agent);
	}
	
	/**
	 * Returns agents belonging to this establishment.
	 *
	 * <p>
	 * Currently, corresponds to agents associated to the delivery drivers
	 * that performs establishment's rounds.
	 * </p>
	 *
	 * @return establishment's agents
	 */
	public Collection<DeliveryDriverAgent> getAgents() {
		return agents;
	}

	/**
	 * Currently, does not does anything special, but allows vehicles to
	 * leave the graph to enter this establishment.
	 *
	 * @param agent delivery agent entering this establishment
	 */
	@Override
	public void enter(MovingAgent agent) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Currently, does not does anything special, but allows vehicles to
	 * leave this establishment and enter the road graph.
	 *
	 * @param agent delivery agent leaving this establishment
	 */
	@Override
	public void leave(MovingAgent agent) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Unused.
	 *
	 * @return 0
	 */
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
