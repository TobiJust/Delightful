package de.thwildau.delightful;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import de.thwildau.network.DelightfulHttpClient;
import de.thwildau.network.ResponseHandler;
import de.thwildau.tools.Constants;
import de.thwildau.tools.UserData;

public class DelightfulActivity extends Activity {

	TextView loginHeaderTextView;
	EditText loginUsernameEditText; // username input
	EditText loginPasswordEditText; // password input
	Button loginSubmitButton;

	// Database Response
	TextView returnMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delightful);
		loginHeaderTextView = (TextView) findViewById(R.id.login_header);
		loginUsernameEditText = (EditText) findViewById(R.id.login_username);
		loginPasswordEditText = (EditText) findViewById(R.id.login_password);
		loginSubmitButton = (Button) findViewById(R.id.login_submit);

		// just for test cases
		returnMessage = (TextView) findViewById(R.id.returnMessageView);

		Typeface header_typeface = Typeface.createFromAsset(getAssets(),
				"fonts/ITCEDSCR.TTF");
		loginHeaderTextView.setTypeface(header_typeface);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.delightful, menu);
		return true;
	}

	public void sendMessage(View view) {

		if(loginUsernameEditText.getText().toString().isEmpty() || loginPasswordEditText.getText().toString().isEmpty()){
			ResponseHandler.handleResponse(this, "{'id': -1, 'message': 'Benutzername/Passwort einfügen.'}");
			return;
		}
		// JUST FOR DEBUG REASONS SET DEBUG TO FALSE TO GET NORMAL RESULTS !!!
		if (Constants.DEBUG) {
			Intent intent = new Intent(this, MainMenuActivity.class);
			startActivity(intent);
		} else {
			// declare parameters that are passed to PHP script i.e. the name
			// "username" and its value submitted by user
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

			// define the parameter
			postParameters.add(new BasicNameValuePair("username",
					loginUsernameEditText.getText().toString()));
			postParameters.add(new BasicNameValuePair("password",
					loginPasswordEditText.getText().toString()));
		
			new DelightfulHttpClient(Constants.URL_LOGIN, postParameters, this).executeConnection();
//			UserData.getLayout(loginUsernameEditText.getText().toString());

		}
	}
	

}
