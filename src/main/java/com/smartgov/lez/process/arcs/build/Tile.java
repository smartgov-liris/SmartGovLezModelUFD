package com.smartgov.lez.process.arcs.build;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.smartgov.lez.core.copert.fields.Pollutant;
import com.smartgov.lez.process.arcs.load.PollutedArc;

public class Tile {

	@JsonSerialize(using = Bounds.Serializer.class)
	private Bounds bounds;
	@JsonIgnore
	private Collection<PollutedArc> arcs;
	private Map<Pollutant, Double> pollution;
			
	public Tile(Bounds bounds) {
		this.bounds = bounds;
		this.arcs = new ArrayList<>();
		this.pollution = new HashMap<>();
		for(Pollutant pollutant : Pollutant.values()) {
			pollution.put(pollutant, 0.);
		}
	}
	
	public void addArc(PollutedArc arc) {
		arcs.add(arc);
	}
	
	public void computePollution() {
//		double totalArcLength = 0;
//		for(PollutedArc arc : arcs) {
//			totalArcLength += arc.getLength();
//		}
		for(Pollutant pollutant : Pollutant.values()) {
			
			if(arcs.size() == 0) {
				pollution.put(pollutant, 0.);
			}
			else {
				double pollutionRate = 0;
				for(PollutedArc arc : arcs) {
					// meanPollution += arc.getPollution().get(pollutant) * arc.getLength();
					pollutionRate += arc.getPollution().get(pollutant);
				}
				pollutionRate = pollutionRate / bounds.getArea();
				pollution.put(pollutant, pollutionRate);
				}
		}
	}

	public Bounds getBounds() {
		return bounds;
	}

	public Collection<PollutedArc> getArcs() {
		return arcs;
	}

	public Map<Pollutant, Double> getPollution() {
		return pollution;
	}
}
