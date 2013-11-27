package com.qmusic.service;

import android.content.Context;
import android.os.Binder;

import com.qmusic.common.IServiceCallback;

public class BDataServiceImp extends Binder implements IServiceCallback {
	static final String TAG = BDataServiceImp.class.getSimpleName();
	Context ctx;

	public BDataServiceImp(Context ctx) {
		this.ctx = ctx;
	}
}
