package com.qmusic.controls;

import com.qmusic.uitls.BLog;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class BPulldownView extends LinearLayout {
	static final String TAG = "BPulldownView";
	float x, y;

	public BPulldownView(Context context) {
		super(context);
		init();
	}

	public BPulldownView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	void init() {
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		BLog.i(TAG, String.format("onSizeChanged:%d,%d,%d,%d", w, h, oldw, oldh));
		View view = getChildAt(0);
		if (view.getHeight() > 0) {
			setPadding(0, -view.getHeight(), 0, 0);
		} else {
			setPadding(0, -150, 0, 0);
		}
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
			float x2 = ev.getX();
			float y2 = ev.getY();
			if (Math.abs(y2 - y) > 10) {
				return true;
			}
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
		int action = (event.getAction() & MotionEvent.ACTION_MASK);
		Log.i(TAG, "onTouchEvent:" + action);
		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			break;
		}
		case MotionEvent.ACTION_UP: {
			scrollTo(0, 0);
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			scrollTo(0, (int) (y - event.getY()));
			break;
		}
		case MotionEvent.ACTION_CANCEL: {
			scrollTo(0, 0);
			break;
		}
		default:
			break;
		}
		return true;
	}

}
