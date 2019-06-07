package com.smartgov.lez.core;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import smartgov.SmartGov;
import smartgov.core.events.EventHandler;
import smartgov.core.main.events.SimulationStopped;
import smartgov.models.lez.environment.LezContext;
import smartgov.models.lez.environment.pollution.Pollution;

public class Main {
	
	public static final Logger logger = LogManager.getLogger(Main.class);
	
    public static void main(String[] args) {
        SmartGov smartGov = new SmartGov(new LezContext("src/main/resources/input/config.properties"));
        SmartGov.getRuntime().addSimulationStoppedListener(new EventHandler<SimulationStopped>() {

			@Override
			public void handle(SimulationStopped event) {
				String outputFolder = smartGov.getContext().getFiles().getFile("outputFolder");
				File agentOutput = new File(outputFolder + File.separator + "agents_" + SmartGov.getRuntime().getTickCount() +".json");
				File arcsOutput = new File(outputFolder + File.separator + "arcs_" + SmartGov.getRuntime().getTickCount() +".json");
				File pollutionPeeks = new File(outputFolder + File.separator + "pollution_peeks_" + SmartGov.getRuntime().getTickCount() +".json");
				
				
				ObjectMapper objectMapper = new ObjectMapper();

				try {
					logger.info("Saving agents state to " + agentOutput.getPath());
					objectMapper.writeValue(agentOutput, smartGov.getContext().agents);
					
					logger.info("Saving arcs state to " + agentOutput.getPath());
					objectMapper.writeValue(arcsOutput, smartGov.getContext().arcs);
					
					logger.info("Saving pollution peeks to " + agentOutput.getPath());
					objectMapper.writeValue(pollutionPeeks, Pollution.pollutionRatePeeks);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        	
        });
        
    	
		ObjectMapper objectMapper = new ObjectMapper();
		String outputFolder = null;
		
		try {
			outputFolder = smartGov.getContext().getFiles().getFile("outputFolder");
		} catch (IllegalArgumentException e) {
			logger.warn("No outputFolder specified in the input configuration.");
		}
		
		if(outputFolder != null) {
			File nodeOutput = new File(outputFolder + File.separator + "init_nodes.json");
			File arcOutput = new File(outputFolder + File.separator + "init_arcs.json");

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
		
		SmartGov.getRuntime().start(43200);
    }
    
}
