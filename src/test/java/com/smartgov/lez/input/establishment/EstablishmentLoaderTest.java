package com.smartgov.lez.input.establishment;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;
import com.smartgov.lez.core.agent.establishment.Establishment;
import com.smartgov.lez.core.agent.establishment.ST8;
import com.smartgov.lez.core.agent.establishment.VehicleCapacity;
import com.smartgov.lez.core.copert.tableParser.CopertParserTest;
import com.smartgov.lez.input.establishment.EstablishmentLoader;

import smartgov.urban.geo.utils.LatLon;
import smartgov.urban.geo.utils.lambert.LambertII;

public class EstablishmentLoaderTest {
	
	private static Map<String, Establishment> loadEstablishments(String fileName) {
		try {
			return EstablishmentLoader.loadEstablishments(
					new File(EstablishmentLoaderTest.class.getResource(fileName).getFile()),
					new File(EstablishmentLoaderTest.class.getResource("fleetProfiles.json").getFile()),
					new File(CopertParserTest.class.getResource("vehicle_classes_test.csv").getFile()),
					new Random(170720191337l)
					);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Test
	public void loadTest() throws JsonParseException, JsonMappingException, IOException {
		loadEstablishments("establishments_lambert.json");
		loadEstablishments("establishments_geo.json");
	}
	
	/*
	 * Test with Lambert II x/y coordinates
	 */
	@Test
	public void testRootEstablishmentFields() {
		Map<String, Establishment> establishments = loadEstablishments("establishments_lambert.json");
		
		Map<String, Establishment> expected = new HashMap<>();
		expected.put("0", new Establishment(
				"0",
				"establishment 1",
				ST8.INDUSTRY,
				new LambertII().unproject(new Coordinate(800439.326324793, 2077962.87711255))
				));
		
		expected.put("1", new Establishment(
				"1",
				"establishment 2",
				ST8.WHOLESALE_BUSINESS,
				new LambertII().unproject(new Coordinate(794507.446834672, 2081036.92197918))
				));
		
		expected.put("2", new Establishment(
				"2",
				"establishment 3",
				ST8.CRAFTS_AND_SERVICES,
				new LambertII().unproject(new Coordinate(793722.658833829, 2080652.38198779))
				));
		
		assertThat(
				establishments.values(),
				hasSize(3)
				);
		
		for(String id : establishments.keySet()) {
			Establishment establishment = establishments.get(id);
			Establishment expectedEstablishment = expected.get(id);
			assertThat(
				establishment.getId(),
				equalTo(expectedEstablishment.getId())
				);
			assertThat(
					establishment.getName(),
					equalTo(expectedEstablishment.getName())
					);
			assertThat(
					establishment.getActivity(),
					equalTo(expectedEstablishment.getActivity())
					);
			assertThat(
					establishment.getLocation(),
					equalTo(expectedEstablishment.getLocation())
					);
		}
	}
	
	/*
	 * Test with geographical coordinates
	 */
	@Test
	public void loadGeoCoordinates() {
		Map<String, Establishment> establishments = loadEstablishments("establishments_geo.json");
		Map<String, LatLon> expected = new HashMap<>();
		expected.put("0", new LatLon(46.986083, 3.810298));
		expected.put("1", new LatLon(46.986057, 3.811918));
		expected.put("2", new LatLon(46.987589, 3.806566));
		
		for(String id : establishments.keySet()) {
			assertThat(
					establishments.get(id).getLocation(),
					equalTo(expected.get(id))
					);
		}
	}
	
	@Test
	public void fleetFactoryTest() {
		Map<String, Establishment> originEstablishments = loadEstablishments("establishments_lambert.json");

		assertThat(
				originEstablishments.values(),
				hasSize(3)
				);

		Map<String, Map<VehicleCapacity, Collection<DeliveryVehicle>>> originFleets = new HashMap<>();
		for(Establishment establishment : originEstablishments.values()) {
			originFleets.put(establishment.getId(), establishment.getFleetByCapacity());
			int expectedFleetSize = 0;
			switch(establishment.getId()) {
			case "0":
				expectedFleetSize = 3;
				break;
			case "2":
				expectedFleetSize = 1;
				break;
			}
			assertThat(
					establishment.getFleet().size(),
					equalTo(expectedFleetSize)
					);
			
		}
		
		for(int i = 0; i < 15; i++) {
			/*
			 * Prove that the generated fleet is always the same.
			 */
			Map<String, Establishment> establishments = loadEstablishments("establishments_lambert.json");
			
			for(Establishment establishment : establishments.values()) {
				assertThat(
						establishment.getFleetByCapacity().keySet(),
						equalTo(originFleets.get(establishment.getId()).keySet())
						);
				
				Establishment originEstablishment = originEstablishments.get(establishment.getId());
				
				for(VehicleCapacity capacity : establishment.getFleetByCapacity().keySet()) {
					assertThat(
							establishment.getFleetByCapacity().get(capacity).size(),
							equalTo(originEstablishment.getFleetByCapacity().get(capacity).size())
							);
					Iterator<DeliveryVehicle> origin = establishment.getFleetByCapacity().get(capacity).iterator();
					Iterator<DeliveryVehicle> current = establishment.getFleetByCapacity().get(capacity).iterator();
					while(origin.hasNext()) {
						assertThat(
								origin.next().equalCharacteristics(current.next()),
								equalTo(true)
								);
					}
				}
			}
		}
	}
	
	@Test
	public void loadRoundsTest() {
		Map<String, Establishment> establishments = loadEstablishments("establishments_lambert.json");
		
		assertThat(
				establishments.values(),
				hasSize(3)
				);
		
		for(Establishment establishment : establishments.values()) {
			/*
			 * Fleet size must be equal to the rounds size.
			 */
			assertThat(
					establishment.getFleet().size(),
					equalTo(establishment.getRounds().size())
					);
		}
		
		Establishment establishmentWithThreeRounds = establishments.get("0");
		List<DeliveryVehicle> vehicles = new ArrayList<>(establishmentWithThreeRounds.getFleet().values());
		vehicles.sort((vehicle1, vehicle2) -> vehicle1.compareTo(vehicle2));
		
		/*
		 * Assert that vehicles are assigned to rounds according to their capacities.
		 * The lighter vehicle must handle the lighter round and reciprocally.
		 */
		List<DeliveryVehicle> lighterVehicles = new ArrayList<>();
		for(DeliveryVehicle vehicle : vehicles) {
			for(DeliveryVehicle lighterVehicle : lighterVehicles) {
				assertThat(
						establishmentWithThreeRounds.getRounds().get(lighterVehicle.getId()).getInitialWeight(),
						lessThanOrEqualTo(establishmentWithThreeRounds.getRounds().get(vehicle.getId()).getInitialWeight())
						);
			}
			lighterVehicles.add(vehicle);
		}
		
	}
}
