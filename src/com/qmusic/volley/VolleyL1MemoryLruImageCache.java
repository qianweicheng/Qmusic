package com.qmusic.volley;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.qmusic.uitls.BLog;

/**
 * The Memory L1 cache
 * 
 * @author andy
 *
 */
public class VolleyL1MemoryLruImageCache extends LruCache<String, Bitmap> implements ImageCache {

	public VolleyL1MemoryLruImageCache(int maxSize) {
		super(maxSize);
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	@Override
	public Bitmap getBitmap(String url) {
		Bitmap result = get(url);
		if (result != null) {
			BLog.d("L1", "L1 Hit:" + url);
		} else {
			BLog.d("L1", "L1 Missed:" + url);
		}
		return result;
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		BLog.d("L1", "L1 add:" + url);
		put(url, bitmap);
	}
}