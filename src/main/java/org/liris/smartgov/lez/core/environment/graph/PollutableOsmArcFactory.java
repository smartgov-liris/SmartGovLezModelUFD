package org.liris.smartgov.lez.core.environment.graph;

import org.liris.smartgov.simulator.urban.osm.environment.graph.OsmArc.RoadDirection;
import org.liris.smartgov.lez.core.environment.lez.Lez;
import org.liris.smartgov.simulator.urban.osm.environment.graph.OsmNode;
import org.liris.smartgov.simulator.urban.osm.environment.graph.Road;
import org.liris.smartgov.simulator.urban.osm.environment.graph.factory.OsmArcFactory;

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
