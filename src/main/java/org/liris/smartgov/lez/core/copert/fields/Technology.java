package org.liris.smartgov.lez.core.copert.fields;

import java.util.regex.Pattern;

public enum Technology implements CopertField {
	DPF ("DPF"),
	DPF_SCR ("DPF\\+SCR"),
	EGR ("EGR"),
	GDI ("GDI"),
	GDI_GPF ("GDI\\+GPF"),
	LNT_DPF ("LNT\\+DPF"),
	PFI ("PFI"),
	SCR ("SCR"),
	NONE (""),
	RANDOM ("Random")
	;

	private String matcher;
	
	private Technology(String matcher) {
		this.matcher = matcher;
	}
	
	@Override
	public String matcher() {
		return matcher;
	}
	
	public static Technology getValue(String string) {
		for(Technology techonology : values()) {
			if (Pattern.matches(techonology.matcher, string)) {
				return techonology;
			}
		}
		return null;
	}
}
