package com.qmusic.localplugin.lockscreen;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.androidquery.util.AQUtility;
import com.qmusic.R;
import com.qmusic.localplugin.LockScreenPlug;
import com.qmusic.uitls.BLog;

public class LockScreenView extends LinearLayout implements OnClickListener {
	int type;
	float downX, downY;
	float abs;

	public LockScreenView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.view_locker, this);
		View btnEasilydo = findViewById(R.id.view_locker_easilydo);
		View btnCall = findViewById(R.id.view_locker_call);
		View btnSMS = findViewById(R.id.view_locker_sms);
		View btnUnlock = findViewById(R.id.view_locker_brower);
		btnEasilydo.setOnClickListener(this);
		btnCall.setOnClickListener(this);
		btnSMS.setOnClickListener(this);
		btnUnlock.setOnClickListener(this);
		setBackgroundColor(0xff000000);
		abs = AQUtility.dip2pixel(context, 100);
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
		int viewId = v.getId();
		Context ctx = v.getContext();
		if (viewId == R.id.view_locker_easilydo) {
			try {
				Intent intent = ctx.getPackageManager().getLaunchIntentForPackage("com.easilydo");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ctx.startActivity(intent);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (viewId == R.id.view_locker_call) {
			try {
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ctx.startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				ex.printStackTrace();
			}
		} else if (viewId == R.id.view_locker_sms) {
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setType("vnd.android-dir/mms-sms");
				ctx.startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				ex.printStackTrace();
			}
		} else if (viewId == R.id.view_locker_brower) {
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// intent.addCategory(Intent.CATEGORY_APP_BROWSER);
				intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
				ctx.startActivity(intent);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		// LockScreenPlug.startAnimationS();
		LockScreenPlug.unlockS();
	}

}