package com.qmusic.dal;

import java.util.HashMap;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class TestTable extends BaseTable {
	public static final String TABLE_NAME = "Test";
	public static final String FIELD_NAME = "name";
	public static final Uri CONTENT_URL = Uri.parse("content://" + BContentProvider.AUTHORITY + "/" + TABLE_NAME);

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected void initField(HashMap<String, Class<?>> fields) {
		fields.put(FIELD_NAME, String.class);
	}

	@Override
	protected void createTable(SQLiteDatabase db) {
		super.createTable(db);
		createIndex(db, new String[] { FIELD_NAME });
	}

}
