package org.processmining.XLog;

import java.awt.Color;
import java.util.Date;
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
import org.processmining.logenhancement.view.LogViewVisualizer;
import org.processmining.utils.ReusableMethods;
import org.python.modules.synchronize;

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
		ProcessInstanceView processInstanceView = new ProcessInstanceView(log.get(499),
				XLogInfoFactory.createLogInfo(log));
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

		Set<XTrace> variants = new HashSet<XTrace>();
		for (XTrace trace : log) {
//			ReusableMethods.printLogs("*****************  Trace: "+ log.indexOf(trace) +"  ********************");
//			BasicLogInfo.basicAttributes(trace.getAttributes());
			variants.add(trace);

		}
		return variants;
	}

	public void showEvents(XTrace trace) {
		ReusableMethods.printLogs("NumberOfEvents: " + trace.size());
		for (XEvent event : trace) {
			ReusableMethods.printLogs("**************** Events " + trace.indexOf(event) + " ***********************");
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
		ImmutableListMultimap<XESTraceVariant, XTrace> variants = lvm.getTraceVariants();
		ReusableMethods.printLogs("LogVariants:  " + variants.size());
		ReusableMethods.printLogs("LogVariantsKeySet:  " + variants.keySet().size());
		int index = 0;
		for (XESTraceVariant xesTraceVariant : variants.keySet()) {
			ImmutableList<XTrace> trace = variants.get(xesTraceVariant);
			ReusableMethods.printLogs("TraceIndex: " + index++);
			ReusableMethods.printLogs("EventSize: " + trace.size());
		}
		ReusableMethods.printLogs("********************Event Clasifiers**************************");
		for (XEventClassifier classifier : log.getClassifiers()) {
			ReusableMethods.printLogs("ClasiferName: " + classifier.name());
		}
	}



	public ImmutableListMultimap<XESTraceVariant, XTrace> getTraceVariants() {
		final LogViewModel logViewModel = new LogViewModel(log);
		final LogInfo logInfo = new LogInfo();
		XEventClasses eventClasses = XUtils.createEventClasses(XLogInfoImpl.NAME_CLASSIFIER, log);
		logViewModel.setEventClasses(eventClasses);
		
		
		
		Runnable varrianceLoader = new Runnable() {
			
			@Override
			public void run() {

				System.out.println("*********** Starting Thread VarianceLoader *************");
				variantLoader(logViewModel);
				
			}
		};
		
		Thread varianceThread = new Thread(varrianceLoader);
		
		Runnable logInfoLoader = new Runnable() {
		
			@Override
			public void run() {
				try {
					varianceThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("*********** Starting Thread LogInfoLoader *************");
				
				logInfoLoader(logViewModel,logInfo);
				if (logInfo.numTraces % 1000 == 0) {
					return;
				}

			}
			
		}; 
		
		Thread logInfoThread = new Thread(logInfoLoader);
		varianceThread.start();
		logInfoThread.start();
		try {
			logInfoThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ReusableMethods.printLogs("numVariants:  " +  logInfo.numVariants );
		ReusableMethods.printLogs("numTraces:  " +  logInfo.numTraces );
		ReusableMethods.printLogs("numEvents:  " +  logInfo.numEvents );
		ReusableMethods.printLogs("avgTraceLength:  " +   logInfo.avgTraceLength);
		ReusableMethods.printLogs("numEventClasses:  " + logInfo.numEventClasses  );
		return null;
	}

	private synchronized void variantLoader(LogViewModel logViewModel ) {
		final ImmutableList<XTrace> tracesCopy = ImmutableList.copyOf(log);
		logViewModel.setTraces(tracesCopy);
		logViewModel.setTraceVariants(
				XUtils.getVariants(tracesCopy, new TraceGroupingFunctionByExactSequence(logViewModel)));
	}

	private synchronized void logInfoLoader(LogViewModel logViewModel, LogInfo logInfo) {
		Set<XEventClass> eventClasses = new HashSet<>();

		logInfo.numVariants = logViewModel.getTraceVariants().keySet().size();

		for (XTrace trace : logViewModel.getTraces()) {
			for (XEvent event : trace) {
				eventClasses.add(logViewModel.getEventClasses().getClassOf(event));
			}
			logInfo.numTraces++;
			logInfo.numEvents += trace.size();
			logInfo.avgTraceLength = logInfo.numEvents / (double) logInfo.numTraces;
			logInfo.numEventClasses = eventClasses.size();
		}
	}

	private static class LogInfo {

		int numTraces;
		long numEvents;
		int numVariants;

		double avgTraceLength;

		int numEventClasses;
		int numAttributes;

		Date firstEvent;
		Date lastEvent;

	}

}
