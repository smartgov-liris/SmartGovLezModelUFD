package org.liris.smartgov.lez.core.agent.driver.vehicle;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.junit.Test;
import org.liris.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;
import org.liris.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicleFactory;
import org.liris.smartgov.lez.core.copert.fields.CopertField;
import org.liris.smartgov.lez.core.copert.fields.EuroNorm;
import org.liris.smartgov.lez.core.copert.fields.Fuel;
import org.liris.smartgov.lez.core.copert.fields.LightWeightVehicleSegment;
import org.liris.smartgov.lez.core.copert.fields.VehicleCategory;
import org.liris.smartgov.lez.core.copert.inputParser.CopertInputReader;
import org.liris.smartgov.lez.core.copert.inputParser.CopertProfile;
import org.liris.smartgov.lez.core.copert.tableParser.CopertFieldNotFoundException;
import org.liris.smartgov.lez.core.copert.tableParser.CopertHeader;
import org.liris.smartgov.lez.core.copert.tableParser.CopertParser;
import org.liris.smartgov.lez.core.copert.tableParser.CopertSelector;

public class DeliveryVehicleFactoryTest {
	
	private static final String test_file_0 = "test_copert_input_0.json";
	private static final String test_file_1 = "test_copert_input_1.json";
	private static final String test_file_2 = "test_copert_input_2.json";
	private static final String test_file_3 = "test_copert_input_3.json";
	private static final String copert_table_test = "copert_table_test.csv";
	private static final String copert_table_full = "copert_table.csv";
	private static final String invalid_copert_class = "invalid_copert_class.json";
	
	private CopertProfile loadCopertProfile(String profileTestFile) {
		URL url = this.getClass().getResource(profileTestFile);
		return CopertInputReader.parseInputFile(new File(url.getFile()));
	}
	
	private CopertParser loadCopertParser(String copertTestTable) {
		URL url = this.getClass().getResource(copertTestTable);
		return new CopertParser(new File(url.getFile()), new Random(1907190830l));
	}
	
	/*
	 * Returns a subset of matching selectors.
	 */
	private static Collection<CopertSelector> selectEqualSelectors(Collection<CopertSelector> selectors, CopertSelector selectorToFind) {
		Collection<CopertSelector> selectedSelectors = new ArrayList<>();
		for(CopertSelector copertSelector : selectors) {
			if (copertSelector.equals(selectorToFind)) {
				selectedSelectors.add(copertSelector);
			}
		}
		return selectedSelectors;
	}
	
	/*
	 * Count the number of vehicles that have a config that match the specified configMatcher.
	 */
	private static int countOccurences(List<DeliveryVehicle> vehicles, Map<CopertHeader, CopertField> configMatcher) {
		if (configMatcher.size() == 0) {
			return 0;
		}
		
		int count = 0;
		for(DeliveryVehicle vehicle : vehicles) {
			boolean match = true;
			CopertField matcher;
			for(Entry<CopertHeader, CopertField> field : configMatcher.entrySet()) {
				switch(field.getKey()) {
				case CATEGORY:
					matcher = vehicle.getCategory();
					break;
				case FUEL:
					matcher = vehicle.getFuel();
					break;
				case SEGMENT:
					matcher = vehicle.getSegment();
					break;
				case TECHNOLOGY:
					matcher = vehicle.getTechnology();
					break;
				case EURO_STANDARD:
					matcher = vehicle.getEuroNorm();
					break;
				default:
					matcher = null;
					break;
				}
				if (matcher != field.getValue()) {
					match = false;
					break;
				}
			}
			if (match) {
				count ++;
			}
		}
		return count;
	}
	
	@Test
	public void generateSelectorsSimpleTest() {
		/*
		 * Generate selectors from a simple profile : 50% Heavy Trucks, 50% Light Weight
		 */
		CopertProfile copertProfile = loadCopertProfile(test_file_0);
		
		DeliveryVehicleFactory factory = new DeliveryVehicleFactory(copertProfile, null);
		LinkedList<CopertSelector> selectors = new LinkedList<>();
		for (int i = 0; i < 10; i ++) {
			selectors.add(new CopertSelector());
		}
		
		// Generate 10 selectors from test_file_0
		factory._generateSelectors(selectors, selectors, copertProfile, 10);
		
		// Should be 50% LIGHT_WEIGHT
		CopertSelector lightWeightSelector = new CopertSelector();
		lightWeightSelector.put(CopertHeader.CATEGORY, VehicleCategory.LIGHT_WEIGHT); // rate : 0.5
		Collection<CopertSelector> lightWeightSelectors = selectEqualSelectors(selectors, lightWeightSelector);
		assertThat(
				lightWeightSelectors,
				hasSize(5)
				);
		
		// Should be 50% HEAVY_DUTY_TRUCK
		CopertSelector heavyTrucksSelector = new CopertSelector(); // rate : 0.5
		heavyTrucksSelector.put(CopertHeader.CATEGORY, VehicleCategory.HEAVY_DUTY_TRUCK);
		Collection<CopertSelector> heavyTrucksSelectors = selectEqualSelectors(selectors, heavyTrucksSelector);
		assertThat(
				heavyTrucksSelectors,
				hasSize(5)
				);
	}
	
	@Test
	public void generateTwoLevelSelectorsTest() {
		/*
		 * Generate selectors for a more complex profile : CATEGORY and FUEL are specified.
		 */
		CopertProfile copertProfile = loadCopertProfile(test_file_1);
		
		DeliveryVehicleFactory factory = new DeliveryVehicleFactory(copertProfile, null);
		LinkedList<CopertSelector> selectors = new LinkedList<>();
		for (int i = 0; i < 16; i ++) {
			selectors.add(new CopertSelector());
		}
		
		// Generate 16 selectors from test_file_1
		factory._generateSelectors(selectors, selectors, copertProfile, 16);
		
		CopertSelector heavyTrucksSelector = new CopertSelector();
		heavyTrucksSelector.put(CopertHeader.CATEGORY, VehicleCategory.HEAVY_DUTY_TRUCK); // rate : 0.5
		heavyTrucksSelector.put(CopertHeader.FUEL, Fuel.DIESEL); // rate : 1
		Collection<CopertSelector> heavyTrucksMatchingSelectors = selectEqualSelectors(selectors, heavyTrucksSelector);
		assertThat(
				heavyTrucksMatchingSelectors,
				hasSize(8)
				);
		
		CopertSelector lightWeightSelector = new CopertSelector();
		lightWeightSelector.put(CopertHeader.CATEGORY, VehicleCategory.LIGHT_WEIGHT); // rate : 0.5
		lightWeightSelector.put(CopertHeader.FUEL, Fuel.PETROL); // rate : 0.25
		Collection<CopertSelector> lightWeightMatchingSelectors = selectEqualSelectors(selectors, lightWeightSelector);
		assertThat(
				lightWeightMatchingSelectors,
				hasSize(2)
				);
		
		lightWeightSelector.put(CopertHeader.FUEL, Fuel.DIESEL); // rate : 0.75
		lightWeightMatchingSelectors = selectEqualSelectors(selectors, lightWeightSelector);
		assertThat(
				lightWeightMatchingSelectors,
				hasSize(6)
				);
	}
	
	@Test
	public void simpleVehicleFactoryTest() {
		/*
		 * Generate 16 vehicles from a simple progile : 50% Heavy Trucks, 50% Light Weight
		 */
		CopertProfile copertProfile = loadCopertProfile(test_file_0);
		CopertParser copertParser = loadCopertParser(copert_table_test);
		DeliveryVehicleFactory factory = new DeliveryVehicleFactory(copertProfile, copertParser);
		
		List<DeliveryVehicle> vehicles = factory.create(16);
		
		for(DeliveryVehicle vehicle : vehicles) {
			/*
			 * Assert that all the fields are well initialized,
			 * even if the user didn't specified all the fields.  
			 */
			assertThat(
					vehicle.getCategory(),
					notNullValue()
					);
			assertThat(
					vehicle.getFuel(),
					notNullValue()
					);
			assertThat(
					vehicle.getSegment(),
					notNullValue()
					);
			assertThat(
					vehicle.getTechnology(),
					notNullValue()
					);
			assertThat(
					vehicle.getEuroNorm(),
					notNullValue()
					);
		}
		
		Map<CopertHeader, CopertField> lightMatcher = new HashMap<>();
		lightMatcher.put(CopertHeader.CATEGORY, VehicleCategory.LIGHT_WEIGHT);
		int light_weight_count = countOccurences(vehicles, lightMatcher);
		
		assertThat(
				light_weight_count,
				equalTo(8));
		
		Map<CopertHeader, CopertField> heavyMatcher = new HashMap<>();
		heavyMatcher.put(CopertHeader.CATEGORY, VehicleCategory.HEAVY_DUTY_TRUCK);
		int heavy_truck_count = countOccurences(vehicles, heavyMatcher);
		
		assertThat(
				heavy_truck_count,
				equalTo(8));
	}
	
	@Test (expected = CopertFieldNotFoundException.class)
	public void testProfileWithNoCopertEntryThrowsException() {
		/*
		 * Try to generate a population with an Heavy Trucks category, and Diesel fuel.
		 * But such Copert entry doesn't exist in the test file, so an error should be thrown.
		 */
		CopertProfile copertProfile = loadCopertProfile(invalid_copert_class);
		CopertParser copertParser = loadCopertParser(copert_table_test);
		DeliveryVehicleFactory factory = new DeliveryVehicleFactory(copertProfile, copertParser);
		
		// Must fail
		factory.create(10);
	}
	
	@Test
	public void twoLevelSelectorVehicleFactoryTest() {
		/*
		 * Generate 16 vehicles from a profile where rates are specified for CATEGORY and FUEL.
		 */
		CopertProfile copertProfile = loadCopertProfile(test_file_1);
		CopertParser copertParser = loadCopertParser(copert_table_test);
		DeliveryVehicleFactory factory = new DeliveryVehicleFactory(copertProfile, copertParser);
		
		List<DeliveryVehicle> vehicles = factory.create(16);
		
		int count;
		Map<CopertHeader, CopertField> matcher = new HashMap<>();
		matcher.put(CopertHeader.CATEGORY, VehicleCategory.HEAVY_DUTY_TRUCK); // rate : 0.5
		matcher.put(CopertHeader.FUEL, Fuel.DIESEL); // rate : 1
		count = countOccurences(vehicles, matcher);
		assertThat(
				count,
				equalTo(8)
				);
		
		matcher = new HashMap<>();
		matcher.put(CopertHeader.CATEGORY, VehicleCategory.LIGHT_WEIGHT); // rate : 0.5
		matcher.put(CopertHeader.FUEL, Fuel.PETROL); // rate : 0.25
		count = countOccurences(vehicles, matcher);
		assertThat(
				count,
				equalTo(2)
				);
		
		matcher.put(CopertHeader.FUEL, Fuel.DIESEL); // rate : 0.75
		count = countOccurences(vehicles, matcher);
		assertThat(
				count,
				equalTo(6)
				);
	}

	@Test
	public void testRoundedVehiclesCount() {
		/*
		 * We specified 50 / 50 for Segments N1-I and N1-II.
		 * But we want 3 vehicles, so 4 selectors will be generated.
		 * This test check that the 3 returned vehicles correspond to
		 * to the specified parameters, without randomness.
		 * Also, check that the third vehicle segment in uniformly chosen
		 * among N1-I and N1-II.
		 * 
		 */
		CopertProfile copertProfile = loadCopertProfile(test_file_2);
		CopertParser copertParser = loadCopertParser(copert_table_full);
		DeliveryVehicleFactory factory = new DeliveryVehicleFactory(copertProfile, copertParser);
		
		int count = 0;
		for (int i = 0; i < 1000; i ++) {
			List<DeliveryVehicle> vehicles = factory.create(3);
			int _count = 0;
			for (DeliveryVehicle vehicle : vehicles) {
				assertThat(
						vehicle.getSegment(),
						anyOf(
							equalTo(LightWeightVehicleSegment.N1_I),
							equalTo(LightWeightVehicleSegment.N1_II)
							)
						);
				if(vehicle.getSegment() == LightWeightVehicleSegment.N1_I) {
					_count ++;
				}
			}
			// There should be at least one N1_I
			assertThat(
					_count,
					greaterThan(0)
					);
			// There should be at least one N1_II
			assertThat(
					_count,
					lessThan(3)
					);
			if (_count == 1) {
				// When we have 1 N1_I and 2 N1_II
				count ++;
			}
		}
		// Actually, count should be more or less 500, but we won't introduce
		// some randomness in the unit test.
		assertThat(
				count,
				greaterThan(0)
				);
		assertThat(
				count,
				lessThan(1000)
				);
	}
	
	@Test
	public void testNonConsecutiveSelectorsTest() {
		/*
		 * Test what append when, for example, we specify CATEGORY
		 * and EURO_STANDARD without specifying SEGMENT and FUEL.
		 */
		CopertProfile copertProfile = loadCopertProfile(test_file_3);
		CopertParser copertParser = loadCopertParser(copert_table_full);
		DeliveryVehicleFactory factory = new DeliveryVehicleFactory(copertProfile, copertParser);
		
		List<DeliveryVehicle> vehicles = factory.create(12);
		
		int euro1 = 0;
		int euro6 = 0;
		for(DeliveryVehicle vehicle : vehicles) {
			assertThat(
					vehicle.getCategory(),
					equalTo(VehicleCategory.LIGHT_WEIGHT)
					);
			assertThat(
					vehicle.getEuroNorm(),
					anyOf(
						equalTo(EuroNorm.EURO1),
						equalTo(EuroNorm.EURO6)
						)
					);
			assertThat(
					vehicle.getSegment(),
					not(equalTo(LightWeightVehicleSegment.RANDOM))
					);
			assertThat(
					vehicle.getFuel(),
					not(equalTo(Fuel.RANDOM))
					);
			
			switch(vehicle.getEuroNorm()) {
			case EURO1:
				euro1++;
				break;
			case EURO6:
				euro6++;
				break;
			default:
				break;
			}
		}
		assertThat(
			euro1,
			equalTo(3)
			);
		
		assertThat(
			euro6,
			equalTo(9)
			);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArgumentExceptionWhenInvalidSum() {
		/*
		 * Error when proportions sum not equal 1
		 */
		CopertProfile copertProfile = loadCopertProfile("bad_input_0.json");
		CopertParser copertParser = loadCopertParser(copert_table_full);
		DeliveryVehicleFactory factory = new DeliveryVehicleFactory(copertProfile, copertParser);
		

		factory.create(12);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArgumentExceptionWhenInvalidRange() {
		/*
		 * Error when proportions not in [0, 1]
		 */
		CopertProfile copertProfile = loadCopertProfile("bad_input_1.json");
		CopertParser copertParser = loadCopertParser(copert_table_full);
		DeliveryVehicleFactory factory = new DeliveryVehicleFactory(copertProfile, copertParser);

		factory.create(12);
	}

}
