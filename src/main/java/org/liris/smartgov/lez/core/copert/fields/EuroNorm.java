package org.liris.smartgov.lez.core.copert.fields;

import java.util.regex.Pattern;

public enum EuroNorm implements CopertField {
	CONVENTIONAL ("Conventional"),
	EURO1 ("(?:Euro 1)|(?:Euro I)"),
	EURO2 ("(?:Euro 2)|(?:Euro II)"),
	EURO3 ("(?:Euro 3)|(?:Euro III)"),
	EURO4 ("(?:Euro 4)|(?:Euro IV)"),
	EURO5 ("(?:Euro 5)|(?:Euro V)"),
	EURO6 ("(?:(?:Euro 6)|(?:Euro VI)).*"),
	RANDOM ("Random");
	
	private final String matcher;
	
	private EuroNorm(String matcher) {
		this.matcher = matcher;
	}
	
	public String matcher() {
		return matcher;
	}
	
	public static EuroNorm getValue(String string) {
		for(EuroNorm norm : values()) {
			if (Pattern.matches(norm.matcher, string)) {
				return norm;
			}
		}
		return null;
	}
	
	public static EuroNorm randomSelector() {
		return RANDOM;
	}
}
