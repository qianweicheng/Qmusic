package com.qmusic;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.os.Debug;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;

import com.androidquery.util.AQUtility;
import com.qmusic.common.BConstants;
import com.qmusic.common.BUser;
import com.qmusic.common.IAsyncDataCallback;
import com.qmusic.common.IServiceCallback;
import com.qmusic.dal.BDatabaseHelper;
import com.qmusic.notification.BNotification;
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
	static SparseArray<String> foreground;
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

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

	public static void init(Application ctx) {
		startDate = System.currentTimeMillis() / 1000;
		foreground = new SparseArray<String>();
		ApplicationInfo appInfo = ctx.getApplicationInfo();
		int appFlags = appInfo.flags;
		if ((appFlags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
			DEBUG = true;
			// Do StrictMode setup here
			// StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
			// .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
			// .build());
			// StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
			// .detectDiskReads().detectDiskWrites().detectNetwork()
			// .penaltyLog().build());
			// watchMem();
		} else {
			DEBUG = false;
			// CrashHandler.init(ctx);
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
			ctx.stopService(new Intent(ctx, BDataService.class));
			BDatabaseHelper.closeDB();
			BNotification.cancel(ctx);
			MobclickAgent.onKillProcess(ctx);
		} catch (Exception ex) {
			MobclickAgent.reportError(ctx, ex.getMessage());
			ex.printStackTrace();
		}
		Log.i(TAG, "====End " + ctx.getPackageName() + "====");
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
		}
		BLog.v(TAG, "open:" + tag);
		foreground.put(tag.hashCode(), tag);
	}

	public static final void background(String tag) {
		BLog.v(TAG, "close:" + tag);
		try {
			foreground.remove(tag.hashCode());
			if (foreground.size() == 0) {
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * this method can't be called from the onCreate or onStart of an activity
	 * 
	 * @return
	 */
	public static final String forgeground() {
		if (foreground.size() > 0) {
			return foreground.valueAt(0);
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

					ActivityManager am = (ActivityManager) AQUtility.getContext().getSystemService(ACTIVITY_SERVICE);
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
