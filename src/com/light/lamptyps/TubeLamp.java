package com.light.lamptyps;



import com.network.lampprotocol.TubeLampProtocol;

/**
 * Tube Lamp Class inherit from SimpleLamp
 * @author Fabian
 *
 */
public class TubeLamp extends SimpleLamp{

	/**length of the tube*/
	protected int length;
	
	/**1 Dimensional Color Array*/
	protected Color[] colors;

	/**
	 * constructor initialized a new Tube Lamp
	 * @param id		ID
	 * @param ip		IP Address of the lamp
	 * @param port		Port of the lamp
	 * @param length	lenght of the lamp
	 */
	public TubeLamp(int id, String ip, int port, int length) {
		super(id, ip, port);
		this.length = length;
		LP = new TubeLampProtocol();
		colors = new Color[length];
	}
	
	/**
	 * Method sets the color to the lamp
	 * @param colors	Array of colors
	 */
	public void setColor(Color[] colors){
		if(colors.length == length)
			this.colors = colors;
	}
	
	/**
	 * Method gets the color of a single lamp in the tube
	 * @param i		lamp number in the Tube
	 * @return		Color of the lamp	
	 */
	public Color getColor(int i){
		if(i < 0 || i >= length) return Color.WHITE;
		return colors[i];
	}
	
	
	/* (non-Javadoc)
	 * @see com.light.lamptyps.SimpleLamp#setColor(java.awt.Color)
	 */
	@Override
	public void setColor(Color RGB) {
		this.color = RGB;
		for(int i = 0; i < length; i++){
			colors[i] = RGB;
		}
		sendMessage();
	}

	/**
	 * Getter of the tube length
	 * @return
	 */
	public int getLength(){
		return length;
	}
	
}
