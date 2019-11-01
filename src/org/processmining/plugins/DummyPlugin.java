package org.processmining.plugins;

import org.deckfour.xes.model.XLog;
import org.processmining.PNetReplayer.PNetReplayerAutomate;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMf;
import org.processmining.plugins.InductiveMiner.plugins.IMPetriNet;
import org.processmining.plugins.inductiveminer.IM;

public class DummyPlugin {
	@Plugin(
			name = "DummyPlugin",
			level = PluginLevel.Local,
			returnLabels = {"Petrinet"},
			returnTypes = {Petrinet.class},
			parameterLabels = { "Log"},
			userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Saad Ahmed", email = "saad.ahmed@rwth-aachen.de")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public Object[] dummyPlugin(UIPluginContext context, XLog log) {
		System.out.println("DummyPlugin: Start");
		
		MiningParametersIMf parametersInductive = new MiningParametersIMf();
		parametersInductive.setNoiseThreshold(0.2f);
		Object[] resultObjects = IMPetriNet.minePetriNet(context, log, parametersInductive);
		Petrinet net = (Petrinet) resultObjects[0]; 
		PNetReplayerAutomate pNetReplayer = new PNetReplayerAutomate(context, net, log);
		pNetReplayer.getPNetRepResult();

		System.out.println("DummyPlugin: End");
		context.getFutureResult(0).cancel(true);
		context.getFutureResult(1).cancel(true);
		return null;
		
	}

}
