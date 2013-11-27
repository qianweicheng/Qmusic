package com.qmusic.dal;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.qmusic.uitls.BLog;

public class BContentProvider extends ContentProvider {
	static final String TAG = "BContentProvider";
	public static final String AUTHORITY = "com.qmusic.provider";
	private UriMatcher sMatcher;
	public static final int SCHEME_ALARM = 1;
	public static final int SCHEME_TEST = 2;

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public boolean onCreate() {
		sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sMatcher.addURI(AUTHORITY, AlarmTable.TABLE_NAME, SCHEME_ALARM);
		sMatcher.addURI(AUTHORITY, TestTable.TABLE_NAME, SCHEME_TEST);
		return true;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = BDatabaseHelper.getDatabase();
		long result = 0;
		switch (sMatcher.match(uri)) {
		case SCHEME_ALARM: {
			result = db.insert(AlarmTable.TABLE_NAME, null, values);
			break;
		}
		case SCHEME_TEST: {
			result = db.insert(TestTable.TABLE_NAME, null, values);
			break;
		}
		default:
			throw new IllegalArgumentException("Unknown URI" + uri);
		}
		if (result > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		Uri newUri = uri.buildUpon().appendPath(String.valueOf(result)).build();
		BLog.i(TAG, "insert:" + newUri.toString());
		return newUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = BDatabaseHelper.getDatabase();
		int result = 0;
		switch (sMatcher.match(uri)) {
		case SCHEME_ALARM:
			result = db.delete(AlarmTable.TABLE_NAME, selection, selectionArgs);
			break;
		case SCHEME_TEST:
			result = db.delete(TestTable.TABLE_NAME, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI" + uri);
		}
		if (result > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		BLog.i(TAG, "delete:" + uri.toString() + ";result:" + result);
		return result;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = BDatabaseHelper.getDatabase();
		Cursor cursor = null;
		switch (sMatcher.match(uri)) {
		case SCHEME_ALARM: {
			cursor = db.query(AlarmTable.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		}
		case SCHEME_TEST: {
			cursor = db.query(TestTable.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		}
		default:
			throw new IllegalArgumentException("Unknown URI" + uri);
		}
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		BLog.i(TAG, "query:" + uri.toString());
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = BDatabaseHelper.getDatabase();
		int result = 0;
		switch (sMatcher.match(uri)) {
		case SCHEME_ALARM: {
			result = db.update(AlarmTable.TABLE_NAME, values, selection, selectionArgs);
			break;
		}
		case SCHEME_TEST: {
			result = db.update(TestTable.TABLE_NAME, values, selection, selectionArgs);
			break;
		}
		default:
			throw new IllegalArgumentException("Unknown URI" + uri);
		}
		if (result > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		BLog.i(TAG, "update:" + uri.toString());
		return result;
	}
}
