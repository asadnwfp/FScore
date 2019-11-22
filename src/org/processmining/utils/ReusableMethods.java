package org.processmining.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.semantics.petrinet.Marking;

public class ReusableMethods {
	// precalculated initial and final markings

	//	private static final boolean Debug = true;
	//	private static final boolean FileOutput = true;
	public enum Logger {
		CONSOLE, FILE_OUTPUT
	};
	public static final boolean DEBUG = true;
	public static Logger logger = Logger.CONSOLE;
	private static File file = new File(".");
	private static PrintStream FileOutput = null;
	private final static PrintStream originalStream = System.out;
	
	private static PluginContext currentContext;
	private static boolean haveContext = false;
	static {
		try {
			FileOutput = new PrintStream(new FileOutputStream(file.getCanonicalPath() + "/notes/output.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Could NOt Create OutPutStream");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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
		switch (logger) {
			case CONSOLE :
				System.setOut(originalStream);
				System.out.println(message);
				context.log(message);
				break;
			case FILE_OUTPUT :
				System.setOut(FileOutput);
				System.out.println(message);
				context.log(message);
				break;

		}

	}
	public static void printLogs(boolean debug, String message) {
		if (debug) {
			printLogs(message);
		}
	}

	public static void printLogs(String message) {

		if (!haveContext) {
			switch (logger) {
				case CONSOLE :
					System.setOut(originalStream);
					System.out.println(message);
					break;
				case FILE_OUTPUT :
					System.setOut(FileOutput);
					System.out.println(message);
					break;

			}
		} else {
			printLogs(message, currentContext);
		}

	}
	
	public static void setContext(PluginContext context) {
		currentContext =context;
		haveContext = true;
	}
	
	public static void removeContext() {
		haveContext = false;
	}
}
