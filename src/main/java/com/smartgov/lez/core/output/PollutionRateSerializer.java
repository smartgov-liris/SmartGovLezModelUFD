package com.smartgov.lez.core.output;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.smartgov.lez.core.environment.pollution.PollutionRate;

public class PollutionRateSerializer extends StdSerializer<PollutionRate> {

	private static final long serialVersionUID = 1L;

	public PollutionRateSerializer() {
		this(null);
	}
	
	protected PollutionRateSerializer(Class<PollutionRate> t) {
		super(t);
	}

	@Override
	public void serialize(PollutionRate value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeNumber(value.getValue());
	}

}
