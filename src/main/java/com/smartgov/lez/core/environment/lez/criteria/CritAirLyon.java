package com.smartgov.lez.core.environment.lez.criteria;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;

public class CritAirLyon implements LezCriteria {

//	private static final CritAir[] allowedCritAirs = {
//		CritAir.CRITAIR_1,
//		CritAir.CRITAIR_2,
//		CritAir.CRITAIR_3
//	};
	
	private Map<CritAir, Boolean> allowed = new HashMap<>();
//	
//	static {
//		
//		for(CritAir critAir : allowedCritAirs) {
//			if(Arrays.asList(allowedCritAirs).contains(critAir)) {
//				allowed.put(critAir, true);
//			}
//			else {
//				allowed.put(critAir, false);
//			}
//		}
//	}
	
	public CritAirLyon(Collection<CritAir> allowed) {
		for(CritAir critAir : CritAir.values()) {
			if(allowed.contains(critAir)) {
				this.allowed.put(critAir, true);
			}
			else {
				this.allowed.put(critAir, false);
			}
		}
	}

	@Override
	public boolean isAllowed(DeliveryVehicle vehicle) {
		return allowed.get(vehicle.getCritAir());
	}

}
