package org.liris.smartgov.lez.core.agent.establishment;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.liris.smartgov.lez.core.output.establishment.EstablishmentListIdSerializer;
import org.liris.smartgov.simulator.core.simulation.time.Date;
import org.liris.smartgov.simulator.urban.osm.environment.graph.OsmNode;

/**
 * Represents a round that a delivery agent might perform between
 * establishments.
 *
 * <p>
 * Notice that this class does not represent the actual path that
 * the agent could follow in the city graph : is just represents
 * the establishment list that it must deliver.
 * </p>
 */
public class Round {
	
	@JsonIgnore
	private Establishment origin;
	@JsonSerialize(using = EstablishmentListIdSerializer.class)
	private List<Establishment> establishments;
	@JsonIgnore
	private Date departure;
	private double initialWeight;
	

	/**
	 * Round constructor.
	 *
	 * @param origin origin establishment : beginning and end of the round
	 * @param establishments establishments to deliver : does not include
	 * origin establishment
	 * @param departure departure date
	 * @param initialWeight might be used to represent the volume of the
	 * delivery
	 */
	public Round(
			Establishment origin,
			List<Establishment> establishments,
			Date departure,
			double initialWeight
			) {
		super();
		this.origin = origin;
		this.establishments = establishments;
		this.departure = departure;
		this.initialWeight = initialWeight;
	}

	/**
	 * Returns the round origin, used as start and final point of the
	 * round.
	 *
	 * @return origin establishment
	 */
	public Establishment getOrigin() {
		return origin;
	}

	/**
	 * Returns establishments delivered by this round, in order.
	 *
	 * @return round establishments
	 */
	public List<Establishment> getEstablishments() {
		return establishments;
	}
	
	public Date getDeparture() {
		return departure;
	}

	/**
	 * Returns the round initial merchandises weight
	 *
	 * @return initial weight
	 */
	public double getInitialWeight() {
		return initialWeight;
	}

	/**
	 * Can be used to compute a node list representation of the round.
	 *
	 * <p>
	 * Nodes return are {@link Establishment#getClosestOsmNode
	 * establishments' closest osm nodes}. Starts with origin, includes
	 * establishments to deliver, and finishes with the origin.
	 * </p>
	 *
	 * @return node list representation of the round.
	 */
	@JsonIgnore
	public List<OsmNode> getNodes() {
		List<OsmNode> nodes = new ArrayList<>();
		
		nodes.add(origin.getClosestOsmNode());
		for (Establishment establishment : establishments) {
			nodes.add(establishment.getClosestOsmNode());
		}
		nodes.add(origin.getClosestOsmNode());
		
		return nodes;
	}

	@Override
	public String toString() {
		List<String> establishmentIds = new ArrayList<>();
		for(Establishment establishment : establishments) {
			establishmentIds.add(establishment.getId());
		}
		return "Round [origin=" + origin.getId()
		+ ", establishments=" + establishmentIds
		+ ", departure=" + departure.getHour() + ":" + departure.getMinutes()
		+ ", weight=" + initialWeight + "]";
	}
	
	

}
