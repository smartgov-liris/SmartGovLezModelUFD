package org.liris.smartgov.lez.core.copert.inputParser;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CopertInputReader {

	public static CopertProfile parseInputFile(File inputFile) {
		CopertProfile copertInput = null;
		try {
			copertInput = new ObjectMapper().readValue(
					inputFile,
					CopertProfile.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return copertInput;
	}
}
