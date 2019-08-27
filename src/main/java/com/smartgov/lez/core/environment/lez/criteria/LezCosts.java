package com.smartgov.lez.core.environment.lez.criteria;

import com.smartgov.lez.core.environment.graph.PollutableOsmArc;

import org.liris.smartgov.simulator.core.environment.graph.Arc;
import org.liris.smartgov.simulator.core.environment.graph.Node;
import org.liris.smartgov.simulator.core.environment.graph.astar.Costs;
import org.liris.smartgov.simulator.urban.geo.environment.graph.GeoNode;
import org.liris.smartgov.simulator.urban.geo.utils.LatLon;

/**
 * Costs implementation that can be used by a vehicle that is not allowed in
 * a LEZ. When shortest path will be computed for this vehicle, a infinite cost
 * will be associated to arcs contained in the LEZ, so that:
 * <ul>
 * <li> The vehicle will find a shortest path outside the lez whenever its possible. </li>
 * <li> If no such path exists, it will try to find a path that cross the lez, with an infinite cost.
 * However, in such a case, the AStar algorithm won't compute a shortest path inside the lez because infinites
 * can't be compared, so this might be improved. </li>
 * </ul>
 *
 */
public class LezCosts implements Costs {

	/**
	 * Returns the normal geographical distance in meters between the specified nodes,
	 * as if there were no LEZ.
	 * @param current current node
	 * @param target target to test
	 * @return heuristic value
	 */
	@Override
	public double heuristic(Node current, Node target) {
		return LatLon.distance(
				((GeoNode) current).getPosition(),
				((GeoNode) target).getPosition()
				);
	}

	/**
	 * Returns an infinie cost if {@link com.smartgov.lez.core.environment.graph.PollutableOsmArc#isInLez()}
	 * is true, or else the normal cost (arc length in meter).
	 * 
	 * @param arc osm arc
	 * @return associated cost
	 */
	@Override
	public double cost(Arc arc) {
		if(((PollutableOsmArc) arc).isInLez())
			return Double.MAX_VALUE;
		return arc.getLength();
	}
	
}
