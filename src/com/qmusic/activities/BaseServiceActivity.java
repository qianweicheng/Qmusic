package com.qmusic.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.qmusic.common.IServiceCallback;
import com.qmusic.service.BDataService;
import com.qmusic.uitls.BLog;

public abstract class BaseServiceActivity extends BaseActivity {
	IServiceCallback dataService;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		bindService(new Intent(this, BDataService.class), serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		synchronized (this) {
			if (dataService != null) {
				unbindService(serviceConnection);
			}
		}
	}

	protected void onServicedBinded(IServiceCallback dataService) {

	}

	ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// Note: this will never get called except in the case that the
			// service crashed or killed by the system
			BLog.e(TAG, "service is killed by system");
			dataService = null;
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			synchronized (this) {
				if (arg1 instanceof IServiceCallback) {
					dataService = (IServiceCallback) arg1;
					onServicedBinded(dataService);
				} else {
					BLog.e(TAG, "arg1 is NOT an instanceof IEdoDataService");
				}
			}
		}
	};
}
