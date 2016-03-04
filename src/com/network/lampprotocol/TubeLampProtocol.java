/**
 * 
 */
package com.network.lampprotocol;

import com.light.lamptyps.SimpleLamp;
import com.light.lamptyps.TubeLamp;

/**
 * NetzwerkTCPUDP - com.network.lampprotocol - TubeLampProtocoll.java
 *
 * @author Phillip Kopprasch
 * @created 29.12.2013
 */
public class TubeLampProtocol extends LampProtocol {

	/* (non-Javadoc)
	 * @see com.network.lampprotocol.LampProtocol#createMessage(com.light.lamptyps.SimpleLamp)
	 */
	@Override
	public byte[] createMessage(SimpleLamp sl) {
		TubeLamp tl = (TubeLamp)sl;
		byte[] packet = new byte[3+(3*tl.getLength())];
		
		packet[0] = 0x68;
		packet[1] = (byte)tl.getID();
		packet[2] = (byte)tl.getLength();
		
		for(int i = 3; i < packet.length; i = i+3){
			int index = (i-3)/3;
			packet[i] = (byte)tl.getColor(index).getRed();
			packet[i+1] = (byte)tl.getColor(index).getGreen();
			packet[i+2] = (byte)tl.getColor(index).getBlue();
		}
		System.out.println("Packet ID " + packet[1]);
		return packet;
	}


}
