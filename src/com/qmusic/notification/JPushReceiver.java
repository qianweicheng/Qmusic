package com.qmusic.notification;

import com.qmusic.activities.SplashActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

public class JPushReceiver extends BroadcastReceiver {
	static final String TAG = JPushReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		String action = intent.getAction();
		Log.d(TAG, "onReceive - " + action);
		if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)) {

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(action)) {
			String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
			String message = bundle.getString(JPushInterface.EXTRA_ALERT);
			String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
			Log.d(TAG, "title:" + title + ";message:" + message + ";extra:" + extra);
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(action)) {
			Intent mIntent = new Intent(context, SplashActivity.class);
			mIntent.putExtras(bundle);
			mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mIntent.setAction(Intent.ACTION_MAIN);
			mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			context.startActivity(mIntent);
		}
	}
}
