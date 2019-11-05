package org.processmining.XLog;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.impl.XAttributeInfoImpl;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.info.impl.XTimeBoundsImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.utils.ReusableMethods;

public class LogProperties {
	private XLog log;
	private PluginContext context;
	
	private XLogInfoImpl logInfo;
	private int totalLogEvents, totalLogTraces;

	public LogProperties(PluginContext context, XLog log) {
		this.log = log;
		this.context = context;
	}

	public void properties() {
		ReusableMethods.printLogs("LogProperties: properties()");
		BasicLogInfo.log = log;
//		BasicLogInfo.basicAttributes();
//		BasicLogInfo.basicClassifiers();
//		BasicLogInfo.basicEventAttributes();
		
		initiateProperties ();
		printEventsClasses();
		printTimeBoundaries();
		metaAttributeInfo();

	}
	
	public XLogInfoImpl initiateProperties () {
		/**
		 * Classifier: MXML Legacy Classifier
		 * Classifier: Event Name
		 * Classifier: Resource
		 */
		 logInfo =  new XLogInfoImpl(log, XLogInfoImpl.NAME_CLASSIFIER, log.getClassifiers());
		 
		 // Setting All Properties for log
		 totalLogEvents = logInfo.getNumberOfEvents();
		 totalLogTraces = logInfo.getNumberOfTraces();
		 
		 return logInfo;
	}
	
	private void printEventsClasses() {
		ReusableMethods.printLogs("##################################################");
		ReusableMethods.printLogs("LogProperties: printEventsClasses()");
		ReusableMethods.printLogs("##################################################");
		for(XEventClassifier classifier: logInfo.getEventClassifiers()) {
			ReusableMethods.printLogs("Classifer: " + classifier.name());
			ReusableMethods.printLogs("Class Size: " + logInfo.getEventClasses(classifier).getClasses().size());
			for(XEventClass xeventClass: logInfo.getEventClasses(classifier).getClasses()) {
				ReusableMethods.printLogs("XEventClass: " + xeventClass);
				
			}
			ReusableMethods.printLogs("**********************************************");
		}
	}
	
	private void printTimeBoundaries() {
		ReusableMethods.printLogs("##################################################");
		ReusableMethods.printLogs("LogProperties: printTimeBoundaries()");
		ReusableMethods.printLogs("##################################################");
		
		ReusableMethods.printLogs("Get Global Time Boundaries");
		
		XTimeBoundsImpl timeBound= (XTimeBoundsImpl) logInfo.getLogTimeBoundaries();
		ReusableMethods.printLogs("Get Start Date" + timeBound.getStartDate());
		ReusableMethods.printLogs("Get End Date" + timeBound.getEndDate());
		
	}
	
	
	private void metaAttributeInfo() {
		ReusableMethods.printLogs("##################################################");
		ReusableMethods.printLogs("LogProperties: metaAttributeInfo()");
		ReusableMethods.printLogs("##################################################");
		

		ReusableMethods.printLogs("Meta Attribute Info: " );
		ReusableMethods.printLogs("**********************************************");
		XAttributeInfoImpl attributeImp = (XAttributeInfoImpl) logInfo.getLogAttributeInfo();
		printXAttributeInfoImpl(attributeImp);

		ReusableMethods.printLogs("##################################################");
		ReusableMethods.printLogs("Log Attribute Info: " );
		ReusableMethods.printLogs("**********************************************");
		attributeImp = (XAttributeInfoImpl) logInfo.getMetaAttributeInfo();
		printXAttributeInfoImpl(attributeImp);
		
		ReusableMethods.printLogs("##################################################");
		ReusableMethods.printLogs("Trace Attribute Info: " );
		ReusableMethods.printLogs("**********************************************");
		attributeImp = (XAttributeInfoImpl) logInfo.getTraceAttributeInfo();
		printXAttributeInfoImpl(attributeImp);
		
		
	}

	private void printXAttributeInfoImpl(XAttributeInfoImpl attributeImp) {
		ReusableMethods.printLogs("Registerd Attributes: " );
		ReusableMethods.printLogs("**********************************************");
		for(String key : attributeImp.getAttributeKeys()) {
			ReusableMethods.printLogs("Attribute: " + key);
			ReusableMethods.printLogs("Attribute Freq: " + attributeImp.getFrequency(key));
			ReusableMethods.printLogs("Attribute Relative Freq: " + attributeImp.getRelativeFrequency(key));
		}
		ReusableMethods.printLogs("Registerd Keys for Extentions: " );
		ReusableMethods.printLogs("**********************************************");
		for(String key : attributeImp.getKeysWithoutExtension()){
			ReusableMethods.printLogs("Extention Key: " + key);
		}
		ReusableMethods.printLogs("Properties of Attribute: " );
		ReusableMethods.printLogs("**********************************************");
		for(XAttribute attribute: attributeImp.getAttributes()) {
		BasicLogInfo.propertiesOfAttribute(attribute);
		}
		
	}
	
	
//	private void propertyXAttributeTimestamp() {
//		XAttributeTimestampImpl
//	}
	
	

}
