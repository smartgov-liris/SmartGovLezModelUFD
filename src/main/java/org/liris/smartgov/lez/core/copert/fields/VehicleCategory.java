package org.liris.smartgov.lez.core.copert.fields;

import java.util.regex.Pattern;

public enum VehicleCategory implements CopertField {
	LIGHT_WEIGHT("Light Commercial Vehicles"),
	HEAVY_DUTY_TRUCK ("Heavy Duty Trucks"),
	RANDOM ("Random");
	
	private final String matcher;
	
	private VehicleCategory(String matcher) {
		this.matcher = matcher;
	}
	
	public String matcher() {
		return matcher;
	}
	
	public static VehicleCategory getValue(String string) {
		for(VehicleCategory value : values()) {
			if (Pattern.matches(value.matcher, string)) {
				return value;
			}
		}
		return null;
	}
}
