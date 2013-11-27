package com.qmusic.test;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.CaptureActivity;
import com.qmusic.R;
import com.qmusic.activities.BaseActivity;
import com.qmusic.activities.SplashActivity;
import com.qmusic.dal.TestTable;

public class TestActivity extends BaseActivity implements View.OnClickListener {
	public static final String TAG = TestActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.button2).setOnClickListener(this);
		findViewById(R.id.button3).setOnClickListener(this);
		findViewById(R.id.button4).setOnClickListener(this);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, SplashActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(SplashActivity.SHUTDOWN, true);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		if (viewId == R.id.button1) {
			onBtn1(v);
		} else if (viewId == R.id.button2) {
			onBtn2(v);
		} else if (viewId == R.id.button3) {
			onBtn3(v);
		} else if (viewId == R.id.button4) {
			onBtn4(v);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 当前只有一个返回值
		if (resultCode == RESULT_OK && requestCode == 101) {
			String result = data.getExtras().getString("result");
			Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
		}
	}

	public void onBtn1(final View view) {
		startActivityForResult(new Intent(this, CaptureActivity.class), 101);
		// for (int i = 0; i < 10; i++) {
		// Thread thread = new Thread(runnable);
		// thread.setName("T" + i);
		// thread.start();
		// }
	}

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			Log.i(TAG, "Start:" + Thread.currentThread().getName());
			TestTable table = new TestTable();
			for (int i = 0; i < 1; i++) {
				ContentValues cv = new ContentValues();
				cv.put(TestTable.FIELD_NAME, Thread.currentThread().getName() + "_" + i);
				table.insert(cv);
			}
			Log.i(TAG, "End:" + Thread.currentThread().getName());
		}
	};

	public void onBtn2(final View view) {
		TestTable table = new TestTable();
		Cursor cursor = table.query(null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			int count = cursor.getColumnCount();
			ContentValues cv = new ContentValues();
			for (int i = 0; i < count; i++) {
				cv.put(cursor.getColumnName(i), cursor.getString(i));
			}
			Log.i(TAG, cv.toString());
		}
	}

	public void onBtn3(final View view) {
		TestTable table = new TestTable();
		table.clear();
	}

	public void onBtn4(final View view) {
		Cursor cursor = getContentResolver().query(TestTable.CONTENT_URL, null, null, null, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				int count = cursor.getColumnCount();
				ContentValues cv = new ContentValues();
				for (int i = 0; i < count; i++) {
					cv.put(cursor.getColumnName(i), cursor.getString(i));
				}
				Log.i(TAG, cv.toString());
			}
		} else {
			Log.e(TAG, "null");
		}
	}
}
