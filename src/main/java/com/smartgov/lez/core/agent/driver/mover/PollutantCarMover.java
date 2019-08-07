package com.smartgov.lez.core.agent.driver.mover;

import java.util.ArrayList;

import com.smartgov.lez.core.agent.driver.DeliveryDriverBody;
import com.smartgov.lez.core.copert.fields.Pollutant;
import com.smartgov.lez.core.environment.graph.PollutableOsmArc;
import com.smartgov.lez.core.environment.graph.PollutionIncreasedEvent;

import smartgov.core.agent.moving.MovingAgentBody;
import smartgov.urban.geo.agent.GeoAgentBody;
import smartgov.urban.geo.agent.mover.BasicGeoMover;
import smartgov.urban.osm.agent.mover.CarMover;

/**
 * This class implements the same behavior has the {@link smartgov.urban.osm.agent.mover.CarMover CarMover},
 * but with utilities to compute pollution emissions. To do so, it records the traveled distance and propagate the
 * pollution on crossed arcs each time a distance threshold has been reached.
 * 
 * 
 * @author pbreugnot
 *
 */
public class PollutantCarMover extends CarMover {
	
	public static double maximumAcceleration = 4.;
	public static double maximumBraking = -6.;
	public static double maximumSpeed = 15.; // m/s
	public static double vehicleSize = 6.;
	
	public static final double pollutionDistanceTreshold = 1000;
	
	// Record of the distance traveled since the last pollution emission.
	private double traveledDistance;
	private double time;
	private double currentSpeed;
	
	private ArrayList<PollutableOsmArc> arcsCrossed;

	
	public PollutantCarMover() {
		super(
			maximumAcceleration,
			maximumBraking,
			maximumSpeed,
			vehicleSize
			);
		arcsCrossed = new ArrayList<>();
	}
	
	private void setUpPollutionListeners() {
		((BasicGeoMover) agentBody.getMover()).addGeoMoveEventListener((event) -> {
				if (currentSpeed > 0) {
					traveledDistance += event.getDistanceCrossed();
					time += event.getDistanceCrossed() / currentSpeed; // Speed remains constant between each move event.
				}
				currentSpeed = agentBody.getSpeed();
			}
			);
		
		((MovingAgentBody) agentBody).addOnArcLeftListener((event) -> {
				arcsCrossed.add((PollutableOsmArc) event.getArc());
				if (traveledDistance >= pollutionDistanceTreshold || ((MovingAgentBody) agentBody).getPlan().isComplete()) {
					polluteArcs();
					traveledDistance = 0;
					time = 0;
					arcsCrossed.clear();
				}
			}
			);
	}
	
	@Override
	public void setAgentBody(GeoAgentBody agentBody) {
		super.setAgentBody(agentBody);
		setUpPollutionListeners();
		currentSpeed = agentBody.getSpeed();
		time = 0;
	}
	
	private void polluteArcs() {
		for(Pollutant pollutant : Pollutant.values()) {
			double emissions = 
					((DeliveryDriverBody) agentBody)
					.getVehicle()
					.getEmissions(pollutant, traveledDistance / time, traveledDistance);
			for (PollutableOsmArc arc : arcsCrossed) {
				arc.increasePollution(pollutant, emissions * arc.getLength() / traveledDistance);
			}
		}
		for (PollutableOsmArc arc : arcsCrossed) {
			arc._triggerPollutionIncreasedListeners(new PollutionIncreasedEvent(arc));
		}
	}

}
