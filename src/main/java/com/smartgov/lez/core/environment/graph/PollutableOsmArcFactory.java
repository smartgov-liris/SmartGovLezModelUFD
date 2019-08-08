package com.smartgov.lez.core.environment.graph;

import smartgov.urban.osm.environment.graph.OsmArc.RoadDirection;

import com.smartgov.lez.core.environment.lez.Lez;

import smartgov.urban.osm.environment.graph.OsmNode;
import smartgov.urban.osm.environment.graph.Road;
import smartgov.urban.osm.environment.graph.factory.OsmArcFactory;

public class PollutableOsmArcFactory  implements OsmArcFactory<PollutableOsmArc> {
	
	private Lez lez;
	
	public PollutableOsmArcFactory(Lez lez) {
		this.lez = lez;
	}

	@Override
	public PollutableOsmArc create(
			String id,
			OsmNode startNode,
			OsmNode targetNode,
			Road road,
			RoadDirection roadDirection) {
		return new PollutableOsmArc(id, startNode, targetNode, road, roadDirection, lez.contains(targetNode));
	}

}
