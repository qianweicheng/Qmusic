package com.qmusic.uitls;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.androidquery.util.AQUtility;
import com.qmusic.MyApplication;
import com.qmusic.R;
import com.qmusic.activities.SplashActivity;
import com.qmusic.common.BConstants;

public class BAppHelper {
	static final String TAG = BAppHelper.class.getSimpleName();
	static int exiting;

	public final static void goHome(Activity ctx) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		intent.addCategory(Intent.CATEGORY_HOME);
		ctx.startActivity(intent);
		ctx.overridePendingTransition(0, 0);
	}

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

	public final static void init(Context ctx) {
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
			BLog.i(TAG, "DeviceID:" + BUtilities.getDeviceId());
			TelephonyManager telManager = (TelephonyManager) ctx.getSystemService(Service.TELEPHONY_SERVICE);
			HashMap<String, Object> telInfo = new HashMap<String, Object>();
			telInfo.put("DeviceID(IMEI,MEID)", telManager.getDeviceId());
			telInfo.put("Line1Number", telManager.getLine1Number());
			telInfo.put("NetworkCountryIso", telManager.getNetworkCountryIso());
			telInfo.put("NetworkOperator", telManager.getNetworkOperator());
			telInfo.put("NetworkOperatorName", telManager.getNetworkOperatorName());
			telInfo.put("SimCountryIso", telManager.getSimCountryIso());
			telInfo.put("SimOperator", telManager.getSimOperator());
			telInfo.put("SimOperatorName", telManager.getSimOperatorName());
			telInfo.put("SimSerialNumber", telManager.getSimSerialNumber());
			telInfo.put("SimState", telManager.getSimState());
			telInfo.put("SubscriberId", telManager.getSubscriberId());
			BLog.i(TAG, BUtilities.objToJsonString(telInfo));
			// Do StrictMode setup here
			// StrictMode.setVmPolicy(new
			// StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog()
			// .penaltyDeath().build());
			// StrictMode.setThreadPolicy(new
			// StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()
			// .detectNetwork().penaltyLog().build());
			// watchMem();
			BLog.i(TAG, BUtilities.objToJsonString(ctx.getApplicationInfo()));
		}
	}

	public static final void watchMem() {
		final Debug.MemoryInfo outInfo = new Debug.MemoryInfo();
		final ActivityManager.MemoryInfo outInfo1 = new ActivityManager.MemoryInfo();
		final ActivityManager am = (ActivityManager) AQUtility.getContext().getSystemService(Context.ACTIVITY_SERVICE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					Debug.getMemoryInfo(outInfo);
					int uss0 = outInfo.getTotalPrivateDirty();
					int pss0 = outInfo.getTotalPss();
					int rss0 = outInfo.getTotalSharedDirty();
					BLog.i(TAG, "uss0=" + uss0 + ";pss0=" + pss0 + ";rss0=" + (uss0 + rss0));
					am.getMemoryInfo(outInfo1);
					BLog.i(TAG, "availMem=" + outInfo1.availMem + ";threshold=" + outInfo1.threshold + ";lowMemory=" + outInfo1.lowMemory);
					long nativeHeapAllocatedSize = Debug.getNativeHeapAllocatedSize();
					long nativeHeapFreeSize = Debug.getNativeHeapFreeSize();
					long nativeHeapSize = Debug.getNativeHeapSize();
					long globalAllocSize = Debug.getGlobalAllocSize();
					long threadAllocSize = Debug.getThreadAllocSize();
					BLog.i(TAG, "nativeHeapAllocatedSize=" + nativeHeapAllocatedSize + ";nativeHeapFreeSize=" + nativeHeapFreeSize + ";nativeHeapSize="
							+ nativeHeapSize + ";globalAllocSize=" + globalAllocSize + ";threadAllocSize=" + threadAllocSize);
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
