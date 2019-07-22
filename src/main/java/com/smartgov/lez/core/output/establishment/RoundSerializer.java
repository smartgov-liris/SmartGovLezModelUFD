package com.smartgov.lez.core.output.establishment;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;
import com.smartgov.lez.core.agent.establishment.Round;

public class RoundSerializer extends StdSerializer<Map<DeliveryVehicle, Round>>{

	private static final long serialVersionUID = 1L;

	public RoundSerializer() {
		this(null);
	}
	
	protected RoundSerializer(Class<Map<DeliveryVehicle, Round>> t) {
		super(t);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void serialize(Map<DeliveryVehicle, Round> value, JsonGenerator gen, SerializerProvider provider)
			throws IOException {
		
		
	}

}
