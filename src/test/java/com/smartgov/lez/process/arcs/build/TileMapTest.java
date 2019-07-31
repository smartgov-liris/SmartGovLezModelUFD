package com.smartgov.lez.process.arcs.build;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.TreeMap;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartgov.lez.core.copert.fields.Pollutant;
import com.smartgov.lez.process.arcs.load.PollutedArcsLoader;
import com.smartgov.lez.process.arcs.load.PollutedArcsLoaderTest;

public class TileMapTest {

	@Test
	public void buildTilesTest() {
		PollutedArcsLoader loader = PollutedArcsLoaderTest.load();
		
		TileMap map = new TileMap();
		map.build(loader.getArcs(), loader.getNodes(), 5);
		
		assertThat(
			map.getTiles().values(),
			hasSize(4) // 7 rows
			);
		for(TreeMap<Integer, Tile> line : map.getTiles().values()) {
			assertThat(
					line.values(),
					hasSize(2) // 2 columns
					);
		}
		
		assertThat(
				map.getTiles().get(3).get(0).getArcs(),
				contains(loader.getArcs().get("0"))
				);
		
		assertThat(
				map.getTiles().get(0).get(1).getArcs(),
				contains(loader.getArcs().get("1"))
				);
		
		assertThat(
				map.getTiles().get(1).get(1).getArcs(),
				contains(loader.getArcs().get("2"))
				);
		
		
	}
	
	@Test
	public void pollutionTest() {
		PollutedArcsLoader loader = PollutedArcsLoaderTest.load();
		
		TileMap map = new TileMap();
		map.build(loader.getArcs(), loader.getNodes(), 10);
		
		for(Pollutant pollutant : Pollutant.values()) {
			assertThat(
					map.getTiles().get(1).get(0).getPollution().get(pollutant),
					equalTo(loader.getArcs().get("0").getPollution().get(pollutant))
					);
			
			assertThat(
					map.getTiles().get(0).get(0).getPollution().get(pollutant),
					equalTo(
							(loader.getArcs().get("1").getPollution().get(pollutant) * loader.getArcs().get("1").getLength()
									+ loader.getArcs().get("2").getPollution().get(pollutant) * loader.getArcs().get("2").getLength())
							/ (loader.getArcs().get("1").getLength() + loader.getArcs().get("2").getLength())
							)
					);
		}
		
		
	}
	
	@Test
	public void serializerTest() throws JsonProcessingException {
		PollutedArcsLoader loader = PollutedArcsLoaderTest.load();
		
		TileMap map = new TileMap();
		map.build(loader.getArcs(), loader.getNodes(), 20);

		String tiles = new ObjectMapper().writeValueAsString(map);
		System.out.println(tiles);
		
		String expected = 
				"{\"tiles\":{\"0\":{\"0\":{"
				+ "\"bounds\":[[45.7406086,4.8833518],[45.7404414,4.8834406]],"
				+ "\"pollution\":"
				+ "{";
		ArrayList<Pollutant> pollutants = new ArrayList<>(map.getTiles().get(0).get(0).getPollution().keySet());
		for(int i = 0; i < pollutants.size() - 1; i++) {
			expected += "\"" + pollutants.get(i) +"\":"+map.getTiles().get(0).get(0).getPollution().get(pollutants.get(i)) + ",";
		}
		expected += "\"" + pollutants.get(pollutants.size() - 1)
				+"\":"+map.getTiles().get(0).get(0).getPollution().get(pollutants.get(pollutants.size() - 1))
				+ "}}}}}";
		assertThat(
				tiles,
				equalTo(expected)
				);
	}
	
	
	
}
