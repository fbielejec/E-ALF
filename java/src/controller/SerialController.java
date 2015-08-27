package controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;

import app.ControllerApp;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

//http://www.drdobbs.com/jvm/control-an-arduino-from-java/240163864

//https://learn.sparkfun.com/tutorials/connecting-arduino-to-processing

public class SerialController implements SerialPortEventListener {

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

	public SerialController() {

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
				String inputLine = input.readLine();
				System.out.println(inputLine);
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
						//
						// Open serial port
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

	private void sendData(String data) {

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

	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}// END: close

	public static void main(String[] args) {

		SerialController test = new SerialController();

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
		}// END: initialize test

		// Wait 5 seconds then shutdown
		try {

			Thread.sleep(2000);

		} catch (InterruptedException ie) {

			//

		}
	}// END: main

}// END: class
