package com.smartgov.lez.core.copert;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.smartgov.lez.core.copert.fields.Load;
import com.smartgov.lez.core.copert.fields.Mode;
import com.smartgov.lez.core.copert.fields.Pollutant;
import com.smartgov.lez.core.copert.fields.RoadSlope;
import com.smartgov.lez.core.copert.tableParser.CopertClassDoesNotExistException;
import com.smartgov.lez.core.copert.tableParser.CopertHeader;
import com.smartgov.lez.core.copert.tableParser.CopertTree;

/**
 * Utility class to store {@link com.smartgov.lez.core.copert.CopertParameters CopertParameters} for
 * each pollutant types.
 * 
 * @author pbreugnot
 *
 */
public class Copert {

	private HashMap<Pollutant, CopertTree> copertParameters;
	
	/**
	 * Build copertParameters for each {@link com.smartgov.lez.core.copert.fields.Pollutant Pollutant} 
	 * according to the given CopertTree.
	 * If at some point in the corresponding query no sub-classes is found, the copertParameters will be
	 * set to null.
	 * 
	 * @param completeTree a copertTree at the pollutant level
	 */
	public Copert(CopertTree completeTree) {
		if(!completeTree.getLevel().equals(CopertHeader.POLLUTANT.columnName())) {
			throw new IllegalArgumentException("The specified tree level must be pollutant. Current is " + completeTree.getLevel());
		}
		copertParameters = new HashMap<>();
		
		for(Pollutant pollutant : Pollutant.values()) {
			CopertTree pollutantSubTree;
			try {
				pollutantSubTree = completeTree.select(pollutant.matcher());
			}
			catch(CopertClassDoesNotExistException e) {
				pollutantSubTree = null;
			}
			copertParameters.put(
					pollutant,
					pollutantSubTree
					);
		}
	}
	
	/**
	 * Returns the copertParameters according to the given
	 * 
	 * {@link com.smartgov.lez.core.copert.fields.Pollutant Pollutant}.
	 * @param pollutant Pollutant
	 * @return Copert parameters
	 */
	public CopertParameters getCopertParameters(Pollutant pollutant) {
		if(!copertParameters.get(pollutant).isSingleLine()) {
			throw new IllegalStateException("Multiple entries correspond to the given pollutant");
		}
		CopertTree finalTree = copertParameters.get(pollutant);
		Map<String, String> finalLine = finalTree.getSubTable().getLine();

		return getCopertParameters(finalLine);
	}
	
	public CopertParameters getCopertParameters(Pollutant pollutant, Mode mode) {
		CopertTree selection = copertParameters.get(pollutant).select(mode.matcher());
		if(!selection.isSingleLine()) {
			throw new IllegalStateException("Multiple entries correspond to mode " + mode + " for pollutant " + pollutant);
		}
		
		Map<String, String> finalLine = selection.getSubTable().getLine();

		return getCopertParameters(finalLine);
	}
	
	public CopertParameters getCopertParameters(Pollutant pollutant, RoadSlope roadSlope, Load load) {
		CopertTree parameters = copertParameters.get(pollutant)
				.select() // Select the only mode that should be available (none...)
				.select(roadSlope.matcher()) // Road slope, doesn't seem to be used
				.select(load.matcher());
//		Set<String> minSpeeds = minSpeedTree.getChildren().keySet();
//		// Looking for a min speed
//		String globalMinSpeed = "1000";
//		String selectedMinSpeed = "-1";
//		for(String minSpeed : minSpeeds) {
//			if(Double.valueOf(minSpeed) < Double.valueOf(globalMinSpeed)) {
//				// Globally available min speed search
//				globalMinSpeed = minSpeed;
//			}
//			if (speed >= Double.valueOf(minSpeed)) {
//				// Potentially acceptable min speed
//				if (Double.valueOf(selectedMinSpeed) < Double.valueOf(minSpeed)) {
//					// Maximize the min speed to fit the best speed range
//					selectedMinSpeed = minSpeed;
//				}
//			}
//		}
//		if(selectedMinSpeed.equals("1000")) {
//			// speed is smaller than the min speed available
//			selectedMinSpeed = globalMinSpeed;
//		}
//		
//		CopertTree maxSpeedTree = minSpeedTree.select(selectedMinSpeed);
//		if(maxSpeedTree.isSingleLine()) {
//			/* Only one max speed possible
//			 * Notice that if the Copert table were well constructed,
//			 * this would always be the case.
//			 * But we have some cases such as :
//			 *  - minSpeed : 0, maxSpeed : 5
//			 *  - minSpeed : 0, maxSpeed : 12
//			 * for the same category...
//			 */
//			Map<String, String> finalLine = maxSpeedTree.getSubTable().getLine();
//
//			return getCopertParameters(finalLine);
//		}
//		
//		Set<String> maxSpeeds = maxSpeedTree.getChildren().keySet();
//		// Looking for a max speed
//		String globalMaxSpeed = "-1";
//		String selectedMaxSpeed = "1000";
//		for(String maxSpeed : maxSpeeds) {
//			if(Double.valueOf(maxSpeed) > Double.valueOf(globalMaxSpeed)) {
//				// Globally available max speed search
//				globalMaxSpeed = maxSpeed;
//			}
//			if (speed < Double.valueOf(maxSpeed)) {
//				// Potentially acceptable max speed
//				if (Double.valueOf(selectedMaxSpeed) > Double.valueOf(maxSpeed)) {
//					// Minimize the max speed to fit the best speed range
//					selectedMaxSpeed = maxSpeed;
//				}
//			}
//		}
//		if(selectedMaxSpeed.equals("-1")) {
//			// Speed is bigger than the max speed available
//			selectedMaxSpeed = globalMaxSpeed;
//		}
//		
//		CopertTree finalTree = maxSpeedTree.select(selectedMaxSpeed);
//
//		/*
//		 * Copert is REALLY not kind.
//		 * Sometime, even at this point, there is still several lines.
//		 * It occurs in such situations (especially for rigid heavy truck, and load of 2, 4, 6):
//		 *  - minSpeed : 0, maxSpeed : 5, params: some params...
//		 *  - minSpeed : 0, maxSpeed : 5, params: some other params...
//		 * I have no idea of what does that mean.
//		 * The characterics are identical, but with different Copert
//		 * parameters.
//		 * So, to fix this, we always select the first remaining line.
//		 */
		Map<String, String> finalLine = parameters.getSubTable().getLine(0);

		return getCopertParameters(finalLine);
	}
	
	private CopertParameters getCopertParameters(Map<String, String> copertLine) {
		return new CopertParameters(
				Double.valueOf(copertLine.get("Alpha")),
				Double.valueOf(copertLine.get("Beta")),
				Double.valueOf(copertLine.get("Gamma")),
				Double.valueOf(copertLine.get("Delta")),
				Double.valueOf(copertLine.get("Epsilon")),
				Double.valueOf(copertLine.get("Zita")),
				Double.valueOf(copertLine.get("Hta"))
				);
	}
}
