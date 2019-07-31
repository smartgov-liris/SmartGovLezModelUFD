package com.smartgov.lez.process.arcs.build;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import com.smartgov.lez.SmartgovLezApplication;
import com.smartgov.lez.process.arcs.build.Tile.Bounds;
import com.smartgov.lez.process.arcs.load.PollutedArc;
import com.smartgov.lez.process.arcs.load.PollutedNode;

import smartgov.urban.geo.utils.LatLon;

public class TileMap {

	private TreeMap<Integer, TreeMap<Integer, Tile>> tiles;
	
	public TileMap() {
		this.tiles = new TreeMap<>();
	}
	
	public TreeMap<Integer, TreeMap<Integer, Tile>> getTiles() {
		return tiles;
	}

	public void build(Map<String, PollutedArc> arcs, Map<String, PollutedNode> nodes, double tileSize) {
		Double[] boundingBox = boundingBox(arcs.values(), nodes);
		SmartgovLezApplication.logger.info(
				"Computed bounding box :"
				+ " top=" + boundingBox[0]
				+ " left=" + boundingBox[1]
				+ " bottom=" + boundingBox[2]				
				+ " right=" + boundingBox[3]
						);
		double meterHeight = LatLon.distance(
				new LatLon(boundingBox[0], boundingBox[3]),
				new LatLon(boundingBox[2], boundingBox[3])
				);
		SmartgovLezApplication.logger.info("Bounding box height : " + meterHeight + " m");
		double latitudeHeight = boundingBox[0] - boundingBox[2];
		
		double meterWidth = LatLon.distance(
				new LatLon(boundingBox[0], boundingBox[1]),
				new LatLon(boundingBox[0], boundingBox[3])
				);
		SmartgovLezApplication.logger.info("Bounding box width : " + meterWidth + " m");
		double longitudeWidth = boundingBox[3] - boundingBox[1];
		
		int widthTileCount = (int) Math.ceil(meterWidth / tileSize);
		int heightTileCount = (int) Math.ceil(meterHeight / tileSize);
		
//		Tile[][] tiles = new Tile[widthTileCount][heightTileCount];

		for(int i = 0; i < heightTileCount; i++) {
			TreeMap<Integer, Tile> line = new TreeMap<>();
			tiles.put(i, line);
			for(int j = 0; j < widthTileCount; j++) {
				Tile newTile = new Tile(new Bounds(
					new LatLon(
							boundingBox[2] + (i + 1) * latitudeHeight / heightTileCount,
							boundingBox[1] + j * longitudeWidth / widthTileCount),
					new LatLon(
							boundingBox[2] + i * latitudeHeight / heightTileCount,
							boundingBox[1] + (j + 1) * longitudeWidth / widthTileCount)
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
			}
		}
		
	}
	
	private static Double[] boundingBox(Collection<PollutedArc> arcs, Map<String, PollutedNode> nodes) {
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
		
		return new Double[] {top, left, bottom, right };
	}
}
