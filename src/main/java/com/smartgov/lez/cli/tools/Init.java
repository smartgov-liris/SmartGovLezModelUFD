package com.smartgov.lez.cli.tools;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.smartgov.lez.core.environment.LezContext;
import com.smartgov.lez.core.environment.pollution.Pollution;

import org.liris.smartgov.simulator.SmartGov;

public class Init {

	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException, ParseException {
		Options opts = new Options();
		
		opts.addOption(new Option("h", "help", false, "Displays this help message"));
		Option config = new Option("c", "config-file", true, "Input configuration file");
		config.setArgName("file");
		opts.addOption(config);
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(opts, args);
		
		String configFile = "config.properties";
		if(cmd.hasOption("h")) {
			String header = "Run the initialization process, without launching the simulation.";
			String footer =""
					+ "\t - Loads OSM data from preprocessed nodes and ways files, specified as <nodes> and "
					+ "<roads> fields in the specified configuration.\n"
					+ "\t - Builds establishments, delivery drivers and fleets, and compute the shortest path of the "
					+ "first step of each round.\n"
					+ "\t - Writes initial nodes and arcs to <output>/init folder.\n"
					+ "\nNotice that this step is just a convenient way to check the initial configuration, "
					+ "but is not required before launching the \"main\" task, that will perform the initialization "
					+ "step anyway.";
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("smartgovlez", header, opts, footer, true);
			return;
		}
		
		if(cmd.hasOption("c")) {
			configFile = cmd.getOptionValue("c");
		}
			
		LezContext context = new LezContext(configFile);

		SmartGov smartGov = new SmartGov(context);
		
		ObjectWriter mapper = new ObjectMapper().writerWithDefaultPrettyPrinter();
		
		File outputInitFolder = null;
		
		try {
			File outputFolder = smartGov.getContext().getFileLoader().load("outputDir");
			outputInitFolder = new File(outputFolder, "init");
		} catch (IllegalArgumentException e) {
			Run.logger.warn("No outputFolder specified in the input configuration.");
		}
		
		File nodesFile = new File(outputInitFolder, "nodes.json");
		Run.logger.info("Writting nodes to " + nodesFile);
		mapper.writeValue(nodesFile, context.nodes.values());
		
		File arcsFile = new File(outputInitFolder, "arcs.json");
		Run.logger.info("Writting arcs to " + arcsFile);
		mapper.writeValue(arcsFile, context.arcs.values());
		
		File establishmentsFile = new File(outputInitFolder, "establishments.json");
		Run.logger.info("Writting establishments to " + establishmentsFile);
		mapper.writeValue(establishmentsFile, context.getEstablishments().values());
		
		File pollutionPeeksFile = new File(outputInitFolder, "pollution_peeks.json");
		Run.logger.info("Writting pollution peeks to " + pollutionPeeksFile);
		mapper.writeValue(pollutionPeeksFile, Pollution.pollutionRatePeeks);
	}
}
