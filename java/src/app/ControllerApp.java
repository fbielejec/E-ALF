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

	private static final String COLLISION_SIGNAL = "C";
	private static final String DONE_SIGNAL = "D";
	private static final String RESET_SIGNAL = "R";
	private static final String ONLINE_SIGNAL = "O";

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

			// Give the Arduino some time
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ie) {
				//
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}// END: initialize

	public String readData() {

		String inputLine = null;

		try {

			inputLine = input.readLine();

		} catch (IOException e) {
			System.err.println(e.toString());
			System.exit(0);
		}

		return inputLine;
	}// END: readData

	public void sendData(String data) {

		try {

			System.out.println("Sending data: '" + data + "'");

			// open the streams and send the data
			output = serialPort.getOutputStream();
			output.write(data.getBytes());

		} catch (Exception e) {
			System.err.println(e.toString());
			System.exit(0);
		}

	}// END: sendData

	// public void sendWeights(float[] weights) {
	//
	//
	//
	// }//END: sendWeights

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
		String inputLine;

		if (controller.initialize()) {

			// send reset signal
			System.out.println("Sending RESET signal.");
			controller.sendData(RESET_SIGNAL);

			// wait for a reboot
			while (!done) {
				inputLine = controller.readData();
				System.out.println(inputLine);
				if (inputLine.contentEquals(ONLINE_SIGNAL)) {
					System.out.println("Device is online.");
					break;
				}
			}

			// TODO: send new individual over serial before loop begins
			float[] weights;// = population.getCurrentWeights();
			System.out.println("Generation " + population.getGenerationNumber());
			System.out.println("Evaluating individual " + population.getCurrentIndex());

			// let them live one by one, get fitness values
			while (!done) {

				inputLine = controller.readData();
				System.out.println(inputLine);

				// process collison
				if (inputLine.contentEquals(COLLISION_SIGNAL)) {

					// TODO: get fitness over serial

					// TODO: set fitness value
					float value = (float) Utils.randomDouble(0, 10);
					population.setFitness(value, population.getCurrentIndex());

					// send new individual over Serial
					weights = population.getCurrentWeights();
					for (int i = 0; i < weights.length; i++) {

						controller.sendData(String.valueOf(weights[i]));

						try {
							Thread.sleep(500);
						} catch (InterruptedException ie) {
							//
						}

					} // END: weights loop

					// wait for confirmation
					inputLine = controller.readData();
					while (!inputLine.contentEquals(DONE_SIGNAL))
						// evaluate next individual
						population.increaseIndex();

					if (population.getCurrentIndex() > population.getPopulationSize()) {

						// TODO
						// Reporting on how this pop has done
						// logging

						System.out.println("Creating new generation");

						// Generate mating pool
						population.naturalSelection();
						// Create next generation
						population.generate();

						// send new individual over Serial
						weights = population.getCurrentWeights();
						for (int i = 0; i < weights.length; i++) {

							controller.sendData(String.valueOf(weights[i]));

							try {
								Thread.sleep(500);
							} catch (InterruptedException ie) {
								//
							}

						} // END: weights loop

					} // END: populationSize check

					System.out.println("Generation " + population.getGenerationNumber());
					System.out.println("Evaluating individual " + population.getCurrentIndex());
				} // END: collision signal check

			} // END: forever loop
		} // END: initialized check

		try {

			Thread.sleep(2000);

		} catch (InterruptedException ie) {
			//
		}

		controller.close();

	}// END: main

	// ///////////////////
	// ---TEST SERIAL---//
	// ///////////////////

	public static void testSerial() {

		ControllerApp test = new ControllerApp();
		if (test.initialize()) {

			test.sendData("y");

			try {

				Thread.sleep(2000);

			} catch (InterruptedException ie) {
				//
			}

			test.sendData("n");

			try {

				Thread.sleep(2000);

			} catch (InterruptedException ie) {
				//
			}

			test.close();
		} // END: initialize test

		// Wait 5 seconds then shutdown
		try {

			Thread.sleep(2000);

		} catch (InterruptedException ie) {

			//

		}
	}// END: testSerial

}// END: class
