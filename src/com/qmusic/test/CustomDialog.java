package com.qmusic.test;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

import com.qmusic.R;

public class CustomDialog extends Dialog {

	public CustomDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.activity_user_guide, null);
		setContentView(view);
		// Display display = getWindow().getWindowManager().getDefaultDisplay();
		Window window = getWindow();
		window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		window.setDimAmount(1);
		// WindowManager.LayoutParams layoutParams = window.getAttributes();
		// layoutParams.horizontalMargin = 0;
		// layoutParams.verticalMargin = 0;
		// layoutParams.horizontalWeight = 21;
		// layoutParams.verticalWeight = 12;
		// layoutParams.dimAmount = 1;
		window.setBackgroundDrawableResource(android.R.color.transparent);
		// layoutParams.type =
		// android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		// layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
		// | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
		// WindowManager.LayoutParams.FLAG_DIM_BEHIND
		// | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
	}
}
