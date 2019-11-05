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
		ReusableMethods.printLogs("##################################################");
		ReusableMethods.printLogs("LogProperties: basicClassifiers()");
		ReusableMethods.printLogs("##################################################");
		
		for(XEventClassifier classifier: log.getClassifiers()) {
			ReusableMethods.printLogs("Classifier: " + classifier.name());
		}
	}
	
	static void basicEventAttributes() {
		ReusableMethods.printLogs("##################################################");
		ReusableMethods.printLogs("LogProperties: basicEventAttributes()");
		ReusableMethods.printLogs("##################################################");

		ReusableMethods.printLogs("Get EventAttributes: **");
		for(XAttribute attribute: log.getGlobalEventAttributes()) {
			propertiesOfAttribute(attribute);
		}
		
		ReusableMethods.printLogs("Get TraceAttributes: **");
		for(XAttribute attribute: log.getGlobalTraceAttributes()) {
			propertiesOfAttribute(attribute);
		}
	}

	static void basicAttributes() {
		ReusableMethods.printLogs("##################################################");
		ReusableMethods.printLogs("LogProperties: basicAttributes()");
		ReusableMethods.printLogs("##################################################");
		XAttributeMap attributable = log.getAttributes();
		ReusableMethods.printLogs(attributable.toString());

		ReusableMethods.printLogs("Attribute Key Set");
		ReusableMethods.printLogs("**********************************************");
		for (String key : attributable.keySet()) {
			ReusableMethods.printLogs("Key : " + key);
			XAttribute attribute = getXAttribute(attributable, key);

			ReusableMethods.printLogs("************ Iteration Finish *******************");
			

		}
		
		ReusableMethods.printLogs("Attributes");
	}
	

	private static XAttribute getXAttribute(XAttributeMap attributeMap, String attributeKey) {
		/**
		 * All known SubInterface
		 * XAttribute, XAttributeBoolean, XAttributeContinuous, XAttributeDiscrete, XAttributeLiteral, XAttributeTimestamp, XElement, XEvent, XLog, XTrace
		 */
		XAttribute attribute = attributeMap.get(attributeKey);
		ReusableMethods.printLogs("Attributes : " + attribute);
		propertiesOfAttribute(attribute);
		
		return attribute;
	}

	private static void propertiesOfAttribute(XAttribute attribute) {
		ReusableMethods.printLogs("**********************************************");
		ReusableMethods.printLogs("AttributeName: " + attribute.getKey());
		Set<XExtension> extentionSet = attribute.getExtensions();
		ReusableMethods.printLogs("extentionSet: " + extentionSet.size());
		ReusableMethods.printLogs("************ Iteration *******************");
		for (XExtension extension : extentionSet) {
			try {
				ReusableMethods.printLogs("extensionName: " + extension.getName());
				ReusableMethods.printLogs("extensionPrefix: " + extension.getPrefix());
			} catch (Exception e) {
				ReusableMethods.printLogs("Excelption: " + e.getClass().getName());
				e.printStackTrace();
			}
			ReusableMethods.printLogs("************ Iteration Finish *******************");
		}
	}

}
