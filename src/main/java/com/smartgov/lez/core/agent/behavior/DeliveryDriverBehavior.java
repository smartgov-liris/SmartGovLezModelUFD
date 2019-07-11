package com.smartgov.lez.core.agent.behavior;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import smartgov.SmartGov;
import smartgov.core.agent.moving.MovingAgentBody;
import smartgov.core.agent.moving.behavior.MoverAction;
import smartgov.core.agent.moving.behavior.MovingBehavior;
import smartgov.core.environment.SmartGovContext;
import smartgov.core.environment.graph.Node;
import smartgov.core.events.EventHandler;
import smartgov.core.simulation.time.Date;
import smartgov.core.simulation.time.DelayedActionHandler;

public class DeliveryDriverBehavior extends MovingBehavior {
	
	private List<Node> round;
	private int currentPosition;
	private MoverAction nextAction;
	
	private Collection<EventHandler<RoundDeparture>> roundDepartureListeners;
	private Collection<EventHandler<RoundEnd>> roundEndListeners;

	public DeliveryDriverBehavior(
			MovingAgentBody agentBody,
			OriginParkingArea origin,
			List<Node> round,
			Date departure,
			SmartGovContext context) {
		super(agentBody, round.get(0), round.get(1), context);
		roundDepartureListeners = new ArrayList<>();
		roundEndListeners = new ArrayList<>();
		
		this.round = round;
		this.currentPosition = 0;
		
		// Start waiting at the origin
		this.nextAction = MoverAction.ENTER(origin);
		
		// Leave at departure date
		SmartGov
			.getRuntime()
			.getClock()
			.addDelayedAction(
				new DelayedActionHandler(
						departure,
						() -> {
							nextAction = MoverAction.LEAVE(origin);
							triggerRoundDepartureListeners(new RoundDeparture());
						}
						)
				);
		
		// After the agents leave the parking, it moves until it finished the round
		agentBody.addOnParkingLeftListener((event) ->
			nextAction = MoverAction.MOVE()
			);
		
		// When the agent enter the parking, it waits
		agentBody.addOnParkingEnteredListener((event) ->
			nextAction = MoverAction.WAIT()
			);
		
		// When a destination is reached
		agentBody.addOnDestinationReachedListener((event) -> {
				currentPosition++;
				if (currentPosition < round.size() - 1)
					// Go to the next node of the round
					refresh(round.get(currentPosition), round.get(currentPosition + 1));
				else
					// Go back the origin parking area
					nextAction = MoverAction.ENTER(origin);
				triggerRoundEndListeners(new RoundEnd());
			});
	}
	
	public List<Node> getRound() {
		return round;
	}

	@Override
	public MoverAction provideAction() {
		return nextAction;
	}
	
	public void addRoundDepartureListener(EventHandler<RoundDeparture> listener) {
		this.roundDepartureListeners.add(listener);
	}
	
	private void triggerRoundDepartureListeners(RoundDeparture event) {
		for(EventHandler<RoundDeparture> listener : roundDepartureListeners) {
			listener.handle(event);
		}
	}
	
	public void addRoundEndListener(EventHandler<RoundEnd> listener) {
		this.roundEndListeners.add(listener);
	}
	
	private void triggerRoundEndListeners(RoundEnd event) {
		for(EventHandler<RoundEnd> listener : roundEndListeners) {
			listener.handle(event);
		}
	}

}
