package com.smartgov.lez.process.arcs.build;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.smartgov.lez.SmartgovLezApplication;
import com.smartgov.lez.core.copert.fields.Pollutant;
import com.smartgov.lez.process.arcs.load.PollutedArc;
import com.smartgov.lez.process.arcs.load.PollutedNode;

import smartgov.urban.geo.utils.LatLon;

public class TileMap {

	
	@JsonSerialize(using = Bounds.Serializer.class)
	private Bounds bounds;
	private TreeMap<Integer, TreeMap<Integer, Tile>> tiles;
	private Map<Pollutant, Double> pollutionPeeks; // g/m-2
	
	
	public TileMap() {
		this.tiles = new TreeMap<>();
		this.pollutionPeeks = new HashMap<>();
		for(Pollutant pollutant : Pollutant.values()) {
			pollutionPeeks.put(pollutant, 0.);
		}
	}
	
	public Bounds getBounds() {
		return bounds;
	}

	public TreeMap<Integer, TreeMap<Integer, Tile>> getTiles() {
		return tiles;
	}

	public Map<Pollutant, Double> getPollutionPeeks() {
		return pollutionPeeks;
	}

	public void build(Map<String, PollutedArc> arcs, Map<String, PollutedNode> nodes, double tileSize) {
		bounds = boundingBox(arcs.values(), nodes);
		SmartgovLezApplication.logger.info(
				"Computed bounding box :"
				+ " top=" + bounds.getTopLeft().lat
				+ " left=" + bounds.getTopLeft().lon
				+ " bottom=" + bounds.getBottomRight().lat			
				+ " right=" + bounds.getBottomRight().lon
						);
		double meterHeight = LatLon.distance(
				new LatLon(bounds.getTopLeft().lat, bounds.getTopLeft().lon),
				new LatLon(bounds.getBottomRight().lat, bounds.getTopLeft().lon)
				);
		SmartgovLezApplication.logger.info("Bounding box height : " + meterHeight + " m");
		double latitudeHeight = bounds.getTopLeft().lat - bounds.getBottomRight().lat;
		
		double meterWidth = LatLon.distance(
				new LatLon(bounds.getTopLeft().lat, bounds.getTopLeft().lon),
				new LatLon(bounds.getTopLeft().lat, bounds.getBottomRight().lon)
				);
		SmartgovLezApplication.logger.info("Bounding box width : " + meterWidth + " m");
		double longitudeWidth = bounds.getBottomRight().lon - bounds.getTopLeft().lon;
		
		int widthTileCount = (int) Math.ceil(meterWidth / tileSize);
		int heightTileCount = (int) Math.ceil(meterHeight / tileSize);
		
//		Tile[][] tiles = new Tile[widthTileCount][heightTileCount];

		for(int i = 0; i < heightTileCount; i++) {
			TreeMap<Integer, Tile> line = new TreeMap<>();
			tiles.put(i, line);
			for(int j = 0; j < widthTileCount; j++) {
				Tile newTile = new Tile(new Bounds(
					new LatLon(
							bounds.getBottomRight().lat + (i + 1) * latitudeHeight / heightTileCount,
							bounds.getTopLeft().lon + j * longitudeWidth / widthTileCount),
					new LatLon(
							bounds.getBottomRight().lat + i * latitudeHeight / heightTileCount,
							bounds.getTopLeft().lon + (j + 1) * longitudeWidth / widthTileCount)
					));
				 
				line.put(j, newTile);
				//tiles[i][j] = newTile;
				//this.tiles.add(newTile);
			}
		}
		
		for(PollutedArc arc : arcs.values()) {
			Double[] startPosition = nodes.get(arc.getStartNode()).getPosition();
			int i = 0;
			while(i < heightTileCount && !tiles.get(i).get(0).getBounds().containsLat(startPosition[0])) {
				i++;
			}
			int j = 0;
			while(j < widthTileCount && !tiles.get(0).get(j).getBounds().containsLon(startPosition[1])) {
				j++;
			}
			tiles.get(i).get(j).addArc(arc);
		}
		
		for(TreeMap<Integer, Tile> line : this.tiles.values()) {
			for(Tile tile : line.values()) {
				tile.computePollution();
				for(Entry<Pollutant, Double> pollution : tile.getPollution().entrySet()) {
					if(pollution.getValue() > pollutionPeeks.get(pollution.getKey()))
						pollutionPeeks.put(pollution.getKey(), pollution.getValue());
				}
			}
		}
		
	}
	
	private static Bounds boundingBox(Collection<PollutedArc> arcs, Map<String, PollutedNode> nodes) {
		double top = - Double.MAX_VALUE;
		double left = Double.MAX_VALUE;
		double bottom = Double.MAX_VALUE;
		double right = - Double.MAX_VALUE;
		
		for(PollutedArc arc : arcs) {
			Double[] startPosition = nodes.get(arc.getStartNode()).getPosition();
			// Longitude
			if (startPosition[0] > top)
				top = startPosition[0];
			if (startPosition[0] < bottom)
				bottom = startPosition[0];
			
			// Latitude
			if (startPosition[1] > right)
				right = startPosition[1];
			if (startPosition[1] < left)
				left = startPosition[1];
		}
		
		return new Bounds(
				new LatLon(top, left),
				new LatLon(bottom, right)
				);
	}
}
