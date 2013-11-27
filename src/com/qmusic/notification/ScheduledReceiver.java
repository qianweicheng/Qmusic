package com.qmusic.notification;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.qmusic.activities.EmptyActivity;
import com.qmusic.common.BConstants;
import com.qmusic.entities.BAlarm;
import com.qmusic.uitls.BLog;
import com.qmusic.uitls.BUtilities;

public class ScheduledReceiver extends BroadcastReceiver {
	public static final String TAG = "ScheduledReceiver";
	static final int INTERVAL = 2 * 60 * 1000;// 2 mins
	static final int DELAY = 2 * 1000;// 2 seconds
	public static final String SCHEDULE_TYPE = "type";
	public static final int SCHEDULE_ALARM = 1;
	public static final int SCHEDULE_DISCOVER = 2;
	public static final int SCHEDULE_RATING = 3;
	public static final int SCHEDULE_ACCOUNT = 4;

	public static final void init(Context ctx) {
		final long triggerAtMillis = System.currentTimeMillis() + INTERVAL;
		final AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		// Alarm
		if (enableAlarm()) {
			BAlarmHelper.init(ctx);
		}
		// Discover
		if (enableDiscover()) {
			Intent intentDiscover = new Intent(ctx, ScheduledReceiver.class);
			intentDiscover.putExtra(ScheduledReceiver.SCHEDULE_TYPE, ScheduledReceiver.SCHEDULE_DISCOVER);
			PendingIntent piDiscover = PendingIntent.getBroadcast(ctx, ScheduledReceiver.SCHEDULE_DISCOVER,
					intentDiscover, PendingIntent.FLAG_UPDATE_CURRENT);
			am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, INTERVAL, piDiscover);
		}
		if (showAppRating()) {
			Intent intentRating = new Intent(ctx, ScheduledReceiver.class);
			intentRating = intentRating.putExtra(ScheduledReceiver.SCHEDULE_TYPE, ScheduledReceiver.SCHEDULE_RATING);
			PendingIntent piRating = PendingIntent.getBroadcast(ctx, SCHEDULE_RATING, intentRating,
					PendingIntent.FLAG_UPDATE_CURRENT);
			am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + DELAY, piRating);
		} else if (showAccount()) {
			Intent intentExpired = new Intent(ctx, ScheduledReceiver.class);
			intentExpired = intentExpired.putExtra(ScheduledReceiver.SCHEDULE_TYPE, ScheduledReceiver.SCHEDULE_ACCOUNT);
			PendingIntent piExpired = PendingIntent.getBroadcast(ctx, SCHEDULE_ACCOUNT, intentExpired,
					PendingIntent.FLAG_UPDATE_CURRENT);
			am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + DELAY, piExpired);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int type = intent.getIntExtra(SCHEDULE_TYPE, 0);
		switch (type) {
		case SCHEDULE_ALARM: {
			BLog.i(TAG, "SCHEDULE_ALARM");
			long id = intent.getLongExtra("id", 0);
			BAlarmHelper.doAlarm(context, id);
			break;
		}
		case SCHEDULE_DISCOVER: {
			BLog.i(TAG, "SCHEDULE_DISCOVER");
			BAlarm alarm = new BAlarm();
			Calendar cal = Calendar.getInstance();
			alarm.title = cal.getTime().toString();
			alarm.subTitle = cal.getTime().toString();
			alarm.time = cal.getTimeInMillis() + DELAY;
			BAlarmHelper.schedule(context, alarm);
			break;
		}
		case SCHEDULE_RATING: {
			BLog.i(TAG, "SCHEDULE_RATING");
			Bundle extras = new Bundle();
			extras.putInt(SCHEDULE_TYPE, SCHEDULE_RATING);
			EmptyActivity.show(context, extras);
			break;
		}
		case SCHEDULE_ACCOUNT: {
			BLog.i(TAG, "SCHEDULE_ACCOUNT");
			Bundle extras = new Bundle();
			extras.putInt(SCHEDULE_TYPE, SCHEDULE_ACCOUNT);
			EmptyActivity.show(context, extras);
			break;
		}
		default: {
			break;
		}
		}
	}

	private static final boolean enableAlarm() {
		return true;
	}

	private static final boolean enableDiscover() {
		return true;
	}

	private static final boolean showAppRating() {
		String countStr = BUtilities.getPref(BConstants.PRE_KEY_RUN_COUNT);
		if (!TextUtils.isEmpty(countStr)) {
			int count = Integer.parseInt(countStr);
			return count % 100 == 0;
		}
		return false;
	}

	private static final boolean showAccount() {
		return false;
	}

}
