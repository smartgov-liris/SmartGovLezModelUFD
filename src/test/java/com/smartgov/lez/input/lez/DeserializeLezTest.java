package com.smartgov.lez.input.lez;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.smartgov.lez.core.environment.lez.Lez;
import com.smartgov.lez.core.environment.lez.criteria.CritAir;
import com.smartgov.lez.core.environment.lez.criteria.CritAirCriteria;

import org.liris.smartgov.simulator.urban.geo.utils.LatLon;

public class DeserializeLezTest {

	@Test
	public void deserializeLezTest() throws JsonParseException, JsonMappingException, IOException {
		
		Lez lez = CritAirLezDeserializer.load(
				new File(this.getClass().getResource("lez.json").getFile())
				);
		
		assertThat(
			lez.getLezCriteria() instanceof CritAirCriteria,
			is(true)
			);
		
		assertThat(
			((CritAirCriteria) lez.getLezCriteria()).getAllowedCritAirs(),
			contains(
				CritAir.CRITAIR_1,
				CritAir.CRITAIR_2,
				CritAir.CRITAIR_3,
				CritAir.CRITAIR_4
				)
			);
		
		assertThat(
			Arrays.asList(lez.getPerimeter()),
			contains(
				new LatLon(45.75450753345595, 4.866525973987469),
				new LatLon(45.755629494110565, 4.876427708999188),
				new LatLon(45.75275316593465, 4.869610199661563)
				)
			);
	}
}
