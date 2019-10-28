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
	
	public static void nameAndClassOfObjects(Object[] objects ) {
			for(Object object : objects) {
				System.out.println("Object Class: " + object.getClass());
				System.out.println("Object String: " + object.toString());
				System.out.println("#######################################");
			}
	}
	

}
