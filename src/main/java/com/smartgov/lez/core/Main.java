package com.smartgov.lez.core;

import java.io.File;
import java.io.IOException;

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
	
    public static void main(String[] args) {
        SmartGov smartGov = new SmartGov(
        		new LezContext(SmartgovLezApplication.class.getResource("static_config.properties").getFile())
        		);
        SmartGov.getRuntime().addSimulationStoppedListener(new EventHandler<SimulationStopped>() {

			@Override
			public void handle(SimulationStopped event) {
				File outputFolder = smartGov.getContext().getFileLoader().load("outputFolder");
				File agentOutput = new File(outputFolder, "agents_" + SmartGov.getRuntime().getTickCount() +".json");
				File arcsOutput = new File(outputFolder, "arcs_" + SmartGov.getRuntime().getTickCount() +".json");
				File pollutionPeeks = new File(outputFolder, "pollution_peeks_" + SmartGov.getRuntime().getTickCount() +".json");
				
				
				ObjectMapper objectMapper = new ObjectMapper();

				try {
					// logger.info("Saving agents state to " + agentOutput.getPath());
					// objectMapper.writeValue(agentOutput, smartGov.getContext().agents.values());
					
					logger.info("Saving arcs state to " + agentOutput.getPath());
					objectMapper.writeValue(arcsOutput, smartGov.getContext().arcs.values());
					
					logger.info("Saving pollution peeks to " + agentOutput.getPath());
					objectMapper.writeValue(pollutionPeeks, Pollution.pollutionRatePeeks);
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
		
		SmartGov.getRuntime().start(43200);
    }
    
}
