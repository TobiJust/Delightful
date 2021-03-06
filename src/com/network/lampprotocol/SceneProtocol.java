package com.network.lampprotocol;

import com.light.lamptyps.SimpleLamp;

public class SceneProtocol {

	public byte[] createMessage(SimpleLamp sl){
		byte[] packet = new byte[6];

		packet[0] = 0x68;
		packet[1] = (byte)sl.getScenario();
		packet[2] = 0x1;
		packet[3] = (byte)sl.getActualColor().getRed();
		packet[4] = (byte)sl.getActualColor().getGreen();
		packet[5] = (byte)sl.getActualColor().getBlue();

		return packet;
	}

}
