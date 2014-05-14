package com.qmusic.test;

import android.os.Bundle;

import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.qmusic.R;
import com.qmusic.activities.BaseActivity;
import com.qmusic.uitls.BAppHelper;

public class Test2Activity extends BaseActivity {
	PullToRefreshScrollView scrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test2);
		scrollView = (PullToRefreshScrollView) findViewById(R.id.activity_test2_list);
	}

	@Override
	public void onBackPressed() {
		BAppHelper.exit(this, true);
	}

}
