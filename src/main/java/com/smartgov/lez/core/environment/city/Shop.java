package com.smartgov.lez.core.environment.city;

import java.util.Map;

import org.locationtech.jts.geom.Coordinate;

import com.smartgov.lez.core.environment.DeliverySpot;

import smartgov.urban.osm.environment.city.Building;

public class Shop extends Building {
	
	private DeliverySpot deliverySpot;

	public Shop(
			String id,
			Map<String, String> attributes,
			Coordinate[] polygon) {
		super(id, attributes, polygon);
	}
		
	public void setDeliverySpot(DeliverySpot spot) {
		this.deliverySpot = spot;
	}
	
	public DeliverySpot getDeliverySpot() {
		return this.deliverySpot;
	}
	
}
