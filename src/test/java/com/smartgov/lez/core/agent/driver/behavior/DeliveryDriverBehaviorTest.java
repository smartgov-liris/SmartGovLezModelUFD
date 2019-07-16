package com.smartgov.lez.core.agent.driver.behavior;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.smartgov.lez.core.agent.driver.DeliveryDriver;
import com.smartgov.lez.core.agent.driver.behavior.DeliveryDriverBehavior;
import com.smartgov.lez.core.agent.driver.behavior.OriginParkingArea;
import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;
import com.smartgov.lez.core.copert.fields.Pollutant;
import com.smartgov.lez.core.simulation.scenario.PollutionScenario;

import smartgov.SmartGov;
import smartgov.core.agent.core.Agent;
import smartgov.core.environment.SmartGovContext;
import smartgov.core.scenario.Scenario;
import smartgov.core.simulation.time.Clock;
import smartgov.core.simulation.time.Date;
import smartgov.urban.osm.agent.OsmAgent;
import smartgov.urban.osm.agent.mover.CarMover;
import smartgov.urban.osm.environment.OsmContext;

public class DeliveryDriverBehaviorTest {
	
	private static SmartGov loadScenario() {
		return new SmartGov(new DeliveryDriverContext());
	}
	
	@Test
	public void testRound() throws InterruptedException {
		loadScenario();
		
		List<String> nodeReached = new ArrayList<>();
		
		DeliveryScenario.driverSpy.addOnDestinationReachedListener((event) ->
					nodeReached.add(event.getNode().getId())
					);

		EventChecker hasLeftParkingArea = new EventChecker();
		DeliveryScenario.driverSpy.addOnParkingLeftListener((event) -> {
			hasLeftParkingArea.check();
			assertThat(
					SmartGov.getRuntime().getClock().compareTo(DeliveryScenario.departure),
					greaterThanOrEqualTo(0)
					);
		});
		
		SmartGov.getRuntime().start(2 * 24 * 3600);
		
		SmartGov.getRuntime().waitUntilSimulatioEnd();
		
		assertThat(
				hasLeftParkingArea.hasBeenTriggered(),
				equalTo(true)
				);
		
		assertThat(
				nodeReached,
				contains("2", "3", "4", "1")
				);
		
		verify(DeliveryScenario.driverSpy, times(2)).handleEnter(any());
		verify(DeliveryScenario.driverSpy, times(1)).handleLeave(any());
		verify(DeliveryScenario.driverSpy, atLeast(10)).handleWait();
		
		
	}
	
	@Test
	public void testRoundEvents() throws InterruptedException {
		loadScenario();
		
		EventChecker departureChecker = new EventChecker();
		((DeliveryDriverBehavior) DeliveryScenario.driverSpy.getAgent().getBehavior())
			.addRoundDepartureListener((event) -> departureChecker.check());
	
		EventChecker endChecker = new EventChecker();
		((DeliveryDriverBehavior) DeliveryScenario.driverSpy.getAgent().getBehavior())
			.addRoundEndListener((event) -> endChecker.check());
		
		SmartGov.getRuntime().start(2 * 24 * 3600);
		
		SmartGov.getRuntime().waitUntilSimulatioEnd();
		
		assertThat(
				departureChecker.hasBeenTriggered(),
				equalTo(true)
				);
		
		assertThat(
				endChecker.hasBeenTriggered(),
				equalTo(true)
				);
	}
	
	private static class EventChecker {
		private Boolean triggered;
		
		public EventChecker() {
			this.triggered = false;
		}
		
		public void check(){
			this.triggered = true;
		}
		
		public Boolean hasBeenTriggered() {
			return triggered;
		}
	}
	
	private static class DeliveryDriverContext extends OsmContext {

		public DeliveryDriverContext() {
			super(DeliveryDriverBehaviorTest.class.getResource("delivery_config.properties").getFile());
		}
		
		@Override
		public Scenario loadScenario(String name) {
			return new DeliveryScenario();
		}
		
	}

	private static class DeliveryScenario extends PollutionScenario {
		
		public static DeliveryDriver driverSpy;
		
		public static Date departure = new Date(Clock.origin, 1, 10, 30);

		@Override
		public Collection<? extends Agent<?>> buildAgents(SmartGovContext context) {
			DeliveryVehicle fakeVehicle = mock(DeliveryVehicle.class);
			doReturn(0.).when(fakeVehicle).getEmissions(any(Pollutant.class), anyDouble(), anyDouble());
			
			driverSpy = spy(new DeliveryDriver(
					fakeVehicle,
					(OsmContext) context
					));

			// Because the car mover agent body is set in the OsmAgentBody constructor
			// with "this" agent body, the real delivery driver is set as agentBody in the car mover.
			// So this will override the CarMover agent body with the spy.
			Whitebox.setInternalState((CarMover) driverSpy.getMover(), "agentBody", driverSpy);
			
			OriginParkingArea parking = mock(OriginParkingArea.class);
			
			DeliveryDriverBehavior behavior = new DeliveryDriverBehavior(
					driverSpy,
					parking,
					Arrays.asList(
							context.nodes.get("1"),
							context.nodes.get("2"),
							context.nodes.get("3"),
							context.nodes.get("4"),
							context.nodes.get("1")
							),
					departure,
					(OsmContext) context
					);

			OsmAgent agent = new OsmAgent(
					"1",
					driverSpy,
					behavior
					);
			driverSpy.initialize();

			return Arrays.asList(agent);
		}
		
	}
}
