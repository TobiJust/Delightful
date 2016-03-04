package com.light.lamptyps;



import com.network.TCPUDP.client.TCPclient;
import com.network.lampprotocol.LampProtocol;
import com.network.lampprotocol.SceneProtocol;

/**
 * Simple Lamp Class defines a single lamp 
 * @author Fabian
 * @version 1.1
 */
public class SimpleLamp {
	
	/**Light State; On = true; Off = false*/
	private boolean LightStatus = false;
	
	/**Color of the lamp*/
	protected Color color;
	
	/**Scene that is used*/
	protected int scene;
	
	/**IP Address of the lamp*/
	protected String IpAddr;
	
	/**Port of the lamp*/
	protected int Port;
	
	/**ID of the lamp; used for internal identification*/
	protected int id;
	
	/**TCP connection*/
	public TCPclient TCP;
	
	/**Protocol for communication with the lamp*/
	protected LampProtocol LP;
	
	/**Scene Protocol*/
	protected SceneProtocol SP;

	/**
	 * constructor initialized a new simple lamp 
	 * @param id		ID
	 * @param ip		IP Address
	 * @param port		Port
	 */
	public SimpleLamp(int id, String ip, int port) {
		this.id = id;
		this.IpAddr = ip;
		this.Port = port;
		this.connectLamp();

		LP = new LampProtocol();
		SP = new SceneProtocol();
	}

	/**
	 * change the state to on 
	 */
	public void switchOn(){
		LightStatus = true;
		setColor(Color.WHITE);
	}

	/**
	 * change the state to off 
	 */
	public void switchOff(){
		LightStatus = false;
		setColor(Color.BLACK);
	}

	/**
	 * current Lamp state
	 * @return 		returns the light state of the lamp
	 */
	public boolean isOn(){
		return LightStatus;
	}

	/**
	 * Sets the color of the lamp.
	 * @param color the color of the lamp.
	 */
	public void setColor(Color RGB){
		color = RGB;
		sendMessage();
	}
	/**
	 * Sets the scene of the lamp.
	 * @param id the scene of the lamp.
	 */
	public void setScenario(int id){
		scene = id;
		sendScene();
	}
	/**
	 * gets the scenario 
	 * @return	reutns the scene
	 */
	public int getScenario(){
		return scene;
	}
	/**
	 * Returns the current color of the lamp.
	 * @return the current color of the lamp. 
	 */
	public Color getActualColor(){
		return color;
	}



	//*********************************************************************
	//Connection
	//*********************************************************************

	/**
	 * Method creates a connection to the lamp
	 * @param InA - IP Address
	 * @param Pt - Port
	 */
	public void connectLamp(){
		if(TCP == null){
			this.TCP = new TCPclient();
			TCP.executeConnection(IpAddr, Port);
			System.out.println(IpAddr + " verbunden");
		}
	}

	/**
	 * Method send a Color to the Lamp
	 * @param c - Color
	 */
	public void sendMessage(){
		if(TCP.isConnected()){
			byte [] Message = LP.createMessage(this);
			TCP.sendData(Message);
			System.out.println("send message: " + Message);
		}
	}
	/**
	 * Method send a Scene to the Lamp
	 * @param c - Color
	 */
	public void sendScene(){
		if(TCP.isConnected()){
			byte [] Message = SP.createMessage(this);
			TCP.sendData(Message);
			System.out.println("send Scene: " + Message[1]);
		}
	}

	/**
	 * Method Closes the connection to the lamp
	 */
	public void closeLamp(){
		TCP.disconnect();
		switchOff();
		System.out.println("connection closed");
	}
	
	/**
	 * Mthod sets the intensity of the light
	 * @param i 	intensity value
	 */
	public void setIntensity(int i){
		color = new Color(i, i, i);
	}

	/**
	 * Method gets the intensity
	 * @return		returns the intensity as color
	 */
	public int getIntensity(){
		return (color.getRed() + color.getGreen() + color.getBlue())/3;
	}
	
	/**
	 * Getter of the ID
	 * @return	ID
	 */
	public int getID(){
		return this.id;
	}
	
	/**
	 * Getter of the IP
	 * @return	IP
	 */
	public String getIP(){
		return IpAddr;
	}
}
