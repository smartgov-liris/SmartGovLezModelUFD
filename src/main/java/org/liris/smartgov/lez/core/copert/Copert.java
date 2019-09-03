package org.liris.smartgov.lez.core.copert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.liris.smartgov.lez.core.copert.fields.CopertField;
import org.liris.smartgov.lez.core.copert.fields.Load;
import org.liris.smartgov.lez.core.copert.fields.Mode;
import org.liris.smartgov.lez.core.copert.fields.Pollutant;
import org.liris.smartgov.lez.core.copert.fields.RoadSlope;
import org.liris.smartgov.lez.core.copert.tableParser.CopertFieldNotFoundException;
import org.liris.smartgov.lez.core.copert.tableParser.CopertHeader;
import org.liris.smartgov.lez.core.copert.tableParser.CopertTree;

/**
 * Utility class to store {@link org.liris.smartgov.lez.core.copert.CopertParameters CopertParameters} for
 * each pollutant types.
 * 
 * @author pbreugnot
 *
 */
public class Copert {

	private HashMap<Pollutant, CopertTree> copertParameters;
	
	/**
	 * Build copertParameters for each {@link org.liris.smartgov.lez.core.copert.fields.Pollutant Pollutant} 
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
			catch(CopertFieldNotFoundException e) {
				pollutantSubTree = null;
			}
			copertParameters.put(
					pollutant,
					pollutantSubTree
					);
		}
	}
	
	/**
	 * Returns the copertParameters according to the given Pollutant.
	 * Work only if the parameters does not depend on Mode, Road Slope, or Load.
	 * (ex : NOx for light weight vehicles)
	 * 
	 * @param pollutant Pollutant
	 * @return Copert parameters
	 * @throws IllegalStateException if the COPERT parameters actually depend on other variables.
	 */
	public CopertParameters getCopertParameters(Pollutant pollutant) {
		if(!copertParameters.get(pollutant).isSingleLine()) {
			throw new IllegalStateException("Multiple entries correspond to the pollutant " + pollutant);
		}
		CopertTree finalTree = copertParameters.get(pollutant);
		Map<String, String> finalLine = finalTree.getSubTable().getLine();

		return getCopertParameters(finalLine);
	}
	
	/**
	 * Returns the copertParameters according to the given
	 * Pollutant and Mode.
	 * Work only if the parameters does not depend Road Slope or Load.
	 * (ex : CH4 for light weight vehicles)
	 * 
	 * @param pollutant Pollutant
	 * @param mode Circulation mode
	 * @return Copert parameters
	 * @throws IllegalStateException if the COPERT parameters actually depend on other variables.
	 */
	public CopertParameters getCopertParameters(Pollutant pollutant, Mode mode) {
		CopertTree selection = copertParameters.get(pollutant).select(mode.matcher());
		if(!selection.isSingleLine()) {
			throw new IllegalStateException("Multiple entries correspond to mode " + mode + " for pollutant " + pollutant);
		}
		
		Map<String, String> finalLine = selection.getSubTable().getLine();

		return getCopertParameters(finalLine);
	}
	
	/**
	 * Returns the copertParameters according to the given
	 * Pollutant, Road slope and load.
	 * Work only if the parameters does not depend on mode.
	 * (ex : CO, NOx, VOC... for heavy duty trucks)
	 * 
	 * @param pollutant Pollutant
	 * @param roadSlope Road slope
	 * @param load Vehicle load
	 * @return Copert parameters
	 * @throws IllegalStateException if the COPERT parameters actually depend on other variables.
	 */
	public CopertParameters getCopertParameters(Pollutant pollutant, RoadSlope roadSlope, Load load) {
		CopertTree parameters = copertParameters.get(pollutant)
				.select() // Select the only mode that should be available (none...)
				.select(roadSlope.matcher()) // Road slope, doesn't seem to be used
				.select(load.matcher());

		if(!parameters.isSingleLine()) {
			throw new IllegalStateException(
					"Multiple entries correspond to roadSlope " + roadSlope
					+ " and load " + load 
					+ " for pollutant " + pollutant
					);
		}
		
		Map<String, String> finalLine = parameters.getSubTable().getLine(0);

		return getCopertParameters(finalLine);
	}
	
	/**
	 * Returns the copertParameters according to the given
	 * Pollutant, Mode, Road slope and load.
	 * 
	 * Using this function is required when the parameters depend on
	 * all parameters.
	 * (ex : PM Exhaust for heavy duty trucks)
	 * 
	 * @param pollutant Pollutant
	 * @param mode circulation mode
	 * @param roadSlope Road slope
	 * @param load Vehicle load
	 * @return Copert parameters
	 */
	public CopertParameters getCopertParameters(Pollutant pollutant, Mode mode, RoadSlope roadSlope, Load load) {
		CopertTree parameters = copertParameters.get(pollutant);
		if(parameters == null) {
			return null;
		}
		List<CopertField> selectors = new ArrayList<>();
		selectors.add(mode);
		selectors.add(roadSlope);
		selectors.add(load);
		Iterator<CopertField> selectorsIterator = selectors.iterator();
		while(!parameters.isSingleLine() && selectorsIterator.hasNext()) {
			CopertTree nextLevel;
			try {
				nextLevel = parameters.select(selectorsIterator.next().matcher());
			}
			catch (CopertFieldNotFoundException e) {
				nextLevel = parameters.select();
			}
			parameters = nextLevel;
		}
		
		// If really we still have multiple lines there, the first line will be selected.
		// But this should never occur.
		Map<String, String> finalLine = parameters.getSubTable().getLine(0);

		return getCopertParameters(finalLine);
	}
	
	private CopertParameters getCopertParameters(Map<String, String> copertLine) {
		return new CopertParameters(
				Double.valueOf(copertLine.get(CopertHeader.ALPHA.columnName())),
				Double.valueOf(copertLine.get(CopertHeader.BETA.columnName())),
				Double.valueOf(copertLine.get(CopertHeader.GAMMA.columnName())),
				Double.valueOf(copertLine.get(CopertHeader.DELTA.columnName())),
				Double.valueOf(copertLine.get(CopertHeader.EPSILON.columnName())),
				Double.valueOf(copertLine.get(CopertHeader.ZITA.columnName())),
				Double.valueOf(copertLine.get(CopertHeader.HTA.columnName())),
				Double.valueOf(copertLine.get(CopertHeader.REDUCTION_FACTOR.columnName())),
				Double.valueOf(copertLine.get(CopertHeader.MIN_SPEED.columnName())),
				Double.valueOf(copertLine.get(CopertHeader.MAX_SPEED.columnName()))
				);
	}
}
