package org.processmining.XLog;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;

public class LogProperties {
	private XLog log;
	private PluginContext context;
	
	public LogProperties(PluginContext context, XLog log) {
		this.log = log;
		this.context = context;
	}

}
