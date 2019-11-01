package org.processmining.plugins;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

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
	public Petrinet dummyPlugin(UIPluginContext context, XLog log) {
		System.out.println("DummyPlugin: Working");
		context.getFutureResult(0).cancel(true);
		return null;
		
	}

}
