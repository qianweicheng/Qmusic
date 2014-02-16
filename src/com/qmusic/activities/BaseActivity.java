package com.qmusic.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.qmusic.MyApplication;

public abstract class BaseActivity extends FragmentActivity {
	String TAG = "BaseActivity";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		TAG = this.getClass().getSimpleName();
	}

	@Override
	protected void onStart() {
		MyApplication.foreground(this.getClass().getSimpleName());
		super.onStart();
	}

	@Override
	protected void onStop() {
		MyApplication.background(this.getClass().getSimpleName());
		super.onStop();
	}

}
