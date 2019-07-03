package com.smartgov.lez.core.environment.graph;

import smartgov.urban.osm.environment.graph.OsmArc.RoadDirection;
import smartgov.urban.osm.environment.graph.OsmNode;
import smartgov.urban.osm.environment.graph.Road;
import smartgov.urban.osm.environment.graph.factory.OsmArcFactory;

public class PollutableOsmArcFactory  implements OsmArcFactory<PollutableOsmArc> {

	@Override
	public PollutableOsmArc create(
			String id,
			OsmNode startNode,
			OsmNode targetNode,
			Road road,
			RoadDirection roadDirection) {
		return new PollutableOsmArc(id, startNode, targetNode, road, roadDirection);
	}

}
