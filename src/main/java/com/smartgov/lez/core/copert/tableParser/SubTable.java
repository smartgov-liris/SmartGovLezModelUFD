package com.smartgov.lez.core.copert.tableParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Utility class that represents a sub-part of a Copert parameters table.
 * Data are stored as an HashMap :
 * <ul>
 *   <li> keys : column names </li>
 *   <li> values : column data, stored as a String ArrayList </li>
 * </ul>
 * @author pbreugnot
 *
 */
public class SubTable extends HashMap<String, ArrayList<String>> {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Mean value of the column. Raise an exception if a field data is not a Double.
	 * @param field column name
	 * @return column mean value
	 */
	public Double mean(String field) {
		ArrayList<String> column = get(field);
		Double mean = 0d;
		for (String value : column) {
			if (!value.isEmpty()) {
				mean += Double.valueOf(value);
			}
		}
		return mean / column.size();
	}
	
	/**
	 * Maximum value of the column. Raise an exception if a field data is not a Double.
	 * @param field column name
	 * @return column maximum value
	 */
	public Double max(String field) {
		ArrayList<String> column = get(field);
		Double max = - Double.MAX_VALUE;
		for (String value : column) {
			if (Double.valueOf(value) > max) {
				max = Double.valueOf(value);
			}
		}
		return max;
	}
	
	/**
	 * Minimum value of the column. Raise an exception if a field data is not a Double.
	 * @param field column name
	 * @return column minimum value
	 */
	public Double min(String field) {
		ArrayList<String> column = get(field);
		Double min = Double.MAX_VALUE;
		for (String value : column) {
			if (Double.valueOf(value) < min) {
				min = Double.valueOf(value);
			}
		}
		return min;
	}
	
	/**
	 * Compute the mean value of each column and return results as an HashMap.
	 * If a column contains a value that is not a Double, all the column is ignored,
	 * but valid columns are still returned.
	 * <ul>
	 *   <li> keys : column names </li>
 	 *   <li> values : column mean value </li>
 	 * </ul>
 	 * WARNING : Should be used with extra caution, because aggregating Copert parameters
	 * between different pollutant doesn't make any sense.
	 * TODO : Fix this programmatically.
	 * 
	 * @return Sub table mean values
	 */
	public HashMap<String, Double> mean() {
		HashMap<String, Double> mean = new HashMap<>();
		for (String field : doubleFieldsKeySet()) {
			mean.put(field, mean(field));
		}
		return mean;
	}
	
	/**
	 * Compute the maximum value of each column and return results as an HashMap.
	 * If a column contains a value that is not a Double, all the column is ignored,
	 * but valid columns are still returned.
	 * <ul>
	 *   <li> keys : column names </li>
 	 *   <li> values : column maximum value </li>
 	 * </ul>
 	 * WARNING : Should be used with extra caution, because aggregating Copert parameters
	 * between different pollutant doesn't make any sense.
	 * TODO : Fix this programmatically.
	 * 
	 * @return Sub table maximum values
	 */
	public HashMap<String, Double> max() {
		HashMap<String, Double> mean = new HashMap<>();
		for (String field : doubleFieldsKeySet()) {
			mean.put(field, max(field));
		}
		return mean;
	}
	
	/**
	 * Compute the minimum value of each column and return results as an HashMap.
	 * If a column contains a value that is not a Double, all the column is ignored,
	 * but valid columns are still returned.
	 * <ul>
	 *   <li> keys : column names </li>
 	 *   <li> values : column minimum value </li>
 	 * </ul>
 	 * WARNING : Should be used with extra caution, because aggregating Copert parameters
	 * between different pollutant doesn't make any sense.
	 * TODO : Fix this programmatically.
	 * 
	 * @return Sub table minimum values
	 */
	public HashMap<String, Double> min() {
		HashMap<String, Double> mean = new HashMap<>();
		for (String field : doubleFieldsKeySet()) {
			mean.put(field, min(field));
		}
		return mean;
	}
	
	private Set<String> doubleFieldsKeySet() {
		// Validating string extracted from the Java documentation.
		// https://docs.oracle.com/javase/8/docs/api/java/lang/Double.html#valueOf-java.lang.String-
		
		final String Digits     = "(\\p{Digit}+)";
		final String HexDigits  = "(\\p{XDigit}+)";
		// an exponent is 'e' or 'E' followed by an optionally
		// signed decimal integer.
		final String Exp        = "[eE][+-]?"+Digits;
		final String fpRegex    =
		    ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
		     "[+-]?(" + // Optional sign character
		     "NaN|" +           // "NaN" string
		     "Infinity|" +      // "Infinity" string

		     // A decimal floating-point string representing a finite positive
		     // number without a leading sign has at most five basic pieces:
		     // Digits . Digits ExponentPart FloatTypeSuffix
		     //
		     // Since this method allows integer-only strings as input
		     // in addition to strings of floating-point literals, the
		     // two sub-patterns below are simplifications of the grammar
		     // productions from section 3.10.2 of
		     // The Java Language Specification.

		     // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
		     "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

		     // . Digits ExponentPart_opt FloatTypeSuffix_opt
		     "(\\.("+Digits+")("+Exp+")?)|"+

		     // Hexadecimal strings
		     "((" +
		     // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
		     "(0[xX]" + HexDigits + "(\\.)?)|" +

		     // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
		     "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

		     ")[pP][+-]?" + Digits + "))" +
		     "[fFdD]?))" +
		     "[\\x00-\\x20]*");// Optional trailing "whitespace"
		
		HashSet<String> doubleFieldKeys = new HashSet<>();
		for (String key : keySet()) {
			Boolean doubleField = true;
			for (String value : get(key)) {
				if (!value.isEmpty() && !Pattern.matches(fpRegex, value)) {
					doubleField = false;
					break;
				}
			}
			if (doubleField) {
				doubleFieldKeys.add(key);
			}
		}
		return doubleFieldKeys;
	}

}
