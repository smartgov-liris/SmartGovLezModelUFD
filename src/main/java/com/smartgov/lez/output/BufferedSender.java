package com.smartgov.lez.output;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class BufferedSender {
	
	public static final ObjectMapper mapper = new ObjectMapper();

	public static void publish(SimpMessagingTemplate template, String topic, Iterator<? extends Object> data) throws MessagingException, JsonProcessingException, InterruptedException {
		int i = 0;
    	Collection<Object> dataToSend = new ArrayList<>();
    	while (data.hasNext()) {
    		if (i == 500) {
    			template.convertAndSend(topic, mapper.writeValueAsString(dataToSend));
    			TimeUnit.MILLISECONDS.sleep(50);
    			dataToSend.clear();
    			i = 0;
    		}
    		dataToSend.add(data.next());
    		i++;
    	}
    	if(dataToSend.size() > 0) {
    		template.convertAndSend(topic, mapper.writeValueAsString(dataToSend));
			TimeUnit.MILLISECONDS.sleep(50);
    	}
	}
}
