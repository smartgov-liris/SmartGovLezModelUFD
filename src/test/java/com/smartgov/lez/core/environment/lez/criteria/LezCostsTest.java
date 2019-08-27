package com.smartgov.lez.core.environment.lez.criteria;

import static org.mockito.Mockito.mock;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.smartgov.lez.core.agent.driver.DeliveryDriverBody;
import com.smartgov.lez.core.agent.driver.behavior.LezBehavior;
import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;
import com.smartgov.lez.core.environment.LezContext;
import com.smartgov.lez.core.environment.graph.PollutableOsmArc;
import com.smartgov.lez.core.environment.lez.Lez;
import com.smartgov.lez.core.environment.lez.LezTest;
import com.smartgov.lez.core.simulation.scenario.PollutionScenario;

import org.liris.smartgov.simulator.SmartGov;
import org.liris.smartgov.simulator.core.agent.core.Agent;
import org.liris.smartgov.simulator.core.agent.moving.MovingAgentBody;
import org.liris.smartgov.simulator.core.agent.moving.behavior.MoverAction;
import org.liris.smartgov.simulator.core.agent.moving.behavior.MovingBehavior;
import org.liris.smartgov.simulator.core.environment.SmartGovContext;
import org.liris.smartgov.simulator.core.environment.graph.Arc;
import org.liris.smartgov.simulator.core.environment.graph.astar.Costs;
import org.liris.smartgov.simulator.core.scenario.Scenario;
import org.liris.smartgov.simulator.urban.geo.utils.LatLon;
import org.liris.smartgov.simulator.urban.osm.agent.OsmAgent;

public class LezCostsTest {
	private static final Lez testLez = new Lez(new LatLon[] {
			new LatLon(46.9864, 3.8120),
			new LatLon(46.9869, 3.8120),
			new LatLon(46.9869, 3.81235),
			new LatLon(46.9864, 3.81235)
		},
		new NothingAllowedCriteria()
		);

	
	private static SmartGov loadSmartGov(Lez lez) {
		return new SmartGov(new TestLezContext(lez));
	}
	
	@Test
	public void testScenarioWithNoLez() throws InterruptedException {
		SmartGov smartGov = loadSmartGov(Lez.none());

		Costs costs = ((MovingBehavior) smartGov.getContext().agents.get("0").getBehavior()).getCosts();
		for(Arc arc : smartGov.getContext().arcs.values()) {
			assertThat(
					((PollutableOsmArc) arc).isInLez(),
					is(false)
					);
			
			assertThat(
					costs.cost(arc),
					equalTo(arc.getLength())
					);
		}

		TestLezScenario scenario = (TestLezScenario) smartGov.getContext().getScenario();

		SmartGov.getRuntime().start(500);
		SmartGov.getRuntime().waitUntilSimulatioEnd();
		
		assertThat(
				scenario.crossedNodes,
				contains("0", "1", "2")
				);
	}
	
	@Test
	public void testScenarioWithLez() throws InterruptedException {
		SmartGov smartGov = loadSmartGov(testLez);
		Costs costs = ((MovingBehavior) smartGov.getContext().agents.get("0").getBehavior()).getCosts();
		
		TestLezScenario scenario = ((TestLezScenario) smartGov.getContext().getScenario());
		assertThat(
				scenario.getLez().getLezCriteria().isAllowed(
						((DeliveryDriverBody) smartGov.getContext().agents.get("0").getBody()).getVehicle()
					),
				is(false)
				);

		for(Arc arc : smartGov.getContext().arcs.values()) {
			if(arc.getTargetNode().getId().equals("1")) {
				assertThat(
						((PollutableOsmArc) arc).isInLez(),
						is(true)
						);
				assertThat(
						costs.cost(arc),
						equalTo(Double.MAX_VALUE)
						);
			}
			else {
				assertThat(
						((PollutableOsmArc) arc).isInLez(),
						is(false)
						);
				assertThat(
						costs.cost(arc),
						equalTo(arc.getLength())
						);
			}
		}
		
		SmartGov.getRuntime().start(500);
		SmartGov.getRuntime().waitUntilSimulatioEnd();
		
		assertThat(
				scenario.crossedNodes,
				contains("0", "3", "2")
				);
		
	}

	private static class TestLezContext extends LezContext {
		
		private Lez lez;

		public TestLezContext(Lez lez) {
			super(LezTest.class.getResource("lez_config.properties").getFile());
			this.lez = lez;
		}
		
		@Override
		protected Scenario loadScenario(String name) {
			return new TestLezScenario(lez);
		}
		
	}
	private static class TestLezScenario extends PollutionScenario {
		
		public List<String> crossedNodes = new ArrayList<>();

		public TestLezScenario(Lez lez) {
			super(lez);
		}
		
		@Override
		public Collection<? extends Agent<?>> buildAgents(SmartGovContext context) {
			DeliveryDriverBody body = new DeliveryDriverBody(mock(DeliveryVehicle.class));
			body.addOnNodeReachedListener((event) -> crossedNodes.add(event.getNode().getId()));
			
			OsmAgent agent = new OsmAgent(
					"0",
					body,
					new TestLezBehavior(body, context, getLez())
					);
			return Arrays.asList(agent);
		}
		
	}
	
	private static class TestLezBehavior extends LezBehavior {

		public TestLezBehavior(
				DeliveryDriverBody agentBody,
				SmartGovContext context,
				Lez lez) {
			super(agentBody, context.nodes.get("0"), context.nodes.get("2"), context, lez);
			
		}

		@Override
		public MoverAction provideAction() {
			if(((MovingAgentBody) getAgentBody()).getPlan().isComplete())
				return MoverAction.WAIT();
			return MoverAction.MOVE();
		}
		
	}
}
