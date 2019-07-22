package com.smartgov.lez.core.simulation.scenario;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

import com.smartgov.lez.core.environment.LezContext;

import smartgov.SmartGov;

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
	
	private static class TestContext extends LezContext {

		public TestContext() {
			super(DeliveriesScenarioTest.class.getResource("deliveries_config.properties").getFile());
		}
		
	}
}
