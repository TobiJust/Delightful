package de.thwildau.tools;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.light.controll.LampControll;
import com.light.lamptyps.Color;

import de.thwildau.delightful.R;
import de.thwildau.delightful.SupplyItemDetailActivity;
import de.thwildau.delightful.SupplyItemListActivity;
import de.thwildau.delightful.dummy.DummyContent;

/**
 * A fragment representing a single SupplyItem detail screen. This fragment is
 * either contained in a {@link SupplyItemListActivity} in two-pane mode (on
 * tablets) or a {@link SupplyItemDetailActivity} on handsets.
 */
public class SupplyItemDetailFragment extends Fragment implements OnItemSelectedListener, OnSeekBarChangeListener, OnClickListener {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * Fragment Variables
	 */
	Spinner themeSpinner;
	ArrayAdapter<String> spinnerAdapter;
	ImageView layoutImageView;
	RelativeLayout layoutBackgroundLinearLayout;
	RelativeLayout layoutBackground;

	SeekBar masterSeekBar;
	SeekBar redSeekBar;
	SeekBar greenSeekBar;
	SeekBar blueSeekBar;

	Button resetButton;
	private Alarm alarm = new Alarm();

	private ArrayList<ToggleButton> allToggleButtons = new ArrayList<ToggleButton>();
	private ArrayList<ToggleButton> chosenLamps = new ArrayList<ToggleButton>();
	private ArrayList<String[]> chosenOffer;
	public LampControll LC = LampControll.getInstance();

	/**
	 * The dummy content this fragment is presenting.
	 */
	private DummyContent.DummyItem mItem;

	private Color notiColor = new Color(5,5,5);

	private String notiText;
	private int notiHour, notiMinute;


	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SupplyItemDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = DummyContent.ITEM_MAP.get(getArguments().getString(
					ARG_ITEM_ID));
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_supplyitem_detail,
				container, false);

		// Show the dummy content as text in a TextView.
		if (mItem != null) {
			((TextView) rootView.findViewById(R.id.supplyitem_detail))
			.setText(mItem.content);
		}
		themeSpinner = (Spinner) rootView.findViewById(R.id.supply_spinner_theme);
		layoutImageView = (ImageView) rootView.findViewById(R.id.supply_room_layout);
		layoutBackgroundLinearLayout = (RelativeLayout) rootView.findViewById(R.id.supply_room_layout_background);
		layoutBackground = (RelativeLayout) rootView.findViewById(R.id.supply_layout_background);
		masterSeekBar = (SeekBar) rootView.findViewById(R.id.supply_seekBar_master);
		redSeekBar = (SeekBar) rootView.findViewById(R.id.supply_seekbar_red);
		greenSeekBar = (SeekBar) rootView.findViewById(R.id.supply_seekbar_green);
		blueSeekBar = (SeekBar) rootView.findViewById(R.id.supply_seekbar_blue);
		//add layout to ImageView
		layoutImageView.setImageBitmap(BitmapFactory.decodeByteArray(UserData.getRoomLayout(), 0, UserData.getRoomLayout().length));
		layoutBackground.setOnTouchListener(new ZoomListener(layoutBackground, layoutBackgroundLinearLayout));

		// set content for drop down list
		themeSpinner.setOnItemSelectedListener(this);
		spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		themeSpinner.setAdapter(spinnerAdapter);

		addLampsToLayout(UserData.getLampCoordinates());
		addActivitiesToSpinner();

		// add listener to seek bars
		masterSeekBar.setOnSeekBarChangeListener(this);
		redSeekBar.setOnSeekBarChangeListener(this);
		greenSeekBar.setOnSeekBarChangeListener(this);
		blueSeekBar.setOnSeekBarChangeListener(this);

		resetButton = (Button) rootView.findViewById(R.id.supply_button_reset);
		resetButton.setOnClickListener(this);

		return rootView;
	}

	private void addActivitiesToSpinner(){
		if(mItem.getSupplies() == null)
			return;

		chosenOffer = new ArrayList<String[]>();
		for(String[] s : mItem.getSupplies()){
			String[] time = {s[1],s[2]};
			chosenOffer.add(time);
			spinnerAdapter.add(s[0]);
			spinnerAdapter.notifyDataSetChanged();
		}
	}
	public void addLampsToLayout(String data){
		System.out.println("DIMENSION " + layoutBackgroundLinearLayout.getWidth() + "  " + layoutBackgroundLinearLayout.getHeight());

		ArrayList<String[]> buttons = parseLampObjectString(data);
		RelativeLayout.LayoutParams params;
		ToggleButton toggle;
		int id = 0;
		for(String[] b : buttons){

			params = new RelativeLayout.LayoutParams(45, 45);
			toggle = new ToggleButton(getActivity());
			params.leftMargin = Integer.parseInt(b[1]);
			params.topMargin = Integer.parseInt(b[2]);
			toggle.setBackgroundResource(R.drawable.selector);
			toggle.setText("OFF");
			toggle.setTextOff("OFF");
			toggle.setTextOn("ON");
			toggle.setTextSize(10);
			toggle.setTypeface(Typeface.DEFAULT_BOLD);
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

	private void setAlarm(){
		Date date = new Date();
		Calendar cal_notify = Calendar.getInstance();
		Calendar cal_now = Calendar.getInstance();
		cal_now.setTime(date);
		cal_notify.setTime(date);
		cal_notify.set(Calendar.HOUR_OF_DAY, notiHour);
		cal_notify.set(Calendar.MINUTE, notiMinute);
		cal_notify.set(Calendar.SECOND, 0);

		int afore = cal_notify.get(Calendar.MINUTE);
		afore = afore - 5;
		System.out.println(afore);
		if(afore < 0){
			cal_notify.add(Calendar.HOUR, -1);
			cal_notify.add(Calendar.MINUTE, 60 + afore);
		}
		else
			cal_notify.add(Calendar.MINUTE, afore);

		if(cal_notify.before(cal_now))
			cal_notify.add(Calendar.DATE, 1);

		/**
		 * Test
		 */
		cal_now.add(Calendar.SECOND, 30);
		System.out.println(cal_now.get(Calendar.HOUR_OF_DAY) + " : " + cal_now.get(Calendar.MINUTE));
		alarm.setAlarm(getActivity(), cal_now, notiColor, 0, -1, -1, notiText);
		//		alarm.setAlarm(getActivity(), cal_notify, notiColor, 0, notiText);
	}
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		notiText = (String) parent.getItemAtPosition(pos);
		notiHour = Integer.parseInt(chosenOffer.get(pos)[0]);
		notiMinute = Integer.parseInt(chosenOffer.get(pos)[1]);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

		int power = masterSeekBar.getProgress();
		int red = redSeekBar.getProgress();
		int green = greenSeekBar.getProgress();
		int blue = blueSeekBar.getProgress();

		//		sendColors(power, red, green, blue);

		//		System.out.println("POWER " + power);

		GradientDrawable drawable = new GradientDrawable();
		drawable.setShape(GradientDrawable.OVAL);
		drawable.setStroke(4, android.graphics.Color.rgb(red, green, blue));
		// drawable.setAlpha(power);

		for(ToggleButton lamp : chosenLamps){
			if(power == 0){
				lamp.setChecked(false);
				drawable.setColor(android.graphics.Color.rgb(255, 190, 0));
			}
			else{
				lamp.setChecked(true);
				drawable.setColor(android.graphics.Color.GREEN);
				lamp.setBackgroundDrawable(drawable);
			}
		}

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		notiColor = new Color(redSeekBar.getProgress(), 
				greenSeekBar.getProgress(), 
				blueSeekBar.getProgress());
	}

	@Override
	public void onClick(View v) {
		if(v == resetButton){
			//Reset SeekBars
			setAlarm();
			Toast.makeText(getActivity(), "Reminder is set", Toast.LENGTH_LONG).show();
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
	private void setColor(int power, int red, int green, int blue) {
		Log.e("color", "P: " + power + " R: " + red + " G: " + green + " B: " + blue);
		notiColor = new Color(red, green, blue);
		UserData.setNotificationColor(getActivity(), notiColor);
	}
}
