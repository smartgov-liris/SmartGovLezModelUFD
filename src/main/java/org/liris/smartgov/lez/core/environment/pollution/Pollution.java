package org.liris.smartgov.lez.core.environment.pollution;

import java.util.HashMap;

import org.liris.smartgov.lez.core.copert.fields.Pollutant;
import org.liris.smartgov.lez.core.output.pollution.PollutionSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A map that stores pollution values for each {@link org.liris.smartgov.lez.core.copert.fields.Pollutant}.
 *
 */
@JsonSerialize(using = PollutionSerializer.class)
public class Pollution extends HashMap<Pollutant, PollutionRate>{

	private static final long serialVersionUID = 1L;
	
	public static Pollution pollutionRatePeeks = new Pollution();

	/**
	 * Pollution constructor. A entry is created for each
	 * {@link org.liris.smartgov.lez.core.copert.fields.Pollutant}, with
	 * a null {@link PollutionRate}.
	 */
	public Pollution() {
		for(Pollutant pollutant : Pollutant.values()) {
			put(pollutant, new PollutionRate(pollutant));
		}
	}
}
