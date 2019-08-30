package org.liris.smartgov.lez.cli.tools;

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
import com.fasterxml.jackson.databind.SerializationFeature;

import org.liris.smartgov.lez.cli.Cli;
import org.liris.smartgov.lez.core.environment.LezContext;
import org.liris.smartgov.lez.core.environment.pollution.Pollution;
import org.liris.smartgov.simulator.SmartGov;

/**
 * Init task
 *
 */
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
			String footer ="\nProcess:\n"
					+ "- Loads OSM data from preprocessed nodes and ways files, specified as <nodes> and "
					+ "<roads> fields in the specified configuration.\n"
					+ "- Builds establishments, delivery drivers and fleets, and compute the shortest path of the "
					+ "first step of each round.\n"
					+ "- Writes initial nodes and arcs to <output>/init folder.\n"
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
		
		ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
		
		File outputInitFolder = null;
		
		try {
			File outputFolder = smartGov.getContext().getFileLoader().load("outputDir");
			outputInitFolder = new File(outputFolder, "init");
		} catch (IllegalArgumentException e) {
			Run.logger.error("No outpurDir specified in the input configuration.");
		}

		File nodesFile = new File(outputInitFolder, "nodes.json");
		Run.logger.info("Writting nodes to " + nodesFile);
		Cli.writeOutput(context.nodes.values(), nodesFile, mapper);
		
		File arcsFile = new File(outputInitFolder, "arcs.json");
		Run.logger.info("Writting arcs to " + arcsFile);
		Cli.writeOutput(context.arcs.values(), arcsFile, mapper);
		
		File establishmentsFile = new File(outputInitFolder, "establishments.json");
		Run.logger.info("Writting establishments to " + establishmentsFile);
		Cli.writeOutput(context.getEstablishments().values(), establishmentsFile, mapper);
		
		File pollutionPeeksFile = new File(outputInitFolder, "pollution_peeks.json");
		Run.logger.info("Writting pollution peeks to " + pollutionPeeksFile);
		Cli.writeOutput(Pollution.pollutionRatePeeks, pollutionPeeksFile, mapper);
	}
}
