package utils;

import java.util.Random;

public class Utils {

	private final static Random randomGenerator = new Random();

	public static double randomDouble(double lower, double upper) {

		if (lower > upper) {
			throw new IllegalArgumentException("Lower cannot exceed upper.");
		}

		double range = upper - lower;
		double scaled = randomGenerator.nextDouble() * range;
		double randomNumber = scaled + lower;

		return randomNumber;

	}// END: random
	
	public static int randomInt(int lower, int upper) {

		if (lower > upper) {
			throw new IllegalArgumentException("Lower cannot exceed upper.");
		}

		// get the range, casting to long to avoid overflow problems
		long range = (long) upper - (long) lower + 1;

		// compute a fraction of the range, 0 <= frac < range
		long fraction = (long) (range * randomGenerator.nextDouble());
		int randomNumber = (int) (fraction + lower);

		return randomNumber;
	}// END: random

	public static double runif( ) {
		double randomNumber = randomGenerator.nextDouble();
		return randomNumber;
	}// END: random
	
	public static void printArray(Object[] x) {
		for (int i = 0; i < x.length; i++) {
			System.out.print(x[i] + " ");
		}
		System.out.println();
	}// END: printArray

	public static void printArray(char[] x) {
		for (int i = 0; i < x.length; i++) {
			System.out.print(x[i] + " ");
		}
		System.out.println();
	}// END: printArray
	
	public static void printArray(double[] x) {
		for (int i = 0; i < x.length; i++) {
			System.out.print(x[i] + " ");
		}
		System.out.println();
	}// END: printArray
	
	public static double map(double value, double fromLow, double fromHigh,
			double toLow, double toHigh) {
		return (value - fromLow) / (fromHigh - fromLow) * (toHigh - toLow) + toLow;
	}// END: map

}// END: class
