package com.smartgov.lez.core.agent;

import org.locationtech.jts.geom.Coordinate;

import com.smartgov.lez.core.environment.city.Shop;

import smartgov.core.environment.graph.SinkNode;
import smartgov.core.environment.graph.SourceNode;

public class Establishment {
	private int id;
	private int siret;
	private int st8; 
	private String nst8;
	private double empmoy;
	private Coordinate coords;
	private Shop shopLeisure;
	private SourceNode sourceNode;
	private SinkNode sinkNode;
	
	private boolean inLEZ;

	private int nbOfTimeSkipped = 0; // the number of time where the establishment appears in a delivery but is skipped because of LEZ

	public Establishment(int id, int siret, int st8, String nst8, double empmoy, Coordinate coords) {
		this.id = id;
		this.siret = siret;
		this.st8 = st8;
		this.nst8 = nst8;
		this.empmoy = empmoy;
		this.coords = coords;
	}
	
	public Establishment(int id, int siret, int st8, String nst8, double empmoy,
			double x, double y) {
		this.id = id;
		this.siret = siret;
		this.st8 = st8;
		this.nst8 = nst8;
		this.empmoy = empmoy;
		this.coords = new Coordinate(x,y);
	}
	

	public int getNbOfTimeSkipped() {
		return nbOfTimeSkipped;
	}

	public void setNbOfTimeSkipped() {
		this.nbOfTimeSkipped++;
	}

	public SourceNode getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(SourceNode sourceNode) {
		this.sourceNode = sourceNode;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public SinkNode getSinkNode() {
		return sinkNode;
	}

	public int getSiret() {
		return siret;
	}

	public void setSiret(int siret) {
		this.siret = siret;
	}

	public int getSt8() {
		return st8;
	}

	public void setSt8(int st8) {
		this.st8 = st8;
	}

	public String getNst8() {
		return nst8;
	}

	public void setNst8(String nst8) {
		this.nst8 = nst8;
	}

	public double getEmpmoy() {
		return empmoy;
	}

	public void setEmpmoy(double empmoy) {
		this.empmoy = empmoy;
	}

	public void setSinkNode(SinkNode sinkNode) {
		this.sinkNode = sinkNode;
	}

	public Shop getShopLeisure() {
		return shopLeisure;
	}

	public void setShopLeisure(Shop shopLeisure) {
		this.shopLeisure = shopLeisure;
	}

	public Coordinate getCoords() {
		return coords;
	}

	public void setCoords(Coordinate coords) {
		this.coords = coords;
	}
	
	public double[] getCoordsInTable(){
		double[] pos = new double[2];
		pos[0] = this.coords.x;
		pos[1] = this.coords.y;
		return pos;
	}

	public void setInLEZ(boolean inLEZ) {
		this.inLEZ = inLEZ;
	}

	public boolean isInLEZ() {
		return inLEZ;
	}
}
