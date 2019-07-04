package com.smartgov.lez.core.environment.pollution;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
// import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunner;

import com.smartgov.lez.core.copert.fields.Pollutant;

import smartgov.SmartGov;
import smartgov.core.main.SimulationRuntime;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SmartGov.class)
public class PollutionTest {
	
	/*
	 * Two pollution rates, that could correspond to two different arcs.
	 */

	/*
	 * The first is the pollution where the maximum NOx rate is reached.
	 */
	private static Pollution buildTestPollution1() {
		Pollution test = new Pollution();

		test.get(Pollutant.NOx).increasePollution(10.0);
		test.get(Pollutant.NOx).increasePollution(5.0);
		
		test.get(Pollutant.N2O).increasePollution(2.0);
		test.get(Pollutant.N2O).increasePollution(4.0);
		
		return test;
		
	}

	/* 
	 * The second is the pollution where the maximum N2O rate is reached.
	 */
	private static Pollution buildTestPollution2() {
		Pollution test = new Pollution();

		test.get(Pollutant.NOx).increasePollution(1.0);
		test.get(Pollutant.NOx).increasePollution(2.0);
		
		test.get(Pollutant.N2O).increasePollution(8.0);
		test.get(Pollutant.N2O).increasePollution(5.0);
		
		return test;
		
	}

	@Test
	public void PollutionRateTest() {
		/*
		 * Fake runtime with a dummy constant tick count of 10 (used to compute pollution rates)
		 */
		PowerMockito.mockStatic(SmartGov.class);
		
		SimulationRuntime fakeRuntime = PowerMockito.mock(SimulationRuntime.class);
		PowerMockito.when(fakeRuntime.getTickCount()).thenReturn(10);
		PowerMockito.when(fakeRuntime.getTickDuration()).thenReturn(1.);
		
		PowerMockito.when(SmartGov.getRuntime()).thenReturn(fakeRuntime);
		
		/*
		 * Instantiating and increasing the two pollution instances.
		 * This should update the static pollution peeks of the Pollution class.
		 */
		Pollution noxPeek = buildTestPollution1();
		Pollution n2oPeek = buildTestPollution2();
		
		for (Pollutant pollutant: Pollutant.values()) {
			switch(pollutant) {
			case NOx:
				/*
				 * Normal NOx values
				 */
				assertThat(
						noxPeek.get(pollutant).getAbsValue(),
						equalTo(15.0)
						);
				assertThat(
						n2oPeek.get(pollutant).getAbsValue(),
						equalTo(3.)
						);

				/*
				 * NOx peek, that should correspond to the noxPeek pollution.
				 */
				assertThat(
						Pollution.pollutionRatePeeks.get(pollutant),
						equalTo(noxPeek.get(pollutant))
						);
				assertThat(
						Pollution.pollutionRatePeeks.get(pollutant).getValue(),
						equalTo(15. / 10.)
						);
				break;
			case N2O:
				/*
				 * Normal N2O values
				 */
				assertThat(
						noxPeek.get(pollutant).getAbsValue(),
						equalTo(6.)
						);
				assertThat(
						n2oPeek.get(pollutant).getAbsValue(),
						equalTo(13.)
						);
				/*
				 * N2O peek, that should correspond to the noxPeek pollution.
				 */
				assertThat(
						Pollution.pollutionRatePeeks.get(pollutant).getValue(),
						equalTo(13. / 10.)
						);
				assertThat(
						Pollution.pollutionRatePeeks.get(pollutant),
						equalTo(n2oPeek.get(pollutant))
						);
				break;
			default:
				/*
				 * For all other pollutants, values must be 0.
				 */
				assertThat(
						noxPeek.get(pollutant).getAbsValue(),
						equalTo(0.)
						);
				assertThat(
						n2oPeek.get(pollutant).getAbsValue(),
						equalTo(0.)
						);
				assertThat(
						Pollution.pollutionRatePeeks.get(pollutant).getValue(),
						equalTo(0.)
						);
				
			}
		}
		
		
		
	}
}
