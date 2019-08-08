package com.smartgov.lez.core.agent.driver.vehicle;

import com.smartgov.lez.core.copert.Copert;
import com.smartgov.lez.core.copert.fields.EuroNorm;
import com.smartgov.lez.core.copert.fields.Fuel;
import com.smartgov.lez.core.copert.fields.HeavyDutyTrucksSegment;
import com.smartgov.lez.core.copert.fields.Technology;
import com.smartgov.lez.core.copert.fields.VehicleCategory;

/**
 * DeliveryVehicle extension, to simplify heavy duty trucks instantiation.
 *
 */
public class HeavyDutyTruck extends DeliveryVehicle {

	/**
	 * HeavyDutyTruck constructor. Equivalent to a call to
	 * {@link DeliveryVehicle#DeliveryVehicle(String, VehicleCategory, Fuel, VehicleSegment, EuroNorm, Technology, Copert)}
	 * with the same parameters, with HEAVY_DUTY_TRUCK as the vehicle category.
	 * 
	 * @param id vehicle id
	 * @param fuel vehicle fuel (diesel or petrol)
	 * @param segment vehicle sub-segment
	 * @param euroNorm euro norm
	 * @param technology technology
	 * @param copert associated copert instance
	 */
	public HeavyDutyTruck(String id, Fuel fuel, HeavyDutyTrucksSegment segment, EuroNorm euroNorm, Technology technology, Copert copert) {
		super(id, VehicleCategory.HEAVY_DUTY_TRUCK, fuel, segment, euroNorm, technology, copert);
	}

}
