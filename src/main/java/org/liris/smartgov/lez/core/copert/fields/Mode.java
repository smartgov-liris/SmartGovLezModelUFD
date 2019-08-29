package org.liris.smartgov.lez.core.copert.fields;

import java.util.regex.Pattern;

public enum Mode implements CopertField {
	URBAN_PEAK ("Urban Peak"),
	URBAN_OFF_PEAK ("Urban Off Peak"),
	RURAL ("Rural"),
	HIGHWAY ("Highway"),
	NONE (""),
	RANDOM ("Random");
	
	private final String matcher;

	private Mode(String matcher) {
		this.matcher = matcher;
	}
	@Override
	public String matcher() {
		return matcher;
	}

	public static Mode getValue(String string) {
		for(Mode mode : values()) {
			if (Pattern.matches(mode.matcher, string)) {
				return mode;
			}
		}
		return null;
	}

}
