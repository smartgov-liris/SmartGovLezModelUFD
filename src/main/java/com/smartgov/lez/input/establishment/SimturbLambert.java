package com.smartgov.lez.input.establishment;

import smartgov.urban.geo.utils.lambert.Lambert;

/**
 * For unknown reasons, the "official" Lambert II projection brought bad results :
 * all latitudes are computed with a translation. This might come from the origin
 * latitude considered (lambda0), but because no other suitable know origin meridian
 * has been found, we just add an offset computed from the bad latitude and the correct
 * latitude (from OSM) of a known establishment.
 *
 */
public class SimturbLambert extends Lambert {

	// Origin latitude
	private static final double phi0 = 52 * Math.PI / 200; // 52 grades (rad)
	
	// Scale factor
	private static final double k0 = 0.99987742;
	
	// Origin translation
	private static final double X0 = 600000;
	private static final double Y0 = 2200000;
	
	// Clarke 1880 IGN geodesic ellipsoid
	private static final double a = 6378249.2; // Semi-major axe
	private static final double b = 6356515; // Semi-minor axe
	private static double e = Math.sqrt(1 - Math.pow(b, 2) / Math.pow(a, 2)); // First eccentricity

	private static final double correction = (4.853813 - 4.522477) * Math.PI / 180;
	// Origin longitude
	private static final double lambda0 = (2 + (20 / 60) + (14.025 / 3600)) // 2°20'14.025"
											* Math.PI / 180; // Radian

	public SimturbLambert() {
		super(a, b, e, lambda0 + correction, phi0, k0, X0, Y0);
		// TODO Auto-generated constructor stub
	}
}
