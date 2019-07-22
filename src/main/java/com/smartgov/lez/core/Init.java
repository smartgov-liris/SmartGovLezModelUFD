package com.smartgov.lez.core;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartgov.lez.SmartgovLezApplication;
import com.smartgov.lez.core.environment.LezContext;
import com.smartgov.lez.core.environment.pollution.Pollution;

import smartgov.SmartGov;

public class Init {

	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		LezContext context = new LezContext(SmartgovLezApplication.class.getResource("config.properties").getFile());
		SmartGov smartGov = new SmartGov(context);
		
		ObjectMapper mapper = new ObjectMapper();
		
		File outputInitFolder = null;
		
		try {
			File outputFolder = smartGov.getContext().getFileLoader().load("outputFolder");
			outputInitFolder = new File(outputFolder, "init");
		} catch (IllegalArgumentException e) {
			Main.logger.warn("No outputFolder specified in the input configuration.");
		}
		
		mapper.writeValue(new File(outputInitFolder, "nodes.json"), context.nodes.values());
		mapper.writeValue(new File(outputInitFolder, "arcs.json"), context.arcs.values());
		mapper.writeValue(new File(outputInitFolder, "establishments.json"), context.getEstablishments().values());
		mapper.writeValue(new File(outputInitFolder, "pollution_peeks.json"), Pollution.pollutionRatePeeks);
	}

}
