package com.smartgov.lez.core.copert.fields;

import com.smartgov.lez.core.agent.vehicle.BadCopertFieldException;
import com.smartgov.lez.core.copert.tableParser.CopertHeader;

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
	 * 	<li> Light Commercial vehicle, heavy duty trucks
	 * 	<li> Petrol, Diesel
	 * 	<li> ...
	 * <ul> 
	 * @return
	 */
	public String matcher();
	
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
		default:
			return null;
		}
	}
	
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
//			String errorMessage = "Unknown field " + value + "for header " + header;
//			throw (new BadCopertFieldException(errorMessage));
		}
	}
	
	public static CopertField randomSelector() {
		return RandomField.RANDOM;
	}
}
