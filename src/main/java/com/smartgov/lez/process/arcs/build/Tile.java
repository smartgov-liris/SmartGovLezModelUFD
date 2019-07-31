package com.smartgov.lez.process.arcs.build;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.smartgov.lez.core.copert.fields.Pollutant;
import com.smartgov.lez.process.arcs.load.PollutedArc;

import smartgov.urban.geo.utils.LatLon;

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
		double totalArcLength = 0;
		for(PollutedArc arc : arcs) {
			totalArcLength += arc.getLength();
		}
		for(Pollutant pollutant : Pollutant.values()) {
			double meanPollution = 0;
			for(PollutedArc arc : arcs) {
					meanPollution += arc.getPollution().get(pollutant) * arc.getLength();
			}
			meanPollution = meanPollution / totalArcLength;
			pollution.put(pollutant, meanPollution);
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

	public static class Bounds {
		public LatLon topLeft;
		public LatLon bottomRight;

		public Bounds(LatLon topLeft, LatLon bottomRight) {
			this.topLeft = topLeft;
			this.bottomRight = bottomRight;
		}
		
		public boolean containsLat(double lat) {
			return (bottomRight.lat <= lat) && (lat <= topLeft.lat);
		}
		
		public boolean containsLon(double lon) {
			return (topLeft.lon <= lon) && (lon <= bottomRight.lon);
		}
		
		public static class Serializer extends StdSerializer<Bounds> {

			private static final long serialVersionUID = 1L;

			public Serializer() {
				this(null);
			}
			
			protected Serializer(Class<Bounds> t) {
				super(t);
			}

			@Override
			public void serialize(Bounds value, JsonGenerator gen, SerializerProvider provider) throws IOException {
				gen.writeObject(
						new Double[][] {
							new Double[] {value.topLeft.lat, value.topLeft.lon },
							new Double[] {value.bottomRight.lat, value.bottomRight.lon }
						}
						);
				
			}
			
		}
		
	}
}
