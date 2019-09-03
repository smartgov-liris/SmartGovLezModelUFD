package org.liris.smartgov.lez.core.agent.driver.vehicle;

import org.liris.smartgov.lez.core.agent.establishment.VehicleCapacity;
import org.liris.smartgov.lez.core.copert.Copert;
import org.liris.smartgov.lez.core.copert.CopertParameters;
import org.liris.smartgov.lez.core.copert.fields.EuroNorm;
import org.liris.smartgov.lez.core.copert.fields.Fuel;
import org.liris.smartgov.lez.core.copert.fields.Load;
import org.liris.smartgov.lez.core.copert.fields.Mode;
import org.liris.smartgov.lez.core.copert.fields.Pollutant;
import org.liris.smartgov.lez.core.copert.fields.RoadSlope;
import org.liris.smartgov.lez.core.copert.fields.Technology;
import org.liris.smartgov.lez.core.copert.fields.VehicleCategory;
import org.liris.smartgov.lez.core.copert.fields.VehicleSegment;
import org.liris.smartgov.lez.core.environment.lez.criteria.CritAir;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A vehicle, that can be associated to an OsmAgentBody.
 * @author pbreugnot
 *
 */
@JsonIgnoreProperties({"copert", "emissions"})
public class DeliveryVehicle implements Comparable<DeliveryVehicle> {

	private String id;
	private VehicleCategory category;
	private Fuel fuel;
	private VehicleSegment segment;
	private EuroNorm euroNorm;
	private Technology technology;
	private Copert copert;
	
	private CritAir critAir;
	
	/**
	 * DeliveryVehicle constructor.
	 * 
	 * @param id vehicle id
	 * @param category Vehicle category (Heavy duty truck or light weight)
	 * @param fuel vehicle fuel (diesel or petrol)
	 * @param segment vehicle sub-segment
	 * @param euroNorm euro norm
	 * @param technology technology
	 * @param copert associated copert instance
	 */
	public DeliveryVehicle(String id, VehicleCategory category, Fuel fuel, VehicleSegment segment,  EuroNorm euroNorm, Technology technology, Copert copert) {
		this.id = id;
		this.category = category;
		this.fuel = fuel;
		this.segment = segment;
		this.technology = technology;
		this.euroNorm = euroNorm;
		this.copert = copert;
		
		this.critAir = CritAir.compute(this);
	}
	
	/**
	 * Returns the vehicle id. This id is not guaranteed to be unique
	 * in all the simulation, and can for example be managed at the
	 * establishments scale.
	 * 
	 * @return vehicle id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns the vehicle category.
	 * 
	 * @return vehicle category
	 */
	public VehicleCategory getCategory() {
		return category;
	}
	
	/**
	 * Returns the vehicle fuel.
	 * 
	 * @return vehicle fuel
	 */
	public Fuel getFuel() {
		return fuel;
	}
	
	/**
	 * Returns the vehicle sub-segment.
	 * 
	 * @return vehicle segment
	 */
	public VehicleSegment getSegment() {
		return segment;
	}

	/**
	 * Returns the vehicle euro norm.
	 * 
	 * @return euro norm
	 */
	public EuroNorm getEuroNorm() {
		return euroNorm;
	}
	
	/**
	 * Returns the vehicle technology
	 * 
	 * @return vehicle technology
	 */
	public Technology getTechnology() {
		return technology;
	}

	/**
	 * Returns the vehicle copert instance, used to compute
	 * pollution emissions.
	 * 
	 * @return copert instance
	 */
	public Copert getCopert() {
		return copert;
	}
	
	/**
	 * Returns the <i>CritAir</i> category associated to this vehicle,
	 * computed from the vehicles properties.
	 * 
	 * @see org.liris.smartgov.lez.core.environment.lez.criteria.CritAir
	 * 
	 * @return <i>CritAir</i> category
	 */
	public CritAir getCritAir() {
		return critAir;
	}



	/**
	 * Compute emissions in g according to the COPERT model.
	 * 
	 * @param pollutant pollutant to consider
	 * @param meanSpeed Mean speed of the vehicle in m/s.
	 * @param distance Traveled distance in m.
	 * @return computed emissions (g)
	 */
	public double getEmissions(Pollutant pollutant, double meanSpeed, double distance) {
		CopertParameters copertParameters = copert.getCopertParameters(
				pollutant,
				Mode.URBAN_PEAK,
				RoadSlope._0,
				Load._50);
		if (copertParameters != null) {
			/*
			 * Conversions needed because COPERT works in km/h, g/km
			 */
			return copertParameters.emissions(meanSpeed * 3600 / 1000) * distance / 1000;
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

	/**
	 * Vehicles can be compared according to their {@link org.liris.smartgov.lez.core.agent.establishment.VehicleCapacity capacities}.
	 * 
	 * @param o vehicle to compare
	 * @return -1, 0, 1 respectively if this vehicle capacity if lower than, equal to or greater than the specified vehicle capacity.
	 */
	@Override
	public int compareTo(DeliveryVehicle o) {
		return new VehicleCapacity(category, segment).compareTo(new VehicleCapacity(o.category, o.segment));
	}
	
	
}
