package org.liris.smartgov.lez.core.copert.fields;

import org.liris.smartgov.lez.core.copert.tableParser.CopertHeader;

/**
 * Common interface for all the Copert tree fields.
 * 
 * @author pbreugnot
 *
 */
public interface CopertField {

	/**
	 * Name of the field used to read the Copert tree.
	 * Actually corresponds to sub-categories entries for each level.
	 * <ul>
	 * 	<li> Light Commercial vehicle, heavy duty trucks</li>
	 * 	<li> Petrol, Diesel</li>
	 * 	<li> ...</li>
	 * </ul> 
	 * @return field matcher
	 */
	public String matcher();
	
	/**
	 * Use to compute the particular field that correspond to the specified string
	 * for the specified header.
	 * 
	 * (e.g. : CATEGORY, "Light Commercial Vehicles")
	 * 
	 * @param header header type
	 * @param string field value
	 * @return Corresponding Copert field
	 */
	public static CopertField getValue(CopertHeader header, String string) {
		switch(header) {
		case CATEGORY:
			return VehicleCategory.getValue(string);
		case FUEL:
			return Fuel.getValue(string);
		case SEGMENT:
			return VehicleSegment.getValue(string);
		case EURO_STANDARD:
			return EuroNorm.getValue(string);
		case TECHNOLOGY:
			return Technology.getValue(string);
		case POLLUTANT:
			return Pollutant.getValue(string);
		case MODE:
			return Mode.getValue(string);
		case ROAD_SLOPE:
			return RoadSlope.getValue(string);
		case LOAD:
			return Load.getValue(string);
		default:
			return null;
		}
	}
	
	/**
	 * Use to compute the particular field that correspond to the specified enumeration
	 * string representation for the specified header. 
	 * 
	 * (e.g. : CATEGORY, "LIGHT_WEIGHT")
	 * 
	 * @param header header type
	 * @param value enum string representation
	 * @return Corresponding Copert field
	 */
	public static CopertField valueOf(CopertHeader header, String value) {
		switch(header){
		case CATEGORY:
			return VehicleCategory.valueOf(value);
		case FUEL:
			return Fuel.valueOf(value);
		case SEGMENT:
			return VehicleSegment.valueOf(value);
		case EURO_STANDARD:
			return EuroNorm.valueOf(value);
		case TECHNOLOGY:
			return Technology.valueOf(value);
		default :
			return null;
		}
	}
	
	/**
	 * Special random Copert field
	 * @return random field
	 */
	public static CopertField randomSelector() {
		return RandomField.RANDOM;
	}
}
