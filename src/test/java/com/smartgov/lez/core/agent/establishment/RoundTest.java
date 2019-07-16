package com.smartgov.lez.core.agent.establishment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;
import org.junit.Test;

import smartgov.urban.osm.environment.graph.OsmNode;

public class RoundTest {

	@Test
	public void testGetNodes() {
		List<Establishment> establishments = new ArrayList<>();
		
		List<Matcher<? super String>> expectedNodeIds = new ArrayList<>();
		expectedNodeIds.add(equalTo("-1"));
		
		OsmNode fakeOrigin = mock(OsmNode.class);
		when(fakeOrigin.getId()).thenReturn("-1");
		
		Establishment fakeEstablishmentOrigin = mock(Establishment.class);
		when(fakeEstablishmentOrigin.getClosestOsmNode()).thenReturn(fakeOrigin);
		
		for(int i = 0; i < 10; i++) {
			OsmNode fakeNode = mock(OsmNode.class);
			when(fakeNode.getId()).thenReturn(String.valueOf(i));
			Establishment fakeEstablishment = mock(Establishment.class);
			when(fakeEstablishment.getClosestOsmNode()).thenReturn(fakeNode);
			
			establishments.add(fakeEstablishment);
			
			expectedNodeIds.add(equalTo(String.valueOf(i)));
		}
		expectedNodeIds.add(equalTo("-1"));
		
		Round round = new Round(fakeEstablishmentOrigin, establishments, 0);
		List<OsmNode> nodes = round.getNodes();
		List<String> nodeIds = new ArrayList<>();
		for(OsmNode node : nodes) {
			nodeIds.add(node.getId());
		}
		
		assertThat(
				nodeIds,
				contains(expectedNodeIds)
				);
		
	}
}
