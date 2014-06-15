package com.qmusic.activities;

import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.qmusic.R;
import com.qmusic.controls.dialogs.AlertDialogFragment;
import com.qmusic.uitls.BLog;

public class HomeActivity extends BaseActivity implements OnClickListener {
	View viewEasilydo, viewCall, viewSMS, viewOpen;
	float downX, downY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED,
		// FLAG_HOMEKEY_DISPATCHED);// 关键代码(监听home)
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.getBoolean("LockScreen")) {
			Window win = getWindow();
			WindowManager.LayoutParams lp = win.getAttributes();
			lp.type = LayoutParams.TYPE_SYSTEM_ALERT;
			lp.flags = LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_LAYOUT_IN_SCREEN;
			win.setAttributes(lp);
			setContentView(R.layout.activity_home);
			viewEasilydo = findViewById(R.id.activity_home_easilydo);
			viewCall = findViewById(R.id.activity_home_call);
			viewSMS = findViewById(R.id.activity_home_sms);
			viewOpen = findViewById(R.id.activity_home_open);
			viewEasilydo.setOnClickListener(this);
			viewCall.setOnClickListener(this);
			viewSMS.setOnClickListener(this);
			viewOpen.setOnClickListener(this);
		} else {
			getHome();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		BLog.e(TAG, "" + event.getKeyCode());
		// if (event.getKeyCode() == KeyEvent.KEYCODE_HOME) {
		// return true;
		// }
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		if (viewId == R.id.activity_home_easilydo) {
			try {
				Intent intent = getPackageManager().getLaunchIntentForPackage("com.easilydo");
				startActivity(intent);
				finish();
			} catch (Exception ex) {
				AlertDialogFragment fragment = AlertDialogFragment.getInstance("Error",
						"You have not installed Easilydo", android.R.string.ok);
				fragment.show(getSupportFragmentManager());
			}
		} else if (viewId == R.id.activity_home_call) {
			try {
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				startActivity(intent);
				finish();
			} catch (ActivityNotFoundException ex) {
				AlertDialogFragment fragment = AlertDialogFragment.getInstance("Error",
						"Can't make call on this device", android.R.string.ok);
				fragment.show(getSupportFragmentManager());
			}
		} else if (viewId == R.id.activity_home_sms) {
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setType("vnd.android-dir/mms-sms");
				startActivity(intent);
				finish();
			} catch (ActivityNotFoundException ex) {
				AlertDialogFragment fragment = AlertDialogFragment.getInstance("Error",
						"Can't send sms on this device", android.R.string.ok);
				fragment.show(getSupportFragmentManager());
			}
		} else if (viewId == R.id.activity_home_open) {
			finish();
		}
	}

	private void getHome() {
		PackageManager packageManager = this.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> resolveInfos = packageManager
				.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		String myPackageName = getPackageName();
		for (ResolveInfo ri : resolveInfos) {
			if (myPackageName.equals(ri.activityInfo.packageName)) {
				continue;
			}
			intent = new Intent();
			ComponentName name = new ComponentName(ri.activityInfo.packageName, ri.activityInfo.name);
			intent.setComponent(name);
			// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
			// Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			moveTaskToBack(true);
			finish();
			overridePendingTransition(0, 0);
			break;
		}
	}
}
