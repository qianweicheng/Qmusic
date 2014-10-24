package com.qmusic.common;

import android.support.v4.util.LruCache;

public class MyLruCache<T> extends LruCache<String, T> {

	public MyLruCache(int maxSize) {
		super(maxSize);
	}

	@Override
	protected T create(String key) {
		// BLog.i(TAG, "create key:" + key);
		return super.create(key);
	}

	@Override
	protected void entryRemoved(boolean evicted, String key, T oldValue, T newValue) {
		super.entryRemoved(evicted, key, oldValue, newValue);
	}

	@Override
	protected int sizeOf(String key, T value) {
		return 1;
	}
}
