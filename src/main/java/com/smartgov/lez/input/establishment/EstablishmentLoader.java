package com.smartgov.lez.input.establishment;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;
import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicleFactory;
import com.smartgov.lez.core.agent.establishment.Establishment;
import com.smartgov.lez.core.agent.establishment.ST8;
import com.smartgov.lez.core.copert.inputParser.CopertProfile;
import com.smartgov.lez.core.copert.tableParser.CopertParser;

public class EstablishmentLoader {
	
	private Map<String, Establishment> loadedEstablishments;
	private Map<String, List<TemporaryRound>> temporaryRounds;
	
	public EstablishmentLoader() {
		this.loadedEstablishments = new HashMap<>();
		temporaryRounds = new HashMap<>();
	}

	public static Map<String, Establishment> loadEstablishments(
			File establishments, File fleetProfiles, File copertFile, Random random) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(EstablishmentLoader.class, new EstablishmentDeserializer(fleetProfiles, copertFile, random));
		mapper.registerModule(module);
		
		return mapper.readValue(establishments, EstablishmentLoader.class).loadedEstablishments();
	}
	
	public static Map<String, Establishment> loadEstablishments(
			File establishments, File fleetProfiles, File copertFile) throws JsonParseException, JsonMappingException, IOException {
		return loadEstablishments(establishments, fleetProfiles, copertFile, new Random());
	}
	
	private Map<String, Establishment> loadedEstablishments() {
		return loadedEstablishments;
	}
	
	public void load(Establishment establishment) {
		this.loadedEstablishments.put(establishment.getId(), establishment);
	}
	
	public void loadTemporaryRounds(String establishmentId, List<TemporaryRound> round) {
		temporaryRounds.put(establishmentId, round);
	}
	
	public void buildFleets(File fleetProfiles, File copertFile, Random random) throws JsonParseException, JsonMappingException, IOException {
		Map<ST8, CopertProfile> fleetProfilesMap = new ObjectMapper().readValue(fleetProfiles, new TypeReference<Map<ST8, CopertProfile>>(){});
		System.out.println(fleetProfilesMap);
		
		// All the vehicles will belong to the loaded copert table
		CopertParser parser = new CopertParser(copertFile, random);
		
		for(Establishment establishment : loadedEstablishments.values()) {
			/*
			 * Hypothesis : each establishment has at its disposal a vehicle by
			 * daily round it must perform.
			 */
			int fleetSize = temporaryRounds.get(establishment.getId()).size();
			DeliveryVehicleFactory vehicleFactory = new DeliveryVehicleFactory(
					fleetProfilesMap.get(establishment.getActivity()),
					parser
					);
			for(int i = 0; i < fleetSize; i++) {
				List<DeliveryVehicle> fleet = vehicleFactory.create(fleetSize);
				for(DeliveryVehicle vehicle : fleet) {
					establishment.addVehicleToFleet(vehicle);
				}
			}
			System.out.println(establishment);
		}
	}
	
	static class TemporaryRound {
		private List<String> ids;
		private double weight;

		public TemporaryRound(List<String> ids, double weight) {
			super();
			this.ids = ids;
			this.weight = weight;
		}

		public List<String> getIds() {
			return ids;
		}
		public double getWeight() {
			return weight;
		}
	}
}
