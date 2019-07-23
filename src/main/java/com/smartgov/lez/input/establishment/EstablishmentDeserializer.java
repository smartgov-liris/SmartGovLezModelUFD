package com.smartgov.lez.input.establishment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateXY;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.smartgov.lez.core.agent.establishment.Establishment;
import com.smartgov.lez.core.agent.establishment.ST8;
import com.smartgov.lez.input.establishment.EstablishmentLoader.TemporaryRound;

import smartgov.urban.geo.utils.LatLon;
import smartgov.urban.geo.utils.lambert.LambertII;

/**
 * Custom Jackson deserializer class used to load and build establishments from json inputs.
 *
 */
public class EstablishmentDeserializer extends StdDeserializer<EstablishmentLoader>{

	private File fleetProfiles;
	private File copertFile;
	private Random random;
	
	private static final long serialVersionUID = 1L;

	public EstablishmentDeserializer(File fleetProfiles, File copertFile, Random random) {
		this(null);
		this.fleetProfiles = fleetProfiles;
		this.copertFile = copertFile;
		this.random = random;
	}
	
	public EstablishmentDeserializer(Class<?> c) {
		super(c);
	}

	@Override
	public EstablishmentLoader deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		EstablishmentLoader loader = new EstablishmentLoader();
		
		// Load establishment data
		JsonNode establishmentArray = p.getCodec().readTree(p);
		for(int i = 0; i < establishmentArray.size(); i++) {
			JsonNode establishmentNode = establishmentArray.get(i);
			String id = establishmentNode.get("id").asText();
			String name = establishmentNode.get("name").asText();
			ST8 activity;
			if(establishmentNode.get("ST8").isNumber()) {
				activity = ST8.byCode(establishmentNode.get("ST8").asText());
			}
			else {
				activity = ST8.valueOf(establishmentNode.get("ST8").asText());
			}
			
			LatLon geoLocation = null;
			if(establishmentNode.has("x")) {
				Coordinate location = new CoordinateXY(
						establishmentNode.get("x").asDouble(),
						establishmentNode.get("y").asDouble()
						);
				
				geoLocation = new LambertII().unproject(location);
			}
			
			else if (establishmentNode.has("lat")) {
				geoLocation = new LatLon(
						establishmentNode.get("lat").asDouble(),
						establishmentNode.get("lon").asDouble()
						);
			}
			
			Establishment establishment = new Establishment(id, name, activity, geoLocation);
			loader._load(establishment);
			
			
			List<TemporaryRound> temporaryRounds = new ArrayList<>();
			JsonNode roundsArray = establishmentNode.get("rounds");
			
			for(int j = 0; j < roundsArray.size(); j++) {
				JsonNode roundNode = roundsArray.get(j);
				double weight = 0;
				if(roundNode.has("weight")) {
					weight = roundNode.get("weight").asDouble();
				}
				
				List<String> establishmentIds = new ArrayList<>();
				JsonNode establishmentIdsNode = roundNode.get("ids");
				for(int k = 0; k < establishmentIdsNode.size(); k++) {
					establishmentIds.add(establishmentIdsNode.get(k).asText());
				}
				temporaryRounds.add(new TemporaryRound(establishmentIds, weight));
			}
			loader._loadTemporaryRounds(establishment.getId(), temporaryRounds);
		}
		
		loader._buildFleets(fleetProfiles, copertFile, random);
		return loader;
	}

}
