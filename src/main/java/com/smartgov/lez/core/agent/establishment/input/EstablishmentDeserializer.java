package com.smartgov.lez.core.agent.establishment.input;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class EstablishmentDeserializer extends StdDeserializer<EstablishmentLoader>{

	private static final long serialVersionUID = 1L;

	public EstablishmentDeserializer() {
		this(null);
	}
	
	protected EstablishmentDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public EstablishmentLoader deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
			JsonNode establishmentArray = p.getCodec().readTree(p);
			for(int i = 0; i < establishmentArray.size(); i++) {
				JsonNode establishment = establishmentArray.get(i);
				System.out.print(establishment.get("name").asText());
			}
		return new EstablishmentLoader();
	}

}
