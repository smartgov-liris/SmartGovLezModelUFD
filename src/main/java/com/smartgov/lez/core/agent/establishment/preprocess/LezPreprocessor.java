package com.smartgov.lez.core.agent.establishment.preprocess;

import java.util.ArrayList;
import java.util.Collection;

import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;
import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicleFactory;
import com.smartgov.lez.core.agent.establishment.Establishment;
import com.smartgov.lez.core.agent.establishment.Round;
import com.smartgov.lez.core.copert.fields.EuroNorm;
import com.smartgov.lez.core.copert.tableParser.CopertHeader;
import com.smartgov.lez.core.copert.tableParser.CopertParser;
import com.smartgov.lez.core.copert.tableParser.CopertSelector;
import com.smartgov.lez.core.environment.lez.Lez;

public class LezPreprocessor {
	
	private Lez lez;
	private CopertParser parser;
	
	public LezPreprocessor(Lez lez, CopertParser parser) {
		this.lez = lez;
		this.parser = parser;
	}

	public void preprocess(Establishment establishment) {
		Collection<DeliveryVehicle> allowedVehicles = new ArrayList<>();
		Collection<DeliveryVehicle> forbiddenVehicles = new ArrayList<>();
		
		for(DeliveryVehicle vehicle : establishment.getFleet().values()) {
			if(lez.getLezCriteria().isAllowed(vehicle)) {
				allowedVehicles.add(vehicle);
			}
			else {
				forbiddenVehicles.add(vehicle);
			}
		}
		
		for(DeliveryVehicle vehicle : forbiddenVehicles) {
			Round round = establishment.getRounds().get(vehicle.getId());
			boolean roundInLez = false;
			int i = 0;
			
			if(lez.contains(establishment.getClosestOsmNode())) {
				roundInLez = true;
			}
			
			while(!roundInLez && i < round.getEstablishments().size()) {
				if(lez.contains(round.getEstablishments().get(i).getClosestOsmNode())) {
					roundInLez = true;
				}
			}
			
			if(roundInLez) {
				CopertSelector selector = new CopertSelector();
				selector.put(CopertHeader.CATEGORY, vehicle.getCategory());
				selector.put(CopertHeader.FUEL, vehicle.getFuel());
				selector.put(CopertHeader.SEGMENT, vehicle.getSegment());
				selector.put(CopertHeader.TECHNOLOGY, vehicle.getTechnology());
				
				selector.put(CopertHeader.EURO_STANDARD, EuroNorm.EURO6);
				
				DeliveryVehicle newVehicle =
					DeliveryVehicleFactory.generateVehicle(
						selector,
						parser,
						vehicle.getId()
						);
				
				establishment.getFleet().put(newVehicle.getId(), newVehicle);
			}
		}
	}
}
