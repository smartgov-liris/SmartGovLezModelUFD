package com.smartgov.lez.core.utils.lambert;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;

public class LambertIITest {

	@Test
	public void testN() {
		double phi = 0.977384381;
		double a = 6378388;
		double e = 0.081991890;
		
		assertThat(
				Math.floor(LambertII.N(phi, a, e) * 1E4) / 1E4,
				equalTo(6393174.9755)
				);
	}
	
	@Test
	public void testL() {
		double phi1 = 0.872664626;
		double e = 0.08199188998;
		
		assertThat(
				Math.round(LambertII.L(phi1, e) * 1E11) / 1E11,
				equalTo(1.00552653649)
				);
		
		double phi2 = -0.3;
		
		assertThat(
				Math.round(LambertII.L(phi2, e) * 1E11) / 1E11,
				equalTo(-0.30261690063)
				);
		
		double phi3 = 0.19998903370;
		
		assertThat(
				Math.round(LambertII.L(phi3, e) * 1E12) / 1E12,
				equalTo(0.200000000009)
				);
		
	}
	
	@Test
	public void testC() {
		double k0_1 = 1;
		double phi0_1 = 0.977384381;
		double a_1 = 6378388;
		double e_1 = 0.08199189;
		
		assertThat(
				Math.round(LambertII.C(k0_1, phi0_1, a_1, e_1) * 1E4) / 1E4,
				equalTo(11464828.2192)
				);
		
		double k0_2 = 0.9998773400;
		double phi0_2 = 0.863937980;
		double a_2 = 6378249.2;
		double e_2 = 0.0824832568;
		
		assertThat(
				Math.round(LambertII.C(k0_2, phi0_2, a_2, e_2) * 1E4) / 1E4,
				equalTo(11603796.976)
				);
	}
	
	@Test
	public void testYs() {
		double k0_1 = 1;
		double phi0_1 = 0.977384381;
		double a_1 = 6378388;
		double e_1 = 0.08199189;
		double Y0_1 = 0;
		
		assertThat(
				Math.round(LambertII.Ys(Y0_1, k0_1, phi0_1, a_1, e_1) * 1E4) / 1E4,
				equalTo(4312250.9718)
				);
		
		double k0_2 = 0.9998773400;
		double phi0_2 = 0.863937980;
		double a_2 = 6378249.2;
		double e_2 = 0.0824832568;
		double Y0_2 = 200000;
		
		assertThat(
				Math.round(LambertII.Ys(Y0_2, k0_2, phi0_2, a_2, e_2) * 1E4) / 1E4,
				equalTo(5657616.6712)
				);
	}
	
	@Test
	public void testPhi() {
		double L1 = 1.00552653648;
		double e = 0.08199188998;
		
		assertThat(
				Math.round(LambertII.phi(L1, e) * 1E11) / 1E11,
				equalTo(0.872664626)
				);
		
		double L2 = -0.3026169006;
		
		assertThat(
				Math.round(LambertII.phi(L2, e) * 1E11) / 1E11,
				equalTo(-0.29999999997)
				);
		
		double L3 = 0.2;
		
		assertThat(
				Math.round(LambertII.phi(L3, e) * 1E11) / 1E11,
				equalTo(0.19998903369)
				);
	}
	
	@Test
	public void testLat() {
		double n = 0.760405966;
		double c = 11603796.9767;
		double Xs = 600000;
		double Ys = 5657616.674;
		double lambdaC = 0.04079234433;
		double e = 0.0824832568;
		
		double x = 1029705.083;
		double y = 272723.849;
		
		Coordinate coord = LambertII.LatLon(n, e, c, lambdaC, Xs, Ys, x, y);
		
		assertThat(
				Math.round(coord.x * 1E11) / 1E11,
				equalTo(0.87266462567)
				);
		
	}
	
	@Test
	public void testLon() {
		double n = 0.760405966;
		double c = 11603796.9767;
		double Xs = 600000;
		double Ys = 5657616.674;
		double lambdaC = 0.04079234433;
		double e = 0.0824832568;
		
		double x = 1029705.083;
		double y = 272723.849;
		
		Coordinate coord = LambertII.LatLon(n, e, c, lambdaC, Xs, Ys, x, y);
		
		assertThat(
				Math.round(coord.y * 1E11) / 1E11,
				equalTo(0.14551209925)
				);
		
	}
	
	@Test
	public void projection() {
		Coordinate coord = new Coordinate(800439.326324793, 2077962.87711255);
		System.out.println(LambertII.LatLon(coord));
	}
}
