package com.smartgov.lez.core.simulation.scenario;

import smartgov.SmartGov;
import smartgov.core.agent.core.Agent;
import smartgov.core.environment.graph.Arc;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.smartgov.lez.core.agent.driver.DeliveryDriver;
import com.smartgov.lez.core.environment.LezContext;
import com.smartgov.lez.core.environment.graph.PollutableOsmArc;

public class PollutionScenarioTest {
	
	private SmartGov loadSmartGov() {
		return new SmartGov(new LezContext(this.getClass().getResource("pollution_scenario.properties").getFile()));
	}
	
	@Test
	public void testLoadPollutionScenario() {
		SmartGov smartGov = loadSmartGov();
		assertThat(
				smartGov.getContext().getScenario() instanceof PollutionScenario,
				equalTo(true)
				);
	}
	
	@Test
	public void testAgentBodiesType() {
		SmartGov smartGov = loadSmartGov();
		for(Agent<?> agent : smartGov.getContext().agents.values()) {
			assertThat(
					agent.getBody() instanceof DeliveryDriver,
					equalTo(true)
					);
		}
	}
	
	@Test
	public void testArcsType() {
		SmartGov smartGov = loadSmartGov();
		for(Arc arc : smartGov.getContext().arcs.values()) {
			assertThat(
					arc instanceof PollutableOsmArc,
					equalTo(true)
					);
		}
	}
	
	@Test
	public void testMemory() throws InterruptedException {
		loadSmartGov();
		
		SmartGov.getRuntime().addSimulationStepListener((event) -> System.out.println(event.getTick()));

		
		SmartGov.getRuntime().start(100);
		while(SmartGov.getRuntime().isRunning()) {
			TimeUnit.SECONDS.sleep(1);
			System.out.println(SmartGov.getRuntime().getSimulationThread().getState());
		}

	}
}
