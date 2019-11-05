package org.processmining.XLog;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.utils.ReusableMethods;

public class LogProperties {
	private XLog log;
	private PluginContext context;

	public LogProperties(PluginContext context, XLog log) {
		this.log = log;
		this.context = context;
	}

	public void properties() {
		ReusableMethods.printLogs("LogProperties: properties()");
		BasicLogInfo.log = log;
		BasicLogInfo.basicAttributes();
		BasicLogInfo.basicClassifiers();
		BasicLogInfo.basicEventAttributes();

	}
	
	
	
	
//	private void propertyXAttributeTimestamp() {
//		XAttributeTimestampImpl
//	}
	
	

}
