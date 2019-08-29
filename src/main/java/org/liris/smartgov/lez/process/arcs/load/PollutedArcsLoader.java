package org.liris.smartgov.lez.process.arcs.load;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PollutedArcsLoader {

	private Map<String, PollutedArc> arcs;
	private Map<String, PollutedNode> nodes;
	
	public PollutedArcsLoader() {
		this.arcs = new TreeMap<>();
		this.nodes = new TreeMap<>();
	}
	
	public void load(File arcsFile, File nodesFile) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<PollutedArc> arcs = mapper.readValue(arcsFile, new TypeReference<List<PollutedArc>>() {});
			for(PollutedArc arc : arcs) {
				this.arcs.put(arc.getId(), arc);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			List<PollutedNode> nodes = mapper.readValue(nodesFile, new TypeReference<List<PollutedNode>>() {});
			for(PollutedNode node : nodes) {
				this.nodes.put(node.getId(), node);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public Map<String, PollutedArc> getArcs() {
		return arcs;
	}
	
	public Map<String, PollutedNode> getNodes() {
		return nodes;
	}
}
