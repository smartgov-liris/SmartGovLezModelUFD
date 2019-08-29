package org.liris.smartgov.lez.core.environment.lez.criteria;

import org.liris.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;

/**
 * Interface used to determines if a vehicle is allowed or not in a Low Emission Zone.
 * 
 */
public interface LezCriteria {
	
	/**
	 * Returns true if and only if the specified vehicle is allowed
	 * to enter the low emission zone.
	 * 
	 * @param vehicle vehicle
	 * @return vehicle permission
	 */
	public boolean isAllowed(DeliveryVehicle vehicle);

}
