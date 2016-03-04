package com.light.lamptyps;

/**
 * MatrixLamp inherits from TubeLamp
 * @author Fabian
 * @version 1.1
 */
public class MatrixLamp extends TubeLamp{
	
	/**Size of the Matrix*/
	protected Dimension Size;

	/**
	 * constructor initialized a new matrix lamp
	 * @param id		define an ID (int)
	 * @param ip		IP of the lamp
	 * @param port		Port of the lamp
	 * @param size		Size of the lamp
	 */
	public MatrixLamp(int id, String ip, int port, Dimension size) {
		super(id, ip, port, size.height * size.width);
		Size = size;
		//LP = new TubeLampProtocol();
	}
	
	/**
	 * Getter of the Size
	 * @return
	 */
	public Dimension getSize(){
		return Size;
	}
	
	/* (non-Javadoc)
	 * @see com.light.lamptyps.SimpleLamp#setColor(java.awt.Color)
	 */
	@Override
	public void setColor(Color RGB) {
		this.color = RGB;
		for(int y = 0; y < Size.height; y++){
			for(int x = 0; x < Size.width; x++){
				setColor(x,y, RGB);			
			}
		}
		sendMessage();
	}

	/**
	 * sets the color to the Matrix
	 * @param cArray		2 Dimensional Color Array
	 */
	public void setColor(Color[][] cArray) {
		if(cArray.length != Size.width || cArray[0].length != Size.height)return;
		for(int y = 0; y < Size.height; y++){
			for(int x = 0; x < Size.width; x++){
				setColor(x,y, cArray[x][y]);			
			}
		}
		sendMessage();
	}
	
	/**
	 * sets the color to one point in the matrix
	 * @param x			x-coordinate
	 * @param y			y-coordinate
	 * @param c			Color
	 */
	protected void setColor(int x, int y, Color c) {
		colors[y*Size.width + x*((y+1)%2) + (Size.width- x- 1)*(y%2)] = c;
	}
	
}
