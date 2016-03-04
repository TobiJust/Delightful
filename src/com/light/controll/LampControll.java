package com.light.controll;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

import android.os.StrictMode;

import com.light.lamptyps.Dimension;
import com.light.lamptyps.MatrixLamp;
import com.light.lamptyps.SimpleLamp;
import com.light.lamptyps.TubeLamp;

/**
 * LampControll controlls all created lamps
 * is implemented as a singleton
 * @author Fabian
 * @version 1.1
 */
public class LampControll {
	
	/**Singleton Instance of the LampControll*/
	private static LampControll instance = null;

	/**ArrayList of all Lamps*/
	ArrayList<SimpleLamp> lamplist = new ArrayList<SimpleLamp>();

	/**constuctor*/
	public LampControll(){
		System.out.println("LAMP CONTROLL");
	}

	/**
	 * Method get instance returns a pointer to the Object instance
	 * @return 	a pointer to the object instance of LampControll
	 */
	public static LampControll getInstance(){
		if(instance == null){
			instance = new LampControll();
		}
		return instance;
	}

	/**
	 * Getter of the Lamp List
	 * @return 	returns an interator to the lamplist
	 */
	public Iterator<SimpleLamp> getLamps(){
		return lamplist.iterator();
	}

	/**
	 * Getter to the LampList
	 * @return 		returns the Arraylist of the lamplist
	 */
	public ArrayList<SimpleLamp> getAllLamps(){
		return lamplist;
	}

	/**
	 * Method returns a specific lamp by ID
	 * @param ID	ID of the lamp
	 * @return		returns the lamp object or null
	 */
	public SimpleLamp getLamp(int ID){
		Iterator<SimpleLamp> iterator = lamplist.iterator();
		while (iterator.hasNext()){
			SimpleLamp SL = iterator.next();
			if(SL.getID() == ID){
				return SL;
			}
		}
		return null;
	}
	
	/**
	 * Method creates a new simple Lamp 
	 * @param id		ID
	 * @param IP		IP Address of the lamp
	 * @param Port		Port of the lamp
	 * @throws UnknownHostException 	HostException
	 */
	public void addLamp(int id, String IP, int Port) throws UnknownHostException{
		SimpleLamp SL = new SimpleLamp(id, IP, Port);
		lamplist.add(SL);
	}
	
	/**
	 * Method creates a new tube Lamp
	 * @param id		ID
	 * @param IP		IP Address
	 * @param Port		Port
	 * @param length	Length of the Lamp
	 * @throws UnknownHostException 	HostException
	 */
	public void addTubeLamp(int id, String IP, int Port, int length) throws UnknownHostException{
		TubeLamp TL = new TubeLamp(id, IP, Port, length);
		//		TL.switchOn();
		lamplist.add(TL);
		System.out.println("LAMPLIST SIZE " + lamplist.size());
	}
	
	/**
	 * Method removes a TubeLamp by ID
	 * @param id	ID of the Tubelamp
	 */
	public void removeTubeLamp(int id){
		for(int i = 0; i < lamplist.size(); i++){
			SimpleLamp SL = lamplist.get(i);
			if(SL.getID() == id){
				SL.closeLamp();
				lamplist.remove(SL);
			}
		}
	}

	/**
	 * Method adds a Matrix Lamp
	 * @param id			ID
	 * @param IP			IP Address
	 * @param Port			Port
	 * @param xDimension	X Dimension
	 * @param yDimension	Y Dimension
	 * @throws UnknownHostException
	 */
	public void addMatrixLamp(int id, String IP, int Port, int xDimension, int yDimension) throws UnknownHostException{
		MatrixLamp ML = new MatrixLamp(id, IP, Port, new Dimension(xDimension, yDimension));
		lamplist.add(ML);
	}
	
	/**
	 * Method removes matrix lamp by ID
	 * @param id		ID of the matrix lamp
	 */
	public void removeMatrixLamp(int id){
		lamplist.get(id).closeLamp();
		lamplist.remove(id);
	}
	
	/**
	 * Method closes the TCP connections of all lamps
	 */
	public void closeAllLamps(){
		Iterator<SimpleLamp> iterator = lamplist.iterator();
		while (iterator.hasNext()) {
			SimpleLamp SL = iterator.next();
			SL.closeLamp();
		}
	}
}
