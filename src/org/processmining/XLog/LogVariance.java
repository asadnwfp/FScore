package org.processmining.XLog;

import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.utils.ReusableMethods;

public class LogVariance {
	
	private XLog log;
	private int cases, events, eventClasses, eventTypes, originators;

	public LogVariance(XLog log) {
		this.log = log;
	}
	
	public void instantiate() {
		cases = log.size();
		ProcessInstanceView processInstanceView = new ProcessInstanceView(log.get(499), XLogInfoFactory.createLogInfo(log));
		processInstanceView.drawEvent(0);
		processInstanceView.drawEvent(1);
		processInstanceView.drawInstanceFlag();
		processInstanceView.drawEventFlag(0);
		
		ReusableMethods.printLogs("TotalVariants: " + showTraces().size());
	}
	public Set<XTrace> showTraces() {
		ReusableMethods.printLogs("##################################################");
		ReusableMethods.printLogs("LogVariance: showTrace()");
		ReusableMethods.printLogs("##################################################");
		ReusableMethods.printLogs("Traces: " + log.size());
		
		Set<XTrace> variants = new HashSet<XTrace>() ;
		for(XTrace trace : log) {
//			ReusableMethods.printLogs("*****************  Trace: "+ log.indexOf(trace) +"  ********************");
//			BasicLogInfo.basicAttributes(trace.getAttributes());
			variants.add(trace);
			

		}
		return variants;
	}
	
	public void showEvents(XTrace trace) {
		ReusableMethods.printLogs("NumberOfEvents: " + trace.size());
		for(XEvent event : trace) {
			ReusableMethods.printLogs("**************** Events "+ trace.indexOf(event) +" ***********************");
			BasicLogInfo.basicAttributes(event.getAttributes());
		}
		
	}

}
