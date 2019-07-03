package com.smartgov.lez.core.copert.tableParser;

import java.io.File;
import java.net.URL;
import org.junit.Test;

import com.smartgov.lez.core.copert.Copert;
import com.smartgov.lez.core.copert.CopertParameters;
import com.smartgov.lez.core.copert.fields.EuroNorm;
import com.smartgov.lez.core.copert.fields.Fuel;
import com.smartgov.lez.core.copert.fields.LightWeightVehicleSegment;
import com.smartgov.lez.core.copert.fields.Pollutant;
import com.smartgov.lez.core.copert.fields.VehicleCategory;
import com.smartgov.lez.core.copert.tableParser.CopertHeader;
import com.smartgov.lez.core.copert.tableParser.CopertParser;
import com.smartgov.lez.core.copert.tableParser.CopertSelector;
import com.smartgov.lez.core.copert.tableParser.CopertTree;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

public class CopertParserTest {
	
	private CopertParser loadParser() {
		URL url = this.getClass().getResource("vehicle_classes_test.csv");
		return new CopertParser(new File(url.getFile()));
	}

	@Test
	public void parseCsvTest() {
		loadParser();
		assertThat(
				loadParser(),
				notNullValue());
	}
	
	@Test
	public void testFirstLevel() {
		CopertParser copertParser = loadParser();
		assertThat(
				copertParser.getCopertTree().getLevel(),
				equalTo("Category")); // First column
		
		assertThat(
				copertParser.getCopertTree().getChildren().keySet(),
				hasSize(2)); // Two entries : Light Weight Commercial and Heady Duty Trucks
		
		assertThat(
				copertParser.getCopertTree().getPath(),
				equalTo(new CopertSelector()) // Void path
				);
	}
	
	@Test
	public void testSecondLevel() {
		CopertParser copertParser = loadParser();
		
		CopertTree subTree1 = copertParser.getCopertTree().getChildren().get(VehicleCategory.LIGHT_WEIGHT.matcher());
		assertThat(
				subTree1.getLevel(),
				equalTo("Fuel")
				);
		assertThat(
				copertParser.getCopertTree().select(VehicleCategory.LIGHT_WEIGHT.matcher()),
				equalTo(subTree1)
				);
		assertThat(
				copertParser.getCopertTree().select(VehicleCategory.LIGHT_WEIGHT.matcher()).getCategory(),
				equalTo(VehicleCategory.LIGHT_WEIGHT.matcher())
				);
		CopertSelector pathToSubtree1 = new CopertSelector();
		pathToSubtree1.put(CopertHeader.CATEGORY, VehicleCategory.LIGHT_WEIGHT);
		assertThat(
				subTree1.getPath(),
				equalTo(pathToSubtree1)
				);
		
		CopertTree subTree2 = copertParser.getCopertTree().getChildren().get(VehicleCategory.HEAVY_DUTY_TRUCK.matcher());
		assertThat(
				subTree2.getLevel(),
				equalTo("Fuel")
				);
		
		assertThat(
				copertParser.getCopertTree().select(VehicleCategory.HEAVY_DUTY_TRUCK.matcher()),
				equalTo(subTree2)
				);
		
		assertThat(
				copertParser.getCopertTree().select(VehicleCategory.HEAVY_DUTY_TRUCK.matcher()).getCategory(),
				equalTo(VehicleCategory.HEAVY_DUTY_TRUCK.matcher())
				);
		CopertSelector pathToSubtree2 = new CopertSelector();
		pathToSubtree2.put(CopertHeader.CATEGORY, VehicleCategory.HEAVY_DUTY_TRUCK);
		assertThat(
				subTree2.getPath(),
				equalTo(pathToSubtree2)
				);
	}
	
	@Test
	public void testSimpleCopertParameters() {
		CopertParser copertParser = loadParser();
		
		CopertTree completeCopertTreeUntilTechnology =
				copertParser.getCopertTree()
				.select(VehicleCategory.LIGHT_WEIGHT.matcher())
				.select(Fuel.PETROL.matcher())
				.select(LightWeightVehicleSegment.N1_II.matcher())
				.select(EuroNorm.EURO1.matcher())
				.select();
		
		Copert copert = new Copert(completeCopertTreeUntilTechnology);
		
		CopertParameters NOxParameters = copert.getCopertParameters(Pollutant.NOx);
		assertThat(
				NOxParameters.getAlpha(),
				equalTo(0d));
		assertThat(
				NOxParameters.getBeta(),
				equalTo(271082027144984d));
		assertThat(
				NOxParameters.getGamma(),
				equalTo(0d));
		assertThat(
				NOxParameters.getDelta(),
				equalTo(323144138307312d));
		assertThat(
				NOxParameters.getEpsilon(),
				equalTo(2d));
		assertThat(
				NOxParameters.getZita(),
				equalTo(39126662368513d));
		assertThat(
				NOxParameters.getHta(),
				equalTo(-1d));
		
	}
	
	@Test
	public void aggregateParametersForAGivenPollutant() {
		CopertParser copertParser = loadParser();
		
		CopertTree completeCopertTreeUntilTechnology =
				copertParser.getCopertTree()
				.select(VehicleCategory.LIGHT_WEIGHT.matcher())
				.select(Fuel.DIESEL.matcher())
				.select(LightWeightVehicleSegment.N1_II.matcher())
				.select(EuroNorm.EURO6.matcher())
				.select();

		// Check that for there are 4 modes for CH4
		assertThat(
				completeCopertTreeUntilTechnology
				.select(Pollutant.CH4.matcher())
				.lineCount(),
				equalTo(4)
				);
		
		CopertSelector selector = new CopertSelector();
		selector.put(CopertHeader.CATEGORY, VehicleCategory.LIGHT_WEIGHT);
		selector.put(CopertHeader.FUEL, Fuel.DIESEL);
		selector.put(CopertHeader.SEGMENT, LightWeightVehicleSegment.N1_II);
		selector.put(CopertHeader.EURO_STANDARD, EuroNorm.EURO6);
		
		Copert copert = new Copert(completeCopertTreeUntilTechnology);
		
		CopertParameters CH4Parameters = copert.getCopertParameters(Pollutant.CH4);
		assertThat(
				CH4Parameters.getAlpha(),
				equalTo(0d)
				);
		assertThat(
				CH4Parameters.getBeta(),
				equalTo(0d)
				);
		assertThat(
				CH4Parameters.getGamma(),
				equalTo(0d));
		assertThat(
				CH4Parameters.getDelta(),
				equalTo((75d + 75d) / 4));
		assertThat(
				CH4Parameters.getEpsilon(),
				equalTo(0d));
		assertThat(
				CH4Parameters.getZita(),
				equalTo(0d));
		assertThat(
				CH4Parameters.getHta(),
				equalTo((1000d + 1000d) / 4));
	}
	
	@Test
	public void randomSubSelectionTest() {
		CopertParser copertParser = loadParser();
		
		CopertTree subCategory = copertParser.getCopertTree()
				.select(VehicleCategory.LIGHT_WEIGHT.matcher())
				.select(Fuel.PETROL.matcher())
				.select(LightWeightVehicleSegment.N1_I.matcher());
		
		assertThat(
				subCategory.getChildren().keySet(),
				hasSize(2));
		
		assertThat(
				subCategory.getChildren().keySet(),
				containsInAnyOrder("Euro 2", "Conventional"));
		
		
		for (int i = 0; i < 100; i++) {
			// Select a random euro norm
			CopertTree randomNorm = subCategory
					.select(); // Random euro norm
			
			String selectedNorm = randomNorm.getCategory();
			
			CopertTree finalLine = randomNorm 
					.select()
					.select(Pollutant.CO.matcher());
			
			assertThat(
					finalLine.lineCount(),
					equalTo(1));
			
			if (selectedNorm.equals("Conventional")) {
				assertThat(
						finalLine.getSubTable().get("Alpha").get(0),
						equalTo("5"));
			}
			else if (selectedNorm.equals("Euro 2")){
				assertThat(
						finalLine.getSubTable().get("Alpha").get(0),
						equalTo("-6"));
			}
			else {
				fail("Unknown Euro Norm");
			}
		}

	}
	
	@Test
	public void testPath() {
		CopertParser copertParser = loadParser();

		CopertSelector selector = new CopertSelector();
		selector.put(CopertHeader.CATEGORY, VehicleCategory.LIGHT_WEIGHT);
		selector.put(CopertHeader.FUEL, Fuel.DIESEL);
		selector.put(CopertHeader.SEGMENT, LightWeightVehicleSegment.N1_II);
		selector.put(CopertHeader.EURO_STANDARD, EuroNorm.EURO6);
		
		// Check that for there are 4 modes for CH4
		assertThat(
				copertParser.getCopertTree().
				select(VehicleCategory.LIGHT_WEIGHT.matcher())
				.select(Fuel.DIESEL.matcher())
				.select(LightWeightVehicleSegment.N1_II.matcher())
				.select(EuroNorm.EURO6.matcher())
				.getPath(),
				equalTo(selector)
				);
		
	}

}
