package com.qmusic.common;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.qmusic.MyApplication;
import com.qmusic.uitls.BLog;

public class BCrashHandler implements UncaughtExceptionHandler {
	public static final String TAG = "CrashHandler";
	/** Default UncaughtException handler */
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private static BCrashHandler INSTANCE;
	private Context mContext;
	private static final String CRASH_REPORTER_EXTENSION = ".txt";
	private static String CRASHFILEPATH;

	private BCrashHandler() {
	}

	public static void init(Context ctx) {
		if (INSTANCE == null) {
			INSTANCE = new BCrashHandler();
		}
		INSTANCE.mContext = ctx;
		INSTANCE.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(INSTANCE);
		File cacheDir = ctx.getExternalCacheDir();
		if (cacheDir != null) {
			CRASHFILEPATH = cacheDir.toString();
		} else {
			CRASHFILEPATH = ctx.getCacheDir().toString();
		}
	}

	@Override
	public void uncaughtException(Thread arg0, Throwable arg1) {
		if (!handleException(arg1) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(arg0, arg1);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(10);
		}
	}

	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return true;
		}
		final String msg = ex.getLocalizedMessage();
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(MyApplication.getContext(), msg, Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		}.start();
		saveCrashInfoToFile(ex);
		return true;
	}

	public static void sendCrashReportsToServer() {
	}

	public static String[] getCrashReportFiles() {
		File filesDir = new File(CRASHFILEPATH);
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(CRASH_REPORTER_EXTENSION);
			}
		};
		String[] crFilesPath = filesDir.list(filter);
		for (int i = 0; i < crFilesPath.length; i++) {
			crFilesPath[i] = CRASHFILEPATH + crFilesPath[i];
			BLog.i(TAG, "upload crash file :" + crFilesPath[i]);
		}
		return crFilesPath;
	}

	private String saveCrashInfoToFile(Throwable ex) {
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		ex.printStackTrace(printWriter);

		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		try {
			PackageManager pm = mContext.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				printWriter.append("\r\n" + pi.versionName);
				printWriter.append("\r\nversionCode=" + pi.versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Error while collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				printWriter.append("\r\n" + field.getName() + ": " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "Error while collect crash info", e);
			}
		}

		String result = info.toString();
		printWriter.close();
		try {
			long timestamp = System.currentTimeMillis();
			String fileName = CRASHFILEPATH + "crash-" + timestamp + CRASH_REPORTER_EXTENSION;
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(result);
			writer.flush();
			writer.close();
			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing report file...", e);
		}
		return null;
	}
}
