package com.qmusic.test;

import java.io.File;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.qmusic.R;
import com.qmusic.activities.BaseActivity;
import com.qmusic.uitls.BAppHelper;
import com.qmusic.uitls.BLog;

public class TestActivity extends BaseActivity implements View.OnClickListener {
	EditText edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		findViewById(R.id.activity_test1_button1).setOnClickListener(this);
		findViewById(R.id.activity_test1_button2).setOnClickListener(this);
		findViewById(R.id.activity_test1_button3).setOnClickListener(this);
		findViewById(R.id.activity_test1_button4).setOnClickListener(this);
		// edit = (EditText) findViewById(R.id.activity_test1_input_edit);
	}

	@Override
	public void onBackPressed() {
		BAppHelper.exit(this, true);
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		if (viewId == R.id.activity_test1_button1) {
			onBtn1(v);
		} else if (viewId == R.id.activity_test1_button2) {
			onBtn2(v);
		} else if (viewId == R.id.activity_test1_button3) {
			onBtn3(v);
		} else if (viewId == R.id.activity_test1_button4) {
			onBtn4(v);
		}
	}

	public void onBtn1(final View view) {
		// LockScreenPlug.lockS();
		// goHome();
		// Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
		// String phoneNum = Uri.encode("10086 w 1");
		// phoneIntent.setData(Uri.parse("tel:10086 w 1"));
		// startActivity(phoneIntent);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		File file = new File("/sdcard/Download/12.pdf");
		file.setReadable(true, false);
		intent.setDataAndType(Uri.fromFile(file), "application/pdf");
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	public void onBtn2(final View view) {
		// goHome();
		// Uri.encode("10086,1");
		// Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
		// String phoneNum = Uri.encode("10086 ; 1");
		// phoneIntent.setData(Uri.parse("tel:10086 ; 1"));
		// startActivity(phoneIntent);

	}

	public void onBtn3(final View view) {
		// Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
		// String phoneNum = Uri.encode("10086 p 1");
		// phoneIntent.setData(Uri.parse("tel:10086 p 1"));
		// startActivity(phoneIntent);
		// "Fri, 18 Jul 2014 06:42:51 GMT"
		// Fri, 18 Jul 2014 11:03:55 GMT

	}

	public void onBtn4(final View view) {
		DownloadManager manger = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		Query query = new Query();
		// query.setFilterById(68);
		// query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
		Cursor cursor = manger.query(query);
		// int count = cursor.getColumnCount();
		final int index1 = cursor.getColumnIndex(DownloadManager.COLUMN_URI);
		final int index2 = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
		int STATUS = DownloadManager.STATUS_PENDING | DownloadManager.STATUS_RUNNING;
		BLog.e(TAG, "index1:" + index1);
		BLog.e(TAG, "index2:" + index2);
		while (cursor.moveToNext()) {
			// ContentValues cv = new ContentValues();
			// for (int i = 0; i < count; i++) {
			// cv.put(cursor.getColumnName(i), cursor.getString(i));
			// }
			// BLog.e(TAG, cv.toString());

			BLog.e(TAG, cursor.getString(index1));
			int s = cursor.getInt(index2);
			BLog.e(TAG, "Status:" + s + ";checked:" + (STATUS & s));
		}

	}

	private void goHome() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
		overridePendingTransition(0, 0);
	}
}
