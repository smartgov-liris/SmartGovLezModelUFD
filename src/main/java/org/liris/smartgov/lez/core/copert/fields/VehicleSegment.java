package org.liris.smartgov.lez.core.copert.fields;

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
		try {
			return LightWeightVehicleSegment.valueOf(string);
		}
		catch(IllegalArgumentException e) {
			// String was not a LightWeightVehicleSegment : should be an HeavyTrucksSegment
			return HeavyDutyTrucksSegment.valueOf(string);
		}
	}
	
	@Override
	public boolean equals(Object object);
	
	@Override
	public int hashCode();
	
}
