package com.qmusic;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Stack;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.os.IBinder;
import android.util.Log;

import com.androidquery.util.AQUtility;
import com.qmusic.common.BConstants;
import com.qmusic.common.BUser;
import com.qmusic.common.IAsyncDataCallback;
import com.qmusic.common.IServiceCallback;
import com.qmusic.dal.BDatabaseHelper;
import com.qmusic.notification.BNotification;
import com.qmusic.notification.ScheduledReceiver;
import com.qmusic.plugin.PluginManager;
import com.qmusic.service.BDataService;
import com.qmusic.uitls.BAppHelper;
import com.qmusic.uitls.BLog;
import com.umeng.analytics.MobclickAgent;

public class MyApplication extends Application {
	public static final String TAG = MyApplication.class.getSimpleName();
	public static boolean DEBUG;
	public static boolean STARTED;
	static ArrayList<WeakReference<IAsyncDataCallback>> callbackList;
	static IServiceCallback dataService;
	static Stack<String> foreground;
	public static long startDate;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "====Start " + getPackageName() + "====");
		init(this);
	}

	@Override
	public void onLowMemory() {
		Log.w(TAG, "low memory");
		super.onLowMemory();
	}

	public static void init(Application ctx) {
		startDate = System.currentTimeMillis();
		foreground = new Stack<String>();
		foreground.setSize(2);
		ApplicationInfo appInfo = ctx.getApplicationInfo();
		if ((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == 0) {
			DEBUG = false;
		} else {
			DEBUG = true;
		}

		AQUtility.setContext(ctx);
		AQUtility.setDebug(DEBUG);
		BLog.setLevel(BLog.ALL);
		BUser.init();
		PluginManager.init(ctx);
		BDatabaseHelper.init(ctx);
		BAppHelper.init(ctx);
		ctx.bindService(new Intent(ctx, BDataService.class), serviceConnection, Context.BIND_AUTO_CREATE);
		// JPushInterface.setDebugMode(DEBUG);
		// JPushInterface.init(ctx);
		// if (BUser.isLogined()) {
		// JPushInterface.setAlias(ctx, BUser.getUser().getId(), null);
		// }
		MobclickAgent.setDebugMode(DEBUG);
		MobclickAgent.setAutoLocation(true);
		MobclickAgent.setSessionContinueMillis(60 * 1000);
	}

	public static final void shutdown() {
		final Context ctx = AQUtility.getContext();
		try {
			PluginManager.destory();
			// this is initialized in BDataService
			ScheduledReceiver.shutdown(ctx);
			ctx.stopService(new Intent(ctx, BDataService.class));
			BDatabaseHelper.closeDB();
			BNotification.cancel(ctx);
			MobclickAgent.onKillProcess(ctx);
		} catch (Exception ex) {
			MobclickAgent.reportError(ctx, ex.getMessage());
			ex.printStackTrace();
		}
		long minutes = (System.currentTimeMillis() - startDate) / 1000 / 60;
		Log.i(TAG, String.format("====End %s. Used %d minute(s)====", ctx.getString(R.string.app_name), minutes));
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}

	public static final void getDataService(final IAsyncDataCallback callback) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				callback.callback(BConstants.OP_RESULT_OK, dataService);
			}
		};
		if (dataService != null) {
			AQUtility.post(runnable);
		} else {
			synchronized (AQUtility.getContext()) {
				if (dataService == null) {
					if (callbackList == null) {
						callbackList = new ArrayList<WeakReference<IAsyncDataCallback>>();
					}
					callbackList.add(new WeakReference<IAsyncDataCallback>(callback));
				} else {
					AQUtility.post(runnable);
				}
			}
		}
	}

	public static final void foreground(String tag) {
		if (foreground.size() == 0) {
			// Note: session start
		}
		BLog.v(TAG, "open:" + tag);
		foreground.push(tag);
	}

	public static final void background(String tag) {
		BLog.v(TAG, "close:" + tag);
		foreground.remove(tag);
		if (foreground.size() == 0) {
			// Note: session end
		}
	}

	/*
	 * this method can't be called from the onCreate or onStart of an activity
	 * 
	 * @return current activity
	 */
	public static final String foreground() {
		if (foreground.size() > 0) {
			return foreground.peek();
		} else {
			return null;
		}
	}

	static ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// Note: this will never get called except in the case that the
			// service crashed or killed by the system
			BLog.e(TAG, "service is killed by system");
			dataService = null;
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			synchronized (AQUtility.getContext()) {
				if (arg1 instanceof IServiceCallback) {
					dataService = (IServiceCallback) arg1;
					if (callbackList != null) {
						Runnable runnable = new Runnable() {
							@Override
							public void run() {
								for (WeakReference<IAsyncDataCallback> wCallback : callbackList) {
									IAsyncDataCallback callback = wCallback.get();
									if (callback != null) {
										callback.callback(BConstants.OP_RESULT_OK, dataService);
									}
								}
							}
						};
						AQUtility.post(runnable);
					}
				} else {
					BLog.e(TAG, "arg1 is NOT an instanceof IEdoDataService");
				}
				STARTED = true;
			}
		}
	};
}
