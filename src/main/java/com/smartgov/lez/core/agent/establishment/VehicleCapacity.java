package com.smartgov.lez.core.agent.establishment;

import com.smartgov.lez.core.copert.fields.HeavyDutyTrucksSegment;
import com.smartgov.lez.core.copert.fields.LightWeightVehicleSegment;
import com.smartgov.lez.core.copert.fields.VehicleCategory;
import com.smartgov.lez.core.copert.fields.VehicleSegment;

/**
 * A convenient class to represent a vehicle capacity, define by
 * their category (Heavy Duty Truck or Light Weight Vehicle) and their
 * sub-segment.
 * 
 * <p>
 * This class implements the Comparable interface, so that it can be used
 * to compare vehicle by their capacity.
 * </p>
 *
 */
public class VehicleCapacity implements Comparable<VehicleCapacity> {

	private VehicleCategory vehicleCategory;
	private VehicleSegment vehicleSegment;

	/**
	 * VehicleCapacity constructor.
	 *
	 * @param vehicleCategory vehicle category
	 * @param vehicleSegment vehicle segment
	 */
	public VehicleCapacity(VehicleCategory vehicleCategory, VehicleSegment vehicleSegment) {
		super();
		this.vehicleCategory = vehicleCategory;
		this.vehicleSegment = vehicleSegment;
	}

	/**
	 * Returns the vehicle category.
	 *
	 * @return vehicle category
	 */
	public VehicleCategory getVehicleCategory() {
		return vehicleCategory;
	}

	/**
	 * Returns the vehicle segment.
	 *
	 * @return vehicle segment
	 */
	public VehicleSegment getVehicleSegment() {
		return vehicleSegment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((vehicleCategory == null) ? 0 : vehicleCategory.hashCode());
		result = prime * result + ((vehicleSegment == null) ? 0 : vehicleSegment.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VehicleCapacity other = (VehicleCapacity) obj;
		if (vehicleCategory != other.vehicleCategory)
			return false;
		if (vehicleSegment == null) {
			if (other.vehicleSegment != null)
				return false;
		} else if (!vehicleSegment.equals(other.vehicleSegment))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VehicleCapacity(" + vehicleCategory + ", " + vehicleSegment + ")";
	}
	
	/**
	 * Vehicle capacities are compared according to their
	 * vehicle categories and the corresponding segments.
	 * 
	 * @param arg0 vehicle capacity to compare
	 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
	 */
	@Override
	public int compareTo(VehicleCapacity arg0) {
		if(vehicleCategory != arg0.getVehicleCategory())
			// Natural category ordering : order of definition in the VehicleCategory enum
			return vehicleCategory.compareTo(arg0.getVehicleCategory());
		if(vehicleSegment instanceof LightWeightVehicleSegment)
			// Natural light weight segment ordering : order of definition in the LightWeightVehicleSegment enum
			return ((LightWeightVehicleSegment) vehicleSegment).compareTo((LightWeightVehicleSegment) arg0.getVehicleSegment());
		else
			// Natural heavy trucks segment ordering : order of definition in the HeavyDutyTrucksSegment enum
			return ((HeavyDutyTrucksSegment) vehicleSegment).compareTo((HeavyDutyTrucksSegment) arg0.getVehicleSegment());
	}
	
	
}
