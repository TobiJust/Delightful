package de.thwildau.delightful;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.light.controll.LampControll;
import com.light.lamptyps.SimpleLamp;

import de.thwildau.tools.UserData;
import de.thwildau.tools.ZoomListener;

public class PlaylistActivity extends Activity implements
OnItemSelectedListener, OnClickListener {
	TextView mainMenuHeaderTextView;

	Spinner themeSpinner, themeSpinner2;
	ArrayAdapter<String> spinnerAdapter;
	ImageView layoutImageView;
	RelativeLayout layoutBackgroundLinearLayout;
	RelativeLayout layoutBackground;
	LinearLayout linearLayout, newLayout;

	ToggleButton toggleButton1;
	ToggleButton toggleButton2;
	Button logoutButton;
	Button backButton;
	Button playButton;
	private Button addButton;

	private int spinnerCount = 2;

	String userID;
	private int sceneID;
	private String sceneName;

	private HashMap<String, Integer> gradientTimeMap = new HashMap<String, Integer>();
	private HashMap<String, int[]> gradientRedMap = new HashMap<String, int[]>();
	private HashMap<String, int[]> gradientGreenMap = new HashMap<String, int[]>();
	private HashMap<String, int[]> gradientBlueMap = new HashMap<String, int[]>();

	private ArrayList<ToggleButton> allToggleButtons = new ArrayList<ToggleButton>();
	private ArrayList<ToggleButton> chosenLamps = new ArrayList<ToggleButton>();
	private ArrayList<String> playlist = new ArrayList<String>(); 

	public LampControll LC = LampControll.getInstance();

	protected boolean isScenePlaying = false;

	private final int CUSTOM_SCENE_ID = 100;

	protected Handler playHandler = new Handler();

	private ArrayList<Spinner> spinnerList = new ArrayList<Spinner>();



	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playlist);

		userID = getIntent().getStringExtra("user");

		mainMenuHeaderTextView = (TextView) findViewById(R.id.control_header);

		themeSpinner = (Spinner) findViewById(R.id.playlist_spinner);
		themeSpinner2 = (Spinner) findViewById(R.id.playlist_spinner_2);
		layoutImageView = (ImageView) findViewById(R.id.playlist_room_layout);
		layoutBackgroundLinearLayout = (RelativeLayout) findViewById(R.id.playlist_room_layout_background);
		layoutBackground = (RelativeLayout) findViewById(R.id.playlist_layout_background);
		linearLayout = (LinearLayout) findViewById(R.id.linearLayout1);
		// set heading type face
		Typeface header_typeface = Typeface.createFromAsset(getAssets(),
				"fonts/ITCEDSCR.TTF");
		mainMenuHeaderTextView.setTypeface(header_typeface);

		//add layout to ImageView
		layoutImageView.setImageBitmap(BitmapFactory.decodeByteArray(UserData.getRoomLayout(), 0, UserData.getRoomLayout().length));

		// set content for drop down list
		themeSpinner.setOnItemSelectedListener(this);
		themeSpinner2.setOnItemSelectedListener(this);
		spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		themeSpinner.setAdapter(spinnerAdapter);
		themeSpinner2.setAdapter(spinnerAdapter);
		spinnerAdapter.addAll(getResources().getStringArray(R.array.themes));
		spinnerAdapter.notifyDataSetChanged();

		spinnerList.add(themeSpinner);
		spinnerList.add(themeSpinner2);

		addLampsToLayout(UserData.getLampCoordinates());
		if(!UserData.getGradients().equals("-1"))
			addGradientsToSpinner(UserData.getGradients());

		// add listener to image view
		layoutBackground.setOnTouchListener(new ZoomListener(layoutBackground, layoutBackgroundLinearLayout));

		logoutButton = (Button) findViewById(R.id.playlist_button_logout);
		logoutButton.setOnClickListener(this);
		backButton = (Button) findViewById(R.id.playlist_button_back);
		backButton.setOnClickListener(this);
		playButton = (Button) findViewById(R.id.playlist_button_play);
		playButton.setOnClickListener(this);
		addButton = (Button) findViewById(R.id.playlist_button_add);
		addButton.setOnClickListener(this);
		playHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				if(msg.what == ControlActivity.UPDATE_BUTTON)
					playButton.setBackgroundResource(R.drawable.play_button);
			}
		};
	}
	/**
	 * Executed when the back button is pressed.
	 * If the user presses the back button twice the application is minimized.
	 */
	@Override
	public void onBackPressed() {
		LC.closeAllLamps();
		Intent intent = new Intent(this, ControlActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);

	}
	public void addGradientsToSpinner(String data){

		try {
			JSONArray arr = new JSONArray(data);
			for(int l = 0; l < arr.length(); l++)
				parseGradientObjectString(arr.getString(l));				
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	private void parseGradientObjectString(String object){
		try {
			JSONObject gradientJSON = new JSONObject(object);
			String sc_name = gradientJSON.getString("scenario_name");
			int sc_time = gradientJSON.getInt("scenario_time");
			String sc_color_red = gradientJSON.getString("scenario_red");
			String sc_color_green = gradientJSON.getString("scenario_green");
			String sc_color_blue = gradientJSON.getString("scenario_blue");

			gradientTimeMap.put(sc_name, sc_time);
			gradientRedMap.put(sc_name, parseColorString(sc_color_red));
			gradientGreenMap.put(sc_name, parseColorString(sc_color_green));
			gradientBlueMap.put(sc_name, parseColorString(sc_color_blue));

			spinnerAdapter.add(sc_name);
			spinnerAdapter.notifyDataSetChanged();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private int[] parseColorString(String colorList){
		try {
			JSONArray json = new JSONArray(colorList);
			int[] returnList = new int[json.length()];
			for(int i = 0; i < json.length(); i++)
				returnList[i] = json.optInt(i);
			return returnList;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public void addLampsToLayout(String data){
		ArrayList<String[]> buttons = parseLampObjectString(data);
		RelativeLayout.LayoutParams params;
		ToggleButton toggle;
		int id = 0;
		for(String[] b : buttons){

			params = new RelativeLayout.LayoutParams(45, 45);
			toggle = new ToggleButton(this);
			params.leftMargin = Integer.parseInt(b[1]);
			params.topMargin = Integer.parseInt(b[2]);
			toggle.setBackgroundResource(R.drawable.selector);
			toggle.setText("OFF");
			toggle.setTextOff("OFF");
			toggle.setTextOn("ON");
			toggle.setTextSize(11);
			//			toggle.setTypeface(Typeface.DEFAULT_BOLD);
			toggle.setHeight(40);
			toggle.setWidth(40);
			toggle.setId(id++);
			toggle.setOnClickListener(this);
			toggle.setTag(b[0]);	//IP
			layoutBackgroundLinearLayout.addView(toggle, params);

			allToggleButtons.add(toggle);
		}

	}
	private ArrayList<String[]> parseLampObjectString(String object){
		String lamps = object.substring(object.indexOf("[")+1, object.lastIndexOf("]"));
		ArrayList<String[]> lampButtons = new ArrayList<String[]>();
		int current = 0;
		while(true){
			current = lamps.indexOf("]");
			if(current == -1)
				break;
			String lamp = lamps.substring(lamps.indexOf("[")+1, current);
			current++;
			lamps = lamps.substring(current);
			lampButtons.add(parseLampString(lamp));
		}
		return lampButtons;

	}
	private String[] parseLampString(String lamp){
		String[] elements = lamp.split(",");
		String[] lampValues = new String[3];
		lampValues[0] = elements[0].replace("\"", "");	// ip
		lampValues[1] = elements[1].replace("\"", "");	// x
		lampValues[2] = elements[2].replace("\"", "");	// y

		return lampValues;
	}
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	private void transformColorAndSetBars(int color) {
		int red = (color >> 16) & 255;
		int green = (color >> 8) & 255;
		int blue = color & 255;

	}

	@Override
	public void onClick(View v) {
		if(v == logoutButton)
			UserData.logout(this, UserData.getUser(this));
		if(v == backButton){
			onBackPressed();
		}
		if(v == playButton){
			if(!isScenePlaying){
				for(Spinner sp : spinnerList){
					playlist.add((String) sp.getSelectedItem());
				}
				System.out.println(playlist.size());
				isScenePlaying = true;
				playButton.setBackgroundResource(R.drawable.stop_button);
				playScenario(playlist.get(0));
			}
			else{
				playlist.clear();
				isScenePlaying = false;
				playButton.setBackgroundResource(R.drawable.play_button);
			}
		}
		if(v == addButton){
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(0, 0, 0, 10);
			newLayout = (LinearLayout) View.inflate(this, R.layout.layout_playlist_element, null);

			if(spinnerCount < 3 ){
				linearLayout.addView(newLayout, spinnerCount+2, layoutParams);
				TextView numberView = (TextView)(linearLayout.getChildAt(spinnerCount+2).findViewById(R.id.numberView));
				Spinner spinnerView = (Spinner)(linearLayout.getChildAt(spinnerCount+2).findViewById(R.id.newSpinnerView));

				numberView.setText("" + ++spinnerCount);
				spinnerView.setAdapter(spinnerAdapter);
				spinnerList.add(spinnerView);
			}
			else
				Toast.makeText(this, "Add more will deform View", Toast.LENGTH_LONG).show();

		}
		for (ToggleButton b : allToggleButtons)
			if (v == b){
				System.out.println("b.getId() " + b.getId());
				try {
					if (b.isChecked()) {
						chosenLamps.add(b);
						LC.addTubeLamp(b.getId(), (String) b.getTag(), 1234, 20);
					}
					else {
						chosenLamps.remove(b);
						LC.removeTubeLamp(b.getId());
					}
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	private void sendColors(int power, int red, int green, int blue) {
		try {

			red = (power * red) / 100;
			green = (power * green) / 100;
			blue = (power * blue) / 100;

			com.light.lamptyps.Color c = new com.light.lamptyps.Color(red,
					green, blue);
			// get Lamps
			for (Iterator<SimpleLamp> iter = LC.getLamps(); iter.hasNext();) {
				SimpleLamp lamp = iter.next();
				lamp.setColor(c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	int sceneIndex = 0;
	private void playScenario(String scene){
		final int sceneTime = UserData.gradientTimeMap.get(scene);
		final int[] redColors = UserData.gradientRedMap.get(scene);
		final int[] greenColors = UserData.gradientGreenMap.get(scene);
		final int[] blueColors = UserData.gradientBlueMap.get(scene);

		new Thread(new Runnable() {

			double runtime = 0;
			int index = 0;
			@Override
			public void run() {
				while(isScenePlaying){
					if(runtime < sceneTime && index < redColors.length){
						sendColors(50, redColors[(int) index], greenColors[(int) index], blueColors[(int) index]);
						try {
							runtime += (double)ControlActivity.DELAY/1000;
							index++;
							Thread.sleep(ControlActivity.DELAY);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else{
						break;
					}
				}
				sceneIndex++;
				if(sceneIndex < playlist.size()){
					playScenario(playlist.get(sceneIndex));
				}
				else{
					isScenePlaying = false;
					playlist.clear();
					Message msg = playHandler .obtainMessage();
					msg.what = ControlActivity.UPDATE_BUTTON;
					msg.obj = playButton;
					playHandler.sendMessage(msg);
				}

			}
		}).start();
	}
}
