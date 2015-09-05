package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;

import controller.Population;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import utils.Utils;

public class ControllerApp implements SerialPortEventListener {

	private static final String RESET_SIGNAL = "R";
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

	public ControllerApp() {

		this.appName = ControllerApp.class.getName();

	}// END: Constructor

	public synchronized void serialEvent(SerialPortEvent oEvent) {

		// System.out.println("Event received: " + oEvent.toString());
		try {

			switch (oEvent.getEventType()) {

			case SerialPortEvent.DATA_AVAILABLE:
				if (input == null) {
					input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
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
				CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
				System.out.println("   port" + currPortId.getName());
				for (String portName : PORT_NAMES) {
					if (currPortId.getName().equals(portName) || currPortId.getName().startsWith(portName)) {

						// Try to connect to the Arduino on this port
						serialPort = (SerialPort) currPortId.open(appName, TIME_OUT);
						portId = currPortId;
						System.out.println("Connected on port" + currPortId.getName());
						break;
					}
				}
			}

			if (portId == null || serialPort == null) {
				System.out.println("Oops... Could not connect to Arduino");
				return false;
			}

			// set port parameters
			serialPort.setSerialPortParams(BAUD_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

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
		String inputLine = input.readLine();
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

		try {

			if (controller.initialize()) {

				// send reset signal
				System.out.println("Sending RESET signal.");
				controller.sendData(RESET_SIGNAL);

				// wait for a reboot
				inputLine = controller.readData();
				while (!inputLine.contentEquals(ONLINE_SIGNAL)) {
					inputLine = controller.readData();
					System.out.println(inputLine);
				}
				System.out.println("Device is online.");

				// send first individual
				weights = population.getCurrentWeights();
				sendWeights(controller, weights);
				// wait for confirmation
				inputLine = controller.readData();
				while (!inputLine.contentEquals(DONE_SIGNAL)) {
					inputLine = controller.readData();
					System.out.println(inputLine);
				}

				System.out.println("Generation " + population.getGenerationNumber());
				System.out.println("Evaluating individual " + population.getCurrentIndex());

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
							if(inputLine.contentEquals(FITNESS_TRANSMITION_SIGNAL)) {
								inputLine = controller.readData();
								value = Float.valueOf(inputLine);
								System.out.println("Received fitness evaluation: " + value);
								break;
							}
						}
						
						population.setFitness(value, population.getCurrentIndex());
						population.increaseIndex();

						if (population.getCurrentIndex() > population.getPopulationSize() - 1) {

							// TODO
							// logging

							System.out.println("Creating new generation");

							// Generate mating pool
							population.naturalSelection();
							// Create next generation
							population.generate();

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

						System.out.println("Generation " + population.getGenerationNumber());
						System.out.println("Evaluating individual " + population.getCurrentIndex());
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
			System.exit(0);

		} // END: try-catch block

	}// END: main

}// END: class
