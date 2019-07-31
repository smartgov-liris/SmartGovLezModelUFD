package com.smartgov.lez.core.simulation.scenario;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smartgov.lez.core.agent.driver.DeliveryDriverBody;
import com.smartgov.lez.core.agent.driver.behavior.DeliveryDriverBehavior;
import com.smartgov.lez.core.agent.establishment.Establishment;
import com.smartgov.lez.core.environment.LezContext;

import smartgov.SmartGov;
import smartgov.core.environment.graph.Node;
import smartgov.urban.osm.agent.OsmAgent;

public class DeliveriesScenarioTest {
	
	public static SmartGov loadDeliveriesScenario() {
		return new SmartGov(new TestContext());
	}

	@Test
	public void loadTest() {
		SmartGov smartGov = loadDeliveriesScenario();
		
		assertThat(
				((LezContext) smartGov.getContext()).getEstablishments().values(),
				hasSize(3)
				);
	}
	
	@Test
	public void testClosestOsmNode() {
		SmartGov smartGov = loadDeliveriesScenario();
		LezContext context = (LezContext) smartGov.getContext();
		
		assertThat(
				context.getEstablishments().get("0").getClosestOsmNode(),
				equalTo(context.nodes.get("1268914688"))
				);
		
		assertThat(
				context.getEstablishments().get("1").getClosestOsmNode(),
				equalTo(context.nodes.get("5285975401"))
				);
		
		assertThat(
				context.getEstablishments().get("2").getClosestOsmNode(),
				equalTo(context.nodes.get("1324640735"))
				);
	}
	
	@Test
	public void testAgents() {
		SmartGov smartGov = loadDeliveriesScenario();
		
		assertThat(
			smartGov.getContext().agents.values(),
			hasSize(4)
			);
		
	}
	
	@Test
	public void testAgentsBehavior() throws InterruptedException {
		SmartGov smartGov = loadDeliveriesScenario();
		
		LezContext context = (LezContext) smartGov.getContext();
		
		Map<String, List<Node>> destinationReached = new HashMap<>();
		Map<String, List<Node>> expectedDestinationReached = new HashMap<>();
		Map<String, EventChecker> parkingEntered = new HashMap<>();
		
		for(Establishment establishment : context.getEstablishments().values()) {
			for(OsmAgent agent : establishment.getAgents()) {
				DeliveryDriverBody driver = (DeliveryDriverBody) agent.getBody();
				
				destinationReached.put(agent.getId(), new ArrayList<>());
				driver.addOnDestinationReachedListener(
						(event) -> destinationReached.get(agent.getId()).add(event.getNode())
						);
				
				parkingEntered.put(agent.getId(), new EventChecker());
				driver.addOnParkingEnteredListener((event) -> parkingEntered.get(agent.getId()).check = true);
				
				expectedDestinationReached.put(agent.getId(), new ArrayList<>());
				DeliveryDriverBehavior driverBehavior = (DeliveryDriverBehavior) agent.getBehavior();
				for(Establishment roundEstablishment : driverBehavior.getRound().getEstablishments()) {
					expectedDestinationReached.get(agent.getId()).add(roundEstablishment.getClosestOsmNode());
				}
				expectedDestinationReached.get(agent.getId()).add(driverBehavior.getRound().getOrigin().getClosestOsmNode());
				
			}
		}
		
		assertThat(
				context.ongoingRounds.keySet(),
				contains("0", "1", "2", "3")
				);
		
		SmartGov.getRuntime().start(24 * 3600);
		
		SmartGov.getRuntime().waitUntilSimulatioEnd();
		
		assertThat(
				destinationReached,
				equalTo(expectedDestinationReached)
				);
		
		for(EventChecker checker : parkingEntered.values()) {
			assertThat(
					checker.check,
					equalTo(true)
					);
		}
		
		assertThat(
				context.ongoingRounds.keySet(),
				hasSize(0)
				);
		
		
	}
	
	private static class EventChecker {
		public boolean check = false;
	}
	
	private static class TestContext extends LezContext {

		public TestContext() {
			super(DeliveriesScenarioTest.class.getResource("deliveries_config.properties").getFile());
		}
		
	}
}
