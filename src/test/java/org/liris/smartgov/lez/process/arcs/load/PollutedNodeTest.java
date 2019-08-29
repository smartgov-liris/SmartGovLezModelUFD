package org.liris.smartgov.lez.process.arcs.load;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.liris.smartgov.lez.process.arcs.load.PollutedNode;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class PollutedNodeTest {

	@Test
	public void loadPollutedNodeTest() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper reader = new ObjectMapper();
		List<PollutedNode> loadedNodes = reader.readValue(
				new File(PollutedNodeTest.class.getResource("nodes.json").getFile()),
				new TypeReference<List<PollutedNode>>() {});
		
		assertThat(
				loadedNodes,
				hasSize(3)
				);
		
		PollutedNode node1 = new PollutedNode(
				"1",
				new Double[]{ 45.7406086, 4.8833518 }
				);
		PollutedNode node2 = new PollutedNode(
				"2",
				new Double[]{ 45.7404842, 4.8834019 }
				);
		PollutedNode node3 = new PollutedNode(
				"3",
				new Double[]{ 45.7404414, 4.8834406 }
				);
		
		assertThat(
				loadedNodes,
				contains(node1, node2, node3)
				);
	}
}
