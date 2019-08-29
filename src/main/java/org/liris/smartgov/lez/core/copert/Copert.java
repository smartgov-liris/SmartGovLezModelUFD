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
	 * Returns the copertParameters according to the given
	 * 
	 * {@link org.liris.smartgov.lez.core.copert.fields.Pollutant Pollutant}.
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

		Map<String, String> finalLine = parameters.getSubTable().getLine(0);

		return getCopertParameters(finalLine);
	}
	
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
				Double.valueOf(copertLine.get("Alpha")),
				Double.valueOf(copertLine.get("Beta")),
				Double.valueOf(copertLine.get("Gamma")),
				Double.valueOf(copertLine.get("Delta")),
				Double.valueOf(copertLine.get("Epsilon")),
				Double.valueOf(copertLine.get("Zita")),
				Double.valueOf(copertLine.get("Hta")),
				Double.valueOf(copertLine.get(CopertHeader.MIN_SPEED.columnName())),
				Double.valueOf(copertLine.get(CopertHeader.MAX_SPEED.columnName()))
				);
	}
}
