package com.network.TCPUDP.client;


/**
 * Interface for a network client, used TCP or UDP
 * @author Fabian
 * @version 1.1
 */
public interface client {

	/**
	 * connect, connects the client with a server
	 * @param ServeripAddress		Server IP Address is needed
	 * @param Port					Port of the Server is needed
	 */
	void connect(String ServeripAddress, int Port);
	
	/**
	 * isConnected returns the state of the current connection
	 * @return		State of connection
	 */
	boolean isConnected();
	
	/**
	 * 
	 * @param sMessage
	 * @return
	 */
	boolean sendData(String sMessage);
	
	/**
	 * receiveMessage get the received packets
	 * @return 		returns the received message as string
	 */
	String receiveMessage();
	
	/**
	 * disconnects the connection
	 */
	void disconnect();
}
