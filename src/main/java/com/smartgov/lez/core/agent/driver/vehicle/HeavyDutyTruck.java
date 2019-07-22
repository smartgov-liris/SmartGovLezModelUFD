package com.smartgov.lez.core.agent.driver.vehicle;

import com.smartgov.lez.core.copert.Copert;
import com.smartgov.lez.core.copert.fields.EuroNorm;
import com.smartgov.lez.core.copert.fields.Fuel;
import com.smartgov.lez.core.copert.fields.HeavyDutyTrucksSegment;
import com.smartgov.lez.core.copert.fields.Technology;
import com.smartgov.lez.core.copert.fields.VehicleCategory;

public class HeavyDutyTruck extends DeliveryVehicle {

	public HeavyDutyTruck(String id, Fuel fuel, HeavyDutyTrucksSegment vehicleSegment, EuroNorm euroNorm, Technology technology, Copert copert) {
		super(id, VehicleCategory.HEAVY_DUTY_TRUCK, fuel, vehicleSegment, euroNorm, technology, copert);
	}

}
