package com.smartgov.lez.core.environment.lez.criteria;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;
import com.smartgov.lez.core.environment.graph.PollutableOsmArc;

import smartgov.core.environment.graph.Arc;
import smartgov.core.environment.graph.Node;
import smartgov.core.environment.graph.astar.Costs;
import smartgov.urban.geo.environment.graph.DistanceCosts;
import smartgov.urban.geo.environment.graph.GeoNode;
import smartgov.urban.geo.utils.LatLon;

public class CritAirLyon implements LezCriteria {

	private static final CritAir[] allowedCritAirs = {
		CritAir.CRITAIR_1,
		CritAir.CRITAIR_2,
		CritAir.CRITAIR_3
	};
	
	public static final Map<CritAir, Boolean> allowed = new HashMap<>();
	
	static {
		
		for(CritAir critAir : allowedCritAirs) {
			if(Arrays.asList(allowedCritAirs).contains(critAir)) {
				allowed.put(critAir, true);
			}
			else {
				allowed.put(critAir, false);
			}
		}
	}

	@Override
	public boolean isAllowed(DeliveryVehicle vehicle) {
		return allowed.get(vehicle.getCritAir());
	}

}
