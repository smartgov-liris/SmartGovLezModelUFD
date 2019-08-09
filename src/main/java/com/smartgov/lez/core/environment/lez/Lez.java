package com.smartgov.lez.core.environment.lez;

import org.locationtech.jts.algorithm.locate.IndexedPointInAreaLocator;
import org.locationtech.jts.algorithm.locate.PointOnGeometryLocator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Location;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

import com.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;
import com.smartgov.lez.core.environment.lez.criteria.AllAllowedCriteria;
import com.smartgov.lez.core.environment.lez.criteria.LezCosts;
import com.smartgov.lez.core.environment.lez.criteria.LezCriteria;

import smartgov.core.environment.graph.astar.Costs;
import smartgov.urban.geo.environment.graph.DistanceCosts;
import smartgov.urban.geo.utils.LatLon;
import smartgov.urban.geo.utils.lonLat.LonLat;
import smartgov.urban.osm.environment.graph.OsmNode;

/**
 * A Low Emission Zone representation.
 *
 */
public class Lez {
	
	private LatLon[] perimeter;
	private PointOnGeometryLocator locator;
	private LezCriteria lezCriteria;
	
	/**
	 * Lez constructor.
	 * 
	 * @param geoCoordinates polygon that describes the perimeter of the LEZ.
	 * If the polygon is not closed, it will be completed automatically.
	 * 
	 * @param lezCriteria criteria associated to this lez, that determines which
	 * vehicles are allowed or not
	 */
	public Lez(LatLon[] perimeter, LezCriteria lezCriteria) {
		this.perimeter = perimeter;
		this.lezCriteria = lezCriteria;
		
		GeometryFactory factory = new GeometryFactory();
		
		Coordinate[] coordinates;
		boolean closed;
		if(perimeter[0].equals(perimeter[perimeter.length - 1])) {
			coordinates = new Coordinate[perimeter.length];
			closed = true;
		}
		else {
			coordinates = new Coordinate[perimeter.length + 1];
			closed = false;
		}
		
		LonLat projector = new LonLat();
		for(int i = 0; i < perimeter.length; i++) {
			coordinates[i] = projector.project(perimeter[i]);
		}
		
		if(!closed) {
			coordinates[coordinates.length - 1] = projector.project(perimeter[0]);
		}
		
		locator = new IndexedPointInAreaLocator(
				new Polygon(
					new LinearRing(
						new CoordinateArraySequence(coordinates),
						factory
						),
					new LinearRing[] {},
					factory
					));
	}
	
	public LatLon[] getPerimeter() {
		return perimeter;
	}

	/**
	 * Return the LEZ criteria associated to this LEZ, that determines
	 * which vehicles are allowed or not.
	 * 
	 * @return lez criteria
	 */
	public LezCriteria getLezCriteria() {
		return lezCriteria;
	}
	
	/**
	 * Returns the cost function associated to this vehicle, depending
	 * on its permission to enter the LEZ or not.
	 * <ul>
	 * <li> If the vehicle is allowed, a normal DistanceCosts is returned, as if the
	 * lez did not exist for this vehicle.</li>
	 * <li> Else, {@link com.smartgov.lez.core.environment.lez.criteria.LezCosts} are used.</li>
	 * </ul>
	 * 
	 * @param deliveryVehicle vehicle
	 * @return cost function associated to the specified vehicle in the current urban area
	 */
	public Costs costs(DeliveryVehicle deliveryVehicle) {
		if(lezCriteria.isAllowed(deliveryVehicle))
			return new DistanceCosts();
		return new LezCosts();
	}

	/*
	 * Used by NoLez class below
	 */
	private Lez() {
		this.lezCriteria = new AllAllowedCriteria();
	}
	
	/**
	 * Determines if the specified osm node is contained in the lez,
	 * thanks to a <a href="https://locationtech.github.io/jts/javadoc/org/locationtech/jts/algorithm/locate/package-summary.html">
	 * JTS Point-In-Polygon algorithm</a>.
	 * 
	 * @param node osm node
	 * @return true if and only if the node is strictly contained in this LEZ
	 */
	public boolean contains(OsmNode node) {
		if(locator.locate(
					new LonLat().project(node.getPosition())
				) == Location.INTERIOR)
			return true;
		return false;
	}
	
	/**
	 * Returns a special LEZ instance with no perimeter and all vehicles allowed,
	 * that can be smartly used by scenarios with a lez parameter to compare results
	 * with or without LEZ.
	 * 
	 * @return a lez with absolutely no restriction
	 */
	public static Lez none() {
		return new NoLez();
	}
	
	private static class NoLez extends Lez {
		
		/*
		 * Overrides the contains method so that no node is contained
		 * in the lez, without using the JTS algorithm.
		 */
		@Override
		public boolean contains(OsmNode node) {
			return false;
		}

	}

}
