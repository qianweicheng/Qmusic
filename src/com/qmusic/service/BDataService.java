package com.qmusic.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.qmusic.notification.ScheduledReceiver;
import com.qmusic.uitls.BLog;

public class BDataService extends Service {
	static final String TAG = "BDataService";
	private BDataServiceImp binder;

	@Override
	public void onCreate() {
		super.onCreate();
		BLog.i(TAG, "onCreate");
		init();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	@Override
	public void onDestroy() {
		BLog.i(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	void init() {
		ScheduledReceiver.init(getApplicationContext());
		binder = new BDataServiceImp(this);
	}
}
