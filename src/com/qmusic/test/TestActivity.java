package com.qmusic.test;

import java.util.Stack;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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
		String text = "伟" + (i++);
		Bitmap bitmapEasilydo = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
		bitmapEasilydo = bitmapEasilydo.copy(Config.ARGB_8888, true);
		int iconWidth = bitmapEasilydo.getWidth();
		int iconHeight = bitmapEasilydo.getHeight();
		// if (bitmapEasilydo.getWidth() != 128 || bitmapEasilydo.getHeight() !=
		// 128) {
		// Bitmap newicon = Bitmap.createScaledBitmap(bitmapEasilydo, 128, 128,
		// true);
		// bitmapEasilydo.recycle();
		// bitmapEasilydo = newicon;
		// }
		Paint paint = new Paint();
		paint.setTextSize(14);
		paint.setColor(0xffff0000);
		float txtWidth = paint.measureText(text);
		Canvas canvas = new Canvas(bitmapEasilydo);
		canvas.drawText(text, iconWidth - txtWidth, iconHeight, paint);
		ImageView img1 = (ImageView) findViewById(R.id.activity_test1_image1);
		img1.setImageBitmap(bitmapEasilydo);
		ShortCutHelper.create(this, text, bitmapEasilydo);
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
