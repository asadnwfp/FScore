package org.processmining.XLog;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.utils.ReusableMethods;

public class LogVariance {
	
	private XLog log;

	public LogVariance(XLog log) {
		this.log = log;
	}
	
	public void showTraces() {
		ReusableMethods.printLogs("##################################################");
		ReusableMethods.printLogs("LogVariance: showTrace()");
		ReusableMethods.printLogs("##################################################");
		ReusableMethods.printLogs("Traces: " + log.size());
		for(XTrace trace : log) {
			ReusableMethods.printLogs("*****************  Trace: "+ log.indexOf(trace) +"  ********************");
			BasicLogInfo.basicAttributes(trace.getAttributes());

			
//			ReusableMethods.printLogs("NumberOfEvents: " + trace.size());
//			for(XEvent event : trace) {
//				ReusableMethods.printLogs("**************** Events "+ trace.indexOf(event) +" ***********************");
//				BasicLogInfo.basicAttributes(event.getAttributes());
//			}
		}
		
	}

}
