package com.smartgov.lez.core.copert.tableParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.smartgov.lez.core.copert.CopertParameters;
import com.smartgov.lez.core.copert.fields.CopertField;
import com.smartgov.lez.core.copert.fields.Pollutant;

/**
 * Utility class used to parse a Copert table.
 * 
 * @author pbreugnot
 *
 */
public class CopertParser {
	
	private CopertTree copertTree;
	
	/**
	 * Loads and parse a CopertTree from the input table.
	 * 
	 * @param copertParametersFile copert table file path
	 */
	public CopertParser(File copertParametersFile) {
		copertTree  = parseFile(copertParametersFile);
	}
	
	/**
	 * @return Root CopertTree
	 */
	public CopertTree getCopertTree() {
		return copertTree;
	}
	
	/**
	 * Find copert parameters for the given parameters.
	 * If several entries correspond to the given parameters
	 * (e.g. : CH4 emissions mode, different loads or speed for some vehicles...)
	 * the parameters are aggregated using a mean value.
	 * 
	 * 
	 * 
	 * @param vehicleClass Category
	 * @param fuel Fuel
	 * @param vehicleSegment Segment
	 * @param norm Euro
	 * @param pollutant Pollutant
	 * @return Copert parameters
	 */
	public static CopertParameters copertParameters(
			CopertTree copertTree,
			Pollutant pollutant) {
		
		CopertTree pollutionTree = null;
		try {
			pollutionTree = copertTree
					.select(pollutant.matcher()); // "Pollutant"
		}
		catch (CopertClassDoesNotExistException e) {
			return null;	
		}
		
		// TODO: Mean value is irrelevant with copert parameters
		HashMap<String, Double> parameters = pollutionTree
			.getSubTable().mean(); // We should have reach the parameters.
								   // We apply the mean operation, it case there is still multiple entries.
		
		return new CopertParameters(
				parameters.get("Alpha"),
				parameters.get("Beta"),
				parameters.get("Gamma"),
				parameters.get("Delta"),
				parameters.get("Epsilon"),
				parameters.get("Zita"),
				parameters.get("Hta")
				);
	}
	
	private CopertTree parseFile(File file) {
		// Original table
		SubTable csv = new SubTable();
		
		try {
			Reader in = new FileReader(file);
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
			Iterator<CSVRecord> recordsIterator = records.iterator();
			
			// Reg exp used to clean the header from any space at the beginning and the end of the header.
			// This is necessary, because the matching column names of the CopertHeader enum
			// values don't includes those characters.
			// Will also remove quotes.
			// To ignore :
			// ^[\\W&&[^-><]]? the eventual first non word character (e.g. : a special control character as the beginning
			// of the file. Such character ha been observed at the beginning of the "Category" header).
			// But doesn't match the non-word character "-", that represents the minus sign of a number at the beginning of a field.
			// Also dosn't match < and > that can be used to specify vehicle categories (e.g. : "<= 3.5 t")
			//
			// To catch :
			// (.*?) (non-greedy) : Capture the actual column name, including words, spaces, non words characters...
			//
			// To ignore again :
			// [\\W&&[^\\]]]*$ : ignores any non-word character at the end. But keeps right brackets,
			// because they can be used to specify units. (e.g. : "Min Speed [km/h]")
			final Pattern pattern = Pattern.compile("^[\\W&&[^-><]]*(.*?)[\\W&&[^\\]]]*$");
			CSVRecord headers = recordsIterator.next();
			ArrayList<String> cleanHeaders = new ArrayList<String>();
			for(String header : headers) {
				Matcher m = pattern.matcher(header);
				m.matches();
				String cleanHeader = m.group(1);
				cleanHeaders.add(cleanHeader);
				csv.put(cleanHeader, new ArrayList<>());
			}
			
			while(recordsIterator.hasNext()) {
				CSVRecord record = recordsIterator.next();
				for(int i = 0; i < cleanHeaders.size(); i++) {
					Matcher m = pattern.matcher(record.get(i));
					m.matches();
					csv.get(cleanHeaders.get(i)).add(
							m.group(1)
							.replace(",", ".") // Replace comma with point, because in the original table decimal values are represented with commas.
							);
				}
			}

			return parseSubTable(cleanHeaders, "", csv, new CopertSelector());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private CopertTree parseSubTable(ArrayList<String> currentColumns, String superCategory, SubTable table, CopertSelector currentPath) {
		String currentColumnName = currentColumns.get(0);
		CopertTree subTree = new CopertTree(currentColumnName, superCategory, table, currentPath);
		if (subTree.isSingleLine()) {
			return subTree;
		}
		
		// We consider the first column.
		// Following operations will consist in removing this column and
		// extract subtables.
		ArrayList<String> currentColumn = table.get(currentColumnName);
		
		// The sub-table key set correspond to the current table keyset,
		// minus the column we will parse in this iteration.
		ArrayList<String> subColumns = new ArrayList<>();
		subColumns.addAll(currentColumns);
		subColumns.remove(currentColumnName);
		
		// New entries for the current column
		HashSet<String> newEntries = new HashSet<String>(currentColumn);

		// Initialize new subtables
		HashMap<String, SubTable> newSubTables = new HashMap<>();
		for (String uniqueSubTableEntry : newEntries) {
			// subTable initialization
			SubTable subTable = new SubTable();
			for(String subColumn : subColumns) {
				// Sub columns
				subTable.put(subColumn, new ArrayList<>());
			}
			
			// Add the new subtables to the current subtables set
			newSubTables.put(uniqueSubTableEntry, subTable);
		}
		
		// Now, we iterate over all the current column entries, and we add each sub-entry to
		// the corresponding sub-table.
			
		for (int i = 0; i < currentColumn.size(); i++) {
			// For the given key, we retrieve all the subEntries to build a new subtable
			for(String subColumn : subColumns) {
				newSubTables.get(currentColumn.get(i)) // Sub-table to consider, according to the current column key
					.get(subColumn) //sub-table column to consider
					.add(
						table.get(subColumn).get(i) // Corresponding value in the original table
						);
			}
		}

		CopertHeader header = CopertHeader.getValue(currentColumnName);
		for(String newEntry : newEntries) {
			ArrayList<String> nextColumns = new ArrayList<>();
			nextColumns.addAll(subColumns); // Recreated every iteration to avoid concurrent modification
			CopertSelector childPath = new CopertSelector(); // Path to the current child
			childPath.putAll(currentPath);
			if(header != null) {
				// If the current category correspond to a CopertHeader, a.k.a a CopertSelector field,
				// We add it to the current path.
				CopertField selectedEntry = CopertField.getValue(header, newEntry);
				childPath.put(header, selectedEntry);
			}
			subTree.put(newEntry, parseSubTable(nextColumns, newEntry, newSubTables.get(newEntry), childPath));
		}
		return subTree;
	}

}
