package com.smartgov.lez.core.agent.driver.vehicle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.smartgov.lez.core.agent.establishment.VehicleCapacity;
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
public class DeliveryVehicle implements Comparable<DeliveryVehicle> {

	private VehicleCategory category;
	private Fuel fuel;
	private VehicleSegment segment;
	private EuroNorm euroNorm;
	private Technology technology;
	private Copert copert;
	
	public DeliveryVehicle(VehicleCategory category, Fuel fuel, VehicleSegment segment,  EuroNorm euroNorm, Technology technology, Copert copert) {
		this.category = category;
		this.fuel = fuel;
		this.segment = segment;
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
	
	public VehicleSegment getSegment() {
		return segment;
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

	@Override
	public String toString() {
		return "DeliveryVehicle [category=" + category + ", fuel=" + fuel + ", vehicleSegment=" + segment
				+ ", euroNorm=" + euroNorm + ", technology=" + technology + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((euroNorm == null) ? 0 : euroNorm.hashCode());
		result = prime * result + ((fuel == null) ? 0 : fuel.hashCode());
		result = prime * result + ((technology == null) ? 0 : technology.hashCode());
		result = prime * result + ((segment == null) ? 0 : segment.hashCode());
		return result;
	}

	/**
	 * Checks if two vehicles have the same Copert characteristics.
	 * (Category, Segment, Euro Norm and Technology)
	 * 
	 * @param vehicle deliveryVehicle to compare
	 * @return true if and only if the two vehicles have the same copert characteristics
	 */
	public boolean equalCharacteristics(DeliveryVehicle vehicle) {
		if (category != vehicle.category)
			return false;
		if (euroNorm != vehicle.euroNorm)
			return false;
		if (fuel != vehicle.fuel)
			return false;
		if (technology != vehicle.technology)
			return false;
		if (segment == null) {
			if (vehicle.segment != null)
				return false;
		} else if (!segment.equals(vehicle.segment))
			return false;
		return true;
	}

	@Override
	public int compareTo(DeliveryVehicle o) {
		return new VehicleCapacity(category, segment).compareTo(new VehicleCapacity(o.category, o.segment));
	}
	
	
}
