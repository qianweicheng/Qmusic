package com.qmusic.test;

import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.qmusic.R;
import com.qmusic.activities.BCommonWebActivity;
import com.qmusic.activities.BWebActivity;
import com.qmusic.activities.BaseActivity;
import com.qmusic.common.BAppHelper;

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
		// Intent intent = new Intent(this, BWebActivity.class);
		// intent.putExtra(BWebActivity.SHOW_PROGRESS_BAR, true);
		// intent.putExtra("url",
		// "file:///android_asset/www/html/index_spa.html");
		// startActivity(intent);\
		RequestQueue queue = Volley.newRequestQueue(this);
		String url = "http://www.google.com";
		StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				return super.getHeaders();
			}
		};

		// Add the request to the RequestQueue.
		queue.add(stringRequest);
	}

	public void onBtn2(final View view) {
		Intent intent = new Intent(this, BWebActivity.class);
		intent.putExtra(BWebActivity.SHOW_PROGRESS_BAR, true);
		intent.putExtra("url", "file:///android_asset/www/html/index.html");
		startActivity(intent);
	}

	public void onBtn3(final View view) {
		Intent intent = new Intent(this, BCommonWebActivity.class);
		intent.putExtra(BWebActivity.SHOW_PROGRESS_BAR, true);
		intent.putExtra(BCommonWebActivity.TITLE, "Index");
		startActivity(intent);
	}

	public void onBtn4(final View view) {
		Intent intent = new Intent(this, BCommonWebActivity.class);
		intent.putExtra(BWebActivity.SHOW_PROGRESS_BAR, true);
		intent.putExtra(BCommonWebActivity.TITLE, "Index2");
		intent.putExtra("mode", 2);
		startActivity(intent);
	}

}
