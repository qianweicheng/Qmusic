package com.qmusic.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.qmusic.controls.dialogs.AlertDialogFragment;
import com.qmusic.controls.dialogs.IFragmentDialogCallback;
import com.qmusic.notification.ScheduledReceiver;
import com.qmusic.uitls.BLog;

public class EmptyActivity extends FragmentActivity implements IFragmentDialogCallback {
	static final String TAG = EmptyActivity.class.getSimpleName();
	int action;

	public static final void show(Context ctx, Bundle extras) {
		Intent intent = new Intent(ctx, EmptyActivity.class);
		intent.putExtras(extras);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey(ScheduledReceiver.SCHEDULE_TYPE)) {
			action = bundle.getInt(ScheduledReceiver.SCHEDULE_TYPE, 0);
			if (ScheduledReceiver.SCHEDULE_RATING == action) {
				String title = bundle.getString("title");
				String msg = bundle.getString("message");
				AlertDialogFragment fragment = AlertDialogFragment.getInstance(title, msg,
						getString(android.R.string.ok), getString(android.R.string.cancel));
				fragment.show(getSupportFragmentManager(), ScheduledReceiver.SCHEDULE_TYPE + action);
			} else if (ScheduledReceiver.SCHEDULE_ACCOUNT == action) {
				String title = bundle.getString("title");
				String msg = bundle.getString("message");
				AlertDialogFragment fragment = AlertDialogFragment.getInstance(title, msg,
						getString(android.R.string.ok), getString(android.R.string.cancel));
				fragment.show(getSupportFragmentManager(), ScheduledReceiver.SCHEDULE_TYPE + action);
			} else {
				AlertDialogFragment fragment = AlertDialogFragment.getInstance("Unknow type", "Message",
						getString(android.R.string.ok), getString(android.R.string.cancel));
				fragment.show(getSupportFragmentManager(), ScheduledReceiver.SCHEDULE_TYPE + action);
			}
		} else {
			finish();
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			BLog.i(TAG, "" + which);
		} else if (which == DialogInterface.BUTTON_NEGATIVE) {
			BLog.i(TAG, "" + which);
		} else if (which == DialogInterface.BUTTON_NEUTRAL) {
			BLog.i(TAG, "" + which);
		} else {
			BLog.i(TAG, "" + which);
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
