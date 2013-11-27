package com.qmusic.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.qmusic.dal.AlarmTable;
import com.qmusic.entities.BAlarm;
import com.qmusic.uitls.BLog;

public final class BAlarmHelper {
	static final String TAG = BAlarmHelper.class.getSimpleName();

	public static void init(Context ctx) {
		AlarmTable alarmDAL = new AlarmTable();
		schedule(ctx, alarmDAL);
	}

	public static void schedule(Context ctx, BAlarm alarm) {
		AlarmTable alarmDAL = new AlarmTable();
		BAlarm al = alarmDAL.query(alarm.id);
		if (al == null) {
			alarmDAL.insert(alarm);
		} else {
			alarmDAL.update(alarm);
		}
		BAlarm current = alarmDAL.current();
		if (current != null && current.time > alarm.time) {
			current.status = BAlarm.STATUS_SCHEDUE;
			alarmDAL.update(current);
		}
		schedule(ctx, alarmDAL);
	}

	public static void remove(Context ctx, long id) {
		AlarmTable alarmDAL = new AlarmTable();
		BAlarm alarm = alarmDAL.query(id);
		if (alarm != null) {
			if (alarm.status == BAlarm.STATUS_SCHEDUING) {
				alarm.status = BAlarm.STATUS_CANCEL;
				alarmDAL.update(alarm);
				schedule(ctx, alarmDAL);
			} else if (alarm.status == BAlarm.STATUS_SCHEDUE) {
				alarm.status = BAlarm.STATUS_CANCEL;
				alarmDAL.update(alarm);
			} else {
				// Do nothing
			}
		}
	}

	private static void schedule(Context ctx, AlarmTable alarmDAL) {
		BAlarm nearestAlarm = alarmDAL.pop();
		if (nearestAlarm != null) {
			Intent intent = new Intent(ctx, ScheduledReceiver.class);
			intent.putExtra(ScheduledReceiver.SCHEDULE_TYPE, ScheduledReceiver.SCHEDULE_ALARM);
			intent.putExtra("id", nearestAlarm.id);
			PendingIntent pi = PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager am = (AlarmManager) ctx.getSystemService(Service.ALARM_SERVICE);
			BLog.i(TAG, "doAlarm title = " + nearestAlarm.title + " subTitle=" + nearestAlarm.subTitle);
			am.set(AlarmManager.RTC_WAKEUP, nearestAlarm.time, pi);
			nearestAlarm.status = BAlarm.STATUS_SCHEDUING;
			alarmDAL.update(nearestAlarm);
		} else {
			BLog.i(TAG, "has no alarm");
		}
	}

	public static void doAlarm(Context ctx, long id) {
		AlarmTable alarmDAL = new AlarmTable();
		// BAlarm alarm = alarmDAL.current();
		BAlarm alarm = alarmDAL.query(id);
		if (alarm != null && alarm.status != BAlarm.STATUS_CANCEL) {
			BNotification.notify(ctx, alarm.title, alarm.subTitle, null);
			alarm.status = BAlarm.STATUS_DONE;
			alarmDAL.update(alarm);
			schedule(ctx, alarmDAL);
		}
	}
}
