package de.thwildau.tools;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import android.R;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.CalendarContract.CalendarCache;
import android.widget.Toast;

import com.light.controll.LampControll;
import com.light.lamptyps.Color;
import com.light.lamptyps.SimpleLamp;

import de.thwildau.delightful.AlarmActivity;
import de.thwildau.delightful.OnAlarmActivity;

public class Alarm extends BroadcastReceiver {


	private MediaPlayer mp = new MediaPlayer();
	private Vibrator vibrator;

	private Color alarmColor;
	private Color notiColor;
	private int alarmSound;
	private int wakeDuration;
	private int maxBrightness;

	NotificationManager manager;
	Notification noti;

	LampControll LC = LampControll.getInstance();

	@Override
	public void onReceive(Context context, Intent intent) {
		WakeLocker.acquire(context);
		alarmColor = UserData.getAlarmColor(context);
		alarmSound = UserData.getAlarmSound(context);
		notiColor = UserData.getNotificationColor(context);
		System.out.println(" " + alarmColor);

		LampControll LC = LampControll.getInstance();
		Set<String> IPs = UserData.getLampAddresses(context);
		try {
			for(String s : IPs){
				LC.addTubeLamp(0, s, 1234, 20);
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//		Intent newIntent = new Intent(context, AlarmActivity.class);
		//		newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//		context.startActivity(newIntent);

		createNotification(context, "Tennis Unterricht beginnt demnächst", "Tennis");

		playSound(context, true);
		setLampColor(context, true);

		Toast.makeText(context, "ALARM !!!!!!", Toast.LENGTH_LONG).show();

		WakeLocker.release();

	}
	public void addLampToAlarm(int id, String IP, int port, int length){
		try {
			LC.addTubeLamp(id, IP, port, length);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setAlarm(Context context, Calendar cal_alarm, Color alarmColor, int alarmSound, int wakeDuration, int maxBrightness, String notiString) {
		this.alarmColor = alarmColor;
		this.alarmSound = alarmSound; 
		this.wakeDuration = wakeDuration;
		this.maxBrightness = maxBrightness;
		System.out.println("Red " + alarmColor.getRed() + " " + notiString + " " + new Date());
		UserData.setAlarmColor(context, alarmColor);
		UserData.setLampAddresses(context, LC.getAllLamps());

		//		Intent i = new Intent(context, Alarm.class);
		//		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

		Intent i = new Intent(context, OnAlarmActivity.class);
		int[] colorList = new int[3];
		colorList[0] = alarmColor.getRed();
		colorList[1] = alarmColor.getGreen();
		colorList[2] = alarmColor.getBlue();
		i.putExtra("color", colorList);
		i.putExtra("sound", alarmSound);
		i.putExtra("noti", notiString);
		String min = ""+cal_alarm.get(Calendar.MINUTE);
		if(cal_alarm.get(Calendar.MINUTE) < 10)
			min = "0"+cal_alarm.get(Calendar.MINUTE);
		i.putExtra("time", cal_alarm.get(Calendar.HOUR_OF_DAY) + " : " + min);

		int _id = (int) System.currentTimeMillis();
		PendingIntent pi = PendingIntent.getActivity(context, _id, i, Intent.FLAG_ACTIVITY_NEW_TASK);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pi);		
	}

	public void cancelAlarm(Context context){
		Intent i = new Intent(context, OnAlarmActivity.class);
		PendingIntent sender = PendingIntent.getActivity(context, 0, i, Intent.FLAG_ACTIVITY_NEW_TASK);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


		alarmManager.cancel(sender);
	}

	private void playSound(Context context, boolean play) {
		if(play){
			AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			am.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);


			//			Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			//			mp = MediaPlayer.create(context, alert);
			mp = MediaPlayer.create(context, alarmSound); 
			//			mp.setLooping(true);
			mp.setVolume(200, 200);
			mp.start();
			mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.release();
				}
			});
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
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void createNotification(Context context, String ticker, String subject ){
		Intent intent = new Intent(context, AlarmActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

		Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		noti = new Notification.Builder(context)
		.setContentTitle("Alarm in 10 Minuten")
		.setContentText(subject)
		.setTicker(ticker)
		.setContentIntent(pIntent)
		.setSound(notificationSound)
		.addAction(R.drawable.ic_media_play, "Call", pIntent)
		.setSmallIcon(R.drawable.stat_notify_chat)
		.build();

		manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		manager.notify(0, noti);

	}
}
