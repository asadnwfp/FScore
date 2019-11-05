package org.processmining.utils;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.semantics.petrinet.Marking;

public class ReusableMethods {
	// precalculated initial and final markings
	
	public static final boolean Debug = true;
	
	public static Marking initMarking = null;
	public static Marking[] finalMarkings = null;

	public static void nullifyMarkings() {
		initMarking = null;
		finalMarkings = null;
	}

	public static double get2DecimalPlaces(double value, boolean min, int... stepSize) {
		if (min) {
			double place = Math.pow(10, stepSize[0]);
			return Math.round(value * place) / place;
		} else {
			return Math.round(value * 10000d) / 10000d;
		}

	}

	public static void nameAndClassOfObjects(Object[] objects) {
		System.out.println("ReusableMethods: nameAndClassOfObjects()");
		for (Object object : objects) {
			System.out.println("Object Class: " + object.getClass());
			System.out.println("Object String: " + object.toString());
			System.out.println("#######################################");
		}
	}

	public static String getAnyThingAfterPlus(String label) {
		System.out.println("ReusableMethods: getAnyThingAfterPlus");
		String pattern = "\\+[^\\+]+$"; // Matches Any Character Starting with the last plus
		String[] labelSplit = label.split(pattern);

		// Printing for Log
		System.out.println("Label: " + label);
		System.out.println("Split: " + labelSplit[0]);
		return labelSplit[0];
	}

	/**
	 * PrintLogs to the screen and console
	 */
	public static void printLogs(String message, PluginContext context) {
		if (Debug) {
			System.out.println(message);
			context.log(message);
		}
	}
	/**
	 * PrintLogs to the screen and console
	 */
	public static void printLogs(String message) {
		if (Debug) {
			System.out.println(message);
		}
	}


}

