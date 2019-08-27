package com.smartgov.lez.process.arcs.build;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.liris.smartgov.simulator.urban.geo.utils.LatLon;

public class Bounds {
	private LatLon topLeft;
	private LatLon bottomRight;
	private Double area;

	public Bounds(LatLon topLeft, LatLon bottomRight) {
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
		this.area = LatLon.distance(
					new LatLon(topLeft.lat, topLeft.lon),
					new LatLon(topLeft.lat, bottomRight.lon)
				)
				* LatLon.distance(
					new LatLon(topLeft.lat, topLeft.lon),
					new LatLon(bottomRight.lat, topLeft.lon)
					);
	}
	
	public LatLon getTopLeft() {
		return topLeft;
	}

	public LatLon getBottomRight() {
		return bottomRight;
	}

	public Double getArea() {
		return area;
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
