package com.smartgov.lez.core.agent.establishment;

import com.smartgov.lez.core.copert.fields.VehicleCategory;
import com.smartgov.lez.core.copert.fields.VehicleSegment;

public class VehicleCapacity {

	private VehicleCategory vehicleCategory;
	private VehicleSegment vehicleSegment;

	public VehicleCapacity(VehicleCategory vehicleCategory, VehicleSegment vehicleSegment) {
		super();
		this.vehicleCategory = vehicleCategory;
		this.vehicleSegment = vehicleSegment;
	}
	public VehicleCategory getVehicleCategory() {
		return vehicleCategory;
	}
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
	
	
}
