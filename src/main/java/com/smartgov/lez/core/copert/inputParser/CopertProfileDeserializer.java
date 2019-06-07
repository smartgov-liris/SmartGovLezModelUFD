package com.smartgov.lez.core.copert.inputParser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class CopertProfileDeserializer extends JsonDeserializer<CopertRate>{

	@Override
	public CopertRate deserialize(JsonParser arg0, DeserializationContext arg1)
			throws IOException, JsonProcessingException {
		
		
		return null;
	}

}
