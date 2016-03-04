package de.thwildau.tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import de.thwildau.delightful.AlarmActivity;
import de.thwildau.delightful.R;

public class AlarmSettingsDialog extends DialogFragment implements OnSeekBarChangeListener, TextWatcher{

	private SeekBar seekBar_Brightness, seekBar_Duration;
	private EditText minuteField;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View descView = inflater.inflate(R.layout.dialog_alarm_settings, null); 

		minuteField = (EditText) descView.findViewById(R.id.edit_duration);
		minuteField.addTextChangedListener(this);

		//set gui elements
		seekBar_Brightness = (SeekBar) descView.findViewById(R.id.seekBar_brightness);
		seekBar_Brightness.setOnSeekBarChangeListener(this);
		seekBar_Duration = (SeekBar) descView.findViewById(R.id.seekBar_duration);
		seekBar_Duration.setOnSeekBarChangeListener(this);

		AlertDialog.Builder alarmDialog = new AlertDialog.Builder(getActivity());
		alarmDialog.setView(descView);
		alarmDialog.setTitle("Alarm Settings");		

		//buttons
		alarmDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				((AlarmActivity) getActivity()).setWakeDuration(seekBar_Duration.getProgress());
				((AlarmActivity) getActivity()).setWakeBrightness(seekBar_Brightness.getProgress());

				Toast.makeText(getActivity(), "OK", Toast.LENGTH_SHORT).show();
			}
		});
		alarmDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		return alarmDialog.show();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if(seekBar == seekBar_Duration)
			minuteField.setText("" + progress);

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(s.length() > 0)
			seekBar_Duration.setProgress(Integer.parseInt(s.toString()));
		minuteField.setSelection(minuteField.getText().length());

	}
}
