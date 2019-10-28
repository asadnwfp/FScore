package org.processmining.utils;

public class ReusableMethods {
	public static double get2DecimalPlaces(double value, boolean min, int... stepSize) {
		if(min) {
			double place = Math.pow(10, stepSize[0]);
			return Math.round(value*place)/place;
		}else {
			return Math.round(value*10000d)/10000d;
		}
		
	}
	

}
