package com.qmusic.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.qmusic.MyApplication;
import com.qmusic.R;
import com.qmusic.common.BConstants;
import com.qmusic.common.BUser;
import com.qmusic.test.TestActivity;
import com.qmusic.uitls.BLog;
import com.qmusic.uitls.BUtilities;
import com.umeng.analytics.MobclickAgent;

public class SplashActivity extends SherlockFragmentActivity {
	static final String TAG = SplashActivity.class.getSimpleName();
	static final int WAITING_TIME = 500;
	public static final String SHUTDOWN = "shutdown";
	public static final String RE_LOGIN = "re_login";
	public static final boolean UI_TEST = true;// only for UI test.
	Intent newIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		BLog.i(TAG, "onCreate");
	}

	@Override
	protected void onStart() {
		super.onStart();
		BLog.i(TAG, "onStart");
		if (newIntent == null) {
			newIntent = getIntent();
		}
		Bundle bundle = newIntent.getExtras();
		if (bundle != null && bundle.size() > 0) {
			if (bundle.getBoolean(SHUTDOWN, false)) {
				Animation anim = AnimationUtils.loadAnimation(this, R.anim.tv_off);
				anim.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						finish();
						MyApplication.shutdown();
					}
				});
				View v = findViewById(R.id.activity_splash_img);
				v.startAnimation(anim);
				return;
			} else if (bundle.getBoolean(RE_LOGIN, false)) {
				checkLogin();
				return;
			} else {
				BLog.i(TAG, "bundle does not match any key. " + bundle.toString());
				new MyAsyncTask().execute();
				return;
			}
		}
		if (UI_TEST) {
			MyApplication.STARTED = true;
			startActivity(new Intent(SplashActivity.this, TestActivity.class));
		} else {
			new MyAsyncTask().execute();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		BLog.i(TAG, "onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		BLog.i(TAG, "onNewIntent");
		newIntent = intent;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		MyApplication.shutdown();
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
				while (!MyApplication.STARTED) {
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
			String showTutorial = BUtilities.getPref(BConstants.PRE_KEY_SHOW_TUTORIAL);
			if (TextUtils.isEmpty(showTutorial)) {
				Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
				startActivity(intent);
			} else {
				checkLogin();
			}
		}
	}

	void checkLogin() {
		if (BUser.isLogined()) {
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			Bundle bundle = getIntent().getExtras();
			if (bundle != null) {
				intent.putExtras(bundle);
			}
			startActivity(intent);
		} else {
			Intent intent = new Intent(this, LoginActivity.class);
			intent.putExtra(LoginActivity.EXIT_WHEN_BACK, true);
			startActivity(intent);
		}
		overridePendingTransition(R.anim.fadein, 0);
	}
}
