package com.smartgov.lez.core.agent.driver.vehicle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.smartgov.lez.core.copert.Copert;
import com.smartgov.lez.core.copert.CopertParameters;
import com.smartgov.lez.core.copert.fields.EuroNorm;
import com.smartgov.lez.core.copert.fields.Fuel;
import com.smartgov.lez.core.copert.fields.Load;
import com.smartgov.lez.core.copert.fields.Mode;
import com.smartgov.lez.core.copert.fields.Pollutant;
import com.smartgov.lez.core.copert.fields.RoadSlope;
import com.smartgov.lez.core.copert.fields.Technology;
import com.smartgov.lez.core.copert.fields.VehicleCategory;
import com.smartgov.lez.core.copert.fields.VehicleSegment;

/**
 * A vehicle, that can be associated to an OsmAgentBody.
 * @author pbreugnot
 *
 */
@JsonIgnoreProperties({"copert", "emissions"})
public class DeliveryVehicle {

	private VehicleCategory category;
	private Fuel fuel;
	private VehicleSegment vehicleSegment;
	private EuroNorm euroNorm;
	private Technology technology;
	private Copert copert;
	
	public DeliveryVehicle(VehicleCategory category, Fuel fuel, VehicleSegment vehicleSegment,  EuroNorm euroNorm, Technology technology, Copert copert) {
		this.category = category;
		this.fuel = fuel;
		this.vehicleSegment = vehicleSegment;
		this.technology = technology;
		this.euroNorm = euroNorm;
		this.copert = copert;
	}
	
	public VehicleCategory getCategory() {
		return category;
	}
	
	public Fuel getFuel() {
		return fuel;
	}
	
	public VehicleSegment getVehicleSegment() {
		return vehicleSegment;
	}

	public EuroNorm getEuroNorm() {
		return euroNorm;
	}
	
	public Technology getTechnology() {
		return technology;
	}

	public Copert getCopert() {
		return copert;
	}



	/**
	 * Compute emissions in g according to the COPERT model.
	 * 
	 * @param pollutant pollutant to consider
	 * @param meanSpeed Mean speed of the vehicle.
	 * @param distance Traveled distance.
	 * @return computed emissions (g)
	 */
	public double getEmissions(Pollutant pollutant, double meanSpeed, double distance) {
		CopertParameters copertParameters = copert.getCopertParameters(
				pollutant,
				Mode.URBAN_PEAK,
				RoadSlope._0,
				Load._50);
		if (copertParameters != null) {
			return copertParameters.emissions(meanSpeed) * distance;
		}
		return 0;
	}
}
