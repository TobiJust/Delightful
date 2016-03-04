package de.thwildau.delightful;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.light.controll.LampControll;
import com.light.lamptyps.Color;
import com.light.lamptyps.SimpleLamp;

import de.thwildau.tools.Alarm;
import de.thwildau.tools.AlarmSettingsDialog;
import de.thwildau.tools.UserData;
import de.thwildau.tools.ZoomListener;

public class AlarmActivity extends Activity implements OnClickListener, OnItemSelectedListener{

	TextView mainMenuHeaderTextView;
	RelativeLayout layoutBackgroundLinearLayout;
	RelativeLayout layoutBackground;


	ImageView layoutImageView;
	TimePicker alarmClock;
	TextView alarmTime;
	Switch switcher;
	Spinner themeSpinner;
	ArrayAdapter<String> spinnerAdapter;

	private HashMap<String, Integer> gradientTimeMap = new HashMap<String, Integer>();
	private HashMap<String, int[]> gradientRedMap = new HashMap<String, int[]>();
	private HashMap<String, int[]> gradientGreenMap = new HashMap<String, int[]>();
	private HashMap<String, int[]> gradientBlueMap = new HashMap<String, int[]>();

	private ArrayList<ToggleButton> allToggleButtons = new ArrayList<ToggleButton>();
	private Button logoutButton, buttonStart, buttonSettings;
	private Alarm alarm = new Alarm();

	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	Calendar calendar = Calendar.getInstance();

	public LampControll LC = LampControll.getInstance();

	private Color alarmColor = new Color(5,5,5);
	private int alarmSound;
	private int wakeDuration;
	private int maxBrightness;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.alarm_layout);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// set heading type face
		mainMenuHeaderTextView = (TextView) findViewById(R.id.alarm_header);
		Typeface header_typeface = Typeface.createFromAsset(getAssets(),
				"fonts/ITCEDSCR.TTF");
		mainMenuHeaderTextView.setTypeface(header_typeface);

		layoutImageView = (ImageView) findViewById(R.id.alarm_room_layout);
		layoutBackgroundLinearLayout = (RelativeLayout) findViewById(R.id.alarm_room_layout_background);
		layoutBackground = (RelativeLayout) findViewById(R.id.alarm_layout_background);

		layoutBackground.setOnTouchListener(new ZoomListener(layoutBackground, layoutBackgroundLinearLayout));
		//add layout to ImageView
		layoutImageView.setImageBitmap(BitmapFactory.decodeByteArray(UserData.getRoomLayout(), 0, UserData.getRoomLayout().length));


		themeSpinner = (Spinner) findViewById(R.id.alarm_spinner_theme);
		themeSpinner.setOnItemSelectedListener(this);
		spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		themeSpinner.setAdapter(spinnerAdapter);
		spinnerAdapter.addAll(getResources().getStringArray(R.array.themes));
		spinnerAdapter.notifyDataSetChanged();

		addLampsToLayout(UserData.getLampCoordinates());
		if(!UserData.getGradients().equals("-1"))
			addGradientsToSpinner(UserData.getGradients());

		switcher = (Switch) findViewById(R.id.switch_alarm);
		switcher.setOnClickListener(this);
		alarmTime = (TextView) findViewById(R.id.timeView);
		alarmTime.setText(sdf.format(calendar.getTime()));

		alarmClock = (TimePicker) findViewById(R.id.timePicker);
		alarmClock.setIs24HourView(DateFormat.is24HourFormat(this));
		alarmClock.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

		logoutButton = (Button)findViewById(R.id.alarm_logout_button);
		logoutButton.setOnClickListener(this);
		buttonStart = (Button)findViewById(R.id.alarm_button_start);
		buttonStart.setOnClickListener(this);
		buttonSettings = (Button)findViewById(R.id.alarm_button_settings);
		buttonSettings.setOnClickListener(this);

	}
	
	@Override
	public void onBackPressed(){
		LC.closeAllLamps();
		super.onBackPressed();
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
		ArrayList<String[]> buttons = parseObjectString(data);
		RelativeLayout.LayoutParams params;
		ToggleButton toggle;
		int id = 0;
		for(String[] b : buttons){

			params = new RelativeLayout.LayoutParams(45, 45);
			toggle = new ToggleButton(this);
			params.leftMargin = Integer.parseInt(b[1]);
			params.topMargin = Integer.parseInt(b[2]);
			toggle.setBackgroundResource(R.drawable.selector);
			toggle.setTextOff("OFF");
			toggle.setText("OFF");
			toggle.setTextOn("ON");
			toggle.setTextSize(11);
			//			toggle.setTypeface(Typeface.DEFAULT_BOLD);
			toggle.setHeight(40);
			toggle.setWidth(40);
			toggle.setId(id++);
			toggle.setOnClickListener(this);
			toggle.setTag(b[0]);	//IP
			layoutBackgroundLinearLayout.addView(toggle, params);
			//
			allToggleButtons.add(toggle);
		}

	}
	private ArrayList<String[]> parseObjectString(String object){
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

	private void setAlarm(){
		Date date = new Date();
		Calendar cal_alarm = Calendar.getInstance();
		Calendar cal_now = Calendar.getInstance();
		cal_now.setTime(date);
		cal_alarm.setTime(date);
		cal_alarm.set(Calendar.HOUR_OF_DAY, alarmClock.getCurrentHour());
		cal_alarm.set(Calendar.MINUTE, alarmClock.getCurrentMinute());
		cal_alarm.set(Calendar.SECOND, 0);
		if(cal_alarm.before(cal_now)){
			cal_alarm.add(Calendar.DATE, 1);
		}

		alarm.setAlarm(AlarmActivity.this, cal_alarm, alarmColor, alarmSound, wakeDuration, maxBrightness, null);
	}
	private void cancelAlarm(){
		alarm.cancelAlarm(AlarmActivity.this);		
	}
	@Override
	public void onClick(View v) {
		if(v == logoutButton){
			UserData.logout(this, UserData.getUser(this));
		}
		if(v == buttonSettings){
			AlarmSettingsDialog alarmDialog = new AlarmSettingsDialog();
			alarmDialog.show((this).getFragmentManager(), "AlarmDialog");
		}
		if(v == buttonStart){
			calendar.set(Calendar.HOUR_OF_DAY, alarmClock.getCurrentHour());
			calendar.set(Calendar.MINUTE, alarmClock.getCurrentMinute());

			alarmTime.setText(sdf.format(calendar.getTime()));
			switcher.setChecked(true);
			setAlarm();
		}
		if(v == switcher){
			System.out.println(switcher.isChecked());
			if(!switcher.isChecked()){
				switcher.setChecked(false);
				cancelAlarm();
			}
			else{
				switcher.setChecked(true);
				setAlarm();
			}
		}
		for (ToggleButton b : allToggleButtons)
			if (v == b)
				try {
					if (b.isChecked())
						//						alarm.addLampToAlarm(b.getId(), (String) b.getTag(), 1234, 20);
						LC.addTubeLamp(b.getId(), (String) b.getTag(), 1234, 20);
					else 
						LC.removeTubeLamp(b.getId());
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		switch (pos) {
		case 0:
			// Sonnenaufgang
			setColor(50, 255, 190, 0);
			setSound(R.raw.sunrise);
			break;
		case 1:
			// Abend
			setColor(50, 220, 70, 160);
			setSound(R.raw.eve);
			break;
		case 2:
			// K�hl
			setColor(50, 224, 255, 255);
			setSound(R.raw.warm);
			break;
		case 3:
			// Warm
			setColor(50, 255, 100, 100);
			setSound(R.raw.warm);
			break;

		default:
			//			transformColorAndSetBars(0xFFFFFF);
			setSound(R.raw.warm);
			break;
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}
	private void setColor(int power, int red, int green, int blue) {
		Log.e("color", "P: " + power + " R: " + red + " G: " + green + " B: " + blue);
		alarmColor = new Color(red, green, blue);
		UserData.setAlarmColor(this, alarmColor);
	}
	private void setSound(int resource) {
		Log.i("sound", "file: " + resource);
		alarmSound = resource;
		UserData.setAlarmSound(this, alarmSound);
	}

	public void setWakeDuration(int duration) {
		wakeDuration = duration;
	}

	public void setWakeBrightness(int brightness) {
		maxBrightness = brightness;

	}


}
