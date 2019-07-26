package com.smartgov.lez.input.establishment;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

import com.smartgov.lez.core.agent.establishment.Establishment;
import com.smartgov.lez.core.copert.tableParser.CopertParser;

public class LoadEstablishments {
	
	public static final String defaultCopertTable = ".." + File.separator + "copert" + File.separator + "Hot_Emissions_Parameters_France.csv";
	public static final String defaultCopertProfiles = "fleetProfiles.json";

	public static void main(String[] args) {
		if(args.length == 0)
			throw new IllegalArgumentException("You must at least specify an establishment file.");

		File copertTable;
		if(args.length > 1)
			copertTable = new File(args[1]);
		else
			copertTable = new File(LoadEstablishments.class.getResource(defaultCopertTable).getFile());
		System.out.println("Loading Copert table from " + copertTable);
		CopertParser parser = new CopertParser(copertTable, new Random(170720191337l));
		
		File fleetProfiles;
		if(args.length > 2)
			fleetProfiles = new File(args[2]);
		else
			fleetProfiles = new File(LoadEstablishments.class.getResource(defaultCopertProfiles).getFile());
		
		System.out.println("Loading fleet profiles table from " + fleetProfiles);
		try {
			System.out.println("Loading establishments from " + new File(args[0]));
			Map<String, Establishment> establishments = EstablishmentLoader.loadEstablishments(
					new File(args[0]),
					fleetProfiles,
					parser
					);
			System.out.println("Number of establishments loaded : " + establishments.size());
			
			System.out.println("Computing bounding box...");
			Double[] box = boundingBox(establishments.values());
			System.out.println("Top : " + box[0]);
			System.out.println("Left : " + box[1]);
			System.out.println("Bottom : " + box[2]);
			System.out.println("Right : " + box[3]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Double[] boundingBox(Collection<Establishment> establishments) {
		Double minX = Double.MAX_VALUE;
		Double minY = Double.MAX_VALUE;
		Double maxX = -Double.MAX_VALUE;
		Double maxY = -Double.MAX_VALUE;
		
		for(Establishment establishment : establishments) {
			if(establishment.getLocation().lat > maxY) {
				maxY = establishment.getLocation().lat;
			}
			if(establishment.getLocation().lat < minY) {
				minY = establishment.getLocation().lat;
			}
			if(establishment.getLocation().lon > maxX) {
				maxX = establishment.getLocation().lon;
			}
			if(establishment.getLocation().lon < minX) {
				minX = establishment.getLocation().lon;
			}
		}
		return new Double[]{maxY, minX, minY, maxX};
	}
}
