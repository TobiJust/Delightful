package com.network.TCPUDP.server;

/**
 * Interface for a server component, used for TCP or UDP
 * @author Fabian
 * @version 1.1
 */
public interface server {

	/**
	 * startServer starts the server
	 * @param Port		Port to locate the service
	 */
	void startServer(int Port);
	
	/**
	 * stopServer stops the server
	 */
	void stopServer();
	
	/**
	 * sendData sends a Message
	 * @param sMessage	Message to be send
	 * @return 			true = successful; false = error
	 */
	boolean sendData(String sMessage);
	
	/**
	 * receiveData receives Data
	 * @return  		returns the message string
	 */
	String receiveData();
}
