package com.qmusic.test;

import java.util.Stack;

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
import com.qmusic.controls.dialogs.BPopupDialog;
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

	Stack<BPopupDialog> stack;

	public void onBtn3(final View view) {
		if (stack == null) {
			stack = new Stack<BPopupDialog>();
		}
		BPopupDialog dialog = new BPopupDialog(this);
		// dialog.showAsDropDown(view);
		dialog.showAtLocation(view.getRootView(), Gravity.BOTTOM, 0, 0);
		stack.push(dialog);
	}

	public void onBtn4(final View view) {
		if (stack.size() > 0) {
			BPopupDialog dialog = stack.pop();
			dialog.dismiss();
		}

	}
}
