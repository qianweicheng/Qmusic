package com.qmusic.activities;

import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.view.MenuItem;
import com.qmusic.R;
import com.qmusic.uitls.BAppHelper;

public class LoginActivity extends BaseActivity {
	public static final String EXIT_WHEN_BACK = "exit_when_back";
	boolean exitWhenBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BAppHelper.setTitle(this, R.layout.activity_login, getString(R.string.login));
		TAG = LoginActivity.class.getSimpleName();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			exitWhenBack = bundle.getBoolean(EXIT_WHEN_BACK);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case android.R.id.home:
			finish();
			if (exitWhenBack) {
				BAppHelper.exit(this, true);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (exitWhenBack) {
			BAppHelper.exit(this, true);
		}
	}

	public void onLoginClicked(View view) {
	}

	public void onCancleClicked(View view) {
		BAppHelper.exit(this, true);
	}
}
