package com.smartgov.lez;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartgovLezApplication {
	
	 public static Logger logger = LoggerFactory.getLogger(SmartgovLezApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(SmartgovLezApplication.class, args);
	}

}
