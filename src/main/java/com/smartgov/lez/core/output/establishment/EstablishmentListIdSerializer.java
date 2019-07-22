package com.smartgov.lez.core.output.establishment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.smartgov.lez.core.agent.establishment.Establishment;

public class EstablishmentListIdSerializer extends StdSerializer<List<Establishment>> {

	private static final long serialVersionUID = 1L;

	public EstablishmentListIdSerializer() {
		this(null);
	}
	
	protected EstablishmentListIdSerializer(Class<List<Establishment>> t) {
		super(t);
	}

	@Override
	public void serialize(List<Establishment> value, JsonGenerator gen, SerializerProvider provider)
			throws IOException {
		List<String> establishmentIds = new ArrayList<>();
		for(Establishment establishment : value) {
			establishmentIds.add(establishment.getId());
		}
		gen.writeObject(establishmentIds);
		
	}

}
