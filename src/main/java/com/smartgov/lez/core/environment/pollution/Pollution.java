package com.smartgov.lez.core.environment.pollution;

import java.util.HashMap;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.smartgov.lez.core.copert.fields.Pollutant;
import com.smartgov.lez.core.output.pollution.PollutionSerializer;

/**
 * A map that stores pollution values for each {@link com.smartgov.lez.core.copert.fields.Pollutant}.
 *
 */
@JsonSerialize(using = PollutionSerializer.class)
public class Pollution extends HashMap<Pollutant, PollutionRate>{

	private static final long serialVersionUID = 1L;
	
	public static Pollution pollutionRatePeeks = new Pollution();

	/**
	 * Pollution constructor. A entry is created for each
	 * {@link com.smartgov.lez.core.copert.fields.Pollutant}, with
	 * a null {@link PollutionRate}.
	 */
	public Pollution() {
		for(Pollutant pollutant : Pollutant.values()) {
			put(pollutant, new PollutionRate(pollutant));
		}
	}
}
