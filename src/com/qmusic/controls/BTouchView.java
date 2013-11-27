package com.qmusic.controls;

import com.androidquery.util.AQUtility;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class BTouchView extends FrameLayout {
	static final String TAG = "BTouchView";
	float x, y;

	public BTouchView(Context context) {
		super(context);
		init();
	}

	public BTouchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	void init() {

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int action = (ev.getAction() & MotionEvent.ACTION_MASK);
		// Log.i(TAG, "onInterceptTouchEvent:" + action);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			y = ev.getY();
			x = ev.getX();
			break;
		case MotionEvent.ACTION_UP: {
			float x2 = ev.getX();
			float y2 = ev.getY();
			Log.i(TAG, "diffX:" + (x - x2) + ";diffY:" + (y - y2));
			if (Math.abs(x - x2) < 5.0 && Math.abs(y - y2) < 5.0) {
				setPressed(true);
				AQUtility.postDelayed(new Runnable() {

					@Override
					public void run() {
						setPressed(false);
						performClick();
					}
				}, 50);
				return true;
			}
			break;
		}
		case MotionEvent.ACTION_MOVE:
			// float x2 = ev.getX();
			// float y2 = ev.getY();
			// // Log.i(TAG, "onTouchEvent-Action x2=" + x2 + ";y2=" + y2);
			// if (Math.abs(x - x2) > 50.0 || Math.abs(y - y2) > 50.0) {
			// Log.i(TAG, "onTouchEvent-Action x2=" + x2 + ";y2=" + y2);
			// return true;
			// }
			break;
		case MotionEvent.ACTION_CANCEL:

			break;
		default:
			break;
		}
		return false;
	};

	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// int action = (event.getAction() & MotionEvent.ACTION_MASK);
	// Log.i(TAG, "onTouchEvent:" + action);
	// switch (action) {
	// case MotionEvent.ACTION_DOWN: {
	// break;
	// }
	// case MotionEvent.ACTION_UP: {
	// break;
	// }
	// case MotionEvent.ACTION_MOVE: {
	// break;
	// }
	// case MotionEvent.ACTION_CANCEL: {
	// break;
	// }
	// default:
	// break;
	// }
	// return true;
	// }
}
