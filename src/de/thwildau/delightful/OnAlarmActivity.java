package de.thwildau.delightful;

import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Set;

import android.R;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.light.controll.LampControll;
import com.light.lamptyps.Color;
import com.light.lamptyps.SimpleLamp;

import de.thwildau.tools.OnAlarmDialog;
import de.thwildau.tools.UserData;

public class OnAlarmActivity extends FragmentActivity {

	private MediaPlayer mp = new MediaPlayer();
	private Vibrator vibrator;
	private Color alarmColor;
	private int alarmSound;
	private String notification;
	private String alarmTime;

	NotificationManager manager;
	Notification noti;

	LampControll LC = LampControll.getInstance();

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		int[] colors = getIntent().getIntArrayExtra("color");
		notification = getIntent().getStringExtra("noti");
		alarmSound = getIntent().getIntExtra("sound", 0);
		alarmTime = getIntent().getStringExtra("time");
		alarmColor = new Color(colors[0], colors[1], colors[2]);

		OnAlarmDialog alert = new OnAlarmDialog();
		alert.setColor(alarmColor);
		alert.setSound(alarmSound);
		alert.setTime(alarmTime);


		Set<String> IPs = UserData.getLampAddresses(this);
		try {
			for(String s : IPs){
				LC.addTubeLamp(0, s, 1234, 20);
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setLampColor(this, true);
		if(notification != null){
			createNotification(this, "Delightful Erinnerung - Wichtig", notification);
			finish();
		}
		else{
			playSound(this, true);
			alert.show(getSupportFragmentManager(), "Alert Demo");
		}

	}

	private void playSound(Context context, boolean play) {
		if(play){
			AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			am.setStreamVolume(AudioManager.STREAM_MUSIC, 10, 0);


			//			Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			//			mp = MediaPlayer.create(context, alert);
			mp = MediaPlayer.create(context, alarmSound); 
			//			mp.setLooping(true);
			mp.setVolume(200, 200);
			mp.start();
			mp.setLooping(true);
			//			mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			//
			//				@Override
			//				public void onCompletion(MediaPlayer mp) {
			//					mp.release();
			//				}
			//			});
			vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(2000);
		}

	}
	private void setLampColor(Context context, boolean set) {
		if(set){


			System.out.println("User " + UserData.getUser(context) + " " + UserData.getLampAddresses(context));
			System.out.println("LC " + LC.getLamp(0) + " Size " + LC.getAllLamps().size());
			// get Lamps
			for (Iterator<SimpleLamp> iter = LC.getLamps(); iter.hasNext();) {
				SimpleLamp lamp = iter.next();
				System.out.println("ALARMCOLOR " + alarmColor.getRed() + " " + alarmColor.getGreen() + " " + alarmColor.getBlue());
				lamp.setColor(alarmColor);
			}
		}

	}
	public FragmentActivity stopSound() {
		Log.i("ALARM","Stop Sound");
		if(mp != null && mp.isPlaying())
			mp.stop();
		mp.release();
		return this;
	}
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void createNotification(Context context, String ticker, String subject ){
		Intent intent = new Intent(context, MainMenuActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, 10, 0);

		Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		mp = MediaPlayer.create(context, notificationSound);
		mp.start();
		noti = new Notification.Builder(context)
		.setContentTitle("Erinnerung ihrer geplanten Akitivität")
		.setContentText(subject)
		.setTicker(ticker)
		.setContentIntent(pIntent)
		.setSound(notificationSound)
		.addAction(R.drawable.ic_media_play, "Call", pIntent)
		.setSmallIcon(de.thwildau.delightful.R.drawable.app_icon)
		.build();

		manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		manager.notify(0, noti);

	}
}
