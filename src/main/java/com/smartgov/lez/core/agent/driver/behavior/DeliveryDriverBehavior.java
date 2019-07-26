package com.smartgov.lez.core.agent.driver.behavior;

import java.util.ArrayList;
import java.util.Collection;

import com.smartgov.lez.core.agent.driver.DeliveryDriverBody;
import com.smartgov.lez.core.agent.establishment.Round;

import smartgov.SmartGov;
import smartgov.core.agent.moving.behavior.MoverAction;
import smartgov.core.agent.moving.behavior.MovingBehavior;
import smartgov.core.environment.SmartGovContext;
import smartgov.core.environment.graph.Node;
import smartgov.core.events.EventHandler;
import smartgov.core.simulation.time.DelayedActionHandler;

/**
 * A delivery driver behavior, that can be described as follow :
 * <ul>
 * 	<li> At initialization, the agent enters the origin establishment.</li>
 * 	<li> The agent waits until the departure Date, and leave the
 * 	origin.</li>
 * 	<li> The agent navigate in the graph to reach all the round's
 * 	establishment, using the shortest path, in order. </li>
 * 	<li> Finally, the agent go back to the origin and enters the
 * 	establishment. </li>
 * </ul>
 */
public class DeliveryDriverBehavior extends MovingBehavior {
	
	private Round round;
	private int currentPosition;
	private MoverAction nextAction;
	
	private Collection<EventHandler<RoundDeparture>> roundDepartureListeners;
	private Collection<EventHandler<RoundEnd>> roundEndListeners;

	/**
	 * DeliveryDriverBehavior constructor.
	 *
	 * @param agentBody associated body
	 * @param round round to perform
	 * @param context currentContext
	 */
	public DeliveryDriverBehavior(
			DeliveryDriverBody agentBody,
			Round round,
			SmartGovContext context) {
		super(
			agentBody,
			round.getOrigin().getClosestOsmNode(),
			round.getEstablishments().get(0).getClosestOsmNode(),
			context
			);
		roundDepartureListeners = new ArrayList<>();
		roundEndListeners = new ArrayList<>();
		
		this.round = round;
		this.currentPosition = 0;
		
		// Start waiting at the origin
		this.nextAction = MoverAction.ENTER(round.getOrigin());
		
		
	}
	
	public void setUpListeners() {
		// Leave at departure date
		SmartGov
			.getRuntime()
			.getClock()
			.addDelayedAction(
				new DelayedActionHandler(
						round.getDeparture(),
						() -> {
							nextAction = MoverAction.LEAVE(round.getOrigin());
							triggerRoundDepartureListeners(new RoundDeparture());
						}
						)
				);
		
		// After the agents leave the parking, it moves until it finished the round
		((DeliveryDriverBody) getAgentBody()).addOnParkingLeftListener((event) ->
			nextAction = MoverAction.MOVE()
			);
		
		// When the agent enter the parking, it waits
		((DeliveryDriverBody) getAgentBody()).addOnParkingEnteredListener((event) ->
			nextAction = MoverAction.WAIT()
			);
		
		// When a destination is reached
		((DeliveryDriverBody) getAgentBody()).addOnDestinationReachedListener((event) -> {
				if (currentPosition < round.getEstablishments().size() - 1) {
					// Go to the next node of the round
					Node currentNode = round.getEstablishments().get(currentPosition).getClosestOsmNode();
					Node nextNode = round.getEstablishments().get(currentPosition + 1).getClosestOsmNode();
					if(!nextNode.equals(currentNode))
						// Sometimes, two consecutive establishment has the same closest osm node.
						refresh(
							round.getEstablishments().get(currentPosition).getClosestOsmNode(),
							round.getEstablishments().get(currentPosition + 1).getClosestOsmNode());
				}
				else
					if (currentPosition == round.getEstablishments().size() - 1) {
						Node currentNode = round.getEstablishments().get(currentPosition).getClosestOsmNode();
						if(!currentNode.equals(round.getOrigin().getClosestOsmNode()))
							// The last establishment could have that same closest osm node as the origin...
							refresh(
									round.getEstablishments().get(currentPosition).getClosestOsmNode(),
									round.getOrigin().getClosestOsmNode());
					}
					else
						// Go back the origin parking area
						nextAction = MoverAction.ENTER(round.getOrigin());
						triggerRoundEndListeners(new RoundEnd());
				currentPosition++;
			});
	}
	
	/**
	 * Returns the round that the agent must perform.
	 *
	 * @return agent's round
	 */
	public Round getRound() {
		return round;
	}

	@Override
	public MoverAction provideAction() {
		return nextAction;
	}
	
	/**
	 * Adds a round departure event handler, triggered when the departure
	 * date has been reached.
	 *
	 * @param listener round departure listener
	 */
	public void addRoundDepartureListener(EventHandler<RoundDeparture> listener) {
		this.roundDepartureListeners.add(listener);
	}
	
	private void triggerRoundDepartureListeners(RoundDeparture event) {
		for(EventHandler<RoundDeparture> listener : roundDepartureListeners) {
			listener.handle(event);
		}
	}

	/**
	 * Adds a round end event handler, triggered when the agent come back
	 * to the origin establishment.
	 *
	 * <p>
	 * Triggered when the agent reaches the destination, but does not
	 * guarantee that the agent as re-entered the establishment (what will
	 * be its next action.
	 * </p>
	 *
	 * @param listener round end listener
	 */
	public void addRoundEndListener(EventHandler<RoundEnd> listener) {
		this.roundEndListeners.add(listener);
	}
	
	private void triggerRoundEndListeners(RoundEnd event) {
		for(EventHandler<RoundEnd> listener : roundEndListeners) {
			listener.handle(event);
		}
	}

}
