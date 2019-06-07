package com.smartgov.lez.core.output;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.smartgov.lez.core.copert.fields.Pollutant;
import com.smartgov.lez.core.environment.pollution.Pollution;
import com.smartgov.lez.core.environment.pollution.PollutionRate;

public class PollutionSerializer extends StdSerializer<Pollution> {

	private static final long serialVersionUID = 1L;

	public PollutionSerializer() {
		this(null);
	}
	
	protected PollutionSerializer(Class<Pollution> t) {
		super(t);
	}

	@Override
	public void serialize(Pollution value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		Map<Pollutant, Double> pollutionRates = new HashMap<>();
		
		for(Entry<Pollutant, PollutionRate> pollutionRate : value.entrySet()) {
			pollutionRates.put(pollutionRate.getKey(), pollutionRate.getValue().getValue());
		}
		
		gen.writeObject(pollutionRates);
		
	}

}
