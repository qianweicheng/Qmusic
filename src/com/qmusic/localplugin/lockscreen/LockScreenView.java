package com.qmusic.localplugin.lockscreen;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.qmusic.R;
import com.qmusic.localplugin.LockScreenPlug;
import com.qmusic.uitls.BLog;

public class LockScreenView extends LinearLayout implements OnClickListener {
	int type;

	public LockScreenView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.activity_home, this);
		View btnEasilydo = findViewById(R.id.activity_home_easilydo);
		View btnCall = findViewById(R.id.activity_home_call);
		View btnSMS = findViewById(R.id.activity_home_sms);
		View btnUnlock = findViewById(R.id.activity_home_open);
		btnEasilydo.setOnClickListener(this);
		btnCall.setOnClickListener(this);
		btnSMS.setOnClickListener(this);
		btnUnlock.setOnClickListener(this);
		setBackgroundColor(0xff000000);
	}

	@Override
	protected void onAttachedToWindow() {
		BLog.e("LockScreenView", "onAttachedToWindow");
		Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_animation);
		View icon = findViewById(R.id.activity_home_icon);
		icon.setAnimation(animation);
		animation.start();
		WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) getLayoutParams();
		type = layoutParams.type;
		layoutParams.type = WindowManager.LayoutParams.TYPE_KEYGUARD;
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) getLayoutParams();
		layoutParams.type = type;
		super.onDetachedFromWindow();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		BLog.e("LockScreenView", "onTouchEvent: x=" + event.getX() + ";y=" + event.getY());
		return super.onTouchEvent(event);
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		Context ctx = v.getContext();
		if (viewId == R.id.activity_home_open) {
			BLog.i("LockScreenView", "unlock");
		} else if (viewId == R.id.activity_home_easilydo) {
			try {
				Intent intent = ctx.getPackageManager().getLaunchIntentForPackage("com.easilydo");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ctx.startActivity(intent);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (viewId == R.id.activity_home_call) {
			try {
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ctx.startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				ex.printStackTrace();
			}
		} else if (viewId == R.id.activity_home_sms) {
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setType("vnd.android-dir/mms-sms");
				ctx.startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				ex.printStackTrace();
			}
		}
		LockScreenPlug.unlockS();
	}

}