package com.smartgov.lez.core.copert.inputParser;

import java.util.ArrayList;
import java.util.List;

import com.smartgov.lez.core.copert.tableParser.CopertHeader;

public class CopertRate {
	
	private String value;
	private float rate;
	private CopertProfile subProfile;
	
	public CopertRate() {
		
	}
	
//	public CopertProfile(String field, float value) {
//		super();
//		this.field = field;
//		this.value = value;
//	}
	
	public CopertRate(String value, float rate, CopertProfile subProfile) {
		super();
		this.value = value;
		this.rate = rate;
		this.subProfile = subProfile;
	}

	public String getValue() {
		return value;
	}

	public float getRate() {
		return rate;
	}

	public CopertProfile getSubProfile() {
		return subProfile;
	}

}
