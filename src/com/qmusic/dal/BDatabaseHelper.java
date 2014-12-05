package com.qmusic.dal;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.qmusic.MyApplication;
import com.qmusic.common.BConstants;
import com.qmusic.uitls.BLog;

public class BDatabaseHelper extends SQLiteOpenHelper {
	static final String TAG = BDatabaseHelper.class.getSimpleName();
	public static final Object write_lock = new Object();
	// db name
	private final static String DATABASE_NAME = BConstants.APP_NAME + ".db";
	// db version
	private static final int DATABASE_VERSION = 1;
	static BDatabaseHelper instance;

	private BDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static final BDatabaseHelper createInstance() {
		return new BDatabaseHelper(MyApplication.getContext());
	}

	/**
	 * Please do NOT close the database after you have used it.
	 * 
	 * @return
	 */
	public synchronized static final SQLiteDatabase getDatabase() {
		if (instance == null) {
			instance = new BDatabaseHelper(MyApplication.getContext());
		}
		return instance.getWritableDatabase();
	}

	@Override
	public synchronized void close() {
		super.close();
		BLog.w(TAG, "do you really need to close the db? you are supporsed to close the db when you exit the app.");
	}

	public synchronized static final void closeDB() {
		if (instance != null) {
			try {
				instance.close();
				instance = null;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		initDb(db, getTables());
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		List<BaseTable> tables = getTables();
		for (BaseTable table : tables) {
			String tableName = table.getTableName();
			String DROP_TABLE = "DROP TABLE IF EXISTS " + tableName;
			db.execSQL(DROP_TABLE);
		}
		initDb(db, tables);
	}

	private void initDb(SQLiteDatabase db, List<BaseTable> tables) {
		for (BaseTable table : tables) {
			table.createTable(db);
		}
	}

	/**
	 * Init table here
	 * 
	 * @param ctx
	 * @return
	 */
	private static List<BaseTable> getTables() {
		ArrayList<BaseTable> tables = new ArrayList<BaseTable>();
		tables.add(new AlarmTable());
		tables.add(new TestTable());
		tables.add(new CommonTable());
		return tables;
	}

	public static final void clearDB() {
		List<BaseTable> tables = getTables();
		for (BaseTable table : tables) {
			table.clear();
		}
	}
}
