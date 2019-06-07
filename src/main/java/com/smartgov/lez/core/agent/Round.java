package com.smartgov.lez.core.agent;

import java.util.List;

public class Round {
	private int siret_carrier;
	private String mg;
	private String vehi_type;
	private String mo_o;
	private String mo;
	private List<Integer> sirets;
	private List<Establishment> establ;
	
	public Round(int siret_carrier, String mg, String vehi_type, String mo_o, String mo, List<Integer> sirets) {
		super();
		this.siret_carrier = siret_carrier;
		this.mg = mg;
		this.vehi_type = vehi_type;
		this.mo_o = mo_o;
		this.mo = mo;
		this.sirets = sirets;
	}
	
	
	// GETTERs & SETTERS
	
	public List<Establishment> getEstabl() {
		return establ;
	}

	public void setEstabl(List<Establishment> establ) {
		this.establ = establ;
	}

	
	public int getSiret_carrier() {
		return siret_carrier;
	}

	public void setSiret_carrier(int siret_carrier) {
		this.siret_carrier = siret_carrier;
	}

	public String getMg() {
		return mg;
	}

	public void setMg(String mg) {
		this.mg = mg;
	}

	public String getVehi_type() {
		return vehi_type;
	}

	public void setVehi_type(String vehi_type) {
		this.vehi_type = vehi_type;
	}

	public String getMo_o() {
		return mo_o;
	}

	public void setMo_o(String mo_o) {
		this.mo_o = mo_o;
	}

	public String getMo() {
		return mo;
	}

	public void setMo(String mo) {
		this.mo = mo;
	}

	public List<Integer> getSirets() {
		return sirets;
	}

	public void setSirets(List<Integer> sirets) {
		this.sirets = sirets;
	}
}
