package com.qmusic.activities;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import cn.jpush.android.api.JPushInterface;

import com.qmusic.R;
import com.qmusic.activities.fragments.Fragment1;
import com.qmusic.activities.fragments.Fragment2;
import com.qmusic.activities.fragments.Fragment3;
import com.qmusic.common.BAppHelper;
import com.qmusic.controls.BTabFragment;
import com.qmusic.uitls.BLog;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends BaseActivity implements OnClickListener {
	static final String TAG = MainActivity.class.getSimpleName();
	public static final int MSG_SWITCH_TAB = 100;
	public static final int MSG_LOGOUT = 101;
	private View tab1, tab2, tab3;
	private Handler handler;
	int currentTab = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState != null) {
			currentTab = savedInstanceState.getInt("currentTab");
		}
		init();
	}

	void init() {
		handler = new MainHandler(this);
		tab1 = findViewById(R.id.activity_main_tab1);
		tab2 = findViewById(R.id.activity_main_tab2);
		tab3 = findViewById(R.id.activity_main_tab3);
		tab1.setOnClickListener(this);
		tab2.setOnClickListener(this);
		tab3.setOnClickListener(this);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			try {
				String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
				JSONObject extraJSON = new JSONObject(extra);
				currentTab = extraJSON.optInt("tab", 1);
				currentTab = Math.max(currentTab, 1);
				currentTab = Math.min(currentTab, 3);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		changeTab(currentTab);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("currentTab", currentTab);
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
	public void onClick(View v) {
		int newTab = 1;
		int id = v.getId();
		if (id == R.id.activity_main_tab1) {
			newTab = 1;
		} else if (id == R.id.activity_main_tab2) {
			newTab = 2;
		} else if (id == R.id.activity_main_tab3) {
			newTab = 3;
		}
		if (currentTab != newTab) {
			changeTab(newTab);
		}
	}

	private void changeTab(int newTab) {
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		BTabFragment fragment1 = (Fragment1) manager.findFragmentByTag("fragment1");
		BTabFragment fragment2 = (Fragment2) manager.findFragmentByTag("fragment2");
		BTabFragment fragment3 = (Fragment3) manager.findFragmentByTag("fragment3");
		if (newTab == 1) {
			if (fragment1 == null) {
				fragment1 = new Fragment1();
				transaction.add(R.id.activity_main_content, fragment1, "fragment1");
			} else {
				transaction.show(fragment1);
			}
			fragment1.onSelected();
			tab1.setSelected(true);
			tab2.setSelected(false);
			tab3.setSelected(false);

			if (fragment2 != null && !fragment2.isHidden()) {
				transaction.hide(fragment2);
			}
			if (fragment3 != null && !fragment3.isHidden()) {
				transaction.hide(fragment3);
			}
			MobclickAgent.onEvent(this, "fragment1");
		} else if (newTab == 2) {
			// if (!BUser.isLogined()) {
			// Intent intent = new Intent(this, LoginActivity.class);
			// startActivity(intent);
			// return;
			// }
			if (fragment2 == null) {
				fragment2 = new Fragment2();
				transaction.add(R.id.activity_main_content, fragment2, "fragment2");
			} else {
				transaction.show(fragment2);
			}
			fragment2.onSelected();
			tab2.setSelected(true);
			tab1.setSelected(false);
			tab3.setSelected(false);
			if (fragment1 != null && !fragment1.isHidden()) {
				transaction.hide(fragment1);
			}
			if (fragment3 != null && !fragment3.isHidden()) {
				transaction.hide(fragment3);
			}
			MobclickAgent.onEvent(this, "fragment2");
		} else if (newTab == 3) {
			// if (!BUser.isLogined()) {
			// Intent intent = new Intent(this, LoginActivity.class);
			// startActivity(intent);
			// return;
			// }
			if (fragment3 == null) {
				fragment3 = new Fragment3();
				transaction.add(R.id.activity_main_content, fragment3, "fragment3");
			} else {
				transaction.show(fragment3);
			}
			fragment3.onSelected();
			tab3.setSelected(true);
			tab1.setSelected(false);
			tab2.setSelected(false);
			if (fragment1 != null && !fragment1.isHidden()) {
				transaction.hide(fragment1);
			}
			if (fragment2 != null && !fragment2.isHidden()) {
				transaction.hide(fragment2);
			}
			MobclickAgent.onEvent(this, "fragment3");
		} else {
			BLog.e(TAG, "error newTabIndex");
			return;
		}
		transaction.commit();
		currentTab = newTab;
	}

	public Handler getHander() {
		return handler;
	}

	static final class MainHandler extends Handler {
		MainActivity activity;

		public MainHandler(MainActivity activity) {
			this.activity = activity;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MainActivity.MSG_SWITCH_TAB: {
				switch (msg.arg1) {
				case R.id.activity_main_tab1:
					activity.tab1.performClick();
					break;
				case R.id.activity_main_tab2:
					activity.tab2.performClick();
					break;
				case R.id.activity_main_tab3:
					activity.tab3.performClick();
					break;
				}
				break;
			}
			case MainActivity.MSG_LOGOUT: {
				activity.changeTab(1);
				break;
			}
			default:
				break;
			}
		}
	}
}
