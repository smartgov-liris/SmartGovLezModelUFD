package com.smartgov.lez.input.lez;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.smartgov.lez.core.environment.lez.Lez;
import com.smartgov.lez.core.environment.lez.criteria.CritAir;
import com.smartgov.lez.core.environment.lez.criteria.CritAirCriteria;

import org.liris.smartgov.simulator.urban.geo.utils.LatLon;

public class CritAirLezDeserializer extends StdDeserializer<Lez> {

	private static final long serialVersionUID = 1L;

	public CritAirLezDeserializer() {
		this(null);
	}
	
	protected CritAirLezDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Lez deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode jsonLez = p.getCodec().readTree(p);
		
		Collection<CritAir> allowed = new ArrayList<>();
		JsonNode allowedArray = jsonLez.get("allowed");
		for(int i = 0; i < allowedArray.size(); i++) {
			allowed.add(CritAir.valueOf(allowedArray.get(i).asText()));
		}
		
		
		JsonNode perimeterArray = jsonLez.get("perimeter");
		LatLon[] coordinates = new LatLon[perimeterArray.size()];
		
		for(int i = 0; i < perimeterArray.size(); i++) {
			coordinates[i] = new LatLon(
					perimeterArray.get(i).get(0).asDouble(),
					perimeterArray.get(i).get(1).asDouble()
					);
		}
		return new Lez(coordinates, new CritAirCriteria(allowed));
	}
	
	public static Lez load(File lezFile) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Lez.class, new CritAirLezDeserializer());
		mapper.registerModule(module);
		
		return mapper.readValue(lezFile, Lez.class);
	}

}
