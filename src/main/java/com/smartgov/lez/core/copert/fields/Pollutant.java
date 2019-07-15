package com.smartgov.lez.core.copert.fields;

import java.util.regex.Pattern;

public enum Pollutant implements CopertField {
	CH4 ("CH4"),
	CO ("CO"),
	FC ("FC"),
	N2O ("N2O"),
	NH3 ("NH3"),
	NOx ("NOx"),
	PM ("PM Exhaust"),
	VOC ("VOC");
	
	private final String matcher;
	
	private Pollutant(String matcher) {
		this.matcher = matcher;
	}
	
	public String matcher() {
		return matcher;
	}
	
	public static Pollutant getValue(String string) {
		for(Pollutant pollutant : values()) {
			if (Pattern.matches(pollutant.matcher, string)) {
				return pollutant;
			}
		}
		return null;
	}
}


