package de.thwildau.delightful;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import de.thwildau.network.DelightfulHttpClient;
import de.thwildau.tools.Constants;
import de.thwildau.tools.Scenes;
import de.thwildau.tools.UserData;

public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		if(Constants.DEBUG){
			Intent login = new Intent(this, DelightfulActivity.class);
			startActivity(login);
		}

		System.out.println("User " + UserData.getUser(this));

		//Check for User
		if(UserData.getUser(this) != null){
			Log.i("SESSION","Valid Session");
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			// define the parameter
			postParameters.add(new BasicNameValuePair("username", UserData.getUser(this)));

			new DelightfulHttpClient(Constants.URL_LOGIN_REQUEST, postParameters, this).executeConnection();

			//Check for layout
			if(UserData.getRoomLayout() != null){
				Log.i("LAYOUT","Layout from User " + UserData.getRoomLayout());
			}
			//Database Request for Layout
			else{
				Log.i("LAYOUT","No Layout from User");
				UserData.getLayout(UserData.getUser(this));
			}
			//Check for Lamp Coordinates
			if(UserData.getLampCoordinates() != null){
				Log.i("LAMPS","Lamps from User " + UserData.getLampCoordinates());
			}
			else{
				Log.i("LAMPS","No Lamps from User");
				ArrayList<NameValuePair> lampParameters = new ArrayList<NameValuePair>();
				lampParameters.add(new BasicNameValuePair("username", UserData.getUser(this)));

				new DelightfulHttpClient(Constants.URL_LAMP, lampParameters, this).executeConnection();
			}
			//Check for gradients for this user
			if(UserData.getGradients() != null){
				Log.i("GRADIENTS","Gradients from User " + UserData.getGradients());
			}
			else{
				Log.i("GRADIENTS","No Gradients from User");
				ArrayList<NameValuePair> gradientParameters = new ArrayList<NameValuePair>();
				gradientParameters.add(new BasicNameValuePair("user", UserData.getUser(this)));

				new DelightfulHttpClient(Constants.URL_GET_GRADIENT, gradientParameters, this).executeConnection();
			}

		}
		//Database Request for User
		else if(UserData.getUser(this) == null){
			Log.i("SESSION","No Session from User - Login");
			Intent login = new Intent(this, DelightfulActivity.class);
			startActivity(login);
		}
		else
			Log.e("SESSION", "Session Error - No User Data");

		UserData.gradientTimeMap.put("Sonnenaufgang", Scenes.SCENE_TIME);
		UserData.gradientRedMap.put("Sonnenaufgang", Scenes.SCENE_SUNRISE_RED);
		UserData.gradientGreenMap.put("Sonnenaufgang", Scenes.SCENE_SUNRISE_GREEN);
		UserData.gradientBlueMap.put("Sonnenaufgang", Scenes.SCENE_SUNRISE_BLUE);

		UserData.gradientTimeMap.put("Abend", Scenes.SCENE_TIME);
		UserData.gradientRedMap.put("Abend", Scenes.SCENE_EVE_RED);
		UserData.gradientGreenMap.put("Abend", Scenes.SCENE_EVE_GREEN);
		UserData.gradientBlueMap.put("Abend", Scenes.SCENE_EVE_BLUE);

		UserData.gradientTimeMap.put("Kühl", Scenes.SCENE_TIME);
		UserData.gradientRedMap.put("Kühl", Scenes.SCENE_COOL_RED);
		UserData.gradientGreenMap.put("Kühl", Scenes.SCENE_COOL_GREEN);
		UserData.gradientBlueMap.put("Kühl", Scenes.SCENE_COOL_BLUE);

		UserData.gradientTimeMap.put("Warm", Scenes.SCENE_TIME);
		UserData.gradientRedMap.put("Warm", Scenes.SCENE_WARM_RED);
		UserData.gradientGreenMap.put("Warm", Scenes.SCENE_WARM_GREEN);
		UserData.gradientBlueMap.put("Warm", Scenes.SCENE_WARM_BLUE);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

}
