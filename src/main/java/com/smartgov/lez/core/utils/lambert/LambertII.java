package com.smartgov.lez.core.utils.lambert;

import org.locationtech.jts.geom.Coordinate;

public class LambertII {

	// Clarke 1880 IGN geodesic ellipsoid
	private static final double a = 6378249.2; // Semi-major axe
	private static final double b = 6356515; // Semi-minor axe
	private static double e = Math.sqrt(1 - Math.pow(b, 2) / Math.pow(a, 2)); // First eccentricity
	
	// Origin longitude
	private static final double lambda0 = (2 + 20 / 60 + 14.025 / 3600) // 2°20'14.025"
											* Math.PI / 180; // Radian
	
	// Origin latitude
	private static final double phi0 = 52 * Math.PI / 200; // 52 grades (rad)
	
	// Scale factor
	private static final double k0 = 0.99987742;
	
	// Origin translation
	private static final double X0 = 600000;
	private static final double Y0 = 2200000;
	
	
	// First parameters set
	private static final double lambdaC = lambda0;
	private static final double n = Math.sin(phi0);
	
	private static final double C = C(k0, phi0, a, e);
	private static final double Xs = X0;
	private static final double Ys = Ys(Y0, k0, phi0, a, e);
	
	private static final double epsilon = 1E-11;
	
	// Grande normale (ALG0021)
	static double N(double phi, double a, double e) {
		return a / Math.sqrt(1 - Math.pow(e, 2) * Math.pow(Math.sin(phi), 2));
	}
	//
	
	// Projection parameters (ALG0019)
	static double C(double k0, double phi0, double a, double e) {
		return k0 * N(phi0, a, e) * (1 / Math.tan(phi0)) * Math.exp(Math.sin(phi0) * L(phi0, e));
	}
	
	
	static double Ys(double Y0, double k0, double phi0, double a, double e) {
		return Y0 + k0 * N(phi0, a, e) * 1 / Math.tan(phi0);
	}
	//
	
	// Isometric latitude (ALG0001)
	static double L(double phi, double e) {
		return Math.log(
				Math.tan(Math.PI / 4 + phi / 2) * Math.pow((1 - e * Math.sin(phi)) / (1 + e * Math.sin(phi)), e / 2)
				);
	}
	//
	
	// Isometric latitude inverse (ALG0002)
	static double phi(double L, double e) {
		double phi = 2 * Math.atan(Math.exp(L)) - Math.PI / 2;
		double phi_i = phi_i(L, phi, e);
		while(Math.abs(phi_i - phi) >= epsilon) {
			phi = phi_i;
			phi_i = phi_i(L, phi_i, e);
		}
		return phi_i;
	}
	
	private static final double phi_i(double L, double phi, double e) {
		return 2 * Math.atan(
					Math.pow((1 + e * Math.sin(phi)) / (1 - e * Math.sin(phi)), e / 2)
					* Math.exp(L)
				) - Math.PI / 2;
	}
	//
	
	// Other variables used in ALG0004
	private static double R(double Xs, double Ys, double x, double y) {
		return Math.sqrt(Math.pow(x - Xs, 2) + Math.pow(y - Ys, 2));
	}
	
	private static double gamma(double Xs, double Ys, double x, double y) {
		return Math.atan((x - Xs) / (Ys - y));
	}
	
	private static double lambda(double lambdaC, double gamma, double n) {
		return lambdaC + gamma / n;
	}
	//
	
	// Conversion to latLon (ALG0004)
	static Coordinate LatLon(double n, double e, double c, double lambdaC, double Xs, double Ys, double x, double y) {
		double R = R(Xs, Ys, x, y);
		double gamma = gamma(Xs, Ys, x, y);
		double lambda = lambda(lambdaC, gamma, n);
		double L = - (1 / n) * Math.log(Math.abs(R / c));
		double phi = phi(L, e);
		return new Coordinate(phi, lambda);
	}
	
	/**
	 * Converts Lambert II X/Y coordinates to Lat/Lon coordinates.
	 * 
	 * @param lambertCoordinate X/Y Lambert II coordinates
	 * @return Lat/Lon coordinates
	 */
	public static Coordinate LatLon(Coordinate lambertCoordinate) {
		double x = lambertCoordinate.x;
		double y = lambertCoordinate.y;
		Coordinate rad =  LatLon(n, e, C, lambdaC, Xs, Ys, x, y);
		return new Coordinate(180 * rad.x / Math.PI, 180 * rad.y / Math.PI);
	}
}

