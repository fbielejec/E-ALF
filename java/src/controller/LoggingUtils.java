package controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class LoggingUtils {

	// For logging
	private static final String TAB = "\t";
	public static final String HASH_COMMENT = "#";
	public static final int HEADER_ROW = 0;
	public static final int GENERATION_NUMBER_COLUMN = 0;
	public static final String WEIGHT = "w";
	public static final String BLANK_SPACE = "\\s+";

	public static void initializeBestIndividualLog(Population population,
			PrintWriter writer) {
		String header = "generation" + TAB + "individual" + TAB + "fitness"
				+ TAB;

		for (int i = 0; i < population.getnWeights(); i++) {

			String w = WEIGHT + i;
			header += w + TAB;

		}
		writer.println(header);

	}

	public static void logBestIndividual(Population population,
			PrintWriter writer) {

		int bestIndex = population.getBestIndex();
		String line = population.getGenerationNumber() + TAB + bestIndex + TAB
				+ population.getBestFitness() + TAB;
		for (float w : population.getBestWeights()) {

			line += w + TAB;

		}
		writer.println(line);
		writer.flush();

	}// END: logBestIndividual

	public static void logCurrentGeneration(Population population,
			PrintWriter writer) {

		// write header
		String header = "generation" + TAB;
		for (int i = 0; i < population.getnWeights(); i++) {
			String w = WEIGHT + i;
			header += w + TAB;
		}
		writer.println(header);

		// String line = population.getGenerationNumber() + TAB;
		for (int individual = 0; individual < population.getPopulationSize(); individual++) {

			String line = population.getGenerationNumber() + TAB;
			float[] weights = population.getIndividualWeights(individual);
			for (float w : weights) {

				line += w + TAB;

			}
			writer.println(line);
		}// END: individuals loop

		writer.flush();

	}// END: logGeneration



	public static String[] readLines(String filename, String comment)
			throws IOException {

		FileReader fileReader = new FileReader(filename);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		List<String> lines = new ArrayList<String>();

		String line = null;
		while ((line = bufferedReader.readLine()) != null) {

			// skip commented lines
			if (!line.contains(comment)) {
				lines.add(line);
			} // END: commented line check

		} // END: lines loop

		bufferedReader.close();

		return lines.toArray(new String[lines.size()]);
	}// END: readLines

}// END: class
