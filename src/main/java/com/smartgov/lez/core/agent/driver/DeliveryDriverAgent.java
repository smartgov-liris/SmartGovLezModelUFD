package com.smartgov.lez.core.agent.driver;

import com.smartgov.lez.core.agent.driver.behavior.DeliveryDriverBehavior;
import com.smartgov.lez.core.agent.establishment.Establishment;

import smartgov.urban.osm.agent.OsmAgent;
import smartgov.urban.osm.agent.OsmAgentBody;

public class DeliveryDriverAgent extends OsmAgent {
	
	private Establishment establishment;

	public DeliveryDriverAgent(
			String id,
			OsmAgentBody body,
			DeliveryDriverBehavior deliveryDriverBehavior) {
		super(id, body, deliveryDriverBehavior);
		this.establishment = deliveryDriverBehavior.getRound().getOrigin();
		establishment.addAgent(this);
	}

	public Establishment getEstablishment() {
		return establishment;
	}

}
