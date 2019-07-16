package com.smartgov.lez.core.agent.establishment.input;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class EstablishmentLoaderTest {

	@Test
	public void loadEstablishment() throws JsonParseException, JsonMappingException, IOException {
		EstablishmentLoader.loadEstablishment(new File(this.getClass().getResource("establishments.json").getFile()));
	}
}
