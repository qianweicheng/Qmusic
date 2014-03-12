package com.qmusic.test;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.qmusic.R;
import com.qmusic.activities.BaseActivity;
import com.qmusic.controls.dialogs.BToast;
import com.qmusic.controls.dialogs.TipsDialogFragment;
import com.qmusic.uitls.BAppHelper;

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
		findViewById(R.id.activity_test1_button5).setOnClickListener(this);
		findViewById(R.id.activity_test1_button6).setOnClickListener(this);
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
		} else if (viewId == R.id.activity_test1_button5) {
			onBtn5(v);
		} else if (viewId == R.id.activity_test1_button6) {
			onBtn6(v);
		}
	}

	BDrawable bd;

	public void onBtn1(final View view) {
		ImageView img = (ImageView) findViewById(R.id.activity_test1_image1);
		bd = new BDrawable(this);
		img.setImageDrawable(bd);
	}

	public void onBtn2(final View view) {
		BToast.toast(view, "cccc", RelativeLayout.ALIGN_TOP);
	}

	public void onBtn3(final View view) {
		BToast.toast(view, "cccc", RelativeLayout.ALIGN_BOTTOM);
	}

	public void onBtn4(final View view) {
		BToast.toast(view, "cccc", 0);
	}

	public void onBtn5(final View view) {

	}

	public void onBtn6(final View view) {

	}
}
