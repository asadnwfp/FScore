package org.processmining.XLog;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.log.utils.XUtils;
import org.processmining.utils.ReusableMethods;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;

/**
 * 
 * @author saadn
 *	Based on Log Variance, Log Features is Another class
 */
public class LogFeatures {
	private XLog log;
	private LogViewModel logViewModel;
	private ImmutableListMultimap<XESTraceVariant, XTrace> variants;
	// Initiating class with log
	public LogFeatures(XLog log) {
		
		this.log=log;
	}
	
	public synchronized void initiateLogViewModel() {
		ReusableMethods.printLogs(ReusableMethods.DEBUG, "*****************************************************************");
		ReusableMethods.printLogs(ReusableMethods.DEBUG, this.getClass().getSimpleName() + ": initiateLogViewModel()");
		ReusableMethods.printLogs(ReusableMethods.DEBUG, "*****************************************************************");
		
		logViewModel = new LogViewModel();
		XEventClasses eventClasses = XUtils.createEventClasses(XLogInfoImpl.NAME_CLASSIFIER, log);
		logViewModel.setEventClasses(eventClasses);
		
		ImmutableListMultimap<XESTraceVariant, XTrace> variants = logViewModel.getTraceVariants();
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"LogVariants:  " + variants.size());
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"LogVariantsKeySet:  " + variants.keySet().size());
	}
	
	public ImmutableListMultimap<XESTraceVariant, XTrace> getTraceVariants() {
		final LogViewModel logViewModel = new LogViewModel(log);
		final LogInfo logInfo = new LogInfo();
		XEventClasses eventClasses = XUtils.createEventClasses(XLogInfoImpl.NAME_CLASSIFIER, log);
		logViewModel.setEventClasses(eventClasses);
		
		
		
		Runnable varrianceLoader = new Runnable() {
			
			@Override
			public void run() {

				ReusableMethods.printLogs(ReusableMethods.DEBUG, "*********** Starting Thread VarianceLoader *************");
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
				ReusableMethods.printLogs(ReusableMethods.DEBUG, "*********** Starting Thread LogInfoLoader *************");
				
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
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"numVariants:  " +  logInfo.numVariants );
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"numTraces:  " +  logInfo.numTraces );
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"numEvents:  " +  logInfo.numEvents );
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"avgTraceLength:  " +   logInfo.avgTraceLength);
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"numEventClasses:  " + logInfo.numEventClasses  );
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
