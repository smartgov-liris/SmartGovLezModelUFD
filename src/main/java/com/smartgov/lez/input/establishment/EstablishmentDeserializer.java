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
import com.smartgov.lez.core.copert.tableParser.CopertParser;
import com.smartgov.lez.input.establishment.EstablishmentLoader.TemporaryRound;

import smartgov.core.simulation.time.Date;
import smartgov.core.simulation.time.WeekDay;
import smartgov.urban.geo.utils.LatLon;

/**
 * Custom Jackson deserializer class used to load and build establishments from json inputs.
 *
 */
public class EstablishmentDeserializer extends StdDeserializer<EstablishmentLoader>{

	private Random random;
	private File fleetProfiles;
	private CopertParser copertParser;
	
	private static final long serialVersionUID = 1L;

	public EstablishmentDeserializer(File fleetProfiles, CopertParser copertParser, Random random) {
		this(null);
		this.random = random;
		this.fleetProfiles = fleetProfiles;
		this.copertParser = copertParser;
	}
	
	public EstablishmentDeserializer(Class<?> c) {
		super(c);
	}

	@Override
	public EstablishmentLoader deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		EstablishmentLoader loader = new EstablishmentLoader(random);
		
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
				try {
					activity = ST8.valueOf(establishmentNode.get("ST8").asText());
				}
				catch(IllegalArgumentException e) {
					activity = ST8.byCode(establishmentNode.get("ST8").asText());
				}
			}
			
			LatLon geoLocation = null;
			if(establishmentNode.has("x")) {
				Coordinate location = new CoordinateXY(
						establishmentNode.get("x").asDouble(),
						establishmentNode.get("y").asDouble()
						);
				
				geoLocation = new SimturbLambert().unproject(location);
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
				
				Double departure = roundNode.get("departure").asDouble();
				int hour = (int) Math.floor(departure);
				int minutes = (int) Math.round(60 * (departure - hour));
				temporaryRounds.add(new TemporaryRound(establishmentIds, new Date(0, WeekDay.MONDAY, hour, minutes), weight));
			}
			loader._loadTemporaryRounds(establishment.getId(), temporaryRounds);
		}
		
		loader._buildFleets(fleetProfiles, copertParser);
		return loader;
	}

}
