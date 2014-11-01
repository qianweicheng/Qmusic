package com.qmusic.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.androidquery.util.AQUtility;
import com.qmusic.MyApplication;
import com.qmusic.R;
import com.qmusic.common.BEnvironment;
import com.qmusic.test.TestActivity;
import com.qmusic.uitls.BLog;

public class SplashActivity extends BaseActivity {
	static final int WAITING_TIME = 500;
	public static final String SHUTDOWN = "shutdown";
	public static final String RE_LOGIN = "re_login";
	public static final String ROUTE = "route";
	public static final String ORIGININTENT = "originIntent";
	Intent newIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		// BLog.i(TAG, "onCreate");
	}

	@Override
	protected void onStart() {
		super.onStart();
		// BLog.i(TAG, "onStart");
		if (newIntent == null) {
			newIntent = getIntent();
		}
		// Note: after 4.4, can't call start activity from
		// onCreate,onStart,onResume
		final Intent intent = newIntent;
		AQUtility.post(new Runnable() {

			@Override
			public void run() {
				process(intent);
			}
		});
		newIntent = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在部分tablet上面会延迟调用onStop,导致onStart不会被调用
		if (newIntent != null) {
			final Intent intent = newIntent;
			AQUtility.post(new Runnable() {

				@Override
				public void run() {
					process(intent);
				}
			});
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// BLog.i(TAG, "onNewIntent");
		newIntent = intent;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		MyApplication.shutdown();
	}

	private void process(Intent newIntent) {
		Bundle bundle = newIntent.getExtras();
		if (bundle != null && bundle.size() > 0) {
			if (bundle.getBoolean(SHUTDOWN, false)) {
				finish();
				MyApplication.shutdown();
				return;
			} else if (bundle.getBoolean(RE_LOGIN, false)) {
				checkLogin();
				return;
			} else if (bundle.getBoolean(ROUTE, false)) {
				try {
					Intent originIntent = bundle.getParcelable(ORIGININTENT);
					startActivity(originIntent);
				} catch (Exception ex) {
					ex.printStackTrace();
					checkLogin();
				}
				return;
			} else {
				BLog.i(TAG, "bundle does not match any key. " + bundle.toString());
			}
		}
		if (BEnvironment.UI_TEST) {
			Intent intent = new Intent(SplashActivity.this, TestActivity.class);
			startActivity(intent);
		} else {
			new MyAsyncTask().execute();
		}
	}

	class MyAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				long startTime = System.nanoTime();
				while (MyApplication.STARTED_TIME == 0) {
					Thread.sleep(20);
				}
				long endTime = System.nanoTime();
				long waitTime = WAITING_TIME - (endTime - startTime) / 1000000;
				if (waitTime > 0) {
					BLog.i(TAG, "wait time:" + waitTime + " ms");
					Thread.sleep(waitTime);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			checkLogin();
		}
	}

	void checkLogin() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}
}
