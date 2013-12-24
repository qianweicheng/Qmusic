package com.qmusic.uitls;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.androidquery.util.AQUtility;
import com.qmusic.MyApplication;
import com.qmusic.R;
import com.qmusic.activities.SplashActivity;
import com.qmusic.common.BConstants;

public class BAppHelper {
	static final String TAG = BAppHelper.class.getSimpleName();
	static int exiting;

	@SuppressLint("InlinedApi")
	public final static boolean exit(Activity activity, boolean force) {
		if (exiting == 1 || force) {
			Intent intent = new Intent(activity, SplashActivity.class);
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			} else {
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			}
			intent.putExtra(SplashActivity.SHUTDOWN, true);
			activity.startActivity(intent);
			activity.overridePendingTransition(0, 0);
			return true;
		} else if (exiting == 0) {
			exiting++;
			Toast.makeText(activity, R.string.press_back_to_exit, Toast.LENGTH_SHORT).show();
			Handler handle = new Handler();
			handle.postDelayed(new Runnable() {
				@Override
				public void run() {
					exiting = 0;
				}
			}, 3000);
			return false;
		}
		return false;
	}

	@SuppressLint("InlinedApi")
	public final static void reLogin(Activity activity) {
		Intent intent = new Intent(activity, SplashActivity.class);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		} else {
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		intent.putExtra(SplashActivity.RE_LOGIN, true);
		activity.startActivity(intent);
		activity.finish();
		activity.overridePendingTransition(0, 0);
	}

	public static final void setTitle(SherlockFragmentActivity activity, int layoutResID, String title, boolean showBack) {
		activity.setContentView(layoutResID);
		ActionBar actionBar = activity.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(showBack);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		// actionBar.setDisplayShowCustomEnabled(true);
		// actionBar.setDisplayShowHomeEnabled(true);
		// actionBar.setBackgroundDrawable(AQUtility.getContext().getResources().getDrawable(R.drawable.edo_navbar));
		if (TextUtils.isEmpty(title)) {
			actionBar.setTitle("  ");
		} else {
			// add padding between back button and title
			actionBar.setTitle(" " + title);
		}
	}

	public static final void setTitle(final Activity activity, int layoutResID, String title) {
		setTitle(activity, layoutResID, title, R.drawable.icon, new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.finish();
			}
		}, 0, null);
	}

	public static final void setTitle(final Activity activity, int layoutResID, String title, int leftResId,
			OnClickListener leftButtonCallback, int rightResId, OnClickListener rightButtonCallback) {
		activity.setContentView(layoutResID);
		ImageButton btnLeft = (ImageButton) activity.findViewById(R.id.common_title_left_icon);
		ImageButton btnRight = (ImageButton) activity.findViewById(R.id.common_title_right_icon);
		TextView txtTitle = (TextView) activity.findViewById(R.id.common_title_title);
		txtTitle.setText(title);
		if (leftResId == 0 || leftButtonCallback == null) {
			btnLeft.setVisibility(View.INVISIBLE);
		} else {
			btnLeft.setImageResource(leftResId);
			btnLeft.setOnClickListener(leftButtonCallback);
		}
		if (rightResId == 0 || rightButtonCallback == null) {
			btnRight.setVisibility(View.INVISIBLE);
		} else {
			btnRight.setImageResource(rightResId);
			btnRight.setOnClickListener(rightButtonCallback);
		}
	}

	public final static int init(Context ctx) {
		String countStr = BUtilities.getPref(BConstants.PRE_KEY_RUN_COUNT);
		int count;
		if (TextUtils.isEmpty(countStr)) {
			count = 0;
		} else {
			count = Integer.parseInt(countStr);
		}
		BUtilities.setPref(BConstants.PRE_KEY_RUN_COUNT, "" + (++count));
		if (MyApplication.DEBUG) {
			String log = String.format("App Opened:%s time(s)", count);
			BLog.i(TAG, log);
			DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
			BLog.i(TAG, displayMetrics.toString());
			Configuration configuration = ctx.getResources().getConfiguration();
			BLog.i(TAG, configuration.toString());
			BLog.i(TAG, BUtilities.objToJsonString(System.getenv()));
			BLog.i(TAG, BUtilities.objToJsonString(System.getProperties()));
			// Do StrictMode setup here
			// StrictMode.setVmPolicy(new
			// StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog()
			// .penaltyDeath().build());
			// StrictMode.setThreadPolicy(new
			// StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()
			// .detectNetwork().penaltyLog().build());
			// watchMem();
		}
		return count;
	}

	static final void watchMem() {
		final Debug.MemoryInfo outInfo = new Debug.MemoryInfo();
		final ActivityManager.MemoryInfo outInfo1 = new ActivityManager.MemoryInfo();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					Debug.getMemoryInfo(outInfo);
					int uss0 = outInfo.getTotalPrivateDirty();
					int pss0 = outInfo.getTotalPss();
					int rss0 = outInfo.getTotalSharedDirty();
					BLog.i(TAG, "uss0=" + uss0 + ";pss0=" + pss0 + ";rss0=" + (uss0 + rss0));

					ActivityManager am = (ActivityManager) AQUtility.getContext().getSystemService(
							Context.ACTIVITY_SERVICE);
					am.getMemoryInfo(outInfo1);
					BLog.i(TAG, "availMem=" + outInfo1.availMem + ";threshold=" + outInfo1.threshold + ";lowMemory="
							+ outInfo1.lowMemory);

					long nativeHeapAllocatedSize = Debug.getNativeHeapAllocatedSize();
					long nativeHeapFreeSize = Debug.getNativeHeapFreeSize();
					long nativeHeapSize = Debug.getNativeHeapSize();
					long globalAllocSize = Debug.getGlobalAllocSize();
					long threadAllocSize = Debug.getThreadAllocSize();
					BLog.i(TAG, "nativeHeapAllocatedSize=" + nativeHeapAllocatedSize + ";nativeHeapFreeSize="
							+ nativeHeapFreeSize + ";nativeHeapSize=" + nativeHeapSize + ";globalAllocSize="
							+ globalAllocSize + ";threadAllocSize=" + threadAllocSize);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}
