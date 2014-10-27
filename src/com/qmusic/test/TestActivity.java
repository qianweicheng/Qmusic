package com.qmusic.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.qmusic.R;
import com.qmusic.activities.BCommonWebActivity;
import com.qmusic.activities.BWebActivity;
import com.qmusic.activities.BaseActivity;
import com.qmusic.uitls.BAppHelper;
import com.qmusic.uitls.BLog;

public class TestActivity extends BaseActivity implements View.OnClickListener {
	EditText edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BLog.d(TAG, "savedInstanceState:" + savedInstanceState);
		setContentView(R.layout.activity_test);
		findViewById(R.id.activity_test1_button1).setOnClickListener(this);
		findViewById(R.id.activity_test1_button2).setOnClickListener(this);
		findViewById(R.id.activity_test1_button3).setOnClickListener(this);
		findViewById(R.id.activity_test1_button4).setOnClickListener(this);
		// edit = (EditText) findViewById(R.id.activity_test1_input_edit);
		BLog.e(TAG, "onCreate");
	}

	@Override
	protected void onResume() {
		super.onResume();
		BLog.e(TAG, "onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		BLog.e(TAG, "onPause");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BLog.e(TAG, "onDestroy");
	}

	@Override
	public void onBackPressed() {
		BAppHelper.exit(this, true);
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		if (viewId == R.id.activity_test1_button1) {
			onBtn1(v);
		} else if (viewId == R.id.activity_test1_button2) {
			onBtn2(v);
		} else if (viewId == R.id.activity_test1_button3) {
			onBtn3(v);
		} else if (viewId == R.id.activity_test1_button4) {
			onBtn4(v);
		}
	}

	public void onBtn1(final View view) {
		// LockScreenPlug.lockS();
		// goHome();
		Intent intent = new Intent(this, BWebActivity.class);
		intent.putExtra(BWebActivity.SHOW_PROGRESS_BAR, true);
		intent.putExtra("url", "file:///android_asset/www/index.html");
		startActivity(intent);
	}

	public void onBtn2(final View view) {
		Intent intent = new Intent(this, BWebActivity.class);
		intent.putExtra(BWebActivity.SHOW_PROGRESS_BAR, true);
		intent.putExtra("url", "file:///android_asset/www/index2.html");
		startActivity(intent);
	}

	public void onBtn3(final View view) {
		Intent intent = new Intent(this, BCommonWebActivity.class);
		intent.putExtra(BWebActivity.SHOW_PROGRESS_BAR, true);
		intent.putExtra("url", "file:///android_asset/www/index.html");
		startActivity(intent);
	}

	public void onBtn4(final View view) {
		Intent intent = new Intent(this, BCommonWebActivity.class);
		intent.putExtra(BWebActivity.SHOW_PROGRESS_BAR, true);
		intent.putExtra("mode", 2);
		intent.putExtra("url", "file:///android_asset/www/index2.html");
		startActivity(intent);
	}

}
