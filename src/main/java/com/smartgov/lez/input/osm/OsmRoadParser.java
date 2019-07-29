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

/**
 * A pre-process used to extract json nodes and roads file from an input osm
 * file.
 */
public class OsmRoadParser {
	
	/**
	 * Highways types extracted from the osm file.
	 * 
	 * Check the <a href="https://wiki.openstreetmap.org/wiki/Key:highway">osm documentation</a>
	 * for more information.
	 * 
	 * <ul>
	 * <li> motorway </li>
	 * <li> trunk </li>
	 * <li> primary </li>
	 * <li> secondary </li>
	 * <li> tertiary </li>
	 * <li> unclassified </li>
	 * <li> residential </li>
	 * <li> motorway_link </li>
	 * <li> trunk_link </li>
	 * <li> primary_link </li>
	 * <li> secondary_link </li>
	 * <li> tertiary_link </li>
	 * <li> living_street </li>
	 * </ul>
	 */
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
			"tertiary_link",
			"living_street"
	};
	
	/**
	 * Parses the input osm file, and write results as json nodes and ways
	 * files.
	 *
	 * Extracts the {@link #highways} road types and associated nodes.
	 *
	 * @param args <i>osm_input_file nodes_output_file ways_output_file</i>
	 * <p>
	 * The osm input file must be in the OSM XML format.
	 * </p>
	 * @throws JAXBException if a case of a problem parsing the input osm file
	 * @throws IOException is case of a problem reading or writing an input / output file
	 */
	public static void main(String[] args) throws JAXBException, IOException {
		long beginTime = System.currentTimeMillis();
		OsmParser parser = new OsmParser();
		
		SmartgovLezApplication.logger.info("Parsing osm data from : " + new File(args[0]));
	    // Parse the test osm file
	    Osm osm = (Osm) parser.parse(new File(args[0]));
	    
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
        		// Considering only highways
        		new TagFilter(
        				highwaysTagMatcher
        		)
        		.or(
        			// Also service highways
        			new TagFilter(new BaseTagMatcher("highway", "service"))
        			.and(
        				// Only service highways with a "service" tag
        				new TagFilter(new BaseTagMatcher("service", ".*"))
        				.and(new TagFilter(
	        					// That is equal to "alley", "parking_aisle" or "driveway"
	        					new BaseTagMatcher("service", "alley")
	        					.or("service", "parking_aisle")
	        					.or("service", "driveway")
	        					)
	        				))
        				)
        		);
//	        		// And
//	        		.and(
//	        			// Ways with no service tag
//	        			TagFilter.not(new TagFilter(new BaseTagMatcher("service", ".*")))
//	        			// Or (if a service tag is present)
//	        			.or(new TagFilter(
//	        					// Ways that as are "alley", "parking_aisle" or "driveway" services
//	        					new BaseTagMatcher("service", "alley")
//	        					.or("service", "parking_aisle")
//	        					.or("service", "driveway")
//	        					)
//	        				)
//	        			)
//        		));
        
        // Keep highway, service, name and ref tags
        parser.setWayTagMatcher(
        		new BaseTagMatcher("highway", ".*")
        		.or("service", ".*")
        		.or("name", ".*")
        		.or("ref", ".*")
        		.or("oneway", ".*")
        		);

        SmartgovLezApplication.logger.info("Filtering ways...");
        long filterBeginTime = System.currentTimeMillis();
        // Filter the ways and their tags
        parser.filterWays();
        SmartgovLezApplication.logger.info("Ways filtered in " + (System.currentTimeMillis() - filterBeginTime) + "ms");
        // Keep only nodes that belong to ways
        parser.setNodeFilter(new WayNodesFilter(osm.getWays()));
        
        // Does not keep any tag for nodes
        parser.setNodeTagMatcher(new NoneTagMatcher());
        
        SmartgovLezApplication.logger.info("Filtering nodes...");
        filterBeginTime = System.currentTimeMillis();
        // Filter nodes
        parser.filterNodes();
        SmartgovLezApplication.logger.info("Nodes filtered in " + (System.currentTimeMillis() - filterBeginTime) + "ms");
        
        SmartgovLezApplication.logger.info("Number of filtered roads : " + osm.getWays().size());
        SmartgovLezApplication.logger.info("Number of filtered nodes : " + osm.getNodes().size());
        
        // Custom object mapper to indent output
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        SmartgovLezApplication.logger.info("Writing filtered roads to " + new File(args[2]));
        parser.writeWays(new File(args[2]), mapper);
        
        SmartgovLezApplication.logger.info("Writing filtered nodes to " + new File(args[1]));
        parser.writeNodes(new File(args[1]), mapper);
        
        SmartgovLezApplication.logger.info("Parsing end. Total process time : " + (System.currentTimeMillis() - beginTime) + "ms");

	}
}
