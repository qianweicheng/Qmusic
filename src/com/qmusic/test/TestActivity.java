package com.qmusic.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.qmusic.R;
import com.qmusic.activities.BaseServiceActivity;
import com.qmusic.common.IServiceCallback;
import com.qmusic.uitls.BAppHelper;
import com.qmusic.uitls.ShortCutHelper;

public class TestActivity extends BaseServiceActivity implements View.OnClickListener {
	EditText edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		findViewById(R.id.activity_test1_button1).setOnClickListener(this);
		// findViewById(R.id.activity_test1_button2).setOnClickListener(this);
		// findViewById(R.id.activity_test1_button3).setOnClickListener(this);
		// findViewById(R.id.activity_test1_button4).setOnClickListener(this);
		edit = (EditText) findViewById(R.id.activity_test1_input_edit);
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
		}
		// else if (viewId == R.id.activity_test1_button2) {
		// onBtn2(v);
		// } else if (viewId == R.id.activity_test1_button3) {
		// onBtn3(v);
		// } else if (viewId == R.id.activity_test1_button4) {
		// onBtn4(v);
		// }
	}

	@Override
	protected void onServicedBinded(IServiceCallback dataService) {
		edit.setText("Service binded");
	}

	public void onBtn1(final View view) {
		Intent intent = new Intent(this, Test2Activity.class);
		startActivity(intent);
	}

	public void onBtn2(final View view) {

	}

	public void onBtn3(final View view) {
		ShortCutHelper.listShortcut(this);
	}

	public void onBtn4(final View view) {

	}
}
