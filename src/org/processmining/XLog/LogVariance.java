package org.processmining.XLog;

import java.awt.Color;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.log.utils.XUtils;
import org.processmining.logenhancement.view.LogViewModel;
import org.processmining.logenhancement.view.LogViewVisualizer;
import org.processmining.logenhancement.view.TraceGroupingFunction;
import org.processmining.logenhancement.view.XESTraceVariant;
import org.processmining.utils.ReusableMethods;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;

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
//		LogVarianceTrace();
		getTraceVariants();
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
	
	private void LogVarianceTrace() {
		LogViewModel lvm = new LogViewModel(log);
		XEventClasses eventClasses = XUtils.createEventClasses(XLogInfoImpl.NAME_CLASSIFIER, log);
		lvm.setEventClasses(eventClasses);
		Map<XEventClass, Color> colorMap = LogViewVisualizer.createColorMap(eventClasses);
		
//		ColoringMode colorMode= new ColoringMode() {
//
//			public String getName() {
//				return "Custom Coloring";
//			}
//
//			public EventColoring createColoring(Iterable<XTrace> traces, final XEventClasses eventClasses) {
//				return new EventColoring() {
//
//					public Color getColor(XEvent event) {
//						return colorMap.get(eventClasses.getClassOf(event));
//					}
//				};
//			}
//		};
//		
//		lvm.setColoring(colorMode);
		ImmutableListMultimap<XESTraceVariant, XTrace>  variants = lvm.getTraceVariants();
		ReusableMethods.printLogs("LogVariants:  " + variants.size());
		ReusableMethods.printLogs("LogVariantsKeySet:  " + variants.keySet().size());
		int index = 0;
		for(XESTraceVariant xesTraceVariant: variants.keySet()) {
			ImmutableList<XTrace> trace = variants.get(xesTraceVariant);
			ReusableMethods.printLogs("TraceIndex: " + index++);
			ReusableMethods.printLogs("EventSize: " + trace.size());
		}
		ReusableMethods.printLogs("********************Event Clasifiers**************************");
		for(XEventClassifier classifier: log.getClassifiers()) {
			ReusableMethods.printLogs("ClasiferName: " + classifier.name());
		}
	}
	
	
//	interface ColoringMode {
//
//		public final static ColoringMode EVENTCLASS = new ColoringMode() {
//
//			public String getName() {
//				return "Color by Event Class";
//			}
//
//			public EventColoring createColoring(Iterable<XTrace> traces, XEventClasses eventClasses) {
//				return new EventColoringByClass(eventClasses);
//			}
//			
//			public String toString() {
//				return getName();
//			}
//			
//		};
//
//		public final static ColoringMode NONE = new ColoringMode() {
//
//			public String getName() {
//				return "No Color";
//			}
//
//			public EventColoring createColoring(Iterable<XTrace> traces, XEventClasses eventClasses) {
//				return new EventColoringWhite();
//			}
//
//			public String toString() {
//				return getName();
//			}
//			
//		};
//
//		EventColoring createColoring(Iterable<XTrace> traces, XEventClasses eventClasses);
//
//		String getName();
//
//	}
	
	public ImmutableListMultimap<XESTraceVariant, XTrace> getTraceVariants() {
		LogViewModel lvm = new LogViewModel(log);
		XEventClasses eventClasses = XUtils.createEventClasses(XLogInfoImpl.NAME_CLASSIFIER, log);
		lvm.setEventClasses(eventClasses);
		ImmutableListMultimap<XESTraceVariant, XTrace> variants = ImmutableListMultimap.of();
		TraceGroupingFunction traceGrouping = null ;
		
//		Set<XESTraceVariant> setTraceVariants = new HashSet<>();
		
		for(XTrace trace : log) {
			variants.put(traceGrouping.apply(trace), trace);
		}

		ReusableMethods.printLogs("LogVariants:  " + variants.size());
		ReusableMethods.printLogs("LogVariantsKeySet:  " + variants.keySet().size());
		return null;
	}

}
