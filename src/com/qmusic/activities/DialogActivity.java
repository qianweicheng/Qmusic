package com.qmusic.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.qmusic.controls.dialogs.AlertDialogFragment;
import com.qmusic.controls.dialogs.IFragmentDialogCallback;
import com.qmusic.notification.ScheduledReceiver;
import com.qmusic.uitls.BLog;
import com.umeng.analytics.MobclickAgent;

public class DialogActivity extends FragmentActivity implements IFragmentDialogCallback {
	static final String TAG = DialogActivity.class.getSimpleName();
	int action;

	/**
	 * @param ctx
	 * @param extras
	 */
	public static final void show(Context ctx, Bundle extras) {
		Intent intent = new Intent(ctx, DialogActivity.class);
		intent.putExtras(extras);
		if (ctx instanceof Activity) {
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		} else {
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey(ScheduledReceiver.SCHEDULE_TYPE)) {
			action = bundle.getInt(ScheduledReceiver.SCHEDULE_TYPE, 0);
			if (ScheduledReceiver.SCHEDULE_RATING == action || ScheduledReceiver.SCHEDULE_ACCOUNT == action) {
				String title = bundle.getString("title");
				String msg = bundle.getString("message");
				String okStr = bundle.getString("ok");
				String cancelStr = bundle.getString("cancel");
				AlertDialogFragment fragment = AlertDialogFragment.getInstance(title, msg, TextUtils.isEmpty(okStr) ? getString(android.R.string.ok) : okStr,
				        TextUtils.isEmpty(cancelStr) ? getString(android.R.string.cancel) : cancelStr);
				fragment.show(getSupportFragmentManager(), ScheduledReceiver.SCHEDULE_TYPE + action);
			} else {
				AlertDialogFragment fragment = AlertDialogFragment.getInstance("Unknow type", "Please set a correct type!", getString(android.R.string.ok),
				        getString(android.R.string.cancel));
				fragment.show(getSupportFragmentManager(), ScheduledReceiver.SCHEDULE_TYPE + action);
			}
		} else {
			finish();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(ScheduledReceiver.SCHEDULE_TYPE, action);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (action) {
		case ScheduledReceiver.SCHEDULE_RATING: {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				MobclickAgent.onEvent(this, "rating", "like");
				try {
					Uri uri = Uri.parse("market://details?id=" + getPackageName() + "&write_review=true");
					Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
					goToMarket.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(goToMarket);
				} catch (Exception ex) {
					BLog.e(TAG, "the device has no appstore!");
				}
			} else {
				MobclickAgent.onEvent(this, "rating", "dislike");
			}
			break;
		}
		case ScheduledReceiver.SCHEDULE_ACCOUNT: {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				BLog.i(TAG, "" + which);
			}
			break;
		}
		}
	}

	@Override
	public void cancel() {
		// Do Nothing
		BLog.i(TAG, "canceled");
	}

	@Override
	public void dismiss() {
		finish();
	}

}
