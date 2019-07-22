package com.smartgov.lez.core.agent.driver.vehicle;

import com.smartgov.lez.core.copert.Copert;
import com.smartgov.lez.core.copert.fields.EuroNorm;
import com.smartgov.lez.core.copert.fields.Fuel;
import com.smartgov.lez.core.copert.fields.LightWeightVehicleSegment;
import com.smartgov.lez.core.copert.fields.Technology;
import com.smartgov.lez.core.copert.fields.VehicleCategory;

public class LightCommercialVehicle extends DeliveryVehicle {

	public LightCommercialVehicle(String id, Fuel fuel, LightWeightVehicleSegment vehicleSegment, EuroNorm euroNorm, Technology technology, Copert copert) {
		super(id, VehicleCategory.LIGHT_WEIGHT, fuel, vehicleSegment, euroNorm, technology, copert);
	}


}
