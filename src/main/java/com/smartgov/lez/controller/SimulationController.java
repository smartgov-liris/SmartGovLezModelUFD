package com.smartgov.lez.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartgov.lez.SmartgovLezApplication;

import smartgov.SmartGov;

@Controller
@RequestMapping("/api")
@CrossOrigin
public class SimulationController {

		public static int agentsRefreshPeriod;
		public static int pollutionRefreshPeriod;
		public static int tickDelay;
		
		@PutMapping("/agents_refresh_period")
		public ResponseEntity<String> updateAgentsRefreshPeriod(@RequestParam("period") Integer period) {
			agentsRefreshPeriod = period;
			
			String message = "Agents refresh period set to " + period;
			SmartgovLezApplication.logger.info(message);
			return new ResponseEntity<>(message, HttpStatus.OK);
		}
		
		@PutMapping("/pollution_refresh_period")
		public ResponseEntity<String> updatePollutionRefreshPeriod(@RequestParam("period") Integer period) {
			pollutionRefreshPeriod = period;
			
			String message = "Pollution refresh period set to " + period;
			SmartgovLezApplication.logger.info(message);
			return new ResponseEntity<>(message, HttpStatus.OK);
		}
		
		@PutMapping("/tick_delay")
		public ResponseEntity<String> updateTickDelay(@RequestParam("delay") Integer delay) {
			// Tick delay used to build a new runtime on /build
			tickDelay = delay;
			
			// Dynamically update the tick delay if the simulation is built/running
			if (SmartGov.getRuntime() != null) {
				SmartGov.getRuntime().setTickDelay(tickDelay);
			}
			
			String message = "Tick delay set to " + delay;
			SmartgovLezApplication.logger.info(message);
			return new ResponseEntity<>(message, HttpStatus.OK);
		}
}
