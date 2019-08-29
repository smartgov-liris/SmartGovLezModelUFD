package org.liris.smartgov.lez.core.agent.establishment;

/**
 * Business categories used at the LAET.
 */
public enum ST8 {
	/**
	 * 1 : Agriculture
	 */
	AGRICULTURE ("1"),
	/**
	 * 2 : Craftsmen and services
	 */
	CRAFTS_AND_SERVICES ("2"),
	/**
	 * 3 : Industries
	 */
	INDUSTRY ("3"),
	/**
	 * 4 : Wholesale business
	 */
	WHOLESALE_BUSINESS ("4"),
	/**
	 * 5 : Large retailers
	 */
	LARGE_RETAILERS ("5"),
	/**
	 * 6 : Small shops
	 */
	SMALL_SHOP ("6"),
	/**
	 * 7 : Tertiary offices
	 */
	TERTIARY_OFFICE ("7"),
	/**
	 * 8 : Transports companies and warehouses
	 */
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
