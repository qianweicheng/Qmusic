package com.qmusic.test;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.qmusic.R;
import com.qmusic.activities.BCommonWebActivity;
import com.qmusic.activities.BWebActivity;
import com.qmusic.activities.BaseActivity;
import com.qmusic.common.BAppHelper;
import com.qmusic.uitls.BLog;
import com.qmusic.volley.QMusicFileRequest;
import com.qmusic.volley.QMusicRequestManager;

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
		RequestQueue queue = QMusicRequestManager.getInstance().getRequestQueue();
		String url = "https://example.org/";
		// String url = "https://www.google.com";
		StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				BLog.d(TAG, response);

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (error != null) {
					BLog.e(TAG, "Error:" + error.getMessage());
				} else {
					BLog.e(TAG, "Error Happens");
				}
			}
		});

		// Add the request to the RequestQueue.
		queue.add(stringRequest);
	}

	public void onBtn2(final View view) {
		QMusicRequestManager.getInstance().enableLargeFileDownload();
		RequestQueue queue = QMusicRequestManager.getInstance().getRequestQueue2();
		// String url = "https://example.org/";
		String url = "https://www.google.com";
		File file = new File(getExternalCacheDir(), "google.txt");
		QMusicFileRequest fileRequest = new QMusicFileRequest(Request.Method.GET, url, file, new Response.Listener<Pair<Integer, Integer>>() {

			@Override
			public void onResponse(Pair<Integer, Integer> response) {
				BLog.d(TAG, "" + response.first + "/" + response.second);

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (error != null) {
					BLog.e(TAG, "Error:" + error.getMessage());
				} else {
					BLog.e(TAG, "Error Happens");
				}
			}
		});

		// Add the request to the RequestQueue.
		queue.add(fileRequest);
	}

	public void onBtn3(final View view) {
		Intent intent = new Intent(this, BCommonWebActivity.class);
		intent.putExtra(BWebActivity.SHOW_PROGRESS_BAR, true);
		intent.putExtra(BWebActivity.TITLE, "Index");
		startActivity(intent);
	}

	public void onBtn4(final View view) {
		Intent intent = new Intent(this, BCommonWebActivity.class);
		intent.putExtra(BWebActivity.SHOW_PROGRESS_BAR, true);
		intent.putExtra(BWebActivity.TITLE, "Index2");
		intent.putExtra("mode", 2);
		startActivity(intent);
	}

}
