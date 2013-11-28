package com.qmusic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.qmusic.R;
import com.qmusic.common.BConstants;
import com.qmusic.uitls.BUtilities;
import com.viewpagerindicator.CirclePageIndicator;

public class GuideActivity extends BaseActivity {
	UserGuideAdapter mAdapter;
	CirclePageIndicator mIndicator;
	ViewPager mPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	void init() {
		setContentView(R.layout.activity_user_guide);
		mAdapter = new UserGuideAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.activity_user_guide_pager);
		mIndicator = (CirclePageIndicator) findViewById(R.id.activity_user_guide_indicator);
		mPager.setAdapter(mAdapter);
		mIndicator.setViewPager(mPager);
		BUtilities.setPref(BConstants.PRE_KEY_SHOW_TUTORIAL, "false");
	}

	public void onBackPressed() {
		Intent intent = new Intent(this, SplashActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(SplashActivity.RE_LOGIN, true);
		startActivity(intent);
	}

	public class UserGuideAdapter extends FragmentStatePagerAdapter {

		public UserGuideAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return FragmentUserGuide.newInstance(position);
		}

		@Override
		public int getCount() {
			return FragmentUserGuide.tutorials.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "Title" + position;
		}
	}
}
