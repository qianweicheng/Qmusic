package com.qmusic.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.qmusic.R;

/**
 * 
 * @author weicheng
 * 
 */
public class BProgressBar extends ProgressBar {

	public BProgressBar(Context context) {
		super(context);
		init();
	}

	public BProgressBar(Context context, AttributeSet set) {
		super(context, set);
		init();
	}

	private void init() {
		// style="?android:attr/progressBarStyleLarge"
		this.setIndeterminateDrawable(this.getResources().getDrawable(R.drawable.b_loading));
		this.setIndeterminate(false);
		this.setBackgroundResource(R.drawable.sharp_round_black);
		int padding = (int) this.getResources().getDimension(R.dimen.margin);
		this.setPadding(padding, padding, padding, padding);
	}
}
