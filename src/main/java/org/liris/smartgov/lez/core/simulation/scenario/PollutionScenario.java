package org.liris.smartgov.lez.core.simulation.scenario;

import java.util.Random;

import org.liris.smartgov.lez.core.copert.tableParser.CopertParser;
import org.liris.smartgov.lez.core.environment.graph.PollutableOsmArcFactory;
import org.liris.smartgov.lez.core.environment.lez.Lez;
import org.liris.smartgov.simulator.core.environment.SmartGovContext;
import org.liris.smartgov.simulator.urban.osm.environment.graph.OsmNode;
import org.liris.smartgov.simulator.urban.osm.environment.graph.Road;
import org.liris.smartgov.simulator.urban.osm.scenario.GenericOsmScenario;


public abstract class PollutionScenario extends GenericOsmScenario<OsmNode, Road> {
	
	protected static Random random = new Random(240720191835l);
	private Lez lez;
	private CopertParser copertParser;
	
	public PollutionScenario(Lez lez) {
		super(OsmNode.class, Road.class, new PollutableOsmArcFactory(lez));
		this.lez = lez;
	}
	
	public PollutionScenario() {
		this(Lez.none());
	}
	
	public Lez getLez() {
		return lez;
	}
	
	protected CopertParser loadParser(SmartGovContext context) {
		copertParser = new CopertParser(context.getFileLoader().load("copert_table"), random);
		return copertParser;
	}
	
	public CopertParser getCopertParser() {
		return copertParser;
	}
		
}
