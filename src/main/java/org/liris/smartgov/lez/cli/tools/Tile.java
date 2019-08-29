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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.liris.smartgov.lez.cli.Cli;
import org.liris.smartgov.lez.process.arcs.build.TileMap;
import org.liris.smartgov.lez.process.arcs.load.PollutedArcsLoader;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Tile {

	public static Logger logger = LogManager.getLogger(Tile.class);

	public static void main(String[] args) throws ParseException, JsonGenerationException, JsonMappingException, IOException {
		Options helpOpts = new Options();
		Option help = new Option("h", "help", false, "Displays this help message");
		helpOpts.addOption(help);
		
		
		Options fullOpts = new Options();
		fullOpts.addOption(help);

		Option nodeFile = new Option("n", "nodes-file", true, "JSON initial nodes file");
		nodeFile.setArgName("file");
		nodeFile.setRequired(true);
	
		Option arcsFile = new Option("a", "arcs-file", true, "JSON arcs file, output of \"run\"");
		arcsFile.setArgName("file");
		arcsFile.setRequired(true);
	
		Option tileOutput = new Option("o", "tiles-output", true, "JSON output tiles");
		tileOutput.setArgName("file");
		tileOutput.setRequired(true);
		
		Option tileSize = new Option("s", "tile-size", true, "Tile size in meter");
		tileOutput.setArgName("file");
		
		Option prettyPrint = new Option("p", "pretty-print", false, "Enables pretty JSON printing");
		
		fullOpts.addOption(nodeFile);
		fullOpts.addOption(arcsFile);
		fullOpts.addOption(tileOutput);
		fullOpts.addOption(tileSize);
		fullOpts.addOption(prettyPrint);
		
		if(args.length == 0) {
			printHelp(fullOpts);
			return;
		}

		CommandLineParser cmdParser = new DefaultParser();
		CommandLine helpCmd = cmdParser.parse(helpOpts, args, true);
		
		if(helpCmd.hasOption("h")) {
			printHelp(fullOpts);
			return;
		}
		
		cmdParser = new DefaultParser();
		CommandLine mainCmd = cmdParser.parse(fullOpts, args);
		
		PollutedArcsLoader loader = new PollutedArcsLoader();
		loader.load(
				new File(mainCmd.getOptionValue("a")),
				new File(mainCmd.getOptionValue("n"))
				);
		
		int tileSizeValue = 100;
		if(mainCmd.hasOption("s")) {
			tileSizeValue = Integer.valueOf(mainCmd.getOptionValue("s"));
		}
		
		TileMap map = new TileMap();
		map.build(loader.getArcs(), loader.getNodes(), tileSizeValue);
		
		logger.info("Tiles width count : " + map.getTiles().get(0).size());
		logger.info("Tiles height count : " + map.getTiles().size());

		ObjectMapper mapper = new ObjectMapper();
		
		if(mainCmd.hasOption("p")) {
			mapper.writerWithDefaultPrettyPrinter().writeValue(
					new File(mainCmd.getOptionValue("o")),
					map);
		}
		else {
			mapper.writeValue(
					new File(mainCmd.getOptionValue("o")),
					map
					);
		}
	}
	
	private static void printHelp(Options opts) {
		String header = "Build JSON tiles with pollution values from simulation output.";
		String footer = "";
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("smartgovlez roads", header, opts, footer, true);
	}
}
