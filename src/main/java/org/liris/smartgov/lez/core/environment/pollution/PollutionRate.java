package org.liris.smartgov.lez.core.environment.pollution;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.liris.smartgov.lez.core.copert.fields.Pollutant;
import org.liris.smartgov.lez.core.output.pollution.PollutionRateSerializer;
import org.liris.smartgov.simulator.SmartGov;

/**
 * Represents an emitted amount of pollution for a given pollutant.
 *
 */
@JsonSerialize(using = PollutionRateSerializer.class)
public class PollutionRate {

	private Pollutant pollutant;
	private double pollution = 0; // Pollution in g
	
	/**
	 * PollutionRate constructor.
	 * 
	 * @param pollutant pollutant
	 */
	public PollutionRate(Pollutant pollutant) {
		this.pollutant = pollutant;
	}
	
	/**
	 * Increase pollution by the specified amount for this pollutant.
	 * {@link Pollution#pollutionRatePeeks} will be updated accordingly.
	 * 
	 * @param pollution emission amount in g
	 */
	public void increasePollution(double pollution) {
		this.pollution += pollution;
		// TODO : improve performances
		if (Pollution.pollutionRatePeeks.get(pollutant) != this) {
			if(getValue() > Pollution.pollutionRatePeeks.get(pollutant).getValue()) {
				Pollution.pollutionRatePeeks.put(pollutant, this);
			}
		}
	}
	
	/**
	 * Returns the absolute pollution accumulated by this pollution rate.
	 * 
	 * @return emission amount in g
	 */
	public double getAbsValue() {
		return pollution;
	}
	
	/**
	 * Returns the pollution accumulated by this pollution rate, averaged in
	 * time according to the SmartGov runtime state.
	 * 
	 * @return pollution rate in g/s
	 */
	public double getValue() {
		return pollution / (SmartGov.getRuntime().getTickCount() * SmartGov.getRuntime().getTickDuration());
	}
}
