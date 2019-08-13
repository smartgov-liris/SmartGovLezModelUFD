package com.smartgov.lez.input.establishment;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartgov.lez.core.agent.establishment.ST8;
import com.smartgov.lez.core.copert.tableParser.CopertHeader;

public class St8FleetProfilesTest {

	@Test
	public void deserializationWithAllSt8Test() throws JsonParseException, JsonMappingException, IOException {
		St8FleetProfiles fleetProfilesMap = new ObjectMapper().readValue(
				new File(this.getClass().getResource("fleetProfiles.json").getFile()),
				St8FleetProfiles.class
				);
		assertThat(
				fleetProfilesMap.keySet(),
				containsInAnyOrder(ST8.values())
				);
		for(ST8 st8 : fleetProfilesMap.keySet()) {
			assertThat(
					fleetProfilesMap.get(st8).getHeader(),
					equalTo(CopertHeader.CATEGORY)
					);
			assertThat(
					fleetProfilesMap.get(st8).getValues(),
					hasSize(2)
					);
		}
	}
	
	@Test
	public void deserializationWithDefaultTest() throws JsonParseException, JsonMappingException, IOException {
		St8FleetProfiles fleetProfilesMap = new ObjectMapper().readValue(
				new File(this.getClass().getResource("industryFleetProfiles.json").getFile()),
				St8FleetProfiles.class
				);
		assertThat(
				fleetProfilesMap.keySet(),
				containsInAnyOrder(ST8.values())
				);
		
		for(ST8 st8 : fleetProfilesMap.keySet()) {
			if(st8 == ST8.INDUSTRY) {
				assertThat(
						fleetProfilesMap.get(st8).getHeader(),
						equalTo(CopertHeader.CATEGORY)
						);
				assertThat(
						fleetProfilesMap.get(st8).getValues(),
						hasSize(2)
						);
			}
			else {
				assertThat(
						fleetProfilesMap.get(st8).getHeader(),
						equalTo(CopertHeader.CATEGORY)
						);
				assertThat(
						fleetProfilesMap.get(st8).getValues(),
						hasSize(1)
						);
				assertThat(
						fleetProfilesMap.get(st8).getValues().get(0).getValue(),
						equalTo("LIGHT_WEIGHT")
						);
				assertThat(
						fleetProfilesMap.get(st8).getValues().get(0).getRate(),
						equalTo(1.0f)
						);
			}
		}
	}
	
	@Test
	public void deserializeWithDefaultOnly() throws JsonParseException, JsonMappingException, IOException {
		St8FleetProfiles fleetProfilesMap = new ObjectMapper().readValue(
				new File(this.getClass().getResource("defaultFleetProfile.json").getFile()),
				St8FleetProfiles.class
				);

		assertThat(
				fleetProfilesMap.keySet(),
				containsInAnyOrder(ST8.values())
				);
		
		for(ST8 st8 : fleetProfilesMap.keySet()) {
			assertThat(
					fleetProfilesMap.get(st8).getHeader(),
					equalTo(CopertHeader.CATEGORY)
					);
			assertThat(
					fleetProfilesMap.get(st8).getValues(),
					hasSize(1)
					);
			assertThat(
					fleetProfilesMap.get(st8).getValues().get(0).getValue(),
					equalTo("LIGHT_WEIGHT")
					);
			assertThat(
					fleetProfilesMap.get(st8).getValues().get(0).getRate(),
					equalTo(1.0f)
					);
		}
	}
}
