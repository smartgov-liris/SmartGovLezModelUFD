package com.smartgov.lez.process.arcs.load;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"incomingArcs", "outgoingArcs", "road"})
public class PollutedNode implements Comparable<PollutedNode> {

	private String id;
	private Double[] position;
	
	public PollutedNode() {}

	public PollutedNode(String id, Double[] position) {
		this.id = id;
		this.position = position;
	}

	public String getId() {
		return id;
	}

	public Double[] getPosition() {
		return position;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + Arrays.hashCode(position);
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
		PollutedNode other = (PollutedNode) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (!Arrays.equals(position, other.position))
			return false;
		return true;
	}

	@Override
	public int compareTo(PollutedNode arg0) {
		return id.compareTo(arg0.getId());
	};
	
	
}
