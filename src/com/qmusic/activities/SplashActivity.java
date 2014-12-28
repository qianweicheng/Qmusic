package com.qmusic.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.qmusic.MyApplication;
import com.qmusic.R;
import com.qmusic.common.BEnvironment;
import com.qmusic.test.TestActivity;
import com.qmusic.uitls.BLog;
import com.qmusic.webdoengine.BWebdoEngine;

public class SplashActivity extends BaseActivity {
	static final String TAG = SplashActivity.class.getSimpleName();
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
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (newIntent == null) {
			newIntent = getIntent();
		}
		process(newIntent);
		newIntent = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Fix: in some device it will not call onStart before onResume
		if (newIntent != null) {
			process(newIntent);
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
			} else {
				new MyAsyncTask(this, bundle).execute();
			}
		} else {
			new MyAsyncTask(this, bundle).execute();
		}
	}

	static class MyAsyncTask extends AsyncTask<Void, Void, Void> {
		SplashActivity context;
		Bundle bundle;
		String type;
		boolean needWait;

		public MyAsyncTask(final SplashActivity context, final Bundle bundle) {
			this.context = context;
			this.bundle = bundle;
			if (bundle == null) {
				needWait = true;
			} else if (bundle.getBoolean(RE_LOGIN, false)) {
				needWait = false;
				type = RE_LOGIN;
			} else if (bundle.getBoolean(ROUTE, false)) {
				needWait = false;
				type = ROUTE;
			} else {
				BLog.w(TAG, "unknow key");
				needWait = true;
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				long startTime = System.nanoTime();
				// wait for BWebdoEngine ready
				while (!BWebdoEngine.isWebdoEngineReady()) {
					Thread.sleep(20);
				}
				long endTime = System.nanoTime();
				long timeCost = (endTime - startTime) / 1000000;
				if (needWait) {
					long waitTime = WAITING_TIME - timeCost;
					if (waitTime > 0) {
						BLog.i(TAG, "wait time:" + waitTime + " ms");
						Thread.sleep(waitTime);
					}
				} else {
					BLog.i(TAG, "time cost:" + timeCost);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (RE_LOGIN.equals(type)) {
				Intent intent = new Intent(context, LoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(intent);
				return;
			} else if (ROUTE.equals(type)) {
				try {
					Intent originIntent = bundle.getParcelable(ORIGININTENT);
					originIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					context.startActivity(originIntent);
					return;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			if (BEnvironment.UI_TEST) {
				Intent intent = new Intent(context, TestActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(intent);
			} else {
				Intent intent = new Intent(context, MainActivity2.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(intent);
			}
		}
	}

}
