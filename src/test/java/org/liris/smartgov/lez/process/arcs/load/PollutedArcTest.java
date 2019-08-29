package org.liris.smartgov.lez.process.arcs.load;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.liris.smartgov.lez.core.copert.fields.Pollutant;
import org.liris.smartgov.lez.process.arcs.load.PollutedArc;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PollutedArcTest {

	@Test
	public void loadPollutedArcs() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper reader = new ObjectMapper();
		List<PollutedArc> loadedArcs = reader.readValue(
				new File(PollutedArcTest.class.getResource("arcs.json").getFile()),
				new TypeReference<List<PollutedArc>>() {});
		
		assertThat(
				loadedArcs,
				hasSize(3)
				);
		
		Map<Pollutant, Double> pollution1 = new HashMap<>();
		for(Pollutant pollutant : Pollutant.values()) {
			pollution1.put(pollutant, 0.);
		}
		pollution1.put(Pollutant.NOx, 10.);
		pollution1.put(Pollutant.CH4, 2.);
		
		PollutedArc arc1 = new PollutedArc(
				"0",
				"1",
				"2",
				14.368659363540491,
				pollution1
				);

		assertThat(
				arc1,
				equalTo(loadedArcs.get(0))
				);
	}
}
