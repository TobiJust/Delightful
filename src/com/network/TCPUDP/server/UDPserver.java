package com.network.TCPUDP.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * UDP server class used the server interface
 * @author Fabian
 * @version 1.1
 */
public class UDPserver implements server{

	/**connect show the current state of the connection*/
	boolean connect = false;
	
	/**global Datagram Socket is used for sending or receiving point for a packet delivery service */
	DatagramSocket s;
		
	/**Port, where the services is located*/
	int Port = 0;
	
	/**IP Adresse of the last client, used to answer*/
	private InetAddress LastIPAddress;
	
	/**Port of the last client, used to answer*/
	private int LastPort;
	
	@Override
	public void startServer(int Port) {
		this.Port = Port;
		try {
			s = new DatagramSocket(this.Port);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void stopServer() {
		try {
			s.close();
			connect = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean sendData(String sMessage) {
		byte[] sendData = new byte[1024];
		sendData = sMessage.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, LastIPAddress, LastPort);
		try {
			s.send(sendPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String receiveData() {
		String sMessage = null;
		byte[] receiveData = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		try {
			s.receive(receivePacket);
			sMessage = new String( receivePacket.getData());
			LastIPAddress = receivePacket.getAddress();
			LastPort = receivePacket.getPort();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sMessage;
	}

	/**
	 * Getter of the client IP address
	 * @return 		returns the client IP address
	 */
	public InetAddress getLastIpAddress(){
		return this.LastIPAddress;
	}
	
	/**
	 * Getter of the client Port
	 * @return		returns the client port
	 */
	public int getLastPort(){
		return this.LastPort;
	}
}
