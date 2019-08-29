package org.liris.smartgov.lez.input.establishment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.liris.smartgov.lez.core.agent.establishment.ST8;
import org.liris.smartgov.lez.core.copert.inputParser.CopertProfile;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@JsonDeserialize(using = St8FleetProfiles.FleetProfilesDeserializer.class)
public class St8FleetProfiles extends HashMap<ST8, CopertProfile> {

	private static final long serialVersionUID = 1L;

	public static class FleetProfilesDeserializer extends StdDeserializer<St8FleetProfiles> {

		private static final long serialVersionUID = 1L;

		public FleetProfilesDeserializer() {
			this(null);
		}
		
		protected FleetProfilesDeserializer(Class<?> vc) {
			super(vc);
		}

		@Override
		public St8FleetProfiles deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			St8FleetProfiles profileMap = new St8FleetProfiles();
			List<ST8> st8toDeserialize = new ArrayList<>();
			for(ST8 st8 : ST8.values()) {
				st8toDeserialize.add(st8);
			}
			
			JsonNode rootNode = p.getCodec().readTree(p);
			for(ST8 st8 : ST8.values()) {
				if(rootNode.has(st8.toString())) {
					/*
					 * Deserialize profiles specified by ST8 categories
					 */
					profileMap.put(
							st8,
							rootNode.get(st8.toString()).traverse(p.getCodec()).readValueAs(CopertProfile.class)
							);
					st8toDeserialize.remove(st8);
				}
			}
			if(!st8toDeserialize.isEmpty()) {
				if(rootNode.has("default")) {
					/*
					 * Use the default profiles for other ST8 categories.
					 * Only a "default" profile might be specified, without
					 * any ST8.
					 */
					CopertProfile defaultProfile = rootNode.get("default").traverse(p.getCodec()).readValueAs(CopertProfile.class);
					for(ST8 st8 : st8toDeserialize) {
						profileMap.put(
								st8,
								defaultProfile
								);
					}
				}
				else {
					throw new IllegalArgumentException("A \"default\" profile must be specified if not all ST8 are specified explicitely.");
				}
			}
			return profileMap;
		}

	}
}
