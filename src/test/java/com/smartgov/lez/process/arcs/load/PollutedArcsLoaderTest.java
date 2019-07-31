package com.smartgov.lez.process.arcs.load;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

public class PollutedArcsLoaderTest {
	
	private static final File arcFile = new File(PollutedArcsLoaderTest.class.getResource("arcs.json").getFile());
	private static final File nodeFile = new File(PollutedArcsLoaderTest.class.getResource("nodes.json").getFile());
	
	public static PollutedArcsLoader load() {
		PollutedArcsLoader loader = new PollutedArcsLoader();
		loader.load(arcFile, nodeFile);
		return loader;
	}

	@Test
	public void loadArcsTest() {
		assertThat(
				load().getArcs().keySet(),
				contains("0", "1", "2")
				);
	}
	
	@Test
	public void loadNodesTest() {
		assertThat(
				load().getNodes().keySet(),
				contains("1", "2", "3")
				);
	}
}
