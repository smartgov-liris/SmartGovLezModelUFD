package com.smartgov.lez.core.agent.driver;

import com.smartgov.lez.core.agent.driver.mover.PollutantCarMover;
import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;

import smartgov.urban.osm.agent.OsmAgentBody;

public class DeliveryDriverBody extends OsmAgentBody {
	
	private DeliveryVehicle vehicle;

	public DeliveryDriverBody(DeliveryVehicle vehicle) {
		super(new PollutantCarMover());
		this.vehicle = vehicle;
	}
	
	public DeliveryVehicle getVehicle() {
		return vehicle;
	}

}
