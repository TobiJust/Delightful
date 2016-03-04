package com.network.lampprotocol;

import com.light.lamptyps.SimpleLamp;

/**
 * Class LampProtocol is used to create byte packets, that are transmitted to the Arduino
 * @author Fabian
 * @version 1.1
 */
public class LampProtocol {
	
	/**
	 * createMessage create the transmitted messages
	 * @param sl		SimpleLamp Object, is used to create the packets. The color should be set before!
	 * @return			returns the byte packet
	 */
	public byte[] createMessage(SimpleLamp sl){
		byte[] packet = new byte[6];
		
		packet[0] = 0x68;
		packet[1] = (byte)sl.getID();
		packet[2] = 0x1;
		packet[3] = (byte)sl.getActualColor().getRed();
		packet[4] = (byte)sl.getActualColor().getGreen();
		packet[5] = (byte)sl.getActualColor().getBlue();
				
		return packet;
	}

}
