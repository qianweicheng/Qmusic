package com.qmusic.localplugin.lockscreen;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.qmusic.localplugin.LockScreenPlug;
import com.qmusic.uitls.BLog;

public class LockScreenView extends LinearLayout implements OnClickListener {
	int type;
	float downX, downY;
	float abs;

	public LockScreenView(Context context) {
		super(context);
		setBackgroundColor(0xff000000);
		abs = 200;
	}

	@Override
	protected void onAttachedToWindow() {
		// WindowManager.LayoutParams layoutParams =
		// (WindowManager.LayoutParams) getLayoutParams();
		// type = layoutParams.type;
		// layoutParams.type = WindowManager.LayoutParams.TYPE_KEYGUARD;
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		// WindowManager.LayoutParams layoutParams =
		// (WindowManager.LayoutParams) getLayoutParams();
		// layoutParams.type = type;
		super.onDetachedFromWindow();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		int action = event.getActionMasked();
		if (action == MotionEvent.ACTION_DOWN) {
			downX = event.getX();
			downY = event.getY();
			BLog.e("LockScreenView", "x:" + downX + ";y=" + downY);
		} else if (action == MotionEvent.ACTION_UP) {
			float upX = event.getX();
			float upY = event.getY();
			float diffX = upX - downX;
			float diffY = upY - downY;
			BLog.e("LockScreenView", "diffX:" + diffX + ";diffY=" + diffY);
			if (Math.abs(diffX) > abs || Math.abs(diffY) > abs) {
				LockScreenPlug.unlockS();
			}
		}
		return super.onInterceptTouchEvent(event);
	}

	@Override
	public void onClick(View v) {
		// LockScreenPlug.startAnimationS();
		LockScreenPlug.unlockS();
	}

}