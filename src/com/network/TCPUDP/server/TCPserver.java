package com.network.TCPUDP.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP server class used the server interface
 * it initialized a TCP server
 * @author Fabian
 * @version 1.1
 */
public class TCPserver implements server {

	/**connect show the current state of the connection*/
	boolean connect = false;
	
	/**global server socket*/
	static ServerSocket s;
	
	/**global socket*/
	static Socket connection;
	
	/**Port, where the services is located*/
	static int Port = 0;


	@Override
	public void startServer(int Port) {
		long start;
		long elapsed;
		long timer;
		start = System.currentTimeMillis();
		try {
			s = new ServerSocket(Port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		do{
			elapsed = System.currentTimeMillis();
			timer = (elapsed-start)/1000;
			try {
				connection = s.accept();
				connect = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}while(connection == null && timer < 15.0 );
	}

	@Override
	public void stopServer() {
		try {
			s.close();
			connection.close();
			connect = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean sendData(String sMessage) {
		try {
			PrintWriter outToClient = new PrintWriter(connection.getOutputStream());
			outToClient.print(sMessage + "\r\n");
			outToClient.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String receiveData() {
		String IncomingMesg = "";
		try {
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			 IncomingMesg = inFromClient.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return IncomingMesg;
	}

}
