package com.qmusic.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.qmusic.MyApplication;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends FragmentActivity {
	protected String TAG = "BaseActivity";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		TAG = this.getClass().getSimpleName();
	}

	@Override
	protected void onStart() {
		MyApplication.foreground(this.getClass().getSimpleName());
		MobclickAgent.onResume(this);
		super.onStart();
	}

	@Override
	protected void onStop() {
		MyApplication.background(this.getClass().getSimpleName());
		MobclickAgent.onPause(this);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}
}
