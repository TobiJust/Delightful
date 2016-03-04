package com.network.TCPUDP.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * UDP server class used the server interface
 * it initialized a UDP server
 * @author Fabian
 * @version 1.1
 */
public class UDPclient implements client {

	/**connect show the current state of the connection*/
	boolean connected = false;

	/**IP Address of the server*/
	InetAddress server = null;
	
	/**client IP Address*/
	InetAddress local = null;
	
	/**global socket creates the socket implementation*/
	DatagramSocket s = null;
	
	/**Server Port*/
	int server_port = 0;
	
	@Override
	public void connect(String ServeripAddress, int Port) {
		try {
			s = new DatagramSocket();
			server = InetAddress.getByName(ServeripAddress);
			server_port = Port;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public boolean sendData(String sMessage) {
		byte[] sendData = new byte[1024];
		sendData = sMessage.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, server, server_port);
		try {
			s.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String receiveMessage() {
		String sMessage = null;
		byte[] receiveData = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		try {
			s.receive(receivePacket);
			sMessage = new String(receivePacket.getData());
		} catch (Exception e) {
			e.printStackTrace();
//			Log.d("Exception",e.getMessage());
//          Log.d("Exception",e.getStackTrace() + "error");
		}
		return sMessage;
	}

	@Override
	public void disconnect() {
		try {
			s.close();
			connected = false;
		} catch (Exception e) {
			e.printStackTrace();
//			Log.d("Exception",e.getMessage());
//          Log.d("Exception",e.getStackTrace() + "error");
		}
	}

}
