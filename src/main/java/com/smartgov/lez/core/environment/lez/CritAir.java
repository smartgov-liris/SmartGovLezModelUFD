package com.smartgov.lez.core.environment.lez;

import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;
import com.smartgov.lez.core.copert.fields.EuroNorm;
import com.smartgov.lez.core.copert.fields.Fuel;
import com.smartgov.lez.core.copert.fields.VehicleCategory;
import com.smartgov.lez.core.copert.fields.VehicleSegment;

public enum CritAir {
	CRITAIR_1,
	CRITAIR_2;


	public static CritAir compute(DeliveryVehicle vehicle) {
		return null;
	}
	
	private static CritAir forLightWeightVehicles(DeliveryVehicle vehicle) {
		return null;
	}
	
}
