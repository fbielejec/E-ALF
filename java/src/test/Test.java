package test;

import linefollowing.Line;
import genetic.EAutonomPopulation;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

@SuppressWarnings("serial")
public class Test extends PApplet {

	private float[] genes = new float[]{(float) 0.1, (float) 0.2};
	
	public static void main(String[] args) {
		PApplet.main(new String[] { "test.Test" });
	}// END: main

	@Override
	public void setup() {

		float x = (float) 1.1;
		float y = (float) 2.2;
		
//		PVector[] forces = new PVector[1];
		PVector force = new PVector(x,y);
//        forces[0] = force;
		
		PVector results1 = feedforward1(force);
		
		System.out.println(results1.x);
		System.out.println(results1.y);
		System.out.println();
		
		double[] results = feedforward(new double[]{x,y});

		System.out.println(results[0]);
		System.out.println(results[1]);
		
		
		
	}// END setup

	@Override
	public void draw() {

	}// END draw

	PVector feedforward1(PVector forces) {

		// Sum all values
		PVector sum = new PVector();
		for (int i = 0; i < genes.length; i++) {
			forces.mult(genes[i]);
			sum.add(forces);
		}

		return sum;
	}

	public double[] feedforward(double[] input) {
		
		double[] sum = new double[genes.length];
		for (int i = 0; i < genes.length; i++) {
			
			for(int j = 0; j < sum.length; j++) {
				input[j] *= genes[i]; 
				sum[j] += input[j]; 
			}
			
		}
		
		return sum;
	}// END: feedforward
	
}// END: class
