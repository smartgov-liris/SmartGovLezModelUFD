package org.liris.smartgov.lez.core.agent.establishment;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.liris.smartgov.lez.core.agent.establishment.VehicleCapacity;
import org.liris.smartgov.lez.core.copert.fields.HeavyDutyTrucksSegment;
import org.liris.smartgov.lez.core.copert.fields.VehicleCategory;

public class VehicleCapacityTest {

	@Test
	public void testEquals() {
		VehicleCapacity capacity1 = new VehicleCapacity(VehicleCategory.HEAVY_DUTY_TRUCK, HeavyDutyTrucksSegment.ARTICULATED_14_20_T);
		VehicleCapacity capacity2 = new VehicleCapacity(VehicleCategory.HEAVY_DUTY_TRUCK, HeavyDutyTrucksSegment.ARTICULATED_14_20_T);
		
		assertThat(
			capacity1,
			equalTo(capacity2)
			);
	}
	
	@Test
	public void testHashcode() {
		VehicleCapacity capacity1 = new VehicleCapacity(VehicleCategory.HEAVY_DUTY_TRUCK, HeavyDutyTrucksSegment.ARTICULATED_14_20_T);
		VehicleCapacity capacity2 = new VehicleCapacity(VehicleCategory.HEAVY_DUTY_TRUCK, HeavyDutyTrucksSegment.ARTICULATED_14_20_T);
		
		assertThat(
			capacity1.hashCode(),
			equalTo(capacity2.hashCode())
			);
	}
}
