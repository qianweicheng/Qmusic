package com.qmusic.dal;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BDatabaseHelper extends SQLiteOpenHelper {
	static final String TAG = BDatabaseHelper.class.getSimpleName();
	// db name
	private final static String DATABASE_NAME = "bohan.db";
	// db version
	private static final int DATABASE_VERSION = 3;
	static BDatabaseHelper instance;
	static Context context;

	private BDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static final void init(Context ctx) {
		context = ctx;
	}

	public synchronized static final SQLiteDatabase getDatabase() {
		if (instance == null) {
			if (context == null) {
				throw new NullPointerException("Context is null");
			}
			instance = new BDatabaseHelper(context);
		}
		return instance.getWritableDatabase();
	}

	public synchronized static final void closeDB() {
		if (instance != null) {
			try {
				instance.close();
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
		tables.add(new CommonTable());
		tables.add(new TestTable());
		return tables;
	}

	public static final void clearDB() {
		List<BaseTable> tables = getTables();
		for (BaseTable table : tables) {
			table.clear();
		}
	}
}
