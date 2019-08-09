package com.smartgov.lez.core.environment.lez.criteria;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;

public class CritAirCriteriaTest {

	@Test
	public void testCritAir() {
		List<CritAir> allowed = Arrays.asList(
				CritAir.CRITAIR_1,
				CritAir.CRITAIR_2,
				CritAir.CRITAIR_3
				);
		
		CritAirCriteria criteria = new CritAirCriteria(
				allowed
				);
		
		for (CritAir critair : CritAir.values()) {
			DeliveryVehicle vehicle = mock(DeliveryVehicle.class);
			when(vehicle.getCritAir()).thenReturn(critair);
			
			if(allowed.contains(critair)) {
				assertThat(
						criteria.isAllowed(vehicle),
						is(true)
						);
			}
			else {
				assertThat(
						criteria.isAllowed(vehicle),
						is(false)
						);
			}
		}
	}
}
