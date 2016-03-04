package de.thwildau.network;

import org.json.JSONException;
import org.json.JSONObject;

import com.light.controll.LampControll;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import de.thwildau.delightful.ControlActivity;
import de.thwildau.delightful.DelightfulActivity;
import de.thwildau.delightful.MainMenuActivity;
import de.thwildau.tools.UserData;

public class ResponseHandler {

	public static int handleResponse(Context context, String response){

		Log.i("RESPONSE", response);
		Intent intent = null;
		JSONObject json = null;

		try {
			json = new JSONObject(response);
			switch(json.getInt("id")){
			case -2:	//Toast
				Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
				break;

			case -1:	//Error
				Builder alert = new AlertDialog.Builder(context);
				alert.setTitle("Error");
				alert.setMessage(json.getString("message"));
				alert.setPositiveButton("Ok", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				AlertDialog alertDiag = alert.create();
				alertDiag.show();
				break;
			case 0:	//Login Success

				UserData.setUserLoggedIn(context, json.getString("user"));

				intent = new Intent(context, MainMenuActivity.class);
				intent.putExtra("user", json.getString("user"));
				context.startActivity(intent);
				break;
			case 2:	//bereits eingeloggt
				Log.i("SESSION","Session from " + json.getString("user"));
				break;
			case 3:	//logout

				LampControll LC = LampControll.getInstance();
				LC.closeAllLamps();
				intent = new Intent(context, DelightfulActivity.class);
				context.startActivity(intent);
				UserData.setUserLoggedIn(context, null);
				break;
			case 4: //session 
				// if user is logged in open the Main Menu Activity
				if(json.getInt("status") == 1) 
					intent = new Intent(context, MainMenuActivity.class);
				// if the user is not logged in open the Login Activity
				else if(json.getInt("status") == 0)
					intent = new Intent(context, DelightfulActivity.class);

				context.startActivity(intent);
				break;
			case 5: //lamps
				UserData.setLampCoordinates(context, json.getString("data"));
				break;
			case 6: //gradient
				UserData.setGradients(context, json.getString("data"));
				break;
		}
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return 0;
}
}
