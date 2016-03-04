/**
 * 
 */
package com.light.lamptyps;

/**
 * Android Color Class 
 * @author Chris
 * @version 1.1
 */
public class Color {

	public static Color WHITE = new Color(255, 255, 255);

	public static Color BLACK = new Color(0, 0, 0);

	private int red;
	private int green;
	private int blue;

	public Color(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public int getRed() {
		return red;
	}

	public void setRed(int red) {
		this.red = red;
	}

	public int getGreen() {
		return green;
	}

	public void setGreen(int green) {
		this.green = green;
	}

	public int getBlue() {
		return blue;
	}

	public void setBlue(int blue) {
		this.blue = blue;
	}
}
