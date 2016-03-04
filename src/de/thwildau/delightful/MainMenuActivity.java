package de.thwildau.delightful;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import de.thwildau.network.DelightfulHttpClient;
import de.thwildau.tools.Constants;
import de.thwildau.tools.UserData;

public class MainMenuActivity extends Activity implements OnClickListener {

	static final String STATE_LAYOUT = "userLayout"; 

	TextView mainMenuHeaderTextView;
	Button logoutButton;
	Button serviceButton;
	Button controlButton;
	Button heatingButton;
	Button activitiesButton;
	Button alarmButton;
	Button informationButton;
	private String userID;

	Intent intent = null;


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_menu_logout_button:
			UserData.setUserLoggedIn(this, userID);
			UserData.logout(this, userID);
			// Logout Button
			break;
		case R.id.main_menu_button_service:
			intent = null;
			Toast.makeText(this, "Choose your hotel service options\n\t\tNot implemented yet.", Toast.LENGTH_LONG).show();
			// Service Button
			break;
		case R.id.main_menu_button_control:
			intent = new Intent(this, ControlActivity.class);
			// Control Button
			break;
		case R.id.main_menu_button_heating:
			intent = null;
			Toast.makeText(this, "Information to the heating behavior/temperature\n\t\t\t\tNot implemented yet.", Toast.LENGTH_LONG).show();
			// Heating Button
			break;
		case R.id.main_menu_button_activities:
			intent = new Intent(this, SupplyItemListActivity.class);
			// Activities Button
			break;
		case R.id.main_menu_button_alarm:
			// Alarm Button
			intent = new Intent(this, AlarmActivity.class);
			break;
		case R.id.main_menu_button_information:
			// Information Button
			Toast.makeText(this, "Information about Delightful\n\tNot implemented yet.", Toast.LENGTH_LONG).show();
			intent = null;
			break;
		}
		if (intent != null){
			intent.putExtra("user", userID);
			showProgressDialog();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu_layout);

		System.out.println("ROOM LAYOUT " + UserData.getRoomLayout());
		System.out.println("ROOM LAYOUT " + UserData.getUser(this));

		userID = UserData.getUser(this);
		System.out.println("UserID " + userID);
		if(UserData.getRoomLayout() == null)
			UserData.getLayout(userID);
		if(UserData.getLampCoordinates() == null){
			ArrayList<NameValuePair> lampParameters = new ArrayList<NameValuePair>();
			lampParameters.add(new BasicNameValuePair("username", userID));
			new DelightfulHttpClient(Constants.URL_LAMP, lampParameters, this).executeConnection();
		}
		if(UserData.getGradients() == null){
			ArrayList<NameValuePair> gradientParameters = new ArrayList<NameValuePair>();
			gradientParameters.add(new BasicNameValuePair("user", userID));
			new DelightfulHttpClient(Constants.URL_GET_GRADIENT, gradientParameters, this).executeConnection();
		}
		mainMenuHeaderTextView = (TextView) findViewById(R.id.main_menu_header);

		Typeface header_typeface = Typeface.createFromAsset(getAssets(),
				"fonts/ITCEDSCR.TTF");
		mainMenuHeaderTextView.setTypeface(header_typeface);

		logoutButton = (Button) findViewById(R.id.main_menu_logout_button);
		serviceButton = (Button) findViewById(R.id.main_menu_button_service);
		controlButton = (Button) findViewById(R.id.main_menu_button_control);
		heatingButton = (Button) findViewById(R.id.main_menu_button_heating);
		activitiesButton = (Button) findViewById(R.id.main_menu_button_activities);
		alarmButton = (Button) findViewById(R.id.main_menu_button_alarm);
		informationButton = (Button) findViewById(R.id.main_menu_button_information);

		logoutButton.setOnClickListener(this);
		serviceButton.setOnClickListener(this);
		controlButton.setOnClickListener(this);
		heatingButton.setOnClickListener(this);
		activitiesButton.setOnClickListener(this);
		alarmButton.setOnClickListener(this);
		informationButton.setOnClickListener(this);

	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){
		if(UserData.getRoomLayout() != null)
			savedInstanceState.putByteArray(STATE_LAYOUT, UserData.getRoomLayout());
	}
	/**
	 * Executed when the back button is pressed.
	 */
	@Override
	public void onBackPressed() {
		Toast.makeText(this, "Bitte loggen Sie sich aus, wenn Sie die Anwendung beenden möchten.", Toast.LENGTH_LONG).show();

	}
	private void showProgressDialog(){
		final ProgressDialog dialog = ProgressDialog.show(this, "", "Loading...", true);

		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					if(UserData.getLampCoordinates() != null && UserData.getRoomLayout() != null && UserData.getGradients() != null){
						startActivity(intent);
						dialog.dismiss();
						break;
					}
				}
			}
		}).start();
	}
}
