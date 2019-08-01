package com.smartgov.lez.core;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartgov.lez.SmartgovLezApplication;
import com.smartgov.lez.core.environment.LezContext;
import com.smartgov.lez.core.environment.pollution.Pollution;

import smartgov.SmartGov;
import smartgov.core.events.EventHandler;
import smartgov.core.simulation.events.SimulationStopped;

public class Main {
	
	public static final Logger logger = LogManager.getLogger(Main.class);
	
    public static void main(String[] args) throws ParseException {
		Options opts = new Options();
		
		opts.addOption(new Option("h", "help", false, "Displays this help message"));
		Option config = new Option("c", "config-file", true, "Input configuration file");
		config.setArgName("file");
		opts.addOption(config);
		Option maxTicks = new Option("t", "max-ticks", true, "Max ticks");
		maxTicks.setArgName("int");
		opts.addOption(maxTicks);
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(opts, args);
		
		if(cmd.hasOption("h")) {
			String header = "Run the simutation, with the specified configuration.";
			String footer = ""
					+ "Raw results are written to the <outputDir>/simulation folder.\n"
					+ "The simulation runs until max ticks count as been reached (default to 10 days) or "
					+ "when the last round has ended.";
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("smartgovlez run", header, opts, footer, true);
			return;
		}

		String configFile = "config.properties";
		if(cmd.hasOption("c")) {
			configFile = cmd.getOptionValue("c");
		}
		
		int maxTicksValue = 10 * 3600 * 24;
		if(cmd.hasOption("t")) {
			maxTicksValue = Integer.valueOf(cmd.getOptionValue("t"));
		}
		
        SmartGov smartGov = new SmartGov(
        		new LezContext(configFile)
        		);
        SmartGov.getRuntime().addSimulationStoppedListener(new EventHandler<SimulationStopped>() {

			@Override
			public void handle(SimulationStopped event) {
				File outputFolder = new File(
						smartGov.getContext().getFileLoader().load("outputDir"),
						"simulation"
						);
				File agentOutput = new File(outputFolder, "agents_" + SmartGov.getRuntime().getTickCount() +".json");
				File arcsOutput = new File(outputFolder, "arcs_" + SmartGov.getRuntime().getTickCount() +".json");
				File pollutionPeeksOutput = new File(outputFolder, "pollution_peeks_" + SmartGov.getRuntime().getTickCount() +".json");
				
				
				ObjectMapper objectMapper = new ObjectMapper();

				try {
					// logger.info("Saving agents state to " + agentOutput.getPath());
					// objectMapper.writeValue(agentOutput, smartGov.getContext().agents.values());
					
					logger.info("Saving arcs state to " + arcsOutput.getPath());
					objectMapper.writeValue(arcsOutput, smartGov.getContext().arcs.values());
					
					logger.info("Saving pollution peeks to " + pollutionPeeksOutput.getPath());
					objectMapper.writeValue(pollutionPeeksOutput, Pollution.pollutionRatePeeks);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        	
        });
        
    	
		ObjectMapper objectMapper = new ObjectMapper();
		File outputFolder = null;
		
		try {
			outputFolder = smartGov.getContext().getFileLoader().load("outputFolder");
		} catch (IllegalArgumentException e) {
			logger.warn("No outputFolder specified in the input configuration.");
		}
		
		if(outputFolder != null) {
			File initOutput = new File(outputFolder, "init");
			File nodeOutput = new File(initOutput, "nodes.json");
			File arcOutput = new File(initOutput, "arcs.json");

			try {
				// Using maps is simpler when processed in JS, but IDs are duplicated.
				logger.info("Saving initial nodes to " + nodeOutput.getPath());
				objectMapper.writeValue(nodeOutput, smartGov.getContext().nodes);
				
				logger.info("Saving initial arcs to " + arcOutput.getPath());
				objectMapper.writeValue(arcOutput, smartGov.getContext().arcs);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		SmartGov.getRuntime().addSimulationStoppedListener((event) -> {
			int simulationTime = (int) Math.floor(SmartGov.getRuntime().getTickCount() * SmartGov.getRuntime().getTickDuration());
			int days = (int) Math.floor(simulationTime / (24 * 3600));
			int hours = (int) Math.floor((simulationTime - days * 24 * 3600) / 3600);
			int minutes = (int) Math.floor((simulationTime - days * 24 * 3600 - hours * 3600) / 60);
			int seconds = (int) Math.floor((simulationTime - days * 24 * 3600 - hours * 3600 - minutes * 60));
			SmartgovLezApplication.logger.info(
				"Total simulated period : "
				+ days + " days, "
				+ hours + " hours, "
				+ minutes + " minutes, "
				+ seconds + "s"
				);
		});
		// SmartGov.getRuntime().start();
		SmartGov.getRuntime().start((int) Math.floor(maxTicksValue));
    }
    
}
