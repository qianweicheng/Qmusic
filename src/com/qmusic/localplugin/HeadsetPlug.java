package com.qmusic.localplugin;

import java.util.WeakHashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.qmusic.common.BConstants;
import com.qmusic.common.IAsyncDataCallback;

public class HeadsetPlug extends BasePlug {
	Context ctx;
	IntentFilter intentFilter;
	HeadsetPlugReceiver headsetPlugReceiver;
	int state;
	WeakHashMap<IAsyncDataCallback, Object> callbacks;
	boolean isEnabled;

	@Override
	public void init(Context ctx) {
		this.ctx = ctx;
		headsetPlugReceiver = new HeadsetPlugReceiver();
		intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.HEADSET_PLUG");
		ctx.registerReceiver(headsetPlugReceiver, intentFilter);
	}

	@Override
	public void destory() {
		ctx.unregisterReceiver(headsetPlugReceiver);
	}

	/**
	 * 
	 * @return 1 has headset; 0 no headset
	 */
	public int getState() {
		return state;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setCallback(IAsyncDataCallback callback) {
		if (callbacks == null) {
			callbacks = new WeakHashMap<IAsyncDataCallback, Object>();
		}
		callbacks.put(callback, null);
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
		if (isEnabled && state == 1) {
			// TODO
		}
	}

	public void removeCallback(IAsyncDataCallback callback) {
		callbacks.remove(callback);
	}

	public class HeadsetPlugReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.hasExtra("state")) {
				state = intent.getIntExtra("state", 0);
				if (isEnabled) {
					if (state == 1) {
						// TODO
					}
					if (callbacks != null) {
						for (IAsyncDataCallback cal : callbacks.keySet()) {
							cal.callback(BConstants.OP_RESULT_OK, state);
						}
					}
				}
			}
		}
	}

}
