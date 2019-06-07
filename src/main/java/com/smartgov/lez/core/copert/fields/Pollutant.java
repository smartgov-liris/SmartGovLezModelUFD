package com.smartgov.lez.core.copert.fields;

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
}


