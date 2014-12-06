package com.qmusic.volley;

import java.io.File;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.text.TextUtils;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;

public class RequestImageManager {
	public static int L1CacheType = 0;
	public static int L2CacheType = 0;
	private static final int DISK_SIZE = 100 * 1024 * 1024;
	private static final int MEM_SIZE = 10 * 1024 * 1024;
	private static RequestImageManager instance;
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	private ImageCache mCacheL1;
	private Cache mCacheL2;

	/**
	 * Volley recommends in-memory L1 cache but both a disk and memory cache are
	 * provided. Volley includes a L2 disk cache out of the box but you can
	 * technically use a disk cache as an L1 cache provided you can live with
	 * potential i/o blocking.
	 *
	 */
	public enum CacheType {
		DISK, MEMORY
	}

	/**
	 * Use a custom L2 cache,support LRU
	 * 
	 * @param context
	 * @param uniqueName
	 * @param diskCacheSize
	 * @param memCacheSize
	 * @param compressFormat
	 * @param quality
	 * @param type
	 */
	private RequestImageManager(final Context context, final int diskCacheSize, final int memCacheSize) {
		// ============L2 Cache=============
		HttpStack stack;
		if (Build.VERSION.SDK_INT >= 9) {
			stack = new HurlStack();
		} else {
			String userAgent = QmusicRequest.getUserAgent();
			stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
		}
		Network network = new BasicNetwork(stack);
		if (L2CacheType == 0) {
			mCacheL2 = new VolleyDiskLruImageCache(new File(context.getCacheDir(), "L2-Cache"), diskCacheSize);
		} else {
			mCacheL2 = new DiskBasedCache(new File(context.getCacheDir(), "L2-Cache"), diskCacheSize);
		}
		mRequestQueue = new RequestQueue(mCacheL2, network);
		mRequestQueue.start();
		// ============L1 Cache=============
		if (L1CacheType == 0) {
			mCacheL1 = new BitmapLruImageCache(memCacheSize);
		} else {
			mCacheL1 = new DiskLruImageCache(context, "L1-Cache", diskCacheSize, CompressFormat.JPEG, 80);
		}
		mImageLoader = new ImageLoader(mRequestQueue, mCacheL1);
	}

	public static final void init(Context ctx) {
		final String packageName = ctx.getPackageName();
		String userAgent = System.getProperties().getProperty("http.agent");
		if (TextUtils.isEmpty(userAgent)) {
			try {
				PackageInfo info = ctx.getPackageManager().getPackageInfo(packageName, 0);
				userAgent = packageName + "/" + info.versionCode;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				userAgent = packageName;
			}
		}
		QmusicRequest.setUserAgent(userAgent);
		instance = new RequestImageManager(ctx, DISK_SIZE, MEM_SIZE);
	}

	public static final RequestImageManager getInstance() {
		if (instance != null) {
			return instance;
		} else {
			throw new IllegalStateException("Not initialized");
		}
	}

	/**
	 * @return instance of the queue
	 * @throws IllegalStatException
	 *             if init has not yet been called
	 */
	public final RequestQueue getRequestQueue() {
		return mRequestQueue;
	}

	/**
	 * @return instance of the image loader
	 */
	public final ImageLoader getImageLoader() {
		return mImageLoader;
	}

	/**
	 * Get image from L1
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap getBitmap(String url) {
		return mCacheL1.getBitmap(createKey(url));
	}

	/**
	 * Put image to L1
	 * 
	 * @param url
	 * @param bitmap
	 */
	public void putBitmap(String url, Bitmap bitmap) {
		mCacheL1.putBitmap(createKey(url), bitmap);
	}

	/**
	 * Executes and image load
	 * 
	 * @param url
	 *            location of image
	 * @param listener
	 *            Listener for completion
	 */
	public void getImage(String url, ImageListener listener) {
		mImageLoader.get(url, listener);
	}

	/**
	 * CacheType
	 */
	public void clearCache(CacheType cacheType) {
		if (cacheType == CacheType.MEMORY) {
			if (mCacheL1 instanceof DiskBasedCache) {
				((DiskBasedCache) mCacheL1).clear();
			} else if (mCacheL1 instanceof VolleyDiskLruImageCache) {
				((VolleyDiskLruImageCache) mCacheL1).clear();
			}
		} else {
			mCacheL2.clear();
		}
	}

	private static final String createKey(String url) {
		return new StringBuilder(url.length() + 12).append("#W").append(0).append("#H").append(0).append(url).toString();
	}
}
