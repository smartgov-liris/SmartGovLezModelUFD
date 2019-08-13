package com.smartgov.lez.input.establishment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;
import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicleFactory;
import com.smartgov.lez.core.agent.establishment.Establishment;
import com.smartgov.lez.core.agent.establishment.Round;
import com.smartgov.lez.core.agent.establishment.VehicleCapacity;
import com.smartgov.lez.core.copert.tableParser.CopertParser;

import smartgov.core.simulation.time.Date;

/**
 * Class used to load establishments from an input json file.
 *
 * <p>
 * The following processes are also performed :
 * <ul>
 * <li> Build rounds from those specified for each establishments </li>
 * <li> Build a fleet for each establishment, according to the number of
 * vehicles required and the copert inputs </li>
 * <li> Associate generated vehicles to rounds </li>
 * </ul>
 */
public class EstablishmentLoader {
	
	private Random random;
	private Map<String, Establishment> loadedEstablishments;
	private Map<String, List<TemporaryRound>> temporaryRounds;
	
	EstablishmentLoader(Random random) {
		this.random = random;
		this.loadedEstablishments = new HashMap<>();
		temporaryRounds = new HashMap<>();
	}

	// TODO : Detail file formats
	/**
	 * Load establishments and build rounds and fleets from the specified inputs.
	 * 
	 * @param establishmentsFile an establishment json file
	 * @param fleetProfiles a json fleet profile
	 * @param parser loaded copert parser
	 * @param random random instance used by the DeliveryVehicleFactory
	 * @return built establishments
	 * @throws JsonParseException json exception
	 * @throws JsonMappingException json exception
	 * @throws IOException file reading exception
	 */
	public static Map<String, Establishment> loadEstablishments(
			File establishmentsFile, File fleetProfiles, CopertParser parser, Random random) throws JsonParseException, JsonMappingException, IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		
		module.addDeserializer(EstablishmentLoader.class, new EstablishmentDeserializer(fleetProfiles, parser, random));
		mapper.registerModule(module);
		
		return mapper.readValue(establishmentsFile, EstablishmentLoader.class).loadedEstablishments();
	}
	
	private Map<String, Establishment> loadedEstablishments() {
		return loadedEstablishments;
	}
	
	void _load(Establishment establishment) {
		this.loadedEstablishments.put(establishment.getId(), establishment);
	}
	
	void _loadTemporaryRounds(String establishmentId, List<TemporaryRound> round) {
		temporaryRounds.put(establishmentId, round);
	}
	
	void _buildFleets(File fleetProfiles, CopertParser parser) throws JsonParseException, JsonMappingException, IOException {
		
		St8FleetProfiles fleetProfilesMap = new ObjectMapper().readValue(fleetProfiles, St8FleetProfiles.class);
		
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

			List<DeliveryVehicle> fleet = vehicleFactory.create(fleetSize, random);
			for(DeliveryVehicle vehicle : fleet) {
				establishment.addVehicleToFleet(vehicle);
			}
		}
		buildRounds();
	}
	
	private void buildRounds() {
		for(String establishmentId : temporaryRounds.keySet()) {
			Establishment loadedEstablishment = loadedEstablishments.get(establishmentId);
			List<Round> rounds = new ArrayList<>();
			for(TemporaryRound tempRound : temporaryRounds.get(establishmentId)) {
				List<Establishment> roundEstablishments = new ArrayList<>();
				for(String id : tempRound.getIds()) {
					Establishment roundEstablishment = loadedEstablishments.get(id);
					if(roundEstablishment == null) {
						throw new IllegalStateException(
								"Establishment id " + "\"" + id + "\" in round " + tempRound.getIds()
								+ "of establishment " + establishmentId + " does not correspond to any input establishment.");
					}
					roundEstablishments.add(roundEstablishment);
				}
				rounds.add(new Round(loadedEstablishment, roundEstablishments, tempRound.getDeparture(), tempRound.getWeight()));
			}
			assignVehiclesToRounds(rounds, loadedEstablishment);
		}
	}
	
	private void assignVehiclesToRounds(List<Round> rounds, Establishment establishment) {
		rounds.sort((round1, round2) -> {
			if(round1.getInitialWeight() < round2.getInitialWeight())
				return -1;
			if(round1.getInitialWeight() < round2.getInitialWeight())
				return 1;
			return 0;
		});
		
		TreeMap<VehicleCapacity, LinkedList<DeliveryVehicle>> availableVehicles = new TreeMap<>();
		for(VehicleCapacity capacity : establishment.getFleetByCapacity().keySet()) {
			availableVehicles.put(capacity, new LinkedList<>(establishment.getFleetByCapacity().get(capacity)));
		}
		
		for(Round round : rounds) {
			DeliveryVehicle selectedVehicle = availableVehicles.firstEntry().getValue().pollFirst();
			if(availableVehicles.firstEntry().getValue().isEmpty()) {
				availableVehicles.pollFirstEntry();
			}
			establishment.addRound(selectedVehicle, round);
		}
		
	}
	
	static class TemporaryRound {
		private List<String> ids;
		private Date departure;
		private double weight;

		public TemporaryRound(List<String> ids, Date departure, double weight) {
			super();
			this.ids = ids;
			this.departure = departure;
			this.weight = weight;
		}

		public List<String> getIds() {
			return ids;
		}
		
		public Date getDeparture() {
			return departure;
		}

		public double getWeight() {
			return weight;
		}
	}
}
