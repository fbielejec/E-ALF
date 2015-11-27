package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.Utils;
import controller.LoggingUtils;
import controller.Population;
import controller.Settings;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/**
 * @author Filip Bielejec
 * @version $Id$
 */
public class ControllerApp implements SerialPortEventListener {

	// private static final String RESET_SIGNAL = "R";
	private static final String ONLINE_SIGNAL = "O";
	private static final String COLLISION_SIGNAL = "C";
	private static final String FITNESS_TRANSMITION_SIGNAL = "T";
	private static final String DONE_SIGNAL = "D";

	private SerialPort serialPort = null;
	private static final String PORT_NAMES[] = {
	// "/dev/tty.usbmodem", // Mac OS X
	// "/dev/usbdev", // Linux
	"/dev/tty", // Linux
	// "/dev/serial", // Linux
	// "COM3", // Windows
	};

	// Port open timeout
	private static final int TIME_OUT = 1000;
	private static final int BAUD_RATE = 9600;

	private String appName;
	private BufferedReader input;
	private OutputStream output;

	// // For logging
	// private static final String TAB = "\t";

	public ControllerApp() {

		this.appName = ControllerApp.class.getName();

	}// END: Constructor

	public synchronized void serialEvent(SerialPortEvent oEvent) {

		// System.out.println("Event received: " + oEvent.toString());
		try {

			switch (oEvent.getEventType()) {

			case SerialPortEvent.DATA_AVAILABLE:
				if (input == null) {
					input = new BufferedReader(new InputStreamReader(
							serialPort.getInputStream()));
				}
				// String inputLine = input.readLine();
				// System.out.println(inputLine);
				break;

			default:
				break;
			}

		} catch (Exception e) {
			System.err.println(e.toString());
		}

	}// END: serialEvent

	public boolean initialize() {

		try {

			CommPortIdentifier portId = null;
			Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();

			// Enumerate system ports and try connecting to Arduino over each
			//
			System.out.println("Trying:");
			while (portId == null && portEnum.hasMoreElements()) {
				// Iterate through your host computer's serial port IDs
				//
				CommPortIdentifier currPortId = (CommPortIdentifier) portEnum
						.nextElement();
				System.out.println("   port" + currPortId.getName());
				for (String portName : PORT_NAMES) {
					if (currPortId.getName().equals(portName)
							|| currPortId.getName().startsWith(portName)) {

						// Try to connect to the Arduino on this port
						serialPort = (SerialPort) currPortId.open(appName,
								TIME_OUT);
						portId = currPortId;
						System.out.println("Connected on port"
								+ currPortId.getName());
						break;
					}
				}
			}

			if (portId == null || serialPort == null) {
				System.out.println("Oops... Could not connect to Arduino");
				return false;
			}

			// set port parameters
			serialPort.setSerialPortParams(BAUD_RATE, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);

			// Give Arduino some time
			// try {
			Thread.sleep(2000);
			// } catch (InterruptedException ie) {
			// //
			// }

			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}// END: initialize

	public String readData() throws IOException {

		String inputLine = "";

		if (!(input == null)) {
			inputLine = input.readLine();
		}

		return inputLine;
	}// END: readData

	public void sendData(String data) throws IOException {

		System.out.println("Sending data: '" + data + "'");

		// open the streams and send the data
		output = serialPort.getOutputStream();
		output.write(data.getBytes());

	}// END: sendData

	private static void sendWeights(ControllerApp controller, float[] weights)
			throws IOException, InterruptedException {
		for (int i = 0; i < weights.length; i++) {

			controller.sendData(String.valueOf(weights[i]));
			Thread.sleep(500);

		} // END: weights loop
	}// END: sendWeights

	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}// END: close

	public static void main(String[] args) {

		boolean done = false;
		ControllerApp controller = new ControllerApp();
		Population population = new Population();
		float[] weights;
		String inputLine;

		PrintWriter logWriter = null;

		try {

			if (controller.initialize()) {

				// wait for a reboot
				inputLine = controller.readData();
				while (!inputLine.contentEquals(ONLINE_SIGNAL)) {
					System.out
							.println("Waiting for ONLINE signal. Reboot the device.");
					// controller.sendData(RESET_SIGNAL);
					inputLine = controller.readData();
				}
				System.out.println("Device is online.");

				// resume / or start new analysis
				File resumeFile = new File("resume.log");
				if (resumeFile.exists()) {

					System.out
							.println("Resume log file found. Resuming previously generated weights");

					// this sets the weights and generation number
					resumeAnalysis(resumeFile, population);
				}//END: resume check

				// TODO: append if resuming
				
				
				System.out.println("Creating log file");
				logWriter = new PrintWriter("fitness.log", "UTF-8");
				LoggingUtils.initializeBestIndividualLog(population, logWriter);

				
				
				
				
				
				
				
				
				
				// send first individual
				weights = population.getCurrentWeights();
				sendWeights(controller, weights);
				// wait for confirmation
				inputLine = controller.readData();
				while (!inputLine.contentEquals(DONE_SIGNAL)) {
					inputLine = controller.readData();
					System.out.println(inputLine);
				}

				System.out.println("Generation "
						+ population.getGenerationNumber());
				System.out.println("Evaluating individual "
						+ population.getCurrentIndex());

				// /////////////////////
				// ---FOREVER LOOP---//
				// /////////////////////

				// let them live one by one, get fitness values
				while (!done) {

					inputLine = controller.readData();
					System.out.println(inputLine);

					// process collison
					if (inputLine.contentEquals(COLLISION_SIGNAL)) {

						float value = 0;
						while (!done) {
							inputLine = controller.readData();
							System.out.println(inputLine);
							if (inputLine
									.contentEquals(FITNESS_TRANSMITION_SIGNAL)) {
								inputLine = controller.readData();
								value = Float.valueOf(inputLine);
								System.out
										.println("Received fitness evaluation: "
												+ value);
								break;
							}
						}

						population.setFitness(value,
								population.getCurrentIndex());
						population.increaseIndex();

						if (population.getCurrentIndex() > population
								.getPopulationSize() - 1) {

							// log best individual from generation
							System.out.println("Writing to log file ");
							LoggingUtils.logBestIndividual(population,
									logWriter);

							System.out.println("Creating new generation");
							// Generate mating pool
							population.naturalSelection();
							// Create next generation
							population.generate();

							// write next generation to resume log
							System.out
									.println("Writing to current generation log");
							PrintWriter currentGenerationWriter = new PrintWriter(
									"resume.log", "UTF-8");
							LoggingUtils.logCurrentGeneration(population,
									currentGenerationWriter);
							currentGenerationWriter.close();

						} // END: popsize check

						// send new individual over Serial
						weights = population.getCurrentWeights();
						sendWeights(controller, weights);

						// wait for confirmation
						inputLine = controller.readData();
						while (!inputLine.contentEquals(DONE_SIGNAL)) {
							inputLine = controller.readData();
							System.out.println(inputLine);
						}

						System.out.println("Generation "
								+ population.getGenerationNumber());
						System.out.println("Evaluating individual "
								+ population.getCurrentIndex());

					} // END: collision signal check

				} // END: forever loop
			} // END: initialized check

		} catch (Exception e) {

			e.printStackTrace();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				//
				ie.printStackTrace();
			}

			controller.close();
			logWriter.close();
			System.exit(0);

		} // END: try-catch block

	}// END: main

	private static void resumeAnalysis(File file, Population population)
			throws IOException {

		LinkedList<float[]> individuals = new LinkedList<float[]>();
		int generationNumber = -1;

		String[] lines = LoggingUtils.readLines(file.getAbsolutePath(),
				LoggingUtils.HASH_COMMENT);
		String[] columnNames = lines[LoggingUtils.HEADER_ROW].split("\t");

		// Find columns with weights
		List<Integer> columns = new LinkedList<Integer>();
		Pattern pattern = Pattern.compile(LoggingUtils.WEIGHT);
		for (int i = 0; i < columnNames.length; i++) {

			// Look for matches in column names
			Matcher matcher = pattern.matcher(columnNames[i]);
			if (matcher.find()) {
				columns.add(i);
			}

		} // END: column names loop

		int nrow = lines.length - 1;
		if (nrow != population.getPopulationSize()) {
			throw new RuntimeException(
					"Number of individuals in resume log file incompatible with current analysis");
		}
		int ncol = columns.size();
		if (ncol != population.getnWeights()) {
			throw new RuntimeException(
					"Number of weights in resume log file incompatible with current analysis");
		}

		int i = 0;
		for (int row = 1; row <= nrow; row++) {

			String[] line = lines[row].split(LoggingUtils.BLANK_SPACE);

			if (i == 0) {
				generationNumber = Integer
						.parseInt(line[LoggingUtils.GENERATION_NUMBER_COLUMN]);
			}

			float[] individual = new float[ncol];
			for (int col = 0; col < ncol; col++) {

				individual[col] = Float.valueOf(line[columns.get(col)]);

			} // END: col loop

			individuals.add(i, individual);

			i++;
		}// END: row loop

		if (individuals.size() != population.getPopulationSize()) {
			throw new RuntimeException("Something went wrong when resuming");
		}

		// resume population
		population.setGenerationNumber(generationNumber);

		for (int ind = 0; ind < individuals.size(); ind++) {
			population.setIndividualWeights(ind, individuals.get(ind));
		}

	}// END: resumeAnalysis

}// END: class
