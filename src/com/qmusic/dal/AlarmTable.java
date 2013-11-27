package com.qmusic.dal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.qmusic.entities.BAlarm;
import com.qmusic.uitls.BLog;

public class AlarmTable extends BaseTable {
	static final String TAG = AlarmTable.class.getSimpleName();
	public static final String TABLE_NAME = "alarm";
	public static final String FIELD_TITLE = "title";
	public static final String FIELD_SUB_TITLE = "subTitle";
	public static final String FIELD_OPTIONS = "options";
	public static final String FIELD_TIME = "time";
	public static final String FIELD_STATUS = "status";
	public static final Uri CONTENT_URL = Uri.parse("content://" + BContentProvider.AUTHORITY + "/" + TABLE_NAME);
	public static final String[] PROJECTION = new String[] { _ID, FIELD_TITLE, FIELD_SUB_TITLE, FIELD_TIME,
			FIELD_STATUS, FIELD_OPTIONS };

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected void initField(HashMap<String, Class<?>> fields) {
		fields.put(FIELD_TITLE, String.class);
		fields.put(FIELD_SUB_TITLE, String.class);
		fields.put(FIELD_OPTIONS, String.class);
		fields.put(FIELD_TIME, Integer.class);
		fields.put(FIELD_STATUS, Integer.class);
	}

	public List<BAlarm> queryAlarm() {
		ArrayList<BAlarm> alarmList = new ArrayList<BAlarm>();
		Cursor mCursor = null;
		try {
			mCursor = query(PROJECTION, null, null, null, null, null, null);
			while (mCursor.moveToNext()) {
				BAlarm alarm = cursorToObject(mCursor);
				alarmList.add(alarm);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (mCursor != null) {
				mCursor.close();
			}
		}
		return alarmList;
	}

	public void insert(BAlarm alarm) {
		ContentValues cv = new ContentValues();
		cv.put(FIELD_TITLE, alarm.title);
		cv.put(FIELD_SUB_TITLE, alarm.subTitle);
		cv.put(FIELD_OPTIONS, alarm.options);
		cv.put(FIELD_TIME, alarm.time);
		cv.put(FIELD_STATUS, alarm.status);
		alarm.id = insert(cv);
		BLog.i(TAG, "insert a new alarm with id:" + alarm.id);
	}

	public void update(BAlarm alarm) {
		int result;
		ContentValues cv = new ContentValues();
		// cv.put("[_id]", alarm.id);
		cv.put(FIELD_TITLE, alarm.title);
		cv.put(FIELD_SUB_TITLE, alarm.subTitle);
		cv.put(FIELD_OPTIONS, alarm.options);
		cv.put(FIELD_TIME, alarm.time);
		cv.put(FIELD_STATUS, alarm.status);
		result = update(cv, _ID + "=?", new String[] { String.valueOf(alarm.id) });
		BLog.i(TAG, "update alarm with taskId:" + alarm.id + ";result:" + result);
	}

	public BAlarm pop() {
		BAlarm alarm = null;
		Cursor mCursor = query(
				PROJECTION,
				"([status]=? or [status]=?) and [time]>=?",
				new String[] { String.valueOf(BAlarm.STATUS_SCHEDUING), String.valueOf(BAlarm.STATUS_SCHEDUE),
						String.valueOf(System.currentTimeMillis() - 1 * 60 * 1000) }, null, null, "[time] ASC", null);
		// while (mCursor.moveToNext()) {
		// alarm = cursorToObject(mCursor);
		// EdoLog.e(TAG, alarm.toString());
		// }
		if (mCursor.moveToFirst()) {
			alarm = cursorToObject(mCursor);
		}
		mCursor.close();
		return alarm;
	}

	public BAlarm current() {
		BAlarm alarm = null;
		Cursor mCursor = query(PROJECTION, "[status]=?", new String[] { "" + BAlarm.STATUS_SCHEDUING }, null, null,
				"[time] DESC", null);
		if (mCursor.moveToFirst()) {
			alarm = cursorToObject(mCursor);
		}
		mCursor.close();
		return alarm;
	}

	public BAlarm query(long id) {
		BAlarm alarm = null;
		Cursor mCursor = query(PROJECTION, _ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
		if (mCursor.moveToFirst()) {
			alarm = cursorToObject(mCursor);
		}
		mCursor.close();
		return alarm;
	}

	private BAlarm cursorToObject(Cursor mCursor) {
		BAlarm alarm = new BAlarm();
		alarm.id = mCursor.getInt(0);
		alarm.title = mCursor.getString(1);
		alarm.subTitle = mCursor.getString(2);
		alarm.time = mCursor.getLong(3);
		alarm.status = mCursor.getInt(4);
		alarm.options = mCursor.getString(5);
		return alarm;
	}
}
