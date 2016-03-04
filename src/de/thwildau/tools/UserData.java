package de.thwildau.tools;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.light.lamptyps.Color;
import com.light.lamptyps.SimpleLamp;

import de.thwildau.network.DelightfulHttpClient;

public class UserData {

	public static Editor editor;
	private static String user;
	private static int roomID;
	private static byte[] layout;
	private static Color color;
	private static Color nColor;
	private static String lampObject;
	private static int sound;
	private static Set<String> allLamps;
	private static String gradientObject;
	
	public static HashMap<String, Integer> gradientTimeMap = new HashMap<String, Integer>();
	public static HashMap<String, int[]> gradientRedMap = new HashMap<String, int[]>();
	public static HashMap<String, int[]> gradientGreenMap = new HashMap<String, int[]>();
	public static HashMap<String, int[]> gradientBlueMap = new HashMap<String, int[]>();

	public static SharedPreferences getSharedPreferences(Context ctx) {
		return PreferenceManager.getDefaultSharedPreferences(ctx);
	}

	public static void setUserLoggedIn(Context ctx, String user){
		UserData.user = user;
		editor = getSharedPreferences(ctx).edit();
		editor.putString("user", user);
		editor.commit();
	}

	public static String getUser(Context ctx) {
		return getSharedPreferences(ctx).getString("user", null);
	}
	public static void setRoomLayout(byte[] layout){
		UserData.layout = layout;
	}
	public static byte[] getRoomLayout(){		
		return layout;
	}
	public static void setRoomID(Context ctx, int roomID) {
		UserData.roomID = roomID;
		editor = getSharedPreferences(ctx).edit();
		editor.putInt("room", roomID);
		editor.commit();
	}
	public static int getRoomID(){
		return roomID;
	}
	public static void setAlarmColor(Context ctx, Color alarmColor) {
		UserData.color = alarmColor;
		System.out.println(UserData.color);
		editor = getSharedPreferences(ctx).edit();
		editor.putInt("red", alarmColor.getRed());
		editor.putInt("green", alarmColor.getGreen());
		editor.putInt("blue", alarmColor.getBlue());
		editor.commit();		
	}
	public static Color getAlarmColor(Context ctx){
		int red = getSharedPreferences(ctx).getInt("red", 50);
		int green = getSharedPreferences(ctx).getInt("green", 50);
		int blue = getSharedPreferences(ctx).getInt("blue", 50);

		return new Color(red, green, blue);
	}
	public static void setNotificationColor(Context ctx, Color notiColor) {
		UserData.nColor = notiColor;
		System.out.println(UserData.nColor);
		editor = getSharedPreferences(ctx).edit();
		editor.putInt("nRed", notiColor.getRed());
		editor.putInt("nGreen", notiColor.getGreen());
		editor.putInt("nBlue", notiColor.getBlue());
		editor.commit();		
	}
	public static Color getNotificationColor(Context ctx){
		int red = getSharedPreferences(ctx).getInt("nRed", 50);
		int green = getSharedPreferences(ctx).getInt("nGreen", 50);
		int blue = getSharedPreferences(ctx).getInt("nBlue", 50);

		return new Color(red, green, blue);
	}
	public static void setAlarmSound(Context ctx, int alarmSound){
		UserData.sound = alarmSound;
		editor = getSharedPreferences(ctx).edit();
		editor.putInt("sound", alarmSound);
		editor.commit();
	}
	public static int getAlarmSound(Context ctx) {
		return getSharedPreferences(ctx).getInt("sound", 0);
	}
	public static void setLampCoordinates(Context context, String lampObject) {
		UserData.lampObject = lampObject;	
	}
	public static String getLampCoordinates() {
		return lampObject;
	}
	public static void setGradients(Context context, String gradientObject){
		UserData.gradientObject = gradientObject;
	}
	public static String getGradients() {
		return gradientObject;
	}

	public static void setLampAddresses(Context context, ArrayList<SimpleLamp> allLamps) {
		Set<String> lampSet = new HashSet<String>();
		for(SimpleLamp sl : allLamps){
			lampSet.add(sl.getIP());			
		}
		System.out.println("LampSet " + UserData.allLamps);
		editor = getSharedPreferences(context).edit();
		editor.remove("lamps").commit();
		System.out.println("TEST " + getSharedPreferences(context).getStringSet("lamps", null));

		editor.putStringSet("lamps", lampSet).commit();

		System.out.println("AFTER TEST " + getSharedPreferences(context).getStringSet("lamps", null));
		UserData.allLamps = getSharedPreferences(context).getStringSet("lamps", null);
	}
	public static Set<String> getLampAddresses(Context ctx) {
		System.out.println(getSharedPreferences(ctx).getStringSet("lamps", null));
		return getSharedPreferences(ctx).getStringSet("lamps", null);
	}

	/**
	 * User Logout by posting a HTTPRequest - Login Screen.
	 * 
	 * @param ctx Context from which the User logged out.
	 * @param user The User who logged out itself.
	 */
	public static void logout(Context ctx, String user) {
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

		// define the parameter
		postParameters.add(new BasicNameValuePair("username", user));
		new DelightfulHttpClient(Constants.URL_LOGOUT, postParameters, ctx).executeConnection();

		//Remove all UserData
		getSharedPreferences(ctx).edit().clear().commit();
		roomID = -1;
		layout = null;
		gradientObject = null;
		lampObject = null;
	}


	public static void getLayout(String userID){

		new UserData().new DownloadLayout().execute(Constants.URL_LAYOUT_IMAGE+"?id="+userID);

	}
	private byte[] getLayoutImage(String url){
		Log.i("LAYOUT", "Try access to " + url);
		try {
			URL imageUrl = new URL(url);
			URLConnection ucon = imageUrl.openConnection();

			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(500);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}
			return baf.toByteArray();
		} catch (Exception e) {
			Log.d("ImageManager", "Error: " + e.toString());
		}
		return null;
	}
	public class DownloadLayout extends AsyncTask<String, Void, Object>{

		@Override
		protected Object doInBackground(String... urls) {
			return getLayoutImage(urls[0]);
		}
		@Override
		protected void onPostExecute(Object layout){
			System.out.println("layout " + layout);
			byte[] layoutImage = (byte[]) layout;
			UserData.setRoomLayout(layoutImage);
		}
	}

}
