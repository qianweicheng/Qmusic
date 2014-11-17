package com.qmusic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.qmusic.R;
import com.qmusic.common.BAppHelper;
import com.qmusic.common.BUser;

public class LoginActivity extends BaseActivity implements OnClickListener {
	public static final String EXIT_WHEN_BACK = "exit_when_back";
	boolean exitWhenBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		findViewById(R.id.activity_login_login_btn).setOnClickListener(this);
		findViewById(R.id.activity_login_cancel_btn).setOnClickListener(this);
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

	@Override
	public void onClick(View v) {
		BUser.getUser().setField(BUser.FIELD_TOKEN, "token");
		Intent intent = new Intent(this, SplashActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(SplashActivity.RE_LOGIN, true);
		startActivity(intent);
	}
}
