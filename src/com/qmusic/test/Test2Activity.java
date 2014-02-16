package com.qmusic.test;

import android.os.Bundle;

import com.qmusic.R;
import com.qmusic.activities.BaseActivity;
import com.qmusic.uitls.BAppHelper;

public class Test2Activity extends BaseActivity {
	public static final String TAG = Test2Activity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test2);
	}

	@Override
	public void onBackPressed() {
		BAppHelper.exit(this, true);
	}

}
