package org.processmining.utils;

import java.util.Collection;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;

public class XEventAnalysis {
	public static void getAnalysis(XLog log) {
		System.out.println("XeventAnalysis: getAnalysis()");
		XEventClassifier eventClassifier = XLogInfoImpl.NAME_CLASSIFIER;
		XEventClasses eventClasses = XEventClasses.deriveEventClasses(eventClassifier,log);
		System.out.println("####################### getAnalysis: Start ###########################");
		for(XEventClass eventClass: eventClasses.getClasses()) {
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			System.out.println("Event Class Name: " + eventClass.getId());
			System.out.println("Event Class Index: " + eventClass.getIndex());
		}

		System.out.println("####################### getAnalysis: Finish ###########################");
	}
	
	public static int getEventClassCount(XLog log) {
		System.out.println("XeventAnalysis: getEventClassCount");
		XEventClassifier eventClassifier = XLogInfoImpl.NAME_CLASSIFIER;
		XEventClasses eventClasses = XEventClasses.deriveEventClasses(eventClassifier,log);

		int count = eventClasses.getClasses().size();
		System.out.println("XeventAnalysis: Count of EventClass: " + count );
		return count;
	}
	
	public static Collection<XEventClass> getEventClassCollection(XLog log){
		System.out.println("XeventAnalysis: getEventClassCollection()");
		XEventClassifier eventClassifier = XLogInfoImpl.NAME_CLASSIFIER;
		XEventClasses eventClasses = XEventClasses.deriveEventClasses(eventClassifier,log);
		return eventClasses.getClasses();
		
	}
}
