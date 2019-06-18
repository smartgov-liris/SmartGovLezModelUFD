package com.smartgov.lez.controller;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

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
import com.smartgov.lez.core.environment.LezContext;
import com.smartgov.lez.core.environment.graph.PollutableOsmArc;
import com.smartgov.lez.core.environment.graph.PollutionIncreasedEvent;
import com.smartgov.lez.core.environment.pollution.Pollution;

import smartgov.SmartGov;
import smartgov.core.agent.core.Agent;
import smartgov.core.environment.graph.Arc;
import smartgov.core.environment.graph.Node;
import smartgov.core.events.EventHandler;
import smartgov.core.main.SimulationRuntime;
import smartgov.core.main.events.SimulationStep;
import smartgov.core.main.events.SimulationStopped;

@Controller
@RequestMapping("/api")
@CrossOrigin
public class SmartGovController {

	private SimpMessagingTemplate template;
	private ObjectMapper objectMapper;
	
	static SmartGov smartGov;
	
	private ConcurrentLinkedQueue<Arc> pollutedArcQueue;
	

    @Autowired
    public SmartGovController(SimpMessagingTemplate template) {
        this.template = template;
        objectMapper = new ObjectMapper();
        pollutedArcQueue = new ConcurrentLinkedQueue<>();
    }
    
	@PutMapping("/build")
	public ResponseEntity<String> build() throws MessagingException, JsonProcessingException {
		smartGov = new SmartGov(new LezContext("src/main/resources/input/config.properties"));

		SmartGov.getRuntime().setTickDelay(SimulationController.tickDelay);

		registerStepListener(SmartGov.getRuntime());
		registerStopListener(SmartGov.getRuntime());
		registerPollutionListeners();
		
		publishNodes(smartGov.getContext().nodes.values());
		
		publishArcs(smartGov.getContext().arcs.values());

		publishAgents(smartGov.getContext().agents.values());
		
		publishPollution(smartGov.getContext().arcs.values());
		
		return new ResponseEntity<>("SmartGov instance built.", HttpStatus.OK);
	}
	
	@PutMapping("/start")
	public ResponseEntity<String> start(@RequestParam("simulationDuration") Integer simulationDuration, @RequestParam("tickDuration") Integer tickDuration) {
		if(smartGov != null) {
			SmartGov.getRuntime().setTickDuration(tickDuration);
			SmartGov.getRuntime().start(simulationDuration);
			return new ResponseEntity<>("Simulation started for " + simulationDuration + "ticks. Tick duration : " + tickDuration, HttpStatus.OK);
		}
		return new ResponseEntity<>("Call /api/build first.", HttpStatus.BAD_REQUEST);
	}
	
	@PutMapping("/pause")
	public ResponseEntity<String> pause() {
		if(smartGov != null) {
			try {
				SmartGov.getRuntime().pause();
			}
			catch (IllegalStateException e) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<>("Simulation paused at " + SmartGov.getRuntime().getTickCount() + " ticks.", HttpStatus.OK);
		}
		return new ResponseEntity<>("Call /api/build first.", HttpStatus.BAD_REQUEST);
	}
	
	@PutMapping("/resume")
	public ResponseEntity<String> resume() {
		if(smartGov != null) {
			try {
				SmartGov.getRuntime().resume();
			}
			catch (IllegalStateException e) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<>("Simulation resumed from " + SmartGov.getRuntime().getTickCount() + " ticks.", HttpStatus.OK);
		}
		return new ResponseEntity<>("Call /api/build first.", HttpStatus.BAD_REQUEST);
	}
	
	@PutMapping("/step")
	public ResponseEntity<String> step() {
		if(smartGov != null) {
			try {
				SmartGov.getRuntime().step();
			}
			catch (IllegalStateException e) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<>("Manual step at " + SmartGov.getRuntime().getTickCount() + " ticks.", HttpStatus.OK);
		}
		return new ResponseEntity<>("Call /api/build first.", HttpStatus.BAD_REQUEST);
	}
	
	@PutMapping("/stop")
	public ResponseEntity<String> stop() throws MessagingException, JsonProcessingException {
		if(smartGov != null) {
			try {
				SmartGov.getRuntime().stop();
			}
			catch (IllegalStateException e) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
			}
			// Re-initialize context
			SmartGov.getSimulationBuilder().build();
			
			publishAgents(smartGov.getContext().agents.values());
			
			publishPollution(smartGov.getContext().arcs.values());
			
			return new ResponseEntity<>("Simulation stopped after " + SmartGov.getRuntime().getTickCount() + " ticks.", HttpStatus.OK);
		}
		return new ResponseEntity<>("Call /api/build first.", HttpStatus.BAD_REQUEST);
	}
	
	private void registerStepListener(SimulationRuntime runtime) {
		runtime.addSimulationStepListener(new EventHandler<SimulationStep>() {

			@Override
			public void handle(SimulationStep event) {
				try {
					// publishStep(runtime.getTickCount());
					if (SimulationController.visualisationEnabled && event.getTick() % SimulationController.agentsRefreshPeriod == 0) {
						publishAgents(smartGov.getContext().agents.values());
					}

					if (SimulationController.visualisationEnabled && event.getTick() % SimulationController.pollutionRefreshPeriod == 0) {
						try {
							publishPollution(smartGov.getContext().arcs.values());
						} catch (MessagingException | JsonProcessingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
	}
	
	private void registerStopListener(SimulationRuntime runtime) {
		runtime.addSimulationStoppedListener(new EventHandler<SimulationStopped>() {

			@Override
			public void handle(SimulationStopped event) {
				publishStop(runtime);
				try {
					publishAgents(smartGov.getContext().agents.values());
					publishPollution(smartGov.getContext().arcs.values());
				} catch (MessagingException | JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
	}
	
	private void registerPollutionListeners() {
		for(Arc arc : smartGov.getContext().arcs.values()) {
			((PollutableOsmArc) arc).addPollutionIncreasedListener(new EventHandler<PollutionIncreasedEvent>() {
				@Override
				public void handle(PollutionIncreasedEvent event) {
					pollutedArcQueue.add(arc);
				}
				
			});
		}
	}
	
    private void publishStep(int simulationStep) throws Exception {
        this.template.convertAndSend("/simulation/steps", simulationStep);
    }
    
    private void publishStop(SimulationRuntime runtime) {
    	this.template.convertAndSend("/simulation/stop", "{\"stop\":{\"after\":" + runtime.getTickCount() + "}}");
    }
    
    private void publishAgents(Collection<Agent> agents) throws MessagingException, JsonProcessingException {
    	this.template.convertAndSend("/simulation/agents", objectMapper.writeValueAsString(agents));
    }
    
    private void publishNodes(Collection<Node> nodes) throws MessagingException, JsonProcessingException {
    	this.template.convertAndSend("/simulation/nodes", objectMapper.writeValueAsString(nodes));
    }
    
    private void publishArcs(Collection<Arc> arcs) throws MessagingException, JsonProcessingException {
    	this.template.convertAndSend("/simulation/pollution_peeks", objectMapper.writeValueAsString(Pollution.pollutionRatePeeks));
    	this.template.convertAndSend("/simulation/arcs", objectMapper.writeValueAsString(arcs));
    }
    
    private void publishPollution(Collection<Arc> arcs) throws MessagingException, JsonProcessingException {
    	template.convertAndSend("/simulation/pollution_peeks", objectMapper.writeValueAsString(Pollution.pollutionRatePeeks));
		template.convertAndSend("/simulation/pollution", objectMapper.writeValueAsString(smartGov.getContext().arcs.values()));
    }
}
