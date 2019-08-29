package org.liris.smartgov.lez.core.copert.fields;

import java.util.regex.Pattern;

public enum Fuel implements CopertField {
	PETROL ("Petrol"),
	DIESEL ("Diesel"),
	RANDOM ("Random");
	
	private final String matcher;
	
	private Fuel(String matcher) {
		this.matcher = matcher;
	}
	
	public String matcher() {
		return matcher;
	}
	
	public static Fuel getValue(String string) {
		for(Fuel value : values()) {
			if (Pattern.matches(value.matcher, string)) {
				return value;
			}
		}
		return null;
	}
	
	public static Fuel randomSelector() {
		return RANDOM;
	}
}
