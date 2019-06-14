package com.smartgov.lez.core.copert.inputParser;

import org.junit.Test;

import com.smartgov.lez.core.copert.inputParser.CopertInputReader;
import com.smartgov.lez.core.copert.inputParser.CopertProfile;
import com.smartgov.lez.core.copert.inputParser.CopertRate;
import com.smartgov.lez.core.copert.tableParser.CopertHeader;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

import java.net.URL;
import java.util.List;

public class CopertInputReaderTest {
	
	private static final String test_file_0 = "test_copert_input_0.json";
	private static final String test_file_1 = "test_copert_input_1.json";
	
	private CopertProfile testInput(String testFile) {
		CopertInputReader reader = new CopertInputReader();
		URL url = this.getClass().getResource(testFile);
		return reader.parseInputFile(url.getFile());
	}

	@Test
	public void loadTestProfile() {
		testInput(test_file_0);
	}
	
	@Test
	public void readSimpleFields() {
		CopertProfile copertProfile = testInput(test_file_0);
		assertThat(
				copertProfile.getValues(),
				hasSize(2));
		assertThat(copertProfile.getHeader(), equalTo(CopertHeader.CATEGORY));
		
		CopertRate rate1 = copertProfile.getValues().get(0);
		assertThat(rate1.getValue(), equalTo("LIGHT_WEIGHT"));
		assertThat(rate1.getRate(), equalTo(0.5f));
		
		CopertRate rate2 = copertProfile.getValues().get(1);
		assertThat(rate2.getValue(), equalTo("HEAVY_DUTY_TRUCK"));
		assertThat(rate2.getRate(), equalTo(0.5f));
	}
	
	@Test
	public void readNestedFields() {
		CopertProfile copertProfile = testInput(test_file_1);
		assertThat(
				copertProfile.getValues(),
				hasSize(2));
		
		CopertProfile subProfile1 = copertProfile.getValues().get(0).getSubProfile();
		assertThat(
				subProfile1.getValues(),
				hasSize(1));
		
		assertThat(
				subProfile1.getValues().get(0).getValue(),
				equalTo("PETROL"));
		
		assertThat(
				subProfile1.getValues().get(0).getRate(),
				equalTo(1f));
		
		CopertProfile subProfile2 = copertProfile.getValues().get(1).getSubProfile();
		assertThat(
				subProfile2.getValues(),
				hasSize(2));
		
		assertThat(
				subProfile2.getValues().get(0).getValue(),
				equalTo("PETROL"));
		
		assertThat(
				subProfile2.getValues().get(0).getRate(),
				equalTo(0.25f));
		
		assertThat(
				subProfile2.getValues().get(1).getValue(),
				equalTo("DIESEL"));
		
		assertThat(
				subProfile2.getValues().get(1).getRate(),
				equalTo(0.75f));
	}

}
