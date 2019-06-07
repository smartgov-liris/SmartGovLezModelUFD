package com.smartgov.lez.core.copert;

/**
 * A utility class to store COPERT parameters and compute pollution emissions.
 * 
 * @author pbreugnot
 *
 */
public class CopertParameters {
	
	private double alpha;
	private double beta;
	private double gamma;
	private double delta;
	private double epsilon;
	private double zita;
	private double hta;
	
	public CopertParameters(double alpha, double beta, double gamma, double delta, double epsilon, double zita, double hta) {
		super();
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = gamma;
		this.delta = delta;
		this.epsilon = epsilon;
		this.zita = zita;
		this.hta = hta;
	}
	
	/**
	 * Return particles emission in g/km according to the COPERT model.
	 * 
	 * @param meanSpeed Average vehicle speed.
	 * @return emissions in g/km
	 */
	public double emissions(double meanSpeed) {
		return (alpha * Math.pow(meanSpeed, 2) + beta * meanSpeed + gamma + delta / meanSpeed)
					/ (epsilon * Math.pow(meanSpeed, 2) + zita * meanSpeed + hta);
				
	}

	public double getAlpha() {
		return alpha;
	}

	public double getBeta() {
		return beta;
	}

	public double getGamma() {
		return gamma;
	}

	public double getDelta() {
		return delta;
	}

	public double getEpsilon() {
		return epsilon;
	}

	public double getZita() {
		return zita;
	}

	public double getHta() {
		return hta;
	}

}
