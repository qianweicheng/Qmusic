package com.qmusic.test;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.qmusic.R;
import com.qmusic.activities.BaseActivity;
import com.qmusic.activities.SplashActivity;
import com.qmusic.controls.BImageView;
import com.qmusic.dal.TestTable;
import com.qmusic.uitls.BLog;

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

	public void onBtn1(final View view) {
		// startActivityForResult(new Intent(this, CaptureActivity.class), 101);
		// for (int i = 0; i < 10; i++) {
		// Thread thread = new Thread(runnable);
		// thread.setName("T" + i);
		// thread.start();
		// }
		BImageView img = (BImageView) findViewById(R.id.img);
		img.setBlackWhite(!img.getBlackWhite());
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
		// TestTable table = new TestTable();
		// Cursor cursor = table.query(null, null, null, null, null, null);
		// while (cursor.moveToNext()) {
		// int count = cursor.getColumnCount();
		// ContentValues cv = new ContentValues();
		// for (int i = 0; i < count; i++) {
		// cv.put(cursor.getColumnName(i), cursor.getString(i));
		// }
		// Log.i(TAG, cv.toString());
		// }
		BImageView img = (BImageView) findViewById(R.id.img);
		img.setRadius(img.getRadius() + 3);
	}

	public void onBtn3(final View view) {
		BImageView img = (BImageView) findViewById(R.id.img);
		img.setRadius(img.getRadius() - 3);
	}

	public void onBtn4(final View view) {
		try {
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "开启语音");

			startActivityForResult(intent, 1001);
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (1001 == requestCode && resultCode == RESULT_OK) {
			Toast.makeText(this, "返回结果正常", Toast.LENGTH_LONG).show();
			ArrayList<String> result = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS); // 获取语言的字符
			for (int i = 0; i < result.size(); i++) {
				BLog.i(TAG, result.get(i));
			}
		}
	}
}
