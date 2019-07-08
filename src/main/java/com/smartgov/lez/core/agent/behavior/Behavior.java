package com.smartgov.lez.core.agent.behavior;

import java.util.List;

import smartgov.SmartGov;
import smartgov.core.agent.moving.MovingAgentBody;
import smartgov.core.agent.moving.behavior.MoverAction;
import smartgov.core.agent.moving.behavior.MovingBehavior;
import smartgov.core.environment.SmartGovContext;
import smartgov.core.environment.graph.Node;
import smartgov.core.simulation.time.Date;
import smartgov.core.simulation.time.DelayedActionHandler;

public class Behavior extends MovingBehavior {
	
	private List<Node> round;
	private int currentPosition;
	private MoverAction nextAction;

	public Behavior(
			MovingAgentBody agentBody,
			OriginParkingArea origin,
			List<Node> round,
			Date departure,
			SmartGovContext context) {
		super(agentBody, round.get(0), round.get(1), context);
		this.round = round;
		this.currentPosition = 0;
		this.nextAction = MoverAction.ENTER(origin);
		
		SmartGov.getRuntime().getClock().addDelayedAction(
				new DelayedActionHandler(
						departure,
						() -> nextAction = MoverAction.LEAVE(origin)
						)
				);
	}

	@Override
	public MoverAction provideAction() {
		// TODO Auto-generated method stub
		return null;
	}

}
