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
		ReusableMethods.printLogs("BasicLogInfo: basicClassifiers()");
		ReusableMethods.printLogs("##################################################");
		
		for(XEventClassifier classifier: log.getClassifiers()) {
			ReusableMethods.printLogs("Classifier: " + classifier.name());
		}
	}
	
	static void basicEventAttributes() {
		ReusableMethods.printLogs("##################################################");
		ReusableMethods.printLogs("BasicLogInfo: basicEventAttributes()");
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
		ReusableMethods.printLogs("BasicLogInfo: basicAttributes()");
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
	
	static void basicAttributes(XAttributeMap attributable) {
		ReusableMethods.printLogs("##################################################");
		ReusableMethods.printLogs("BasicLogInfo: basicAttributes()");
		ReusableMethods.printLogs("##################################################");
		ReusableMethods.printLogs(attributable.toString());

		ReusableMethods.printLogs("Attribute Key Set");
		ReusableMethods.printLogs("**********************************************");
		for (String key : attributable.keySet()) {
			ReusableMethods.printLogs("Key : " + key);
			getXAttribute(attributable, key);

			ReusableMethods.printLogs("************ Iteration Finish *******************");
			

		}
	}
	

	private static XAttribute getXAttribute(XAttributeMap attributeMap, String attributeKey) {
		/**
		 * All known SubInterface
		 * XAttribute, XAttributeBoolean, XAttributeContinuous, XAttributeDiscrete, XAttributeLiteral, XAttributeTimestamp, XElement, XEvent, XLog, XTrace
		 */
		XAttribute attribute = attributeMap.get(attributeKey);
		ReusableMethods.printLogs("Attribute: " + attribute);
		propertiesOfAttribute(attribute);
		
		return attribute;
	}

	static void propertiesOfAttribute(XAttribute attribute) {
		ReusableMethods.printLogs("******************* Properties of Attribute ***************************");
		ReusableMethods.printLogs("AttributeName: " + attribute.getKey());
		Set<XExtension> extentionSet = attribute.getExtensions();
		System.out.println("ExtentionSet: " + extentionSet);
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
