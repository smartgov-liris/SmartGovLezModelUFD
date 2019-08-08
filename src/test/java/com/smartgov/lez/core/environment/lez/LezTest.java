package com.smartgov.lez.core.environment.lez;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.smartgov.lez.core.environment.lez.Lez;

import smartgov.urban.geo.utils.LatLon;
import smartgov.urban.osm.environment.graph.OsmNode;

public class LezTest {

	@Test
	public void testOsmNodeInLez() {
		Lez lez = new Lez(new LatLon[] {
							new LatLon(45., 4.),
							new LatLon(45., 4.1),
							new LatLon(45.1, 4.)
						},
						null
				);
		
		LatLon[] testPoints = {
				new LatLon(45.05, 4.05),
				new LatLon(45., 4.),
				new LatLon(46., 5.)
		};
		
		List<OsmNode> nodes = new ArrayList<>();
		
		for(int i = 0; i < 3; i++) {
			OsmNode fakeNode = mock(OsmNode.class);
			
			when(fakeNode.getPosition()).thenReturn(testPoints[i]);
			nodes.add(fakeNode);
		}
		
		assertThat(
				lez.contains(nodes.get(0)),
				equalTo(true)
				);
		assertThat(
				lez.contains(nodes.get(1)),
				equalTo(false)
				);
		assertThat(
				lez.contains(nodes.get(2)),
				equalTo(false)
				);
		
	}
	
	@Test
	public void testNoLezContainsNothing() {
		Lez lez = Lez.none();
		
		OsmNode fakeNode = mock(OsmNode.class);
		
		assertThat(
				lez.contains(fakeNode),
				equalTo(false)
				);
	}
}
