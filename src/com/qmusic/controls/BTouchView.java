package com.qmusic.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class BTouchView extends FrameLayout {
	static final String TAG = "BTouchView";
	private static final int SWIPE_MIN_DISTANCE = 80;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 100;
	float x, y;
	GestureDetector gestureDetector;
	OnGesture onGesture;

	public static interface OnGesture {
		public static int LEFT = 1;
		public static int RIGHT = 2;
		public static int CLICK = 3;
		public static int DOUBLE_CLICK = 4;
		public static int UP = 5;
		public static int DOWN = 5;

		void onGesture(int type);
	}

	public BTouchView(Context context) {
		super(context);
		init();
	}

	public BTouchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	void init() {
		gestureDetector = new GestureDetector(getContext(), new MyGestureDetector());
	}

	public void setGestureListener(OnGesture callback) {
		onGesture = callback;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int action = (ev.getAction() & MotionEvent.ACTION_MASK);
		Log.i(TAG, "onInterceptTouchEvent:" + action);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			y = ev.getY();
			x = ev.getX();
			break;
		case MotionEvent.ACTION_UP: {
			float x2 = ev.getX();
			float y2 = ev.getY();
			Log.i(TAG, "diffX:" + (x - x2) + ";diffY:" + (y - y2));
			break;
		}
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		default:
			break;
		}
		return false;
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		int action = (event.getAction() & MotionEvent.ACTION_MASK);
		Log.i(TAG, "onTouchEvent:" + action);
		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			break;
		}
		case MotionEvent.ACTION_UP: {
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			break;
		}
		case MotionEvent.ACTION_CANCEL: {
			break;
		}
		default:
			break;
		}
		return true;
	}

	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(e1.getY() - e2.getY()) < SWIPE_MAX_OFF_PATH
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				// left
				if (onGesture != null) {
					onGesture.onGesture(OnGesture.LEFT);
				}
			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(e1.getY() - e2.getY()) < SWIPE_MAX_OFF_PATH
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				// right
				if (onGesture != null) {
					onGesture.onGesture(OnGesture.RIGHT);
				}
			}

			return false;
		}
	}
}
