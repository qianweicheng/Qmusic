package com.qmusic.test;

import java.util.Stack;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.qmusic.R;
import com.qmusic.activities.BaseActivity;
import com.qmusic.controls.BClipDrawable;
import com.qmusic.controls.dialogs.BPopupDialog;
import com.qmusic.uitls.BAppHelper;
import com.qmusic.uitls.ShortCutHelper;

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
		} else if (viewId == R.id.activity_test1_button3) {
			onBtn3(v);
		} else if (viewId == R.id.activity_test1_button4) {
			onBtn4(v);
		}
	}

	BClipDrawable bd;
	int i = 200;

	public void onBtn1(final View view) {
		String text = "钱" + (i++);
		ShortCutHelper.create(this, text);
	}

	public void onBtn2(final View view) {

	}

	Stack<BPopupDialog> stack;

	public void onBtn3(final View view) {
		ShortCutHelper.listShortcut(this);
	}

	public void onBtn4(final View view) {
		if (stack.size() > 0) {
			BPopupDialog dialog = stack.pop();
			dialog.dismiss();
		}

	}
}
