package com.qmusic;

import java.util.Stack;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.util.AQUtility;
import com.qmusic.common.BAppHelper;
import com.qmusic.common.BLocationManager;
import com.qmusic.common.BUser;
import com.qmusic.dal.BDatabaseHelper;
import com.qmusic.localplugin.PluginManager;
import com.qmusic.notification.BNotification;
import com.qmusic.service.BDataService;
import com.qmusic.uitls.BLog;
import com.qmusic.uitls.BUtilities;
import com.umeng.analytics.MobclickAgent;

public class MyApplication extends Application {
	public static final String TAG = MyApplication.class.getSimpleName();
	public static boolean DEBUG;
	public static long STARTED_TIME;
	static Stack<String> foreground;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "====Start " + getPackageName() + "====");
		init(this);
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		Log.w(TAG, "onTrimMemory:" + level);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Log.w(TAG, "low memory");
	}

	public static void init(Application ctx) {
		STARTED_TIME = System.currentTimeMillis();
		ApplicationInfo appInfo = ctx.getApplicationInfo();
		if ((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == 0) {
			DEBUG = false;
		} else {
			DEBUG = true;
		}
		AQUtility.setContext(ctx);
		String userAgent = System.getProperties().getProperty("http.agent");
		AjaxCallback.setAgent(userAgent);
		AQUtility.setDebug(DEBUG);
		BLog.setLevel(BLog.ALL);
		RunningAppProcessInfo appProcessInfo = BUtilities.getCurProcess(ctx);
		BLog.i(TAG, BUtilities.objToJsonString(appProcessInfo));
		if (appProcessInfo != null) {
			BLog.i(TAG, "name:%s,pid:%d,uid:%d", appProcessInfo.processName, appProcessInfo.pid, appProcessInfo.uid);
			if (appInfo.packageName.equals(appProcessInfo.processName)) {
				// Note: UI component
				foreground = new Stack<String>();
				foreground.setSize(2);
				BAppHelper.init(ctx);
				BUser.init();
				ctx.startService(new Intent(ctx, BDataService.class));
				PluginManager.init(ctx);
				BLocationManager.init(ctx);
				MobclickAgent.setDebugMode(DEBUG);
				MobclickAgent.updateOnlineConfig(ctx);
				MobclickAgent.setSessionContinueMillis(60 * 1000);
			} else {
				// Note:Other component for remote process
			}
		} else {
			BLog.e(TAG, "Should never get here");
		}
	}

	public static final void shutdown() {
		final Context ctx = AQUtility.getContext();
		try {
			ctx.stopService(new Intent(ctx, BDataService.class));
			BDatabaseHelper.closeDB();
			BNotification.cancel(ctx);
			MobclickAgent.onKillProcess(ctx);
		} catch (Exception ex) {
			MobclickAgent.reportError(ctx, ex.getMessage());
			ex.printStackTrace();
		}
		long minutes = (System.currentTimeMillis() - STARTED_TIME) / 1000 / 60;
		Log.i(TAG, String.format("====End %s. Used %d minute(s)====", ctx.getString(R.string.app_name), minutes));
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}

	/**
	 * should only be called from UI
	 * 
	 * @param tag
	 */
	public static final void foreground(String tag) {
		if (foreground.size() == 0) {
			// Note: session start
		}
		BLog.v(TAG, "open:" + tag);
		foreground.push(tag);
	}

	/**
	 * should only be called from UI
	 * 
	 * @param tag
	 */
	public static final void background(String tag) {
		BLog.v(TAG, "close:" + tag);
		foreground.remove(tag);
		if (foreground.size() == 0) {
			// Note: session end
		}
	}

	/*
	 * should only be called from UI. This method can't be called from the
	 * onCreate or onStart of an activity
	 * 
	 * @return current activity
	 */
	public static final String foreground() {
		if (foreground != null && foreground.size() > 0) {
			return foreground.peek();
		} else {
			return null;
		}
	}
}
