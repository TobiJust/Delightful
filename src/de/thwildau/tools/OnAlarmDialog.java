package de.thwildau.tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.light.lamptyps.Color;

import de.thwildau.delightful.OnAlarmActivity;
import de.thwildau.delightful.R;

public class OnAlarmDialog extends DialogFragment{

	private Color color;
	private int sound;
	String time, info;
	private TextView timeView;
	private TextView infoView;
	private static final int DIALOG_ICON = R.drawable.app_icon;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){

		getActivity().getWindow().addFlags(
				LayoutParams.FLAG_TURN_SCREEN_ON | LayoutParams.FLAG_DISMISS_KEYGUARD);
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View descView = inflater.inflate(R.layout.dialog_on_alarm, null); 


		//set gui elements
		timeView = (TextView) descView.findViewById(R.id.onAlarm_Time);
		infoView = (TextView) descView.findViewById(R.id.onAlarm_Info);

		AlertDialog.Builder alarmDialog = new AlertDialog.Builder(getActivity());
		alarmDialog.setIcon(DIALOG_ICON ).setView(descView);
		alarmDialog.setTitle("ALARM");
		timeView.setText(time);
		infoView.setText("ALARM !!! ALARM !!! ALARM !!! ALARM !!!");

		alarmDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				((OnAlarmActivity) getActivity()).stopSound().finish();				
			}
		});

		return alarmDialog.create();
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		getActivity().finish();
	}

	public void setColor(Color color) {
		this.color = color;
	}
	public void setSound(int alarmSound) {
		this.sound = alarmSound;
	}
	public void setTime(String time) {
		this.time = time;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}
