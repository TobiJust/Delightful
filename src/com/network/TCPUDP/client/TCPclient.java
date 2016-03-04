package com.network.TCPUDP.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import de.thwildau.network.DelightfulHttpClient.UrlConnector;

import android.os.AsyncTask;

/**
 * TCP client used the client interface
 * it initialized a TCP client
 * @author Fabian
 * @version 1.1
 */
public class TCPclient implements client {
	
	
	protected static HashMap<String, Socket> connections = new HashMap<String, Socket>();

	/**connect show the current state of the connection*/
	boolean connected = false;

	/**IP Address of the server*/
	InetAddress server = null;
	
	/**client IP Address*/
	InetAddress local = null;

	/**global socket creates the socket implementation*/
	Socket s = null;

	/**Server Port*/
	int server_port = 0;

	/**outToServer is used to format the message */
	PrintWriter outToServer;
	
	/**outToServerOS is used to send the packets*/
	OutputStream outToServerOS;
	
	/**Array of lampstreams*/
	ArrayList lampStreams = new ArrayList<OutputStream>();

	@Override
	public void connect(String ServeripAddress, int Port) {
		server_port = Port;
		s = TCPclient.connections.get(ServeripAddress);
		try {
			server = InetAddress.getByName(ServeripAddress);
			if(s == null || !s.isConnected()){
				s = new Socket(server, server_port);
				TCPclient.connections.put(ServeripAddress, s);
			}
			
			connected = true;
			outToServerOS = s.getOutputStream();
		} catch (Exception e) {
			e.printStackTrace();
		};
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public boolean sendData(String sMessage) {
		try {
			outToServer = new PrintWriter(s.getOutputStream(),true);
			outToServer.print(sMessage + "\r\n");
			outToServer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean sendData(byte[] Message) {
		try {
			outToServerOS.write(Message);
			outToServerOS.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String receiveMessage() {
		String sMessage = "";
		try {
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
			sMessage = inFromServer.readLine();
		} catch (Exception e) {
			e.printStackTrace();
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
		}
	}

	/**
	 * execute Connection initialized the asynchrony task
	 * @param ip		server ip
	 * @param port		server port
	 */
	public void executeConnection(String ip, int port){
		String[] server = {"0", ip, String.valueOf(port)};
		new LampConnector().execute(server);
	}
	
	/**
	 * LampConnector transfers the TCP connection into an asynchrony task 
	 * @author 
	 * @version 1.1
	 */
	public class LampConnector extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... server) {
			String response = null;
			try {
				switch(Integer.parseInt(server[0])){
				case 0:
					connect(server[1], Integer.parseInt(server[2]));
					break;
				case 1:
					//					sendData(Message);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}
		@Override
		protected void onPostExecute(String response){
			//			System.out.println("Post Execute");
		}
	}

}
