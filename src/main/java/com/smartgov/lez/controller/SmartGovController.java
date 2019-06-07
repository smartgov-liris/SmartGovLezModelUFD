package com.smartgov.lez.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import smartgov.SmartGov;
import smartgov.core.agent.core.Agent;
import smartgov.core.events.EventHandler;
import smartgov.core.main.SmartGovRuntime;
import smartgov.core.main.events.SimulationStep;
import smartgov.models.lez.environment.LezContext;

@Controller
@RequestMapping("/api")
@CrossOrigin
public class SmartGovController {

	private SimpMessagingTemplate template;
	private ObjectMapper objectMapper;
	
	static SmartGov smartGov;
	

    @Autowired
    public SmartGovController(SimpMessagingTemplate template) {
        this.template = template;
        objectMapper = new ObjectMapper();
    }
    
	@PutMapping("/build")
	public ResponseEntity<String> build() {
		smartGov = new SmartGov(new LezContext("src/main/resources/input/config.properties"));

		SmartGov.getRuntime().setTickDelay(100);
		SmartGov.getRuntime().setTickDuration(0.5);
		registerStepListener(SmartGov.getRuntime());

		return new ResponseEntity<>("SmartGov instance built.", HttpStatus.OK);
	}
	
	@PutMapping("/start")
	public ResponseEntity<String> start(@RequestParam("ticks") Integer ticks) {
		if(smartGov != null) {
			SmartGov.getRuntime().start(ticks);
			return new ResponseEntity<>("Simulation started.", HttpStatus.OK);
		}
		return new ResponseEntity<>("Call /api/build first.", HttpStatus.BAD_REQUEST);
	}
	
	private void registerStepListener(SmartGovRuntime runtime) {
		runtime.addSimulationStepListener(new EventHandler<SimulationStep>() {

			@Override
			public void handle(SimulationStep event) {
				try {
					// publishStep(runtime.getTickCount());
					publishAgents(smartGov.getContext().agents.values());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
	}
	
    private void publishStep(int simulationStep) throws Exception {
        this.template.convertAndSend("/simulation/steps", simulationStep);
    }
    
    private void publishAgents(Collection<Agent> agents) throws MessagingException, JsonProcessingException {
    	this.template.convertAndSend("/simulation/agents", objectMapper.writeValueAsString(agents));
    }
}
