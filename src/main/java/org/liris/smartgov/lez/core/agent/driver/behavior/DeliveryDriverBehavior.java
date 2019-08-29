package org.liris.smartgov.lez.core.agent.driver.behavior;

import java.util.ArrayList;
import java.util.Collection;

import org.liris.smartgov.lez.cli.tools.Run;
import org.liris.smartgov.lez.core.agent.driver.DeliveryDriverBody;
import org.liris.smartgov.lez.core.agent.establishment.Establishment;
import org.liris.smartgov.lez.core.agent.establishment.Round;
import org.liris.smartgov.lez.core.environment.lez.Lez;
import org.liris.smartgov.simulator.SmartGov;
import org.liris.smartgov.simulator.core.agent.moving.behavior.MoverAction;
import org.liris.smartgov.simulator.core.environment.SmartGovContext;
import org.liris.smartgov.simulator.core.environment.graph.Node;
import org.liris.smartgov.simulator.core.events.EventHandler;
import org.liris.smartgov.simulator.core.simulation.time.DelayedActionHandler;

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
public class DeliveryDriverBehavior extends LezBehavior {
	
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
			context,
			Lez.none()
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
			
			if (currentPosition <= round.getEstablishments().size() - 1) {
				Establishment currentEstablishment = round.getEstablishments().get(currentPosition);
				Run.logger.info(
						"[" + SmartGov.getRuntime().getClock().getHour()
						+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
						+ "Agent " + getAgentBody().getAgent().getId()
						+ " has reached establishment [" + currentEstablishment.getId()
						+ "] " + currentEstablishment.getName()
						);
				if (currentPosition < round.getEstablishments().size() - 1) {
					// Go to the next node of the round
					Node currentNode = currentEstablishment.getClosestOsmNode();
					Node nextNode = round.getEstablishments().get(currentPosition + 1).getClosestOsmNode();
					
					while(nextNode.equals(currentNode) && currentPosition < round.getEstablishments().size() - 1) {
						// Sometimes, two consecutive establishment has the same closest osm node.
						currentPosition++;
						currentEstablishment = round.getEstablishments().get(currentPosition);
						Run.logger.info(
								"[" + SmartGov.getRuntime().getClock().getHour()
								+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
								+ "Agent " + getAgentBody().getAgent().getId()
								+ " has reached establishment [" + currentEstablishment.getId()
								+ "] " + currentEstablishment.getName()
								);
						currentNode = round.getEstablishments().get(currentPosition).getClosestOsmNode();
						nextNode = round.getEstablishments().get(currentPosition + 1).getClosestOsmNode();
					}
					
					if (currentPosition == round.getEstablishments().size() - 1) {
						// Nodes were equals until the last establishment
						currentNode = round.getEstablishments().get(currentPosition).getClosestOsmNode();
						if(!currentNode.equals(round.getOrigin().getClosestOsmNode())) {
							// Last trip to the origin
							refresh(
									currentNode,
									round.getOrigin().getClosestOsmNode());
						}
						else {
							// The last node is also the same as origin...
							// Go back the origin parking area and end round
							nextAction = MoverAction.ENTER(round.getOrigin());
							triggerRoundEndListeners(new RoundEnd());
						}
					}
					else {
						// Go on normally to the next distinct node
						refresh(
							currentNode,
							nextNode);
					}
				}
				else {
					// currentPosition == round.getEstablishments().size() - 1
					// The last establishment has been reached normally
					Node currentNode = round.getEstablishments().get(currentPosition).getClosestOsmNode();
					if(!currentNode.equals(round.getOrigin().getClosestOsmNode())) {
						// The origin node is distinct from the last establishment node
						refresh(
								currentNode,
								round.getOrigin().getClosestOsmNode());
					}
					else {
						// The origin node is the same as the last establishment node.
						
						// Because this destination callback won't be call again (because
						// no "refresh" is performed, we end the round there, and the last part
						// below (when "currentPosition == round.getEstablishments().size()") will
						// never be called.

						// So go back to the origin parking area and end round
						nextAction = MoverAction.ENTER(round.getOrigin());
						triggerRoundEndListeners(new RoundEnd());
					}
				}
			}
			else {
				// currentPosition == round.getEstablishments().size()
				// The origin has been reached
				
				// Go back the origin parking area and end round
				nextAction = MoverAction.ENTER(round.getOrigin());
				triggerRoundEndListeners(new RoundEnd());
			}
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
	
//	private static class RefreshThread extends Thread {
//		private DeliveryDriverBehavior behavior;
//		private Node origin;
//		private Node destination;
//		
//		public RefreshThread(DeliveryDriverBehavior behavior, Node origin, Node destination) {
//			super();
//			this.behavior = behavior;
//			this.origin = origin;
//			this.destination = destination;
//		}
//		
//		public void run() {
//			behavior.refresh(origin, destination);
//			behavior.nextAction = MoverAction.MOVE();
//		}
//	}

}
