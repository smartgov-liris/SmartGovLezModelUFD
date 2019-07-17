package com.smartgov.lez.core.copert.tableParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Recursive data structure designed to navigate easily in Copert parameters.
 * 
 * @author pbreugnot
 *
 */
public class CopertTree {
	
	private HashMap<String, CopertTree> children;
	private SubTable subTable;
	private String levelColumn; // Correspond to the first sub-table column name
	private String category;
	private CopertSelector path;
	private Random random;

	public CopertTree(String levelColumn, String category, SubTable subTable, CopertSelector path, Random random) {
		children = new HashMap<>();
		this.levelColumn = levelColumn;
		this.category = category;
		this.subTable = subTable;
		this.path = path;
		this.random = random;
	}
	
	/**
	 * Representation of the CopertTree level.
	 * Correspond to the first sub-table column name.
	 * 
	 * @return tree level
	 */
	public String getLevel() {
		return levelColumn;
	}
	
	public String getCategory() {
		return category;
	}
	
	public CopertSelector getPath() {
		return path;
	}
	
	/**
	 * If there is only one CopertTree child from there, returns this child.
	 * If multiple children are present, select a random child.
	 * Return null if there is no more available child.
	 * 
	 * @return a random child available from this node.
	 */
	public CopertTree select() {
		return select(random);
	}
	
	private CopertTree select(Random random) {
		return selectRandomChildFromKeySet(children.keySet(), random);
	}
	
	/**
	 * @return a Collection of all the available children
	 */
	public Collection<CopertTree> selectAll() {
		return children.values();
	}
	
	/**
	 * Tries to find a child that match the given regular expression.
	 * 
	 * If exactly one child matches, returns this child.
	 * If multiple children match, select a random child among them.
	 * Return null if no child matches.
	 * @param matcher regular expression selector
	 * @return A matching child
	 */
	public CopertTree select(String matcher) {
		if (matcher.equals("Random")) {
			return select();
		}
		ArrayList<String> matchingKeys = new ArrayList<>();
		for(String key : children.keySet()) {
			if (Pattern.matches(matcher, key)) {
				matchingKeys.add(key);
			}
		}
		if (matchingKeys.size() == 0) {
			String message =
					"No matching Copert value for matcher \"" + matcher + "\" at level \"" + levelColumn + "\""
					+ "\nAvailable sub-classes :\n";
			for(String subClass : children.keySet()) {
				message += (subClass + "\n");
			}
			message += "Current path : " + path;
			throw new CopertFieldNotFoundException(message);
		}
		return selectRandomChildFromKeySet(matchingKeys, random);
	}
	
	/**
	 * Returns all the children associated to a key that match the given regular expression.
	 * Can be used to aggregate parameters on sub-categories(e.g. : Articulated Heavy Trucks).
	 * 
	 * @param regExp regular expression selector
	 * @return matching children
	 */
	public Collection<CopertTree> selectAllRegExp(String regExp) {
		ArrayList<CopertTree> matchingTrees = new ArrayList<>();
		for(String key : children.keySet()) {
			if (Pattern.matches(regExp, key)) {
				matchingTrees.add(children.get(key));
			}
		}
		return matchingTrees;
	}
	
	private CopertTree selectRandomChildFromKeySet(Collection<String> keySet, Random random) {
		if (keySet.size() == 0) {
			return null;
		}
		if (keySet.size() == 1) {
			return children.get(keySet.iterator().next());
		}
		ArrayList<String> availableChildKeys = new ArrayList<>(keySet);
		String randomKey = availableChildKeys.get(random.nextInt(availableChildKeys.size()));
		return children.get(randomKey);
	}
	
	public void put(String value, CopertTree childValue) {
		children.put(value, childValue);
	}
	
	public SubTable getSubTable() {
		return subTable;
	}
	
	public HashMap<String, CopertTree> getChildren() {
		return children;
	}
	
	/**
	 * @return True if and only if the associated SubTable contains exactly one line.
	 */
	public boolean isSingleLine() {
		return subTable.get(levelColumn).size() == 1;
	}
	
	/**
	 * 
	 * @return Line count of the current subtable
	 */
	public int lineCount() {
		return subTable.get(levelColumn).size();
	}
}
