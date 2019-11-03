package org.processmining.ETConformance;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

public class ETConformanceAutomation {
	private Petrinet net;
	private XLog log;
	private PluginContext context;
	
	public ETConformanceAutomation(PluginContext context, Petrinet net, XLog log) {
		this.net = net;
		this.log = log;
		this.context = context;
	}

}
