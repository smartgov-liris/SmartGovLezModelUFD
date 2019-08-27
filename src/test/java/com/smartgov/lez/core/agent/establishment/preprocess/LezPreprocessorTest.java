package com.smartgov.lez.core.agent.establishment.preprocess;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;
import com.smartgov.lez.core.agent.establishment.Establishment;
import com.smartgov.lez.core.agent.establishment.Round;
import com.smartgov.lez.core.agent.establishment.preprocess.LezPreprocessor;
import com.smartgov.lez.core.copert.fields.CopertFieldsTest;
import com.smartgov.lez.core.copert.fields.EuroNorm;
import com.smartgov.lez.core.copert.fields.Fuel;
import com.smartgov.lez.core.copert.fields.HeavyDutyTrucksSegment;
import com.smartgov.lez.core.copert.fields.Technology;
import com.smartgov.lez.core.copert.fields.VehicleCategory;
import com.smartgov.lez.core.copert.tableParser.CopertParser;
import com.smartgov.lez.core.environment.lez.Lez;
import com.smartgov.lez.core.environment.lez.criteria.NothingAllowedCriteria;

import org.liris.smartgov.simulator.urban.osm.environment.graph.OsmNode;

public class LezPreprocessorTest {

	private static CopertParser loadCopertParser() {
		URL url = CopertFieldsTest.class.getResource("complete_test_table.csv"); // Complete Copert table with Light Commercial Vehicles and Heavy Duty Trucks
		return new CopertParser(new File(url.getFile()), new Random(1907190831l));
	}
	
	/*
	 * An example where anything is contained in the lez, so does the origin
	 * establishment, so all the vehicles must be replaced.
	 */
	@Test
	public void testBasicPreprocess() {
		Lez lez = mock(Lez.class);
		when(lez.getLezCriteria()).thenReturn(new NothingAllowedCriteria());
		when(lez.contains(any())).thenReturn(true);
		
		Map<String, DeliveryVehicle> testFleet = new HashMap<>();
		
		testFleet.put(
			"0",
			new DeliveryVehicle(
				"0",
				VehicleCategory.HEAVY_DUTY_TRUCK,
				Fuel.DIESEL,
				HeavyDutyTrucksSegment.ARTICULATED_14_20_T,
				EuroNorm.EURO2,
				Technology.DPF_SCR,
				null
				)
			);
		
		testFleet.put(
				"1",
				new DeliveryVehicle(
					"1",
					VehicleCategory.HEAVY_DUTY_TRUCK,
					Fuel.DIESEL,
					HeavyDutyTrucksSegment.ARTICULATED_14_20_T,
					EuroNorm.EURO1,
					Technology.DPF_SCR,
					null
					)
				);
		
		Establishment establishment = mock(Establishment.class);
		when(establishment.getFleet()).thenReturn(testFleet);
		
		LezPreprocessor preprocessor = new LezPreprocessor(lez, loadCopertParser());
		preprocessor.preprocess(establishment);
		
		DeliveryVehicle expectedCharacteristics = new DeliveryVehicle(
				null,
				VehicleCategory.HEAVY_DUTY_TRUCK,
				Fuel.DIESEL,
				HeavyDutyTrucksSegment.ARTICULATED_14_20_T,
				EuroNorm.EURO6,
				null,
				null
				);
		
		assertThat(
				testFleet.values(),
				hasSize(2)
				);
		
		for (DeliveryVehicle vehicle : testFleet.values()) {
			assertThat(
					vehicle.getCategory().equals(expectedCharacteristics.getCategory()),
					is(true)
					);
			assertThat(
					vehicle.getSegment().equals(expectedCharacteristics.getSegment()),
					is(true)
					);
			assertThat(
					vehicle.getFuel().equals(expectedCharacteristics.getFuel()),
					is(true)
					);
			assertThat(
					vehicle.getEuroNorm().equals(expectedCharacteristics.getEuroNorm()),
					is(true)
					);
		}
	}
	
	/*
	 * An example with an establishment not located in the lez, but
	 * with a round that has an establishment in the lez. The associated
	 * vehicle must be replaced.
	 */
	@Test
	public void testRoundPreprocess() {
		OsmNode originFakeNode = mock(OsmNode.class);
		when(originFakeNode.getId()).thenReturn("0");
		OsmNode destinationFakeNode = mock(OsmNode.class);
		when(destinationFakeNode.getId()).thenReturn("1");
		
		Establishment origin = mock(Establishment.class);
		when(origin.getClosestOsmNode()).thenReturn(originFakeNode);
		
		Map<String, DeliveryVehicle> originFleet = new HashMap<>();
		originFleet.put("0", new DeliveryVehicle(
				"0",
				VehicleCategory.HEAVY_DUTY_TRUCK,
				Fuel.DIESEL,
				HeavyDutyTrucksSegment.ARTICULATED_14_20_T,
				EuroNorm.EURO2,
				Technology.DPF_SCR,
				null
				)
				);
		when(origin.getFleet()).thenReturn(originFleet);
		
		Establishment destination = mock(Establishment.class);
		when(destination.getClosestOsmNode()).thenReturn(destinationFakeNode);
		
		Round round = mock(Round.class);
		List<Establishment> establishments = Arrays.asList(destination);
		when(round.getEstablishments()).thenReturn(establishments);
		
		Map<String, Round> rounds = new HashMap<>();
		rounds.put("0", round);
		
		when(origin.getRounds()).thenReturn(rounds);
		
		Lez lez = mock(Lez.class);
		when(lez.getLezCriteria()).thenReturn(new NothingAllowedCriteria());
		
		when(lez.contains(originFakeNode)).thenReturn(false);
		when(lez.contains(destinationFakeNode)).thenReturn(true);
		
		assertThat(
				lez.contains(originFakeNode),
				is(false)
				);
		
		assertThat(
				lez.contains(destinationFakeNode),
				is(true)
				);
		
		LezPreprocessor preprocessor = new LezPreprocessor(lez, loadCopertParser());
		preprocessor.preprocess(origin);
		
		DeliveryVehicle expectedCharacteristics = new DeliveryVehicle(
				null,
				VehicleCategory.HEAVY_DUTY_TRUCK,
				Fuel.DIESEL,
				HeavyDutyTrucksSegment.ARTICULATED_14_20_T,
				EuroNorm.EURO6,
				Technology.DPF_SCR,
				null
				);
		
		DeliveryVehicle vehicle = origin.getFleet().get("0");
		assertThat(
				vehicle.getCategory().equals(expectedCharacteristics.getCategory()),
				is(true)
				);
		assertThat(
				vehicle.getSegment().equals(expectedCharacteristics.getSegment()),
				is(true)
				);
		assertThat(
				vehicle.getFuel().equals(expectedCharacteristics.getFuel()),
				is(true)
				);
		assertThat(
				vehicle.getEuroNorm().equals(expectedCharacteristics.getEuroNorm()),
				is(true)
				);
	}
}
