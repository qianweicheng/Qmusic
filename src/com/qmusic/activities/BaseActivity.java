package com.qmusic.activities;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.qmusic.MyApplication;

public abstract class BaseActivity extends SherlockFragmentActivity {

	@Override
	protected void onStart() {
		MyApplication.foreground(this.getClass().getSimpleName());
		super.onStart();
	}

	@Override
	protected void onStop() {
		MyApplication.background(this.getClass().getSimpleName());
		super.onStop();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
