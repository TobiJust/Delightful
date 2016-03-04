package com.light.controll;

import java.net.UnknownHostException;
import java.util.Iterator;

import com.light.lamptyps.Color;
import com.light.lamptyps.SimpleLamp;


public class LampTest {

	public static LampControll LC = new LampControll();
	/**
	 * @param args
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException {
		//create Lamp
		LC.addLamp(0, "localhost", 12345);

		//get Lamps
		for (Iterator<SimpleLamp> iter = LC.getLamps(); iter.hasNext();) {
			SimpleLamp lamp = iter.next();
			//Color
			//Red 0 - 255
			//Green 0 -255
			//Blue 0-255
			Color c = new Color(0,0,255);
			//send Color
			lamp.sendMessage();
			
		}
	}

}
