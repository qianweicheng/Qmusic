package com.qmusic.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.qmusic.R;
import com.qmusic.activities.BWebActivity;
import com.qmusic.activities.BaseServiceActivity;
import com.qmusic.common.IServiceCallback;
import com.qmusic.localplugin.LockScreenPlug;
import com.qmusic.uitls.BAppHelper;
import com.qmusic.uitls.ShortCutHelper;

public class TestActivity extends BaseServiceActivity implements View.OnClickListener {
	EditText edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		findViewById(R.id.activity_test1_button1).setOnClickListener(this);
		findViewById(R.id.activity_test1_button2).setOnClickListener(this);
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
		} else if (viewId == R.id.activity_test1_button2) {
			onBtn2(v);
		}
		// else if (viewId == R.id.activity_test1_button3) {
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
		LockScreenPlug.lockS();
	}

	public void onBtn2(final View view) {
		// Intent intent = new Intent(Intent.ACTION_MAIN);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
		// Intent.FLAG_ACTIVITY_CLEAR_TOP
		// | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		// intent.addCategory(Intent.CATEGORY_HOME);
		// startActivity(intent);
		// finish();
		// overridePendingTransition(0, 0);
		Intent intent = new Intent(this, BWebActivity.class);
		intent.putExtra(BWebActivity.URL, "http://192.168.1.108/~weicheng/test.html");
		startActivity(intent);
	}

	public void onBtn3(final View view) {
		ShortCutHelper.listShortcut(this);
	}

	public void onBtn4(final View view) {

	}
}
