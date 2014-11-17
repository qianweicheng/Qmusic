package com.qmusic.activities;

import android.content.Intent;
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
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onStart() {
		MyApplication.foreground(this.getClass().getSimpleName());
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStop() {
		MyApplication.background(this.getClass().getSimpleName());
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
