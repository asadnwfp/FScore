package org.processmining.PNetReplayer;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

public class PNetReplayerAutomate {
	private Petrinet net;
	private XLog log;
	private PluginContext context;

	public PNetReplayerAutomate(PluginContext context, Petrinet net, XLog log) {
		this.net = net;
		this.log = log;
		this.context = context;
	}

	public PNRepResult getPNetRepResult() {
		System.out.println("PNetReplayer: getPNetReplayer");
		// Getting the PNetConfigurations
		PNetConfiguration pNetConfiguration = new PNetConfiguration();
		Object[] resultConfiguration  = pNetConfiguration.getConfiguration((UIPluginContext) context, net, log);
		return null;
	}

	

}
