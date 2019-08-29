package org.liris.smartgov.lez.cli;

import java.io.IOException;
import java.util.Arrays;

import javax.xml.bind.JAXBException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.liris.smartgov.lez.cli.tools.Init;
import org.liris.smartgov.lez.cli.tools.Roads;
import org.liris.smartgov.lez.cli.tools.Run;
import org.liris.smartgov.lez.cli.tools.Tile;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Cli {
	
	// public static Logger logger = LogManager.getLogger(Cli.class);

	public static void main(String[] args) throws ParseException, JsonGenerationException, JsonMappingException, IOException, JAXBException {
		
		if(args.length == 0) {
			printMainHelp();
			return;
		}
		
		switch(args[0]) {
		case "help":
			printMainHelp();
			return;
		case "roads":
			Roads.main(Arrays.copyOfRange(args, 1, args.length));
			return;
		case "init":
			Init.main(Arrays.copyOfRange(args, 1, args.length));
			return;
		case "run":
			Run.main(Arrays.copyOfRange(args, 1, args.length));
			return;
		case "tile":
			Tile.main(Arrays.copyOfRange(args, 1, args.length));
			return;
		default:
			Options opts = new Options();
			opts.addOption(new Option("h", "help", false, "display an help message"));
			
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(opts, args);
			
			if(cmd.hasOption("h")) {
				printMainHelp();
			}
			return;
		}
		
	}
	
	private static void printMainHelp() {
		System.out.println("Available tasks :\n"
				+ "\t help - displays this help message\n"
				+ "\t roads - preprocess roads to produce json nodes and ways files\n"
				+ "\t init - load json ndoes and ways file, and initialize delivery agents\n"
				+ "\t run - run simulation\n"
				+ "\t tile - aggregate output results in tiles");
		System.out.println("Use \"<task> -h\" to get detailed information about each task options.");
	}
}
