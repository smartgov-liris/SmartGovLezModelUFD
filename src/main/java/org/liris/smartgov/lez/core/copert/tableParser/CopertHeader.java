package org.liris.smartgov.lez.core.copert.tableParser;

import org.liris.smartgov.lez.core.copert.fields.CopertField;
import org.liris.smartgov.lez.core.copert.fields.EuroNorm;
import org.liris.smartgov.lez.core.copert.fields.Fuel;
import org.liris.smartgov.lez.core.copert.fields.RandomField;
import org.liris.smartgov.lez.core.copert.fields.VehicleCategory;

public enum CopertHeader {
	CATEGORY ("Category"),
	FUEL ("Fuel"),
	SEGMENT ("Segment"),
	EURO_STANDARD ("Euro Standard"),
	TECHNOLOGY ("Technology"),
	POLLUTANT ("Pollutant"),
	MODE ("Mode"),
	ROAD_SLOPE ("Road Slope"),
	LOAD ("Load"),
	MIN_SPEED ("Min Speed [km/h]"),
	MAX_SPEED ("Max Speed [km/h]"),
	ALPHA ("Alpha"),
	BETA ("Beta"),
	GAMMA ("Gamma"),
	DELTA ("Delta"),
	EPSILON ("Epsilon"),
	ZITA ("Zita"),
	HTA ("Hta"),
	THITA ("Thita"),
	REDUCTION_FACTOR ("Reduction Factor [%]"),
	BIO_REDUCTION_FACTOR ("Bio Reduction Factor [%]"),
	NULL (""); // There are column with no headers at the end of Copert tables.
	
	private final String columnName;
	
	private CopertHeader(String columnName) {
		this.columnName = columnName;
	}
	
	public String columnName() {
		return columnName;
	}
	
	public static CopertField randomField(CopertHeader header) {
		switch(header) {
		case CATEGORY:
			return VehicleCategory.RANDOM;
		case FUEL:
			return Fuel.RANDOM;
		case SEGMENT:
			return RandomField.RANDOM;
		case EURO_STANDARD:
			return EuroNorm.RANDOM;
		default:
			return null;
		}
	}
	
	public static CopertHeader getValue(String string) {
		for(CopertHeader value : values()){
			if (value.columnName().equals(string)) {
				return value;
			}
		}
		return null;
	}
	
}
