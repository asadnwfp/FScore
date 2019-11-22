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
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"LogProperties: properties()");
		BasicLogInfo.log = log;
//		BasicLogInfo.basicAttributes();
//		BasicLogInfo.basicClassifiers();
//		BasicLogInfo.basicEventAttributes();
		
//		initiateProperties ();
//		printEventsClasses();
//		printTimeBoundaries();
//		metaAttributeInfo();
		
		//Creating Log Traces
		ReusableMethods.setContext(context);
		new LogVariance(log).instantiate();
		new LogFeatures(log).initiateLogViewModel();
		ReusableMethods.removeContext();
//		ReusableMethods.printLogs("**********************************************");

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
		ReusableMethods.printLogs(ReusableMethods.DEBUG,
				"*****************************************************************");
		ReusableMethods.printLogs(ReusableMethods.DEBUG, this.getClass().getSimpleName() + ": printEventsClasses()");
		ReusableMethods.printLogs(ReusableMethods.DEBUG,
				"*****************************************************************");
		
		for(XEventClassifier classifier: logInfo.getEventClassifiers()) {
			ReusableMethods.printLogs(ReusableMethods.DEBUG,"Classifer: " + classifier.name());
			ReusableMethods.printLogs(ReusableMethods.DEBUG,"Class Size: " + logInfo.getEventClasses(classifier).getClasses().size());
			for(XEventClass xeventClass: logInfo.getEventClasses(classifier).getClasses()) {
				ReusableMethods.printLogs(ReusableMethods.DEBUG,"XEventClass: " + xeventClass);
				
			}
			ReusableMethods.printLogs(ReusableMethods.DEBUG,"**********************************************");
		}
	}
	
	private void printTimeBoundaries() {
		ReusableMethods.printLogs(ReusableMethods.DEBUG,
				"*****************************************************************");
		ReusableMethods.printLogs(ReusableMethods.DEBUG, this.getClass().getSimpleName() + ": printTimeBoundaries()");
		ReusableMethods.printLogs(ReusableMethods.DEBUG,
				"*****************************************************************");
		
		
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"Get Global Time Boundaries");
		
		XTimeBoundsImpl timeBound= (XTimeBoundsImpl) logInfo.getLogTimeBoundaries();
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"Get Start Date" + timeBound.getStartDate());
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"Get End Date" + timeBound.getEndDate());
		
	}
	
	
	private void metaAttributeInfo() {
		ReusableMethods.printLogs(ReusableMethods.DEBUG,
				"*****************************************************************");
		ReusableMethods.printLogs(ReusableMethods.DEBUG, this.getClass().getSimpleName() + ": metaAttributeInfo()");
		ReusableMethods.printLogs(ReusableMethods.DEBUG,
				"*****************************************************************");
		
		

		ReusableMethods.printLogs(ReusableMethods.DEBUG,"Meta Attribute Info: " );
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"**********************************************");
		XAttributeInfoImpl attributeImp = (XAttributeInfoImpl) logInfo.getLogAttributeInfo();
		printXAttributeInfoImpl(attributeImp);

		ReusableMethods.printLogs(ReusableMethods.DEBUG,"##################################################");
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"Log Attribute Info: " );
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"**********************************************");
		attributeImp = (XAttributeInfoImpl) logInfo.getMetaAttributeInfo();
		printXAttributeInfoImpl(attributeImp);
		
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"##################################################");
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"Trace Attribute Info: " );
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"**********************************************");
		attributeImp = (XAttributeInfoImpl) logInfo.getTraceAttributeInfo();
		printXAttributeInfoImpl(attributeImp);
		
		
	}

	private void printXAttributeInfoImpl(XAttributeInfoImpl attributeImp) {
		ReusableMethods.printLogs(ReusableMethods.DEBUG,
				"*****************************************************************");
		ReusableMethods.printLogs(ReusableMethods.DEBUG,
				this.getClass().getSimpleName() + ": printXAttributeInfoImpl()");
		ReusableMethods.printLogs(ReusableMethods.DEBUG,
				"*****************************************************************");
		
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"Registerd Attributes: " );
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"**********************************************");
		for(String key : attributeImp.getAttributeKeys()) {
			ReusableMethods.printLogs(ReusableMethods.DEBUG,"Attribute: " + key);
			ReusableMethods.printLogs(ReusableMethods.DEBUG,"Attribute Freq: " + attributeImp.getFrequency(key));
			ReusableMethods.printLogs(ReusableMethods.DEBUG,"Attribute Relative Freq: " + attributeImp.getRelativeFrequency(key));
		}
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"Registerd Keys for Extentions: " );
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"**********************************************");
		for(String key : attributeImp.getKeysWithoutExtension()){
			ReusableMethods.printLogs(ReusableMethods.DEBUG,"Extention Key: " + key);
		}
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"Properties of Attribute: " );
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"**********************************************");
		for(XAttribute attribute: attributeImp.getAttributes()) {
		BasicLogInfo.propertiesOfAttribute(attribute);
		}
		
	}
	
	
//	private void propertyXAttributeTimestamp() {
//		XAttributeTimestampImpl
//	}
	
	

}
