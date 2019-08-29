package org.liris.smartgov.lez.core.copert.fields;

import java.util.regex.Pattern;

public enum RoadSlope implements CopertField {

	PLUS_6 ("0\\.06"),
	PLUS_4 ("0\\.04"),
	PLUS_2 ("0\\.02"),
	_0 ("0\\.00"),
	MINUS_2 ("-0\\.02"),
	MINUS_4 ("-0\\.04"),
	MINUS_6 ("-0\\.06"),
	NONE (""),
	RANDOM ("Random");
	
	private final String matcher;
	
	private RoadSlope(String matcher) {
		this.matcher = matcher;
	}

	@Override
	public String matcher() {
		return matcher;
	}
	
	public static RoadSlope getValue(String string) {
		for(RoadSlope roadSlope : values()) {
			if (Pattern.matches(roadSlope.matcher, string)) {
				return roadSlope;
			}
		}
		return null;
	}
}
