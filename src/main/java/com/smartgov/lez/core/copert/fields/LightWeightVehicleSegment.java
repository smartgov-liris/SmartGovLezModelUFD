package com.smartgov.lez.core.copert.fields;

import java.util.regex.Pattern;

public enum LightWeightVehicleSegment implements VehicleSegment {
	N1_I ("N1-I"),
	N1_II ("N1-II"),
	N1_III ("N1-III"),
	RANDOM ("Random");
	
	private final String matcher;
	
	private LightWeightVehicleSegment(String matcher) {
		this.matcher = matcher;
	}
	
	public String matcher() {
		return matcher;
	}
	
	public static LightWeightVehicleSegment getValue(String string) {
		for(LightWeightVehicleSegment value : values()) {
			if (Pattern.matches(value.matcher, string)) {
				return value;
			}
		}
		return null;
	}
	
	public static LightWeightVehicleSegment randomSelector() {
		return RANDOM;
	}
}
