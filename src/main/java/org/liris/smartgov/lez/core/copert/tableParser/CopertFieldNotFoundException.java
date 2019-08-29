package org.liris.smartgov.lez.core.copert.tableParser;

import java.util.NoSuchElementException;

/**
 * Exception thrown when a {@link org.liris.smartgov.lez.core.copert.tableParser.CopertTree#select() select()}
 * operation on a {@link org.liris.smartgov.lez.core.copert.tableParser.CopertTree CopertTree}
 * doesn't find anything.
 * 
 * @author pbreugnot
 *
 */
public class CopertFieldNotFoundException extends NoSuchElementException{

	private static final long serialVersionUID = 1L;
	
	public CopertFieldNotFoundException(String message) {
		super(message);
	}

}
