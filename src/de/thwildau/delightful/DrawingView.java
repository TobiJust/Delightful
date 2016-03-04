package de.thwildau.delightful;

import java.util.Iterator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.light.controll.LampControll;
import com.light.lamptyps.SimpleLamp;

public class DrawingView extends ImageView {

	public int width;
	public int height;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;
	private Paint mPaint;
	private Paint circlePaint;
	private Path circlePath;
	private Context context;

	public LampControll LC = new LampControll();

	public DrawingView(Context c, AttributeSet set) {
		super(c, set);
		context = c;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.GREEN);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12); 

		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		circlePaint = new Paint();
		circlePath = new Path();
		circlePaint.setAntiAlias(true);
		circlePaint.setColor(Color.BLUE);
		circlePaint.setStyle(Paint.Style.STROKE);
		circlePaint.setStrokeJoin(Paint.Join.MITER);
		circlePaint.setStrokeWidth(4f);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

		canvas.drawPath(mPath, mPaint);

		canvas.drawPath(circlePath, circlePaint);

	}

	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;

	private void touch_start(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;

			circlePath.reset();
			circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
		}
	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		circlePath.reset();
		// commit the path to our offscreen
		mCanvas.drawPath(mPath, mPaint);
		// kill this so we don't double draw
		mPath.reset();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			invalidate();
			break;
		}
		if (x < this.getWidth() && y < this.getHeight() && x > 0 && y > 0) {
			Drawable imgDrawable = (this).getDrawable();
			Bitmap bitmap = Bitmap.createBitmap(
					this.getWidth(),
					this.getHeight(),
					Bitmap.Config.ARGB_8888);

			Canvas canvas2 = new Canvas(bitmap);
			imgDrawable.draw(canvas2);

			int pixel = bitmap.getPixel((int) x,
					(int) y);

			int redValue = Color.red(pixel);
			int blueValue = Color.blue(pixel);
			int greenValue = Color.green(pixel);
			Log.e("gradient_view", "RGB = " + redValue + ":"
					+ greenValue + ":" + blueValue);

			((GradientActivity) context).saveGradientColors(redValue, greenValue, blueValue);
			((GradientActivity) context).sendColors(redValue, greenValue, blueValue);
		}

		return true;
	}

	public void resetView(){
		System.out.println("RESET DRAWING VIEW");
		mCanvas.drawColor(0, Mode.CLEAR);
		invalidate();
	}

	private void sendColors(int red, int green, int blue) {
		try {
			LC.addTubeLamp(0, "192.168.0.10", 1234, 45);// ("localhost", 12345,
			// 45);
			// ("localhost", 12345,
			// 5,
			// 5);

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
}
