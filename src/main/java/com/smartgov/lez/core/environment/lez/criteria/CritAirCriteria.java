package com.smartgov.lez.core.environment.lez.criteria;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;

public class CritAirCriteria implements LezCriteria {
	
	private Collection<CritAir> allowed;
	private Map<CritAir, Boolean> allowedIndex = new HashMap<>();
	
	public CritAirCriteria(Collection<CritAir> allowed) {
		this.allowed = allowed;
		for(CritAir critAir : CritAir.values()) {
			if(allowed.contains(critAir)) {
				this.allowedIndex.put(critAir, true);
			}
			else {
				this.allowedIndex.put(critAir, false);
			}
		}
	}

	@Override
	public boolean isAllowed(DeliveryVehicle vehicle) {
		return allowedIndex.get(vehicle.getCritAir());
	}
	
	public Collection<CritAir> getAllowedCritAirs() {
		return allowed;
	}

}
