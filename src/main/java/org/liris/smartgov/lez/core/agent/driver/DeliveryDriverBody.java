package org.liris.smartgov.lez.core.agent.driver;

import org.liris.smartgov.lez.core.agent.driver.mover.PollutantCarMover;
import org.liris.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;
import org.liris.smartgov.simulator.urban.osm.agent.OsmAgentBody;

/**
 * OsmAgentBody with an associated DeliveryVehicle to perform deliveries.
 */
public class DeliveryDriverBody extends OsmAgentBody {
	
	private DeliveryVehicle vehicle;

	/**
	 * DeliveryDriverBody constructor.
	 *
	 * @param vehicle delivery vehicle
	 */
	public DeliveryDriverBody(DeliveryVehicle vehicle) {
		super(new PollutantCarMover());
		this.vehicle = vehicle;
	}
	
	/**
	 * Returns the delivery vehicle associated to this agent body.
	 *
	 * @return agent body's delivery vehicle
	 */
	public DeliveryVehicle getVehicle() {
		return vehicle;
	}

}
