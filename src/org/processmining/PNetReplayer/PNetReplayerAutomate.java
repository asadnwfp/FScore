package org.processmining.PNetReplayer;

import java.text.NumberFormat;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.connections.petrinets.PNRepResultAllRequiredParamConnection;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithILP;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.ui.PNReplayerUI;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

import nl.tue.astar.AStarException;

public class PNetReplayerAutomate {
	private Petrinet net;
	private XLog log;
	private PluginContext context;

	public PNetReplayerAutomate(PluginContext context, Petrinet net, XLog log) {
		this.net = net;
		this.log = log;
		this.context = context;
	}

	public PNRepResult getPNetRepResult() throws ConnectionCannotBeObtained, AStarException {
		System.out.println("PNetReplayer: getPNetReplayer()");
		// Getting the PNetConfigurations
		PNetConfiguration pNetConfiguration = new PNetConfiguration();
		IPNReplayAlgorithm selectedAlg;
		IPNReplayParameter parameters;
		TransEvClassMapping mapping;

		Object[] resultConfiguration = pNetConfiguration.getConfiguration((UIPluginContext) context, net, log);
		context.log("replay is performed. All parameters are set.");

		// get all parameters
		selectedAlg = (IPNReplayAlgorithm) resultConfiguration[PNetConfiguration.ALGORITHM];
		parameters = (IPNReplayParameter) resultConfiguration[PNetConfiguration.PARAMETERS];
		mapping = (TransEvClassMapping) resultConfiguration[PNetConfiguration.MAPPING];

		PNetConfiguration.run = false;

		PNRepResult replayRes = selectedAlg.replayLog(context, net, log, mapping, parameters);

		// add connection
		if (replayRes != null) {
			if (parameters.isCreatingConn()) {
				createConnections(context, net, log, mapping, selectedAlg, parameters, replayRes);
			}
		}

		context.getFutureResult(0).setLabel("Replay result - log " + XConceptExtension.instance().extractName(log)
				+ " on " + net.getLabel() + " using " + selectedAlg.toString());

		return replayRes;
	}

	protected void createConnections(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping,
			IPNReplayAlgorithm selectedAlg, IPNReplayParameter parameters, PNRepResult replayRes) {
		context.addConnection(new PNRepResultAllRequiredParamConnection("Connection between replay result, "
				+ XConceptExtension.instance().extractName(log) + ", and " + net.getLabel(), net, log, mapping,
				selectedAlg, parameters, replayRes));
	}

	public void setNet(Petrinet net) {
		this.net = net;
	}

}
