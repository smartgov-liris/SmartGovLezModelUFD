package org.liris.smartgov.lez.core.copert.fields;

import java.util.regex.Pattern;

public enum Load implements CopertField {
	_0 ("0\\.0"),
	_50 ("0\\.5"),
	_100 ("1\\.0"),
	NONE (""),
	RANDOM ("Random");
	
	private final String matcher;
	
	private Load(String matcher) {
		this.matcher = matcher;
	}

	@Override
	public String matcher() {
		return matcher;
	}
	
	public static Load getValue(String string) {
		for(Load load : values()) {
			if (Pattern.matches(load.matcher, string)) {
				return load;
			}
		}
		return null;
	}
}
