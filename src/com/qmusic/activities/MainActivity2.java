package com.qmusic.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.qmusic.R;
import com.qmusic.activities.fragments.Fragment1;
import com.qmusic.activities.fragments.FragmentLeftMenu;
import com.qmusic.activities.fragments.FragmentRightMenu;
import com.qmusic.common.BAppHelper;
import com.special.ResideMenu.ResideMenu;

public class MainActivity2 extends BaseActivity implements View.OnClickListener, ResideMenu.OnMenuListener {
	private ResideMenu resideMenu;
	private Fragment leftFragment;
	private Fragment rightFragment;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		setUpMenu();
		changeFragment(new Fragment1());
	}

	private void setUpMenu() {
		// attach to current activity;
		resideMenu = new ResideMenu(this);
		resideMenu.setBackground(R.drawable.menu_background);
		resideMenu.attachToActivity(this);
		resideMenu.setMenuListener(this);
		// valid scale factor is between 0.0f and 1.0f. leftmenu'width is
		// 150dip.
		resideMenu.setScaleValue(0.5f);
		// create menu items;
		leftFragment = new FragmentLeftMenu();
		rightFragment = new FragmentRightMenu();
		resideMenu.addMenuItem(leftFragment, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(rightFragment, ResideMenu.DIRECTION_RIGHT);
		// You can disable a direction by setting ->
		// resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
		findViewById(R.id.activity_main_title_left_menu).setOnClickListener(this);
		findViewById(R.id.activity_main_title_right_menu).setOnClickListener(this);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return resideMenu.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			BAppHelper.exit(this, false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View view) {
		int viewId = view.getId();
		if (viewId == R.id.activity_main_title_left_menu) {
			resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
		} else if (viewId == R.id.activity_main_title_right_menu) {
			resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
		}
	}

	@Override
	public void openMenu() {

	}

	@Override
	public void closeMenu() {

	}

	private void changeFragment(Fragment targetFragment) {
		resideMenu.clearIgnoredViewList();
		getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_fragment, targetFragment, "fragment")
		        .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
	}

}
