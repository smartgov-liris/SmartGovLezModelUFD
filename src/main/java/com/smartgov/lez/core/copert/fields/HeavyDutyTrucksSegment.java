package com.smartgov.lez.core.copert.fields;

import java.util.regex.Pattern;

public enum HeavyDutyTrucksSegment implements VehicleSegment {
	SUP_3_5_T (">3.5 t"),
	RIGID_INF_7_5_T ("Rigid <=7\\.5 t"),
	RIGID_7_5_12_T ("Rigid 7\\.5 - 12 t"),
	RIGID_12_14_T ("Rigid 12 - 14 t"),
	RIGID_14_20_T ("Rigid 14 - 20 t"),
	RIGID_20_26_T ("Rigid 20 - 26 t"),
	RIGID_26_28_T ("Rigid 26 - 28 t"),
	RIGID_28_32_T ("Rigid 28 - 32 t"),
	RIGID_SUP_32_T ("Rigid >32 t"),
	ARTICULATED_14_20_T ("Articulated 14 - 20 t"),
	ARTICULATED_20_28_T ("Articulated 20 - 28 t"),
	ARTICULATED_28_34_T ("Articulated 28 - 34 t"),
	ARTICULATED_34_40_T ("Articulated 34 - 40 t"),
	ARTICULATED_40_50_T ("Articulated 40 - 50 t"),
	ARTICULATED_50_60_T ("Articulated 50 - 60 t"),
	RIGID (
			"(?:" + SUP_3_5_T.matcher() + ")|" +
			"(?:" + RIGID_INF_7_5_T.matcher() + ")|" +
			"(?:" + RIGID_7_5_12_T.matcher() + ")|" +
			"(?:" + RIGID_12_14_T.matcher() + ")|" +
			"(?:" + RIGID_14_20_T.matcher() + ")|" +
			"(?:" + RIGID_20_26_T.matcher() + ")|" +
			"(?:" + RIGID_26_28_T.matcher() + ")|" +
			"(?:" + RIGID_28_32_T.matcher() + ")|" +
			"(?:" + RIGID_SUP_32_T.matcher() + ")|"),
	ARTICULATED (
			"(?:" + ARTICULATED_14_20_T.matcher() + ")|" +
			"(?:" + ARTICULATED_20_28_T.matcher() + ")|" +
			"(?:" + ARTICULATED_28_34_T.matcher() + ")|" +
			"(?:" + ARTICULATED_34_40_T.matcher() + ")|" +
			"(?:" + ARTICULATED_40_50_T.matcher() + ")|" +
			"(?:" + ARTICULATED_50_60_T.matcher() + ")|"
			),
	RANDOM ("Random");
	
	private final String matcher;
	
	private HeavyDutyTrucksSegment(String matcher) {
		this.matcher = matcher;
	}
	
	public String matcher() {
		return matcher;
	}
	
	public static HeavyDutyTrucksSegment getValue(String string) {
		for(HeavyDutyTrucksSegment value : values()) {
			if (Pattern.matches(value.matcher, string)) {
				return value;
			}
		}
		return null;
	}
	
	public static HeavyDutyTrucksSegment randomSelector() {
		return RANDOM;
	}
}
