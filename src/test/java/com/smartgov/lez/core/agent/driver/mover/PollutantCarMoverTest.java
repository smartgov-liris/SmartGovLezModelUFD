package com.smartgov.lez.core.agent.driver.mover;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartgov.lez.core.agent.driver.DeliveryDriver;
import com.smartgov.lez.core.agent.driver.mover.PollutantCarMover;
import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;
import com.smartgov.lez.core.copert.fields.Pollutant;
import com.smartgov.lez.core.environment.graph.PollutableOsmArc;

import smartgov.SmartGov;
import smartgov.core.agent.core.Agent;
import smartgov.core.agent.moving.MovingAgentBody;
import smartgov.core.agent.moving.behavior.MoverAction;
import smartgov.core.agent.moving.behavior.MovingBehavior;
import smartgov.core.agent.moving.events.arc.ArcLeftEvent;
import smartgov.core.agent.moving.events.node.DestinationReachedEvent;
import smartgov.core.environment.SmartGovContext;
import smartgov.core.environment.graph.Arc;
import smartgov.core.events.EventHandler;
import smartgov.core.scenario.Scenario;
import smartgov.urban.osm.agent.OsmAgent;
import smartgov.urban.osm.agent.OsmAgentBody;
import smartgov.urban.osm.environment.OsmContext;
import smartgov.urban.osm.environment.graph.OsmArc.RoadDirection;
import smartgov.urban.osm.environment.graph.OsmNode;
import smartgov.urban.osm.environment.graph.Road;
import smartgov.urban.osm.environment.graph.factory.OsmArcFactory;
import smartgov.urban.osm.scenario.GenericOsmScenario;

public class PollutantCarMoverTest {
	
	private static SmartGov loadTestScenario() {
		return new SmartGov(new PollutantCarMoverContext());
	}
	
	@Test
	public void testLoadScenario() {
		SmartGov smartGov = loadTestScenario();
		
		assertThat(
				smartGov.getContext().nodes.values(),
				hasSize(4)
				);
		
		assertThat(
				smartGov.getContext().arcs.values(),
				hasSize(4)
				);
		
		for (Arc arc : smartGov.getContext().arcs.values()) {
			assertThat(
					arc instanceof PollutableOsmArc,
					equalTo(true)
					);
		}
		
		assertThat(
				smartGov.getContext().agents.get("1").getBody() instanceof DeliveryDriver,
				equalTo(true)
				);
		
	}
	
	@Test
	public void testAgentIsMoving() throws InterruptedException {
		SmartGov smartGov = loadTestScenario();
		
		List<String> crossedArcIds = new ArrayList<>();
		DistanceBean crossedDistance = new DistanceBean();
		
		((MovingAgentBody) smartGov.getContext().agents.get("1").getBody())
			.addOnArcLeftListener(new EventHandler<ArcLeftEvent>() {

				@Override
				public void handle(ArcLeftEvent event) {
					crossedArcIds.add(event.getArc().getId());
					crossedDistance.add(event.getArc().getLength());
				}
				
			});
		
		SmartGov.getRuntime().start(1000);
		
		while(SmartGov.getRuntime().isRunning()) {
			TimeUnit.MICROSECONDS.sleep(10);
		}
		
		assertThat(
				crossedArcIds,
				hasItems("1", "2", "3", "4")
				);
		
		assertThat(
				crossedDistance.get(),
				greaterThan(PollutantCarMover.pollutionDistanceTreshold * 5)
				);
		
	}
	
	@Test
	public void testThatPolluteFunctionIsCalled() throws Exception {
		SmartGov smartGov = loadTestScenario();

		Map<String, CounterBean> arcCrossedCounters = new HashMap<>();

		for(Arc arc : smartGov.getContext().arcs.values()) {
			arcCrossedCounters.put(arc.getId(), new CounterBean());
		}
		
		((MovingAgentBody) smartGov.getContext().agents.get("1").getBody())
		.addOnArcLeftListener(new EventHandler<ArcLeftEvent>() {

			@Override
			public void handle(ArcLeftEvent event) {
				arcCrossedCounters.get(event.getArc().getId()).increment();
			}
			
		});
		
		
		
		SmartGov.getRuntime().start(10000);
		while(SmartGov.getRuntime().isRunning()) {
			TimeUnit.MICROSECONDS.sleep(10);
		}
		
		for(Arc arcSpy : smartGov.getContext().arcs.values()) {
			for (Pollutant pollutant : Pollutant.values()) {
				Integer crossCount = arcCrossedCounters.get(arcSpy.getId()).get();
				
				/*
				 *  Doesn't check exact equality, because depending on when the agent stop, the last
				 *  arcs does not necessarily had the time to be polluted.
				 */
				Mockito.verify((PollutableOsmArc) arcSpy, Mockito.atLeast(crossCount - 1))
					.increasePollution(Mockito.eq(pollutant), Mockito.anyDouble());
				
				Mockito.verify((PollutableOsmArc) arcSpy, Mockito.atMost(crossCount))
					.increasePollution(Mockito.eq(pollutant), Mockito.anyDouble());
			}
		}

	}
	
	/*
	 * Utility class to measure distances from event listeners.
	 */
	private class DistanceBean {
		private Double distance = 0.;
		
		public void add(Double distance) {
			this.distance += distance;
		}
		
		public Double get() {
			return distance;
		}
	}
	
	/*
	 * Utility class to measure counts from event listeners.
	 */
	private class CounterBean {
		private Integer count = 0;
		
		public void increment() {
			count++;
		}
		
		public Integer get() {
			return count;
		}
	}
	

	/*
	 * Test context, that loads the following scenario.
	 */
	private static class PollutantCarMoverContext extends OsmContext {

		public PollutantCarMoverContext() {
			super(PollutantCarMoverTest.class.getResource("pollutant_car_mover_test.properties").getFile());
		}
		
		@Override
		public Scenario loadScenario(String name) {
			return new PollutantCarMoverTestScenario();
		}
		
	}
	
	/*
	 * Creates exactly one agent that performs loop between the 4 nodes, using a fake vehicle that emits
	 * dummy pollution values.
	 */
	private static class PollutantCarMoverTestScenario extends GenericOsmScenario<SpyOsmNode, Road> {

		public PollutantCarMoverTestScenario() {
			super(SpyOsmNode.class, Road.class, new SpyPollutableOsmArcFactory());
		}

		@Override
		public Collection<? extends Agent<?>> buildAgents(SmartGovContext context) {
			DeliveryVehicle fakeVehicle = PowerMockito.mock(DeliveryVehicle.class);
			doReturn(1.0).when(fakeVehicle).getEmissions(Mockito.any(Pollutant.class), Mockito.anyDouble(), Mockito.anyDouble());
			
			OsmAgentBody deliveryDriver = new DeliveryDriver(fakeVehicle, (OsmContext) context);
			OsmAgent agent = new OsmAgent("1", deliveryDriver, new PollutantCarMoverTestBehavior(deliveryDriver, context));
			
			deliveryDriver.initialize();
			
			Collection<OsmAgent> agents = new ArrayList<>();
			agents.add(agent);
			
			return agents;
		}
		
	}
	
	/*
	 * The agent will just perform loops between the 4 nodes.
	 */
	private static class PollutantCarMoverTestBehavior extends MovingBehavior {

		public PollutantCarMoverTestBehavior(OsmAgentBody agentBody,
				SmartGovContext context) {
			super(agentBody, context.nodes.get("1"), context.nodes.get("3"), context);
			agentBody.addOnDestinationReachedListener(new EventHandler<DestinationReachedEvent>() {

				@Override
				public void handle(DestinationReachedEvent event) {
					refresh(getDestination(), getOrigin());
					agentBody.initialize();
				}
				
			});
		}

		@Override
		public MoverAction provideAction() {
			return MoverAction.MOVE();
		}
		
	}
	
	/*
	 * Creates spy PollutableOsmArc, so that they will be added to the context.
	 */
	private static class SpyPollutableOsmArcFactory implements OsmArcFactory<PollutableOsmArc> {
		
		public static Map<String, PollutableOsmArc> spies = new HashMap<>();

		@Override
		public PollutableOsmArc create(String id, OsmNode startNode, OsmNode targetNode, Road road, RoadDirection roadDirection) {

			PollutableOsmArc spy = PowerMockito.spy(new PollutableOsmArc(id, startNode, targetNode, road, roadDirection));
			spies.put(id, spy);
			return spy;
		}
		
	}
	
	/*
	 * When "new PollutableOsmArc(...)" is called, THIS pollutable osm arc is used as
	 * incoming / outgoing arc in nodes.
	 * 
	 * But we want to work with spies, so that we can check methods are called.
	 * 
	 * So we use this node class, that will be unserialized instead of normal OsmNode,
	 * and that do not return normal arcs as incoming / outgoing arcs, but corresponding 
	 * spies.
	 */
	private static class SpyOsmNode extends OsmNode {

		@JsonCreator
		public SpyOsmNode(
				@JsonProperty("id") String id,
				@JsonProperty("lat") double lat,
				@JsonProperty("lon") double lon) {
			super(id, lat, lon);
		}
		
		@Override
		public List<Arc> getIncomingArcs() {
			List<Arc> spies = new ArrayList<>();
			for(Arc arc : incomingArcs) {
				spies.add(SpyPollutableOsmArcFactory.spies.get(arc.getId()));
			}
			return spies;
		}
		
		@Override
		public List<Arc> getOutgoingArcs() {
			List<Arc> spies = new ArrayList<>();
			for(Arc arc : outgoingArcs) {
				spies.add(SpyPollutableOsmArcFactory.spies.get(arc.getId()));
			}
			return spies;
		}
	}
}