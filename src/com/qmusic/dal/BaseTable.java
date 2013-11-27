package com.qmusic.dal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public abstract class BaseTable implements BaseColumns {
	static final String TAG = BaseTable.class.getSimpleName();
	private HashMap<String, Class<?>> fields;
	SQLiteDatabase db;

	public BaseTable() {
		fields = new HashMap<String, Class<?>>();
		fields.put(_ID, Integer.class);
		initField(fields);
	}

	public Collection<String> getKeys() {
		return fields.keySet();
	}

	public HashMap<String, Class<?>> getFields() {
		return fields;
	}

	protected abstract String getTableName();

	protected abstract void initField(HashMap<String, Class<?>> fields);

	protected void createTable(SQLiteDatabase db) {
		String tableName = getTableName();
		HashMap<String, Class<?>> fields = getFields();
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ").append(tableName).append(" (");
		Set<String> keys = fields.keySet();
		boolean isFirst = true;
		for (String key : keys) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(",");
			}
			sb.append(" [").append(key).append("] ");
			Class<?> type = fields.get(key);
			if (Integer.class.equals(type) || int.class.equals(type) || Long.class.equals(type)
					|| long.class.equals(type)) {
				sb.append(" INTEGER ");
				if (key.equals(_ID)) {
					sb.append(" PRIMARY KEY AUTOINCREMENT ");
				}
			} else if (String.class.equals(type)) {
				sb.append(" TEXT ");
			} else if (Float.class.equals(type) || float.class.equals(type) || Double.class.equals(type)
					|| double.class.equals(type)) {
				sb.append(" REAL ");
			} else if (Boolean.class.equals(type) || boolean.class.equals(type)) {
				sb.append(" NUMERIC ");
			} else {// BLOB
				sb.append(" BLOB ");
			}
		}
		sb.append(") ;");
		db.execSQL(sb.toString());
	}

	protected void createIndex(SQLiteDatabase db, String[] cloumns) {
		if (cloumns != null && cloumns.length > 0) {
			StringBuilder sb = new StringBuilder();
			final String FORMAT = " CREATE INDEX INDEX%d on %s ([%s]); ";
			for (int i = 0; i < cloumns.length; i++) {
				sb.append(String.format(FORMAT, i, getTableName(), cloumns[i]));
			}
			db.execSQL(sb.toString());
		}
	}

	public Cursor query(String[] columns, String selection, String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		Cursor cursor = null;
		try {
			if (db == null) {
				db = BDatabaseHelper.getDatabase();
			}
			cursor = db.query(getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return cursor;
	}

	public Cursor query(String[] columns, String selection, String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {
		Cursor cursor = null;
		try {
			if (db == null) {
				db = BDatabaseHelper.getDatabase();
			}
			cursor = db.query(getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return cursor;
	}

	public long insert(ContentValues values) {
		long result = 0;
		try {
			if (db == null) {
				db = BDatabaseHelper.getDatabase();
			}
			result = db.insert(getTableName(), null, values);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public int update(ContentValues values, String whereClause, String[] whereArgs) {
		int result = 0;
		try {
			if (db == null) {
				db = BDatabaseHelper.getDatabase();
			}
			result = db.update(getTableName(), values, whereClause, whereArgs);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public int delete(String whereClause, String[] whereArgs) {
		int result = 0;
		try {
			if (db == null) {
				db = BDatabaseHelper.getDatabase();
			}
			result = db.delete(getTableName(), whereClause, whereArgs);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public void clear() {
		try {
			if (db == null) {
				db = BDatabaseHelper.getDatabase();
			}
			db.delete(getTableName(), "1=1", null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
