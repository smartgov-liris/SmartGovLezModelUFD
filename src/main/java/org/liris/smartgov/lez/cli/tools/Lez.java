package org.liris.smartgov.lez.cli.tools;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.liris.smartgov.lez.core.environment.LezContext;
import org.liris.smartgov.simulator.SmartGov;

public class Lez {

	public static void main(String[] args) throws ParseException {
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
		
		// LezPreprocessor preprocessor = new LezPreprocessor()
		
		
	}
}
