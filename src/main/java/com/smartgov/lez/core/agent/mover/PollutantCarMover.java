package com.smartgov.lez.core.agent.mover;

import java.util.ArrayList;

import com.smartgov.lez.core.agent.DeliveryDriver;
import com.smartgov.lez.core.copert.fields.Pollutant;
import com.smartgov.lez.core.environment.graph.PollutableOsmArc;
import com.smartgov.lez.core.environment.graph.PollutionIncreasedEvent;

import smartgov.core.agent.moving.MovingAgentBody;
import smartgov.core.agent.moving.events.ArcLeftEvent;
import smartgov.core.events.EventHandler;
import smartgov.urban.geo.agent.GeoAgentBody;
import smartgov.urban.geo.agent.event.GeoMoveEvent;
import smartgov.urban.geo.agent.mover.BasicGeoMover;
import smartgov.urban.osm.agent.mover.CarMover;

/**
 * This class implements the same behavior has the {@link smartgov.urban.osm.agent.mover.CarMover CarMover},
 * but it had utilities to compute pollution emissions. To do so, it records the traveled distance and propagate the
 * pollution on crossed arcs each time a distance threshold has been reached.
 * 
 * 
 * @author pbreugnot
 *
 */
public class PollutantCarMover extends CarMover {
	
	// TODO: should be handled as a simulation parameter
	public static final double pollutionDistanceTreshold = 200;
	
	// Record of the distance traveled since the last pollution emission.
	private double traveledDistance;
	private double time;
	private double currentSpeed;
	
	private ArrayList<PollutableOsmArc> arcsCrossed;

	public PollutantCarMover() {
		super(4.0, -6.0, 15.0, 7.0);
		arcsCrossed = new ArrayList<>();
	}
	
	private void setUpPollutionListeners() {
		((BasicGeoMover) agentBody.getMover()).addGeoMoveEventListener(new EventHandler<GeoMoveEvent>() {

			@Override
			public void handle(GeoMoveEvent event) {
				if (currentSpeed > 0) {
					traveledDistance += event.getDistanceCrossed();
					time += event.getDistanceCrossed() / currentSpeed; // Speed remains constant between each move event.
				}
				currentSpeed = agentBody.getSpeed();
			}
			
		});
		
		((MovingAgentBody) agentBody).addOnArcLeftListener(new EventHandler<ArcLeftEvent>() {

			@Override
			public void handle(ArcLeftEvent event) {
				arcsCrossed.add((PollutableOsmArc) event.getArc());
				if (traveledDistance >= pollutionDistanceTreshold) {
					polluteArcs();
					traveledDistance = 0;
					time = 0;
					arcsCrossed.clear();
				}
			}
			
		});
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
					((DeliveryDriver) agentBody)
					.getVehicle()
					.getEmissions(pollutant, traveledDistance / time, traveledDistance);
			for (PollutableOsmArc arc : arcsCrossed) {
				arc.increasePollution(pollutant, emissions * arc.getLength() / traveledDistance);
			}
		}
		for (PollutableOsmArc arc : arcsCrossed) {
			arc.triggerPollutionIncreasedListeners(new PollutionIncreasedEvent(arc));
		}
	}

}
