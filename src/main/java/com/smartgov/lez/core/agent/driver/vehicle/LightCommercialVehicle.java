package com.smartgov.lez.core.agent.driver.vehicle;

import com.smartgov.lez.core.copert.Copert;
import com.smartgov.lez.core.copert.fields.EuroNorm;
import com.smartgov.lez.core.copert.fields.Fuel;
import com.smartgov.lez.core.copert.fields.LightWeightVehicleSegment;
import com.smartgov.lez.core.copert.fields.Technology;
import com.smartgov.lez.core.copert.fields.VehicleCategory;

/**
 * DeliveryVehicle extension, to simplify light weight vehicles instantiation.
 *
 */
public class LightCommercialVehicle extends DeliveryVehicle {
	
	/**
	 * LightCommercialVehicle constructor. Equivalent to a call to
	 * {@link DeliveryVehicle#DeliveryVehicle(String, VehicleCategory, Fuel, VehicleSegment, EuroNorm, Technology, Copert)}
	 * with the same parameters, with LIGHT_WEIGHT as the vehicle category.
	 * 
	 * @param id vehicle id
	 * @param fuel vehicle fuel (diesel or petrol)
	 * @param segment vehicle sub-segment
	 * @param euroNorm euro norm
	 * @param technology technology
	 * @param copert associated copert instance
	 */
	public LightCommercialVehicle(String id, Fuel fuel, LightWeightVehicleSegment segment, EuroNorm euroNorm, Technology technology, Copert copert) {
		super(id, VehicleCategory.LIGHT_WEIGHT, fuel, segment, euroNorm, technology, copert);
	}


}
