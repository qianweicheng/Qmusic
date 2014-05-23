package com.qmusic.dal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.qmusic.entities.BCommonEntity;

public class CommonTable extends BaseTable {
	public static final String TABLE_NAME = "Common";
	public static final String FIELD_CATEGORY = "category";
	public static final String FIELD_NAME = "name";
	public static final String FIELD_OPTION = "option";
	public static final String FIELD_STATUS = "state";
	public static final String FIELD_TIME = "time";
	public static final Uri CONTENT_URL = Uri.parse("content://" + BContentProvider.AUTHORITY + "/" + TABLE_NAME);
	private static final String[] PROJECTION = new String[] { _ID, FIELD_CATEGORY, FIELD_NAME, FIELD_OPTION,
			FIELD_STATUS, FIELD_TIME };

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected void initField(HashMap<String, Class<?>> fields) {
		fields.put(FIELD_CATEGORY, String.class);
		fields.put(FIELD_NAME, String.class);
		fields.put(FIELD_OPTION, String.class);
		fields.put(FIELD_STATUS, Integer.class);
		fields.put(FIELD_TIME, Integer.class);
	}

	public int count(String category) {
		Cursor cursor = null;
		int result = 0;
		try {
			cursor = query(new String[] { _ID }, FIELD_CATEGORY + "=?", new String[] { category }, null, null, null,
					null);
			result = cursor.getCount();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return result;
	}

	public List<BCommonEntity> query(String category) {
		List<BCommonEntity> result = new ArrayList<BCommonEntity>();
		Cursor cursor = null;
		try {
			cursor = super.query(PROJECTION, FIELD_CATEGORY + "=?", new String[] { category }, null, null, null);
			while (cursor.moveToNext()) {
				BCommonEntity common = cursorToEntity(cursor);
				result.add(common);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return result;
	}

	public long insert(BCommonEntity common) {
		if (common.id > 0) {
			Cursor cursor = query(null, _ID + "=" + common.id, null, null, null, null);
			if (cursor.getCount() > 0) {
				return update(common);
			}
		}

		ContentValues cv = new ContentValues();
		cv.put(FIELD_CATEGORY, common.category);
		cv.put(FIELD_NAME, common.name);
		cv.put(FIELD_OPTION, common.option);
		cv.put(FIELD_STATUS, common.state);
		cv.put(FIELD_TIME, System.currentTimeMillis());
		return super.insert(cv);
	}

	@SuppressWarnings("rawtypes")
	public void insert(String category, Collection list) {
		try {
			SQLiteDatabase sqliteDatabase = BDatabaseHelper.getDatabase();
			sqliteDatabase.beginTransaction();
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				Object item = iterator.next();
				ContentValues cv = new ContentValues();
				cv.put(CommonTable.FIELD_CATEGORY, category);
				cv.put(CommonTable.FIELD_TIME, System.currentTimeMillis());
				cv.put(CommonTable.FIELD_STATUS, 0);
				cv.put(CommonTable.FIELD_OPTION, "" + item);
				sqliteDatabase.insert(TABLE_NAME, null, cv);
			}

			sqliteDatabase.setTransactionSuccessful();
			sqliteDatabase.endTransaction();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public int update(BCommonEntity common) {
		if (common.id > 0) {
			ContentValues cv = new ContentValues();
			cv.put(FIELD_CATEGORY, common.category);
			cv.put(FIELD_NAME, common.name);
			cv.put(FIELD_OPTION, common.option);
			cv.put(FIELD_STATUS, common.state);
			cv.put(FIELD_TIME, System.currentTimeMillis());
			return super.update(cv, _ID + "=?", new String[] { String.valueOf(common.id) });
		} else {
			return (int) insert(common);
		}
	}

	public List<BCommonEntity> query(Map<String, Object> wheres) {
		List<BCommonEntity> result = new ArrayList<BCommonEntity>();
		Cursor cursor = null;
		try {
			StringBuilder sb = null;
			String[] whereArgs = null;
			if (wheres != null && wheres.size() > 0) {
				sb = new StringBuilder();
				whereArgs = new String[wheres.size()];
				Set<String> keys = wheres.keySet();
				boolean isFirst = true;
				int i = 0;
				for (String key : keys) {
					if (isFirst) {
						isFirst = false;
					} else {
						sb.append(" and ");
					}
					sb.append("[").append(key).append("]=?");
					whereArgs[i] = wheres.get(key).toString();
					i++;
				}
			}
			cursor = super.query(PROJECTION, sb == null ? null : sb.toString(), whereArgs, null, null, "[time] desc");
			while (cursor.moveToNext()) {
				BCommonEntity cv = cursorToEntity(cursor);
				result.add(cv);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return result;
	}

	private BCommonEntity cursorToEntity(Cursor cursor) {
		BCommonEntity common = new BCommonEntity();
		common.id = cursor.getLong(0);
		common.category = cursor.getString(1);
		common.name = cursor.getString(2);
		common.option = cursor.getString(3);
		common.state = cursor.getInt(4);
		common.time = new Date(cursor.getLong(5));
		return common;
	}
}
