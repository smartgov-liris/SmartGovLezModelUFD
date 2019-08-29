package org.liris.smartgov.lez.core.agent.driver;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.liris.smartgov.lez.core.agent.driver.behavior.DeliveryDriverBehavior;
import org.liris.smartgov.lez.core.agent.establishment.Establishment;
import org.liris.smartgov.lez.core.output.establishment.EstablishmentIdSerializer;
import org.liris.smartgov.simulator.urban.osm.agent.OsmAgent;

/**
 * An OsmAgent associated to an establishment, that can perform deliveries
 * with a DeliveryDriverBody through a DeliveryDriverBehavior.
 */
public class DeliveryDriverAgent extends OsmAgent {
	
	@JsonSerialize(using = EstablishmentIdSerializer.class)
	private Establishment establishment;

	/**
	 * DeliveryDriverAgent constructor.
	 *
	 * @param id agent id
	 * @param body delivery driver body
	 * @param deliveryDriverBehavior initialized delivery behavior. The
	 * origin will be used as this agent's establishment.
	 */
	public DeliveryDriverAgent(
			String id,
			DeliveryDriverBody body,
			DeliveryDriverBehavior deliveryDriverBehavior) {
		super(id, body, deliveryDriverBehavior);
		this.establishment = deliveryDriverBehavior.getRound().getOrigin();
		establishment.addAgent(this);
	}

	/**
	 * Returns this agent's establishment, initialized from the specified
	 * DeliveryDriverBehavior.
	 *
	 * @return agent's establishment
	 */
	public Establishment getEstablishment() {
		return establishment;
	}

}
