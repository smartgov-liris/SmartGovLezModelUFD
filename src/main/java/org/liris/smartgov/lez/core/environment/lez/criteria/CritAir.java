package org.liris.smartgov.lez.core.environment.lez.criteria;

import org.liris.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;

/**
 * <i>CritAir</i> categories are used in France, and more particularly by the LEZ
 * in Lyon to determine vehicle permissions.
 * They describe vehicle pollution levels, depending on euro norms and vehicle
 * categories.
 *
 */
public enum CritAir {
	CRITAIR_1,
	CRITAIR_2,
	CRITAIR_3,
	CRITAIR_4,
	CRITAIR_5,
	NONE;

	/**
	 * Computes the <i>CritAir</i> category associated to the vehicle
	 * characteristics, from the <a href="https://www.crit-air.fr/fr/informations-sur-la-vignette-critair/la-vignette-critair/qui-recoit-quelle-couleur-critair.html">
	 * CritAir table</a>.
	 * 
	 * @param vehicle vehicle
	 * @return associated CritAir category
	 */
	public static CritAir compute(DeliveryVehicle vehicle) {
		switch(vehicle.getCategory()) {
		case HEAVY_DUTY_TRUCK:
			switch(vehicle.getFuel()) {
			case DIESEL:
				switch(vehicle.getEuroNorm()) {
				case CONVENTIONAL:
					return NONE;
				case EURO1:
					return NONE;
				case EURO2:
					return NONE;
				case EURO3:
					return CRITAIR_5;
				case EURO4:
					return CRITAIR_4;
				case EURO5:
					return CRITAIR_3;
				case EURO6:
					return CRITAIR_2;
				default:
					return NONE;
				}
			case PETROL:
				switch(vehicle.getEuroNorm()) {
				case CONVENTIONAL:
					return NONE;
				case EURO1:
					return NONE;
				case EURO2:
					return NONE;
				case EURO3:
					return CRITAIR_3;
				case EURO4:
					return CRITAIR_3;
				case EURO5:
					return CRITAIR_2;
				case EURO6:
					return CRITAIR_1;
				default:
					return NONE;
				}
			default:
				return null;
			}
		case LIGHT_WEIGHT:
			switch(vehicle.getFuel()) {
			case DIESEL:
				switch(vehicle.getEuroNorm()) {
				case CONVENTIONAL:
					return NONE;
				case EURO1:
					return NONE;
				case EURO2:
					return CRITAIR_5;
				case EURO3:
					return CRITAIR_4;
				case EURO4:
					return CRITAIR_3;
				case EURO5:
					return CRITAIR_2;
				case EURO6:
					return CRITAIR_2;
				default:
					return NONE;
				}
			case PETROL:
				switch(vehicle.getEuroNorm()) {
				case CONVENTIONAL:
					return NONE;
				case EURO1:
					return NONE;
				case EURO2:
					return CRITAIR_3;
				case EURO3:
					return CRITAIR_3;
				case EURO4:
					return CRITAIR_2;
				case EURO5:
					return CRITAIR_1;
				case EURO6:
					return CRITAIR_1;
				default:
					return NONE;
				}
			default:
				return null;
			}
		default:
			return null;
		
		}
	}
}
