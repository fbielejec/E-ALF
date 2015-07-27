package utils;

import java.util.LinkedList;
import java.util.Random;

import linefollowing.EAutonom;
import processing.core.PApplet;

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

	public static double runif() {
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
		return (value - fromLow) / (fromHigh - fromLow) * (toHigh - toLow)
				+ toLow;
	}// END: map

	// /////////////////
	// ---GA UTILS---//
	// ////////////////

	public static String displayWeights(EAutonom autonom) {
		// TODO: format to display network

		String message = "";
		LinkedList<Double> weights = autonom.getNeuralNetwork().getWeights();
		for (double w : weights) {
			message = message.concat(String.format("%.4g", w) + " ");
		}

		return message;
	}// END: displayWeights
	
	// ////////////////////////
	// ---PROCESSING UTILS---//
	// ////////////////////////

	public static void dashline(float x0, float y0, float x1, float y1, float[] spacing,
			PApplet p) {
		float distance = PApplet.dist(x0, y0, x1, y1);
		float[] xSpacing = new float[spacing.length];
		float[] ySpacing = new float[spacing.length];
		float drawn = (float) 0.0; // amount of distance drawn

		if (distance > 0) {
			int i;
			boolean drawLine = true; // alternate between dashes and gaps

			/*
			 * Figure out x and y distances for each of the spacing values I
			 * decided to trade memory for time; I'd rather allocate a few dozen
			 * bytes than have to do a calculation every time I draw.
			 */
			for (i = 0; i < spacing.length; i++) {
				xSpacing[i] = PApplet.lerp(0, (x1 - x0), spacing[i] / distance);
				ySpacing[i] = PApplet.lerp(0, (y1 - y0), spacing[i] / distance);
			}

			i = 0;
			while (drawn < distance) {
				if (drawLine) {
					p.line(x0, y0, x0 + xSpacing[i], y0 + ySpacing[i]);
				}
				x0 += xSpacing[i];
				y0 += ySpacing[i];
				/* Add distance "drawn" by this line or gap */
				drawn = drawn + PApplet.mag(xSpacing[i], ySpacing[i]);
				i = (i + 1) % spacing.length; // cycle through array
				drawLine = !drawLine; // switch between dash and gap
			}
		}
	}

	/*
	 * Draw a dashed line with given dash and gap length. x0 starting
	 * x-coordinate of line. y0 starting y-coordinate of line. x1 ending
	 * x-coordinate of line. y1 ending y-coordinate of line. dash - length of
	 * dashed line in pixels gap - space between dashes in pixels
	 */
	public static void dashline(float x0, float y0, float x1, float y1, float dash,
			float gap, PApplet p) {
		float[] spacing = { dash, gap };
		dashline(x0, y0, x1, y1, spacing, p);
	}

}// END: class
