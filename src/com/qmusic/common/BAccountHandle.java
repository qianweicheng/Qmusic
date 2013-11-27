package com.qmusic.common;

import android.content.Context;

import com.androidquery.auth.AccountHandle;
import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;

public class BAccountHandle extends AccountHandle {
	static final String TAG = BAccountHandle.class.getSimpleName();
	Context ctx;

	public BAccountHandle() {
		ctx = AQUtility.getContext();
	}

	@Override
	public boolean authenticated() {
		// Do not check authenticated
		return true;
	}

	@Override
	protected void auth() {
		// Do auth here
	}

	@Override
	public boolean expired(AbstractAjaxCallback<?, ?> cb, AjaxStatus status) {
		int code = status.getCode();
		return code == 400 || code == 401;
	}

	@Override
	public boolean reauth(final AbstractAjaxCallback<?, ?> cb) {

		AQUtility.post(new Runnable() {

			@Override
			public void run() {
				auth(cb);
			}
		});

		return false;
	}
}
