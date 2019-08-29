package org.liris.smartgov.lez.cli.tools;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
public class Roads {
	
	public static Logger logger = LogManager.getLogger(Roads.class);
	
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
	 * @throws ParseException in case of bad command line options
	 */
	public static void main(String[] args) throws JAXBException, IOException, ParseException {
		Options helpOpts = new Options();
		Option help = new Option("h", "help", false, "Displays this help message");
		helpOpts.addOption(help);
		
		
		Options fullOpts = new Options();
		fullOpts.addOption(help);
		Option osmFile = new Option("f", "osm-file", true, "Input OSM file");
		osmFile.setArgName("file");
		osmFile.setRequired(true);
		Option nodeOutput = new Option("n", "nodes-file", true, "JSON nodes output file");
		nodeOutput.setArgName("file");
		nodeOutput.setRequired(true);
		Option waysOutput = new Option("w", "ways-file", true, "JSON ways output file");
		waysOutput.setArgName("file");
		waysOutput.setRequired(true);
		fullOpts.addOption(osmFile);
		fullOpts.addOption(nodeOutput);
		fullOpts.addOption(waysOutput);
		
		if(args.length == 0) {
			printHelp(fullOpts);
			return;
		}

		CommandLineParser cmdParser = new DefaultParser();
		CommandLine helpCmd = cmdParser.parse(helpOpts, args, true);
		
		if(helpCmd.hasOption("h")) {
			printHelp(fullOpts);
			return;
		}
		
		cmdParser = new DefaultParser();
		CommandLine mainCmd = cmdParser.parse(fullOpts, args);
		
		long beginTime = System.currentTimeMillis();
		OsmParser parser = new OsmParser();
		
		logger.info("Parsing osm data from : " + new File(mainCmd.getOptionValue("f")));
	    // Parse the test osm file
	    Osm osm = (Osm) parser.parse(new File(mainCmd.getOptionValue("f")));
	    
	    logger.info("Nodes found : " + osm.getNodes().size());
	    logger.info("Ways found : " + osm.getWays().size());
	    
	    logger.info("Applying filters...");
	    
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
        
        // Keep highway, service, name and ref tags
        parser.setWayTagMatcher(
        		new BaseTagMatcher("highway", ".*")
        		.or("service", ".*")
        		.or("name", ".*")
        		.or("ref", ".*")
        		.or("oneway", ".*")
        		);

        logger.info("Filtering ways...");
        long filterBeginTime = System.currentTimeMillis();
        // Filter the ways and their tags
        parser.filterWays();
        logger.info("Ways filtered in " + (System.currentTimeMillis() - filterBeginTime) + "ms");
        // Keep only nodes that belong to ways
        parser.setNodeFilter(new WayNodesFilter(osm.getWays()));
        
        // Does not keep any tag for nodes
        parser.setNodeTagMatcher(new NoneTagMatcher());
        
        logger.info("Filtering nodes...");
        filterBeginTime = System.currentTimeMillis();
        // Filter nodes
        parser.filterNodes();
        logger.info("Nodes filtered in " + (System.currentTimeMillis() - filterBeginTime) + "ms");
        
        logger.info("Number of filtered roads : " + osm.getWays().size());
        logger.info("Number of filtered nodes : " + osm.getNodes().size());
        
        // Custom object mapper to indent output
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        File waysOutputFile = new File(mainCmd.getOptionValue("w"));
        File waysOutputDir = waysOutputFile.getParentFile();
        if(!waysOutputDir.exists())
        	waysOutputDir.mkdirs();
        logger.info("Writing filtered roads to " + waysOutputFile);
        parser.writeWays(waysOutputFile, mapper);
        
        File nodeOutputFile = new File(mainCmd.getOptionValue("n"));
        File nodeOutputDir = nodeOutputFile.getParentFile();
        if(!nodeOutputDir.exists())
        	nodeOutputDir.mkdirs();
        logger.info("Writing filtered nodes to " + nodeOutputFile);
        parser.writeNodes(nodeOutputFile, mapper);
        
        logger.info("Parsing end. Total process time : " + (System.currentTimeMillis() - beginTime) + "ms");

	}
	
	private static void printHelp(Options opts) {
		String header = "\nBuild JSON nodes and ways input file from the specified osm node.";
		String footer ="\n Process :\n"
				+ "- Loads the input .osm file.\n"
				+ "- Filters ways to keep required highways.\n"
				+ "- Filters tags to keep 'highway', 'name', 'ref', 'oneway' and 'service' tags.\n"
				+ "- Writes the output nodes and ways files to [nodes-file] and [ways-file]\n";
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("smartgovlez roads", header, opts, footer, true);
	}
}
