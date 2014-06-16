package com.qmusic.localplugin.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.qmusic.localplugin.LockScreenPlug;

public class LockScreenReceiver extends BroadcastReceiver implements Runnable {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			LockScreenPlug.lockS();
			// AQUtility.postDelayed(this, 1000);
		} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
			// AQUtility.removePost(this);
		}
	}

	@Override
	public void run() {
		LockScreenPlug.lockS();
	}
}
