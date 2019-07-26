package com.smartgov.lez.core.environment.lez;

import org.graphstream.algorithm.AStar;

import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;

public interface LezCriteria {
	
	public boolean isAllowed(DeliveryVehicle vehicle);
	public AStar.Costs entryCosts(DeliveryVehicle vehicle);

}
