package org.liris.smartgov.lez.core.agent.driver.behavior;

import org.liris.smartgov.lez.core.agent.driver.DeliveryDriverBody;
import org.liris.smartgov.lez.core.environment.lez.Lez;
import org.liris.smartgov.simulator.core.environment.SmartGovContext;
import org.liris.smartgov.simulator.core.environment.graph.Node;
import org.liris.smartgov.simulator.urban.geo.agent.behavior.GeoMovingBehavior;

/**
 * Abstract behavior that describes the behavior of an agent
 * moving in an urban environment including a Low Emission
 * Zone.
 */
public abstract class LezBehavior extends GeoMovingBehavior {

	/**
	 * LezBehavior constructor.
	 * <p>
	 * The costs associated to arcs for this behavior are
	 * retrieved from the {@link org.liris.smartgov.lez.core.environment.lez.Lez#costs}
	 * function, applied to the delivery driver's current vehicle.
	 * </p>
	 *
	 * @param agentBody delivery driver body
	 * @param origin initial origin
	 * @param destination initial destination
	 * @param context current context
	 * @param lez current lez
	 */
	public LezBehavior(
			DeliveryDriverBody agentBody,
			Node origin,
			Node destination,
			SmartGovContext context,
			Lez lez
			) {
		super(agentBody, origin, destination, context, lez.costs(agentBody.getVehicle()));
	}

}
