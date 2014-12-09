package com.qmusic.volley;

import java.io.File;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.text.TextUtils;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
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
import com.qmusic.common.BConstants;

public class QMusicRequestManager {
	public static int L1CacheType = 0;
	public static int L2CacheType = 0;
	private static final int DISK_SIZE = 100 * 1024 * 1024;
	private static final int MEM_SIZE = 10 * 1024 * 1024;
	private static QMusicRequestManager instance;
	private RequestQueue mRequestQueue;
	private RequestQueue mRequestQueue2;// used to download large file;
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
	private QMusicRequestManager(final Context context, final int diskCacheSize, final int memCacheSize) {
		// ============L2 Cache=============
		HttpStack stack = getHttpStack(false);
		Network network = new BasicNetwork(stack);
		if (L2CacheType == 0) {
			// TODO: this L2 cache implement ignores the HTTP cache headers
			mCacheL2 = new VolleyL2DiskLruCache(new File(context.getCacheDir(), "L2-Cache"), diskCacheSize);
		} else {
			// The build-in L2 cache has no LRU
			mCacheL2 = new DiskBasedCache(new File(context.getCacheDir(), "L2-Cache"), diskCacheSize);
		}
		mRequestQueue = new RequestQueue(mCacheL2, network);
		mRequestQueue.start();
		// ============L1 Cache=============
		if (L1CacheType == 0) {
			mCacheL1 = new VolleyL1MemoryLruImageCache(memCacheSize);
		} else {
			mCacheL1 = new VolleyL1DiskLruImageCache(context, "L1-Cache", diskCacheSize, CompressFormat.JPEG, 80);
		}
		mImageLoader = new ImageLoader(mRequestQueue, mCacheL1);
	}

	public final void enableLargeFileDownload() {
		HttpStack stack = getHttpStack(false);
		Network network = new QMusicNetwork(stack);
		mRequestQueue2 = new RequestQueue(mCacheL2, network, 1);
		mRequestQueue2.start();
	}

	public static final void init(Context ctx) {
		// VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT, or SUPPRESS.
		// Note: to enable the log for volley
		// adb shell setprop log.tag.Volley VERBOSE
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
		instance = new QMusicRequestManager(ctx, DISK_SIZE, MEM_SIZE);
	}

	public static final QMusicRequestManager getInstance() {
		if (instance != null) {
			return instance;
		} else {
			throw new IllegalStateException("Not initialized");
		}
	}

	public final RequestQueue getRequestQueue() {
		return mRequestQueue;
	}

	public final RequestQueue getRequestQueue2() {
		return mRequestQueue2;
	}

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
			} else if (mCacheL1 instanceof VolleyL2DiskLruCache) {
				((VolleyL2DiskLruCache) mCacheL1).clear();
			}
		} else {
			mCacheL2.clear();
		}
	}

	private static final String createKey(String url) {
		return new StringBuilder(url.length() + 12).append("#W").append(0).append("#H").append(0).append(url).toString();
	}

	private static final HttpStack getHttpStack(boolean trusted) {
		// ============Support SSL==========
		// if there are untrusted SSL, the use this.
		HttpStack stack;
		if (trusted) {
			if (Build.VERSION.SDK_INT >= 9) {
				stack = new HurlStack();
			} else {
				stack = new HttpClientStack(AndroidHttpClient.newInstance(BConstants.APP_NAME));
			}
		} else if (Build.VERSION.SDK_INT >= 9) {
			SSLContext sslContext = QMusicHTTPSTrustManager.getSSLContext();
			HostnameVerifier hostnameVerifier = QMusicHTTPSTrustManager.getHostnameVerifier();
			SSLSocketFactory sf = sslContext.getSocketFactory();
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
			HttpsURLConnection.setDefaultSSLSocketFactory(sf);
			stack = new HurlStack();
		} else {
			HttpParams httpParams = new BasicHttpParams();
			// httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
			// HttpVersion.HTTP_1_1);
			HttpConnectionParams.setConnectionTimeout(httpParams, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS);
			HttpConnectionParams.setSoTimeout(httpParams, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS);
			// ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new
			// ConnPerRouteBean(NETWORK_POOL));
			ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(25));

			// Added this line to avoid issue at:
			// http://stackoverflow.com/questions/5358014/android-httpclient-oom-on-4g-lte-htc-thunderbolt
			HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", QMusicSSLSocketFactory.createInstance(), 443));
			ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, registry);
			HttpClient client = new DefaultHttpClient(cm, httpParams);
			// client = AndroidHttpClient.newInstance(BConstants.APP_NAME);
			stack = new HttpClientStack(client);
		}
		return stack;
	}
}
