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
		LezContext context = new LezContext(SmartgovLezApplication.class.getResource("static_config.properties").getFile());

		SmartGov smartGov = new SmartGov(context);
		
		ObjectMapper mapper = new ObjectMapper();
		
		File outputInitFolder = null;
		
		try {
			File outputFolder = smartGov.getContext().getFileLoader().load("outputFolder");
			outputInitFolder = new File(outputFolder, "init");
		} catch (IllegalArgumentException e) {
			Main.logger.warn("No outputFolder specified in the input configuration.");
		}
		
//		File nodesFile = new File(outputInitFolder, "nodes.json");
//		Main.logger.info("Writting nodes to " + nodesFile);
//		mapper.writeValue(nodesFile, context.nodes.values());
//		
//		File arcsFile = new File(outputInitFolder, "arcs.json");
//		Main.logger.info("Writting arcs to " + arcsFile);
//		mapper.writeValue(arcsFile, context.arcs.values());
//		
//		File establishmentsFile = new File(outputInitFolder, "establishments.json");
//		Main.logger.info("Writting establishments to " + establishmentsFile);
//		mapper.writeValue(establishmentsFile, context.getEstablishments().values());
//		
//		File pollutionPeeksFile = new File(outputInitFolder, "pollution_peeks.json");
//		Main.logger.info("Writting pollution peeks to " + pollutionPeeksFile);
//		mapper.writeValue(pollutionPeeksFile, Pollution.pollutionRatePeeks);
	}

}
