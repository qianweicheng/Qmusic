package com.qmusic.test;

import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.qmusic.R;
import com.qmusic.activities.BaseActivity;
import com.qmusic.controls.BDrawable;
import com.qmusic.controls.BClipDrawable;
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
		ImageView img = (ImageView) findViewById(R.id.activity_test1_image1);
		// bd = new BDrawable(this);
		bd = new BClipDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(),
				R.drawable.icon)), Gravity.CENTER, BClipDrawable.HORIZONTAL);
		img.setImageDrawable(bd);
		bd.setLevel(i);
	}

	// RotateDrawable d;
	BDrawable d;

	public void onBtn2(final View view) {
		// d = new RotateDrawable();
		// d.setDrawable(new BitmapDrawable(getResources(),
		// BitmapFactory.decodeResource(getResources(), R.drawable.tab1)));

		d = new BDrawable(this);
		ImageView img = (ImageView) findViewById(R.id.activity_test1_image1);
		img.setImageDrawable(d);
	}

	public void onBtn3(final View view) {
		d.start();
	}

	public void onBtn4(final View view) {
		BitmapDrawable d = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(),
				R.drawable.tab1));
		ImageView img = (ImageView) findViewById(R.id.activity_test1_image1);
		img.setImageDrawable(d);
	}
}
