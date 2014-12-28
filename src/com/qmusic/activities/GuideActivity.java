package com.qmusic.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.qmusic.R;
import com.qmusic.activities.fragments.UserGuideFragment;
import com.qmusic.common.BConstants;
import com.qmusic.uitls.BLog;
import com.qmusic.uitls.BUtilities;
import com.viewpagerindicator.CirclePageIndicator;

public class GuideActivity extends BaseActivity {
	static final String TAG = GuideActivity.class.getSimpleName();
	UserGuideAdapter mAdapter;
	CirclePageIndicator mIndicator;
	ViewPager mPager;
	List<Fragment> fragments;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	void init() {
		setContentView(R.layout.activity_user_guide);
		fragments = new ArrayList<Fragment>();
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

	public void registerPage(UserGuideFragment fragment) {
		fragments.add(fragment);
		BLog.i(TAG, "add fragment:" + fragment.getId());
	}

	public void unRegisterPage(UserGuideFragment fragment) {
		fragments.remove(fragment);
		BLog.i(TAG, "remove fragment:" + fragment.getId());
	}

	public class UserGuideAdapter extends FragmentStatePagerAdapter {

		public UserGuideAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return UserGuideFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			return UserGuideFragment.tutorials.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "Title" + position;
		}
	}
}
