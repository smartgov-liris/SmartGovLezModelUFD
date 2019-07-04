package com.smartgov.lez.core.agent;

import com.smartgov.lez.core.agent.mover.PollutantCarMover;
import com.smartgov.lez.core.agent.vehicle.DeliveryVehicle;

import smartgov.urban.osm.agent.OsmAgentBody;
import smartgov.urban.osm.environment.OsmContext;

public class DeliveryDriver extends OsmAgentBody {
	
	private DeliveryVehicle vehicle;

	public DeliveryDriver(
			DeliveryVehicle vehicle,
			OsmContext environment) {
		super(new PollutantCarMover());
		this.vehicle = vehicle;
	}
	
	public DeliveryVehicle getVehicle() {
		return vehicle;
	}

}
