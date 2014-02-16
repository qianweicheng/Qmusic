package com.qmusic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.qmusic.R;
import com.qmusic.common.BUser;
import com.qmusic.uitls.BAppHelper;

public class LoginActivity extends BaseActivity {
	public static final String EXIT_WHEN_BACK = "exit_when_back";
	boolean exitWhenBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			exitWhenBack = bundle.getBoolean(EXIT_WHEN_BACK);
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
		BUser.getUser().setToken("token");
		Intent intent = new Intent(this, SplashActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(SplashActivity.RE_LOGIN, true);
		startActivity(intent);
	}

	public void onCancleClicked(View view) {
		BAppHelper.exit(this, true);
	}
}
