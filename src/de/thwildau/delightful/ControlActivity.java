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
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.light.controll.LampControll;
import com.light.lamptyps.SimpleLamp;

import de.thwildau.tools.Scenes;
import de.thwildau.tools.UserData;
import de.thwildau.tools.ZoomListener;

public class ControlActivity extends Activity implements
OnItemSelectedListener, OnSeekBarChangeListener, OnClickListener {
	protected static final int UPDATE_BUTTON = 0;
	protected static final long DELAY = 200;

	TextView mainMenuHeaderTextView;

	Spinner themeSpinner;
	ArrayAdapter<String> spinnerAdapter;
	ImageView layoutImageView;
	RelativeLayout layoutBackgroundLinearLayout;
	RelativeLayout layoutBackground;

	SeekBar masterSeekBar;
	SeekBar redSeekBar;
	SeekBar greenSeekBar;
	SeekBar blueSeekBar;

	ToggleButton toggleButton1;
	ToggleButton toggleButton2;
	Button logoutButton;
	Button scenarioButton;
	Button playlistButton;
	Button eurythmButton;
	Button playScenarioButton;

	GradientDrawable drawable = new GradientDrawable();

	String userID;
	private int sceneID;
	private String sceneName;
	protected boolean isScenePlaying;

	//	private HashMap<String, Integer> gradientTimeMap = new HashMap<String, Integer>();
	//	private HashMap<String, int[]> gradientRedMap = new HashMap<String, int[]>();
	//	private HashMap<String, int[]> gradientGreenMap = new HashMap<String, int[]>();
	//	private HashMap<String, int[]> gradientBlueMap = new HashMap<String, int[]>();

	private ArrayList<ToggleButton> allToggleButtons = new ArrayList<ToggleButton>();
	private ArrayList<ToggleButton> chosenLamps = new ArrayList<ToggleButton>();

	private Handler playHandler = new Handler();
	public LampControll LC = LampControll.getInstance();

	private final int CUSTOM_SCENE_ID = 100;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.control_layout);

		userID = getIntent().getStringExtra("user");

		mainMenuHeaderTextView = (TextView) findViewById(R.id.control_header);

		themeSpinner = (Spinner) findViewById(R.id.control_spinner_theme);
		layoutImageView = (ImageView) findViewById(R.id.control_room_layout);
		layoutBackgroundLinearLayout = (RelativeLayout) findViewById(R.id.control_room_layout_background);
		layoutBackground = (RelativeLayout) findViewById(R.id.control_layout_background);

		masterSeekBar = (SeekBar) findViewById(R.id.control_seekBar_master);
		redSeekBar = (SeekBar) findViewById(R.id.control_seekbar_red);
		greenSeekBar = (SeekBar) findViewById(R.id.control_seekbar_green);
		blueSeekBar = (SeekBar) findViewById(R.id.control_seekbar_blue);
		// set heading type face
		Typeface header_typeface = Typeface.createFromAsset(getAssets(),
				"fonts/ITCEDSCR.TTF");
		mainMenuHeaderTextView.setTypeface(header_typeface);

		//add layout to ImageView
		layoutImageView.setImageBitmap(BitmapFactory.decodeByteArray(UserData.getRoomLayout(), 0, UserData.getRoomLayout().length));

		// set content for drop down list
		themeSpinner.setOnItemSelectedListener(this);
		spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		themeSpinner.setAdapter(spinnerAdapter);
		spinnerAdapter.addAll(getResources().getStringArray(R.array.themes));
		spinnerAdapter.notifyDataSetChanged();

		drawable.setShape(GradientDrawable.OVAL);
		addLampsToLayout(UserData.getLampCoordinates());
		if(!UserData.getGradients().equals("-1"))
			addGradientsToSpinner(UserData.getGradients());

		// add listener to seek bars
		masterSeekBar.setOnSeekBarChangeListener(this);
		redSeekBar.setOnSeekBarChangeListener(this);
		greenSeekBar.setOnSeekBarChangeListener(this);
		blueSeekBar.setOnSeekBarChangeListener(this);

		// add listener to image view
		layoutBackground.setOnTouchListener(new ZoomListener(layoutBackground, layoutBackgroundLinearLayout));
		logoutButton = (Button) findViewById(R.id.control_logout_button);
		logoutButton.setOnClickListener(this);
		scenarioButton = (Button) findViewById(R.id.button_scenario);
		scenarioButton.setOnClickListener(this);
		playlistButton = (Button) findViewById(R.id.button_playlist);
		playlistButton.setOnClickListener(this);
		playScenarioButton = (Button) findViewById(R.id.control_play_button);
		playScenarioButton.setOnClickListener(this);
		eurythmButton = (Button) findViewById(R.id.button_eurythmie);
		eurythmButton.setOnClickListener(this);

		playHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				if(msg.what == UPDATE_BUTTON)
					playScenarioButton.setBackgroundResource(R.drawable.play_button);
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
		Intent intent = new Intent(this, MainMenuActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);

	}
	public void addGradientsToSpinner(String data){

		try {
			JSONArray arr = new JSONArray(data);
			System.out.println("DATA " + arr.length());
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

			//			gradientTimeMap.put(sc_name, sc_time);
			//			gradientRedMap.put(sc_name, parseColorString(sc_color_red));
			//			gradientGreenMap.put(sc_name, parseColorString(sc_color_green));
			//			gradientBlueMap.put(sc_name, parseColorString(sc_color_blue));

			UserData.gradientTimeMap.put(sc_name, sc_time);
			UserData.gradientRedMap.put(sc_name, parseColorString(sc_color_red));
			UserData.gradientGreenMap.put(sc_name, parseColorString(sc_color_green));
			UserData.gradientBlueMap.put(sc_name, parseColorString(sc_color_blue));

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
		System.out.println("DIMENSION " + layoutBackground.getWidth() + "  " + layoutImageView.getHeight());
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

		transformColorAndSetBars(0xFFFFFF);
		sceneID = CUSTOM_SCENE_ID ;
		sceneName = (String) parent.getItemAtPosition(pos);
		System.out.println(sceneName);

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

		int power = masterSeekBar.getProgress();
		int red = redSeekBar.getProgress();
		int green = greenSeekBar.getProgress();
		int blue = blueSeekBar.getProgress();

		sendColors(power, red, green, blue);

		for(ToggleButton lamp : chosenLamps){
			//			if(power == 0){
			////				lamp.setChecked(false);
			//				drawable.setColor(Color.rgb(255, 190, 0));	
			//
			//			}
			//			else{
			drawable.setStroke(4, Color.argb(power*2, red, green, blue));
			lamp.setChecked(true);
			drawable.setColor(Color.rgb(155, 205, 50));
			//			}
			lamp.setBackgroundDrawable(drawable);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	private void setColor(int power, int red, int green, int blue) {
		Log.e("color", "P: " + power + " R: " + red + " G: " + green + " B: " + blue);

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
		if(v == scenarioButton){
			Intent intent = new Intent(this, GradientActivity.class);
			startActivity(intent);
		}
		if(v == playlistButton){
			Intent intent = new Intent(this, PlaylistActivity.class);
			startActivity(intent);
		}
		if(v == playScenarioButton){
			if(!isScenePlaying){
				isScenePlaying = true;
				playScenarioButton.setBackgroundResource(R.drawable.stop_button);
				sendScenario();
			}
			else{
				isScenePlaying = false;
				playScenarioButton.setBackgroundResource(R.drawable.play_button);
			}

		}
		if(v == eurythmButton){
			Toast.makeText(this, "Eurythmics currently not implemented.", Toast.LENGTH_LONG).show();
		}
		int power = masterSeekBar.getProgress();
		int red = redSeekBar.getProgress();
		int green = greenSeekBar.getProgress();
		int blue = blueSeekBar.getProgress();
		for (ToggleButton b : allToggleButtons)
			if (v == b){
				try {
					System.out.println("b.getId() " + b.getId());
					drawable.setStroke(4, Color.argb(power*2, red, green, blue));
					if (b.isChecked()) {
						chosenLamps.add(b);
						LC.addTubeLamp(b.getId(), (String) b.getTag(), 1234, 20);
						drawable.setColor(Color.rgb(155, 205, 50));
					}
					else {
						chosenLamps.remove(b);
						LC.removeTubeLamp(b.getId());
						//						drawable.setColor(Color.rgb(255, 190, 0));
					}
					b.setBackgroundDrawable(drawable);
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
							runtime += (double)DELAY/1000;
							index++;
							System.out.println(runtime + "  " + index + " " + sceneTime + " " + redColors.length);
							Thread.sleep(DELAY);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else{
						break;
					}
				}
				isScenePlaying = false;
				Message msg = playHandler.obtainMessage();
				msg.what = UPDATE_BUTTON;
				msg.obj = playScenarioButton;
				playHandler.sendMessage(msg);

			}
		}).start();
	}

	private void sendScenario(){
		System.out.println("SCENE ID " + sceneID);
		if(sceneID == CUSTOM_SCENE_ID)
			playScenario(sceneName);
		else{
			com.light.lamptyps.Color c = new com.light.lamptyps.Color(0,0,0);
			// get Lamps
			for (Iterator<SimpleLamp> iter = LC.getLamps(); iter.hasNext();) {
				SimpleLamp lamp = iter.next();
				lamp.setScenario(sceneID);
			}
		}
	}
}
