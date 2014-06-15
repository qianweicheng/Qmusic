package com.qmusic.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.qmusic.service.BDataService;
import com.qmusic.uitls.BLog;

public class StartupReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		BLog.d("StartupReceiver", intent.getAction());
		Intent service = new Intent(context, BDataService.class);
		service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startService(service);
	}
}
