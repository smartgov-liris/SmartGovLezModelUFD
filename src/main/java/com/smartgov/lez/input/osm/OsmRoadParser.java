package com.smartgov.lez.input.osm;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.smartgov.lez.SmartgovLezApplication;
import com.smartgov.osmparser.Osm;
import com.smartgov.osmparser.OsmParser;
import com.smartgov.osmparser.examples.roads.WayNodesFilter;
import com.smartgov.osmparser.filters.elements.TagFilter;
import com.smartgov.osmparser.filters.tags.BaseTagMatcher;
import com.smartgov.osmparser.filters.tags.NoneTagMatcher;
import com.smartgov.osmparser.filters.tags.TagMatcher;

public class OsmRoadParser {
	
	public static final String[] highways = {
			"motorway",
			"trunk",
			"primary",
			"secondary",
			"tertiary",
			"unclassified",
			"residential",
			"motorway_link",
			"trunk_link",
			"primary_link",
			"secondary_link",
			"tertiary_link"
	};
	
	public static void main(String[] args) throws JAXBException, IOException {
		OsmParser parser = new OsmParser();
		
		SmartgovLezApplication.logger.info("Parsing osm data from : " + new File(OsmRoadParser.class.getResource(args[0]).getFile()));
	    // Parse the test osm file
	    Osm osm = (Osm) parser.parse(new File(OsmRoadParser.class.getResource(args[0]).getFile()));
	    
	    SmartgovLezApplication.logger.info("Nodes found : " + osm.getNodes().size());
	    SmartgovLezApplication.logger.info("Ways found : " + osm.getWays().size());
	    
	    SmartgovLezApplication.logger.info("Applying filters...");
	    
	    // Start from a tag matche that doesn't match anything
	    TagMatcher highwaysTagMatcher = new NoneTagMatcher();
	    
	    // Increment the matcher with required highway types
	    // Everything that is not an highway won't be kept
	    for(String highway : highways) {
	    	highwaysTagMatcher = highwaysTagMatcher.or(new BaseTagMatcher("highway", highway));
	    }
	    
	    // Filter only highways
        parser.setWayFilter(
        		new TagFilter(
        				highwaysTagMatcher
        		)
        		);
        
        // Keep highway, name and ref tags
        parser.setWayTagMatcher(new BaseTagMatcher("highway", ".*").or("name", ".*").or("ref", ".*").or("oneway", ".*"));

        // Filter the ways and their tags
        parser.filterWays();
        
        // Keep only nodes that belong to ways
        parser.setNodeFilter(new WayNodesFilter(osm.getWays()));
        
        // Does not keep any tag for nodes
        parser.setNodeTagMatcher(new NoneTagMatcher());
        
        // Filter nodes
        parser.filterNodes();
        
        SmartgovLezApplication.logger.info("Number of roads filtered : " + osm.getWays().size());
        SmartgovLezApplication.logger.info("Number of nodes filtered : " + osm.getNodes().size());
        
        // Custom object mapper to indent output
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        SmartgovLezApplication.logger.info("Writing filtered roads to " + new File(OsmRoadParser.class.getResource("ways.json").getFile()));
        parser.writeWays(new File(OsmRoadParser.class.getResource("ways.json").getFile()), mapper);
        
        SmartgovLezApplication.logger.info("Writing filtered nodes to " + new File(OsmRoadParser.class.getResource("nodes.json").getFile()));
        parser.writeNodes(new File(OsmRoadParser.class.getResource("nodes.json").getFile()), mapper);

	}
}
