package app;

import processing.core.PApplet;

/*
 * ArduinoDataDisplay
 * based on Arduino Cookbook code from Recipe 4.4 
 * 
 * Displays bar graphs of sensor data sent as CSV from Arduino
 * in all cases, N is the Row to be associated with the given message
 * Labels sent as: "Label,N,the label\n"  // "the label" is used for Row N
 * Range sent as : "Range,N,Min, Max\n"  // Row N has a range from min to max
 *    if Min is negative then the bar grows from the midpoint of Min and Max, 
 *    else the bar grows from Min
 * Data sent as:  "Data,N,val\n"   // val is plotted for row N
 */

@SuppressWarnings("serial")
public class ArduinoDataDisplay extends PApplet {

	public static void main(String[] args) {
		PApplet.main(new String[] { "app.ArduinoDataDisplay" });
	}// END: main

	@Override
	public void setup() {

		size(1280, 720);

	}// END setup

	@Override
	public void draw() {

	}// END draw

}// END: Main