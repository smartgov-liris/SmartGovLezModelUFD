package com.smartgov.lez.input.establishment;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartgov.lez.core.copert.tableParser.CopertParser;

public class LambertToGpsConverter {

	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, JsonParseException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Random random = new Random(240720191835l);
		CopertParser parser = new CopertParser(new File(LambertToGpsConverter.class.getResource("../copert/Hot_Emissions_Parameters_France.csv").getFile()), random);
		
		mapper.writeValue(new File("simturb_establishments.json"), EstablishmentLoader.loadEstablishments(
						new File(LambertToGpsConverter.class.getResource("establishments_lyon.json").getFile()),
						new File(LambertToGpsConverter.class.getResource("fleetProfiles.json").getFile()),
						parser,
						random
						).values());
	}
}
