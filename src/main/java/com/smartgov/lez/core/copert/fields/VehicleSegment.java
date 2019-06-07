package com.smartgov.lez.core.copert.fields;

/**
 * Common interface for Light Commercial Vehicle and Heavy Duty Trucks segments.
 * @author pbreugnot
 *
 */
public interface VehicleSegment extends CopertField {

	public static VehicleSegment getValue(String string) {
		LightWeightVehicleSegment lightSegment;
		lightSegment = LightWeightVehicleSegment.getValue(string);
		if (lightSegment != null) {
			return lightSegment;
		}
		else {
			return HeavyDutyTrucksSegment.getValue(string);
		}
	}
	
	public static VehicleSegment valueOf(String string) {
		LightWeightVehicleSegment lightSegment;
		lightSegment = LightWeightVehicleSegment.valueOf(string);
		if (lightSegment != null) {
			return lightSegment;
		}
		else {
			return HeavyDutyTrucksSegment.valueOf(string);
		}
	}
}
