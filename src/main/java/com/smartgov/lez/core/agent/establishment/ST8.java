package com.smartgov.lez.core.agent.establishment;

/**
 * Business categories used at the LAET.
 */
public enum ST8 {
	AGRICULTURE ("1"),
	CRAFTS_AND_SERVICES ("2"),
	INDUSTRY ("3"),
	WHOLESALE_BUSINESS ("4"),
	LARGE_RETAILERS ("5"),
	SMALL_SHOP ("6"),
	TERTIARY_OFFICE ("7"),
	TRANSPORT_WAREHOUSE ("8");
	
	private final String code;
	
	private ST8(String code) {
		this.code = code;
	}
	
	/**
	 * Numeric representation of this ST8 category.
	 *
	 * @return category code
	 */
	public String getCode() {
		return code;
	}
	
	/**
	 * Returns the ST8 category corresponding to
	 * the specified code.
	 *
	 * @param code category code
	 * @return corresponding ST8 category
	 */
	public static ST8 byCode(String code) {
		for(ST8 st8 : ST8.values()) {
			if(st8.getCode().equals(code))
				return st8;
		}
		return null;
	}
}
