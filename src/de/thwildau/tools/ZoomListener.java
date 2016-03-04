package de.thwildau.tools;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;



public class ZoomListener implements OnTouchListener {

	private RelativeLayout rl;
	private RelativeLayout roomLayout;
	private static final String TAG = "Touch";

	int oldX, oldY;
	float oldSpacing;

	// The 3 states (events) which the user is trying to perform
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// these PointF objects are used to record the point(s) the user is touching
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	float newDist;

	public ZoomListener(RelativeLayout rl, RelativeLayout roomLayout){
		this.rl = rl;
		this.roomLayout = roomLayout;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		float scale = 1.0f;
		dumpEvent(event);

		final int action = event.getAction();
		switch(action & MotionEvent.ACTION_MASK){
		case MotionEvent.ACTION_DOWN:   // first finger down only
			start.set(event.getX(), event.getY());
			oldX = rl.getScrollX();
			oldY = rl.getScrollY();

			Log.d(TAG, "mode=DRAG"); // write to LogCat
			mode = DRAG;
			break;

		case MotionEvent.ACTION_UP: // first finger lifted

		case MotionEvent.ACTION_POINTER_UP: // second finger lifted

			mode = NONE;
			Log.d(TAG, "mode=NONE");
			break;

		case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

			oldDist = spacing(event);
			oldSpacing = roomLayout.getScaleX();
			Log.d(TAG, "oldDist=" + oldDist);
			if (oldDist > 5f) {
				midPoint(mid, event);
				mode = ZOOM;
				Log.d(TAG, "mode=ZOOM");
			}
			break;

		case MotionEvent.ACTION_MOVE:

			if (mode == DRAG) 
			{ 
				int scrollX = (oldX + (int) (start.x - event.getX()));
				int scrollY = (oldY + (int) (start.y - event.getY()));

				if(scrollX <= 200 && scrollX > -200)
					rl.setScrollX(scrollX);
				if(scrollY <= 100 && scrollY > -80)
					rl.setScrollY(scrollY);
			} 
			else if (mode == ZOOM) 
			{ 
				// pinch zooming
				newDist = spacing(event);
				Log.d(TAG, "newDist=" + newDist);
				if (newDist > 50) 
				{
					scale = oldSpacing * (newDist / oldDist); // setting the scaling of the
					// matrix...if scale > 1 means
					// zoom in...if scale < 1 means
					// zoom out
					if (scale > 0.8f && scale < 2.5f){
						roomLayout.setScaleX(scale);
						roomLayout.setScaleY(scale);
					}
				}
			}
			break;
		}
		return true;
	}
	/*
	 * --------------------------------------------------------------------------
	 * Method: spacing Parameters: MotionEvent Returns: float Description:
	 * checks the spacing between the two fingers on touch
	 * ----------------------------------------------------
	 */

	private float spacing(MotionEvent event) 
	{
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/*
	 * --------------------------------------------------------------------------
	 * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
	 * Description: calculates the midpoint between the two fingers
	 * ------------------------------------------------------------
	 */

	private void midPoint(PointF point, MotionEvent event) 
	{
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	/** Show an event in the LogCat view, for debugging */
	private void dumpEvent(MotionEvent event) 
	{
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE","POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_").append(names[actionCode]);

		if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) 
		{
			sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}

		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++) 
		{
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";");
		}

		sb.append("]");
		Log.d("Touch Events ---------", sb.toString());
	}

}
