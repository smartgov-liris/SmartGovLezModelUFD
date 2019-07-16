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
	private double minSpeed;
	private double maxSpeed;
	
	public CopertParameters(
			double alpha,
			double beta,
			double gamma,
			double delta,
			double epsilon,
			double zita,
			double hta,
			double minSpeed,
			double maxSpeed) {
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = gamma;
		this.delta = delta;
		this.epsilon = epsilon;
		this.zita = zita;
		this.hta = hta;
		this.minSpeed = minSpeed;
		this.maxSpeed = maxSpeed;
	}
	
	/**
	 * Return particles emission in g/km according to the COPERT model.
	 * 
	 * @param meanSpeed Average vehicle speed.
	 * @return emissions in g/km
	 */
	public double emissions(double meanSpeed) {
		if (meanSpeed < minSpeed) {
			return copertFormula(minSpeed);
		}
		if (meanSpeed > maxSpeed) {
			return copertFormula(maxSpeed);
		}
		return copertFormula(meanSpeed);
				
	}
	
	private double copertFormula(double speed) {
		return (alpha * Math.pow(speed, 2) + beta * speed + gamma + delta / speed)
				/ (epsilon * Math.pow(speed, 2) + zita * speed + hta);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(alpha);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(beta);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(delta);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(epsilon);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(gamma);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(hta);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(maxSpeed);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(minSpeed);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(zita);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CopertParameters other = (CopertParameters) obj;
		if (Double.doubleToLongBits(alpha) != Double.doubleToLongBits(other.alpha))
			return false;
		if (Double.doubleToLongBits(beta) != Double.doubleToLongBits(other.beta))
			return false;
		if (Double.doubleToLongBits(delta) != Double.doubleToLongBits(other.delta))
			return false;
		if (Double.doubleToLongBits(epsilon) != Double.doubleToLongBits(other.epsilon))
			return false;
		if (Double.doubleToLongBits(gamma) != Double.doubleToLongBits(other.gamma))
			return false;
		if (Double.doubleToLongBits(hta) != Double.doubleToLongBits(other.hta))
			return false;
		if (Double.doubleToLongBits(maxSpeed) != Double.doubleToLongBits(other.maxSpeed))
			return false;
		if (Double.doubleToLongBits(minSpeed) != Double.doubleToLongBits(other.minSpeed))
			return false;
		if (Double.doubleToLongBits(zita) != Double.doubleToLongBits(other.zita))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CopertParameters [alpha=" + alpha + ", beta=" + beta + ", gamma=" + gamma + ", delta=" + delta
				+ ", epsilon=" + epsilon + ", zita=" + zita + ", hta=" + hta + ", minSpeed=" + minSpeed + ", maxSpeed="
				+ maxSpeed + "]";
	}
	
}
