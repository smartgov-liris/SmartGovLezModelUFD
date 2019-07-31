package com.smartgov.lez.process.arcs.load;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.smartgov.lez.core.copert.fields.Pollutant;

@JsonIgnoreProperties({"roadDirection"})
public class PollutedArc implements Comparable<PollutedArc>{

	private String id;
	private String startNode;
	private String targetNode;
	private double length;
	private Map<Pollutant, Double> pollution;
	
	public PollutedArc() {}

	public PollutedArc(String id, String startNode, String targetNode, double length, Map<Pollutant, Double> pollution) {
		this.id = id;
		this.startNode = startNode;
		this.targetNode = targetNode;
		this.length = length;
		this.pollution = pollution;
	}

	public String getId() {
		return id;
	}

	public String getStartNode() {
		return startNode;
	}

	public String getTargetNode() {
		return targetNode;
	}
	
	public double getLength() {
		return length;
	}

	public Map<Pollutant, Double> getPollution() {
		return pollution;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		long temp;
		temp = Double.doubleToLongBits(length);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((pollution == null) ? 0 : pollution.hashCode());
		result = prime * result + ((startNode == null) ? 0 : startNode.hashCode());
		result = prime * result + ((targetNode == null) ? 0 : targetNode.hashCode());
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
		PollutedArc other = (PollutedArc) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (Double.doubleToLongBits(length) != Double.doubleToLongBits(other.length))
			return false;
		if (pollution == null) {
			if (other.pollution != null)
				return false;
		} else if (!pollution.equals(other.pollution))
			return false;
		if (startNode == null) {
			if (other.startNode != null)
				return false;
		} else if (!startNode.equals(other.startNode))
			return false;
		if (targetNode == null) {
			if (other.targetNode != null)
				return false;
		} else if (!targetNode.equals(other.targetNode))
			return false;
		return true;
	}

	@Override
	public int compareTo(PollutedArc arg0) {
		return id.compareTo(arg0.getId());
	}
	
	
}
