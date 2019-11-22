package org.processmining.XLog;

import java.util.Set;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XLog;
import org.processmining.utils.ReusableMethods;

public class BasicLogInfo {
	public static XLog log;
	static void basicClassifiers() {
		ReusableMethods.printLogs(ReusableMethods.DEBUG,
				"*****************************************************************");
		ReusableMethods.printLogs(ReusableMethods.DEBUG, BasicLogInfo.class.getSimpleName() + ": basicClassifiers()");
		ReusableMethods.printLogs(ReusableMethods.DEBUG,
				"*****************************************************************");
		
		
		for(XEventClassifier classifier: log.getClassifiers()) {
			ReusableMethods.printLogs(ReusableMethods.DEBUG,"Classifier: " + classifier.name());
		}
	}
	
	static void basicEventAttributes() {
		ReusableMethods.printLogs(ReusableMethods.DEBUG,
				"*****************************************************************");
		ReusableMethods.printLogs(ReusableMethods.DEBUG, BasicLogInfo.class.getSimpleName() + ": basicEventAttributes()");
		ReusableMethods.printLogs(ReusableMethods.DEBUG,
				"*****************************************************************");
		

		ReusableMethods.printLogs(ReusableMethods.DEBUG, "Get EventAttributes:");
		for(XAttribute attribute: log.getGlobalEventAttributes()) {
			propertiesOfAttribute(attribute);
		}
		
		ReusableMethods.printLogs(ReusableMethods.DEBUG, "Get TraceAttributes:");
		for(XAttribute attribute: log.getGlobalTraceAttributes()) {
			propertiesOfAttribute(attribute);
		}
	}

	static void basicAttributes() {
		ReusableMethods.printLogs(ReusableMethods.DEBUG,
				"*****************************************************************");
		ReusableMethods.printLogs(ReusableMethods.DEBUG, BasicLogInfo.class.getSimpleName() + ": basicAttributes()");
		ReusableMethods.printLogs(ReusableMethods.DEBUG,
				"*****************************************************************");
		
		
		XAttributeMap attributable = log.getAttributes();
		ReusableMethods.printLogs(ReusableMethods.DEBUG,attributable.toString());

		ReusableMethods.printLogs(ReusableMethods.DEBUG, "Attribute Key Set");
		ReusableMethods.printLogs(ReusableMethods.DEBUG, "**********************************************");
		for (String key : attributable.keySet()) {
			ReusableMethods.printLogs(ReusableMethods.DEBUG,"Key : " + key);
			XAttribute attribute = getXAttribute(attributable, key);

			ReusableMethods.printLogs(ReusableMethods.DEBUG, "************ Iteration Finish *******************");
			

		}
		
		ReusableMethods.printLogs(ReusableMethods.DEBUG, "Attributes");
	}
	
	static void basicAttributes(XAttributeMap attributable) {
		ReusableMethods.printLogs(ReusableMethods.DEBUG,
				"*****************************************************************");
		ReusableMethods.printLogs(ReusableMethods.DEBUG, BasicLogInfo.class.getSimpleName() + ": basicAttributes()");
		ReusableMethods.printLogs(ReusableMethods.DEBUG,
				"*****************************************************************");
		
		ReusableMethods.printLogs(ReusableMethods.DEBUG,attributable.toString());

		ReusableMethods.printLogs(ReusableMethods.DEBUG, "Attribute Key Set");
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"**********************************************");
		for (String key : attributable.keySet()) {
			ReusableMethods.printLogs(ReusableMethods.DEBUG,"Key : " + key);
			getXAttribute(attributable, key);

			ReusableMethods.printLogs(ReusableMethods.DEBUG,"************ Iteration Finish *******************");
			

		}
	}
	

	private static XAttribute getXAttribute(XAttributeMap attributeMap, String attributeKey) {
		/**
		 * All known SubInterface
		 * XAttribute, XAttributeBoolean, XAttributeContinuous, XAttributeDiscrete, XAttributeLiteral, XAttributeTimestamp, XElement, XEvent, XLog, XTrace
		 */
		XAttribute attribute = attributeMap.get(attributeKey);
		ReusableMethods.printLogs(ReusableMethods.DEBUG, "Attribute: " + attribute);
		propertiesOfAttribute(attribute);
		
		return attribute;
	}

	static void propertiesOfAttribute(XAttribute attribute) {
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"******************* Properties of Attribute ***************************");
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"AttributeName: " + attribute.getKey());
		Set<XExtension> extentionSet = attribute.getExtensions();
		System.out.println("ExtentionSet: " + extentionSet);
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"extentionSet: " + extentionSet.size());
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"************ Iteration *******************");
		for (XExtension extension : extentionSet) {
			try {
				ReusableMethods.printLogs(ReusableMethods.DEBUG,"extensionName: " + extension.getName());
				ReusableMethods.printLogs(ReusableMethods.DEBUG,"extensionPrefix: " + extension.getPrefix());
			} catch (Exception e) {
				ReusableMethods.printLogs(ReusableMethods.DEBUG,"Excelption: " + e.getClass().getName());
				e.printStackTrace();
			}
			ReusableMethods.printLogs(ReusableMethods.DEBUG,"************ Iteration Finish *******************");
		}
		
	}

}
