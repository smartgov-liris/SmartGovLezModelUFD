package com.smartgov.lez.core.agent.establishment.input;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.smartgov.lez.core.agent.establishment.Establishment;

public class EstablishmentLoader {
	
	private List<Establishment> loadedEstablishment;
	
	public EstablishmentLoader() {
		this.loadedEstablishment = new ArrayList<>();
	}

	public static List<Establishment> loadEstablishment(File establishments) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(EstablishmentLoader.class, new EstablishmentDeserializer());
		mapper.registerModule(module);
		
		return mapper.readValue(establishments, EstablishmentLoader.class).loadedEstablishments();
	}
	
	private List<Establishment> loadedEstablishments() {
		return loadedEstablishment;
	}
	
	public void load(Establishment establishment) {
		this.loadedEstablishment.add(establishment);
	}
}
