package de.thwildau.delightful;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.light.controll.LampControll;
import com.light.lamptyps.SimpleLamp;

import de.thwildau.network.DelightfulHttpClient;
import de.thwildau.tools.Constants;
import de.thwildau.tools.UserData;

public class GradientActivity extends Activity implements OnClickListener{

	private static final int TAKE_PICTURE = 0;
	private static final int SELECT_PHOTO = 100;

	TextView headerTextView, durationView;
	ImageView colorPickerImageView;
	Button resetButton, cameraButton, galleryButton, gradientButton, submitButton, logoutButton, cancelButton;
	DrawingView drawingView;
	SeekBar seekbarDuration;
	EditText editTextDuration;
	private Uri imageUri;

	private String gradientName;
	private int gradientTime;
	private int imageView_Height;

	private ArrayList<Integer> redList = new ArrayList<Integer>();
	private ArrayList<Integer> greenList = new ArrayList<Integer>();
	private ArrayList<Integer> blueList = new ArrayList<Integer>();

	public LampControll LC = LampControll.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.color_gradient_layout);

		headerTextView = (TextView) findViewById(R.id.gradient_header);
		Typeface header_typeface = Typeface.createFromAsset(getAssets(),
				"fonts/ITCEDSCR.TTF");
		headerTextView.setTypeface(header_typeface);

		editTextDuration = (EditText) findViewById(R.id.gradient_duration_editText);
		durationView = (TextView) findViewById(R.id.duration_textView);
		//		colorPickerImageView = (ImageView) findViewById(R.id.gradient_color_view);
		resetButton = (Button) findViewById(R.id.gradient_button_reset);
		cameraButton = (Button) findViewById(R.id.camera_button);
		galleryButton = (Button) findViewById(R.id.gallery_button);
		submitButton = (Button) findViewById(R.id.gradient_submit);
		cancelButton = (Button) findViewById(R.id.gradient_cancel);
		logoutButton = (Button) findViewById(R.id.gradient_logout_button);
		gradientButton = (Button) findViewById(R.id.gradient_image_button);
		drawingView = (DrawingView) findViewById(R.id.gradient_drawing_view);
		colorPickerImageView = (DrawingView) findViewById(R.id.gradient_drawing_view);
		//		imageView_Height = colorPickerImageView.getLayoutParams().height;
		imageView_Height = drawingView.getLayoutParams().height;
		drawingView = new DrawingView(this, null);

		cameraButton.setOnClickListener(this);
		galleryButton.setOnClickListener(this);
		resetButton.setOnClickListener(this);
		submitButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		logoutButton.setOnClickListener(this);
		gradientButton.setOnClickListener(this);

		seekbarDuration = (SeekBar) findViewById(R.id.gradient_seekBar_duration);
		seekbarDuration.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				gradientTime = seekBar.getProgress();

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				durationView.setText(progress + " min");

			}
		});
		//		colorPickerImageView
		drawingView
		.setOnTouchListener(new ImageView.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x = event.getX();
				float y = event.getY();
				if (x < drawingView.getWidth()
						&& y < drawingView
						.getHeight() && x > 0 && y > 0) {
					//					Log.e("gradient_view", "Touch coordinates: "
					//							+ String.valueOf(x) + "x"
					//							+ String.valueOf(y));
					Drawable imgDrawable = (drawingView)
							.getDrawable();
					Bitmap bitmap = Bitmap.createBitmap(
							drawingView.getWidth(),
							drawingView.getHeight(),
							Bitmap.Config.ARGB_8888);

					Canvas canvas = new Canvas(bitmap);
					imgDrawable.draw(canvas);

					int pixel = bitmap.getPixel((int) x,
							(int) y);

					int redValue = Color.red(pixel);
					int blueValue = Color.blue(pixel);
					int greenValue = Color.green(pixel);
					//					Log.e("gradient_view", "RGB = " + redValue + ":"
					//							+ greenValue + ":" + blueValue);
					saveGradientColors(redValue, greenValue, blueValue);
					sendColors(redValue, greenValue, blueValue);
				}
				return true;
			}
		});
	}

	public void saveGradientColors(int red, int green, int blue){
		redList.add(red);
		greenList.add(green);
		blueList.add(blue);
	}
	public void sendColors(int red, int green, int blue) {
		try {
			Log.e("sendColors","Trying to send colors " +  red + " " + green + " " + blue);

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
	protected void getPhotoFromLibrary(View v) {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, SELECT_PHOTO);

	}

	protected void takePhoto(View v){
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		File photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
		imageUri = Uri.fromFile(photo);
		startActivityForResult(intent, TAKE_PICTURE);
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case TAKE_PICTURE:
			if(resultCode == Activity.RESULT_OK){
				Log.i("CAMERA", "Photo taken");
				Uri selectedImage = imageUri;
				getContentResolver().notifyChange(selectedImage, null);
				ContentResolver cr = getContentResolver();
				Bitmap bitmap;
				try{
					bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
					double scale = (double)imageView_Height/bitmap.getHeight();
					colorPickerImageView.getLayoutParams().width = (int) (scale * bitmap.getWidth());
					colorPickerImageView.setImageBitmap(bitmap);
					Toast.makeText(this, selectedImage.toString(), Toast.LENGTH_LONG).show();
				}
				catch (Exception e){
					Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
					Log.e("CAMERA", e.toString());
				}
			}
			break;
		case SELECT_PHOTO:
			if(resultCode == Activity.RESULT_OK){
				Uri selectedImage = data.getData();
				InputStream imageStream = null;
				try {
					imageStream = getContentResolver().openInputStream(selectedImage);
					Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
					double scale = (double)imageView_Height/bitmap.getHeight();
					colorPickerImageView.getLayoutParams().width = (int) (scale * bitmap.getWidth());
					colorPickerImageView.setImageBitmap(bitmap);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally{
					try {
						imageStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			break;
		}
	}


	@Override
	public void onClick(View v) {
		if(v==logoutButton)
			UserData.logout(this, UserData.getUser(this));
		if(v==resetButton){
			//			drawingView.resetView();
			resetColors();
		}
		if(v==cameraButton)
			takePhoto(v);
		if(v==galleryButton)
			getPhotoFromLibrary(v);
		if(v==gradientButton){
			Bitmap gradient = BitmapFactory.decodeResource(getResources(), R.drawable.verlauf);
			System.out.println(gradient.getWidth());
			colorPickerImageView.getLayoutParams().width = gradient.getWidth();
			colorPickerImageView.setImageBitmap(gradient);
		}
		if(v==submitButton)
			sendGradient();
		if(v==cancelButton)
			cancelGradient();
	}
	/**
	 * Executed when the back button is pressed.
	 * If the user presses the back button twice the application is minimized.
	 */
	@Override
	public void onBackPressed() {
		ArrayList<NameValuePair> gradientParameters = new ArrayList<NameValuePair>();
		gradientParameters.add(new BasicNameValuePair("user", UserData.getUser(this)));
		UserData.setGradients(this, null);
		new DelightfulHttpClient(Constants.URL_GET_GRADIENT, gradientParameters, this).executeConnection();

		showProgressDialog();
		//		Intent intent = new Intent(this, ControlActivity.class);
		//		startActivity(intent);

	}

	private void cancelGradient() {
		// TODO Auto-generated method stub

	}
	private void resetColors(){
		//		redList = new ArrayList<Integer>();
		//		greenList = new ArrayList<Integer>();
		//		blueList = new ArrayList<Integer>();
	}

	private void sendGradient() {
		// TODO Auto-generated method stub
		System.out.println(redList.size() + " " + greenList.size() + "  " + blueList.size());
		JSONArray redJson = new JSONArray(redList);
		JSONArray greenJson = new JSONArray(greenList);
		JSONArray blueJson = new JSONArray(blueList);

		gradientName = editTextDuration.getText().toString();
		if(gradientName.length() <= 0)
			Toast.makeText(this, "Please insert a name.", Toast.LENGTH_LONG).show();
		else if(gradientTime <= 0)
			Toast.makeText(this, "Please insert a time.", Toast.LENGTH_LONG).show();
		else{
			ArrayList<NameValuePair> gradientParameters = new ArrayList<NameValuePair>();
			gradientParameters.add(new BasicNameValuePair("userid", UserData.getUser(this)));
			gradientParameters.add(new BasicNameValuePair("name", gradientName));
			gradientParameters.add(new BasicNameValuePair("time", ""+gradientTime));
			gradientParameters.add(new BasicNameValuePair("red", redJson.toString()));
			gradientParameters.add(new BasicNameValuePair("green", greenJson.toString()));
			gradientParameters.add(new BasicNameValuePair("blue", blueJson.toString()));

			new DelightfulHttpClient(Constants.URL_SAVE_GRADIENT, gradientParameters, this).executeConnection();
		}
	}
	private void showProgressDialog(){
		final ProgressDialog dialog = ProgressDialog.show(this, "", "Loading...", true);

		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					if(UserData.getGradients() != null){
						startActivity(new Intent(GradientActivity.this, ControlActivity.class));
						dialog.dismiss();
						break;
					}
				}
			}
		}).start();
	}
}
