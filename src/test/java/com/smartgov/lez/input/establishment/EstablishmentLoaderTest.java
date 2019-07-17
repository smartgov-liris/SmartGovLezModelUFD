package com.smartgov.lez.input.establishment;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
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
import com.smartgov.lez.core.copert.fields.CopertField;
import com.smartgov.lez.input.establishment.EstablishmentLoader;

public class EstablishmentLoaderTest {
	
	private static Map<String, Establishment> loadEstablishments() {
		try {
			return EstablishmentLoader.loadEstablishments(
					new File(EstablishmentLoaderTest.class.getResource("establishments.json").getFile()),
					new File(EstablishmentLoaderTest.class.getResource("fleetProfiles.json").getFile()),
					new File(CopertField.class.getResource("complete_test_table.csv").getFile()),
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
		loadEstablishments();
	}
	
	@Test
	public void testRootEstablishmentFields() {
		Map<String, Establishment> establishments = loadEstablishments();
		
		Map<String, Establishment> expected = new HashMap<>();
		expected.put("0", new Establishment(
				"0",
				"establishment 1",
				ST8.INDUSTRY,
				new Coordinate(800439.326324793, 2077962.87711255)
				));
		
		expected.put("1", new Establishment(
				"1",
				"establishment 2",
				ST8.WHOLESALE_BUSINESS,
				new Coordinate(794507.446834672, 2081036.92197918)
				));
		
		expected.put("2", new Establishment(
				"2",
				"establishment 3",
				ST8.CRAFTS_AND_SERVICES,
				new Coordinate(793722.658833829, 2080652.38198779)
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
	
	@Test
	public void fleetFactoryTest() {
		Map<String, Establishment> originEstablishments = loadEstablishments();
		
		Map<String, Map<VehicleCapacity, Collection<DeliveryVehicle>>> originFleets = new HashMap<>();
		for(Establishment establishment : originEstablishments.values()) {
			originFleets.put(establishment.getId(), establishment.getFleet());
		}
		
		// TODO: check fleet size
		
		for(int i = 0; i < 15; i++) {
			Map<String, Establishment> establishments = loadEstablishments();
			
			Map<String, Map<VehicleCapacity, Collection<DeliveryVehicle>>> fleets = new HashMap<>();
			for(Establishment establishment : establishments.values()) {
				fleets.put(establishment.getId(), establishment.getFleet());
			}
			
			assertThat(
					fleets,
					equalTo(originFleets)
					);
		}
	}
}
