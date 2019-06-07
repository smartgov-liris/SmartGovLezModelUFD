package com.smartgov.lez.core.copert.tableParser;

import java.util.HashMap;

import com.smartgov.lez.core.copert.fields.CopertField;

public class CopertSelector extends HashMap<CopertHeader, CopertField> {

	private static final long serialVersionUID = 1L;

	public CopertSelector() {
		for (CopertHeader copertHeader : CopertHeader.values()) {
			put(copertHeader, CopertField.randomSelector());
		}
	}
	
	@Override
	public String toString() {
		String str = "\n";
		for(Entry<CopertHeader, CopertField> field : entrySet()) {
			str += field.getKey() + " : " + field.getValue() + "\n";
		}
		return str;
	}
	
}
