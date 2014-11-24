package com.qmusic.webdoengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.qmusic.common.BConstants;
import com.qmusic.uitls.BIOUtilities;
import com.qmusic.uitls.BLog;
import com.qmusic.uitls.BUtilities;

public final class BWebdoEngine {
	static final String TAG = "BWebdoEngine";
	private static boolean USE_ASSET = true;
	private static volatile boolean resourceReady;
	private static HashMap<String, BWebView> cachedWebView;
	// done
	public static final String URL_HTML_SPA = "html/index_spa.html";
	public static final String URL_HTML = "html/index.html";

	@SuppressLint("NewApi")
	public static final void init(final Context ctx) {
		cachedWebView = new HashMap<String, BWebView>();
		// ==========================================
		AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				resourceReady = false;
				boolean result = updateResource(ctx);
				return result;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				// Add check in case that getWebview is called before update
				// resource finished
				if (!cachedWebView.containsKey(URL_HTML_SPA)) {
					cachedWebView.put(URL_HTML_SPA, getWebview(URL_HTML_SPA));
				}
				if (!cachedWebView.containsKey(URL_HTML)) {
					cachedWebView.put(URL_HTML, getWebview(URL_HTML));
				}
				resourceReady = true;
			}
		};
		asyncTask.execute();
	}

	/**
	 * 
	 * @param type
	 *            : in which webView the statement should be executed, if it is
	 *            empty then all.
	 * @param statement
	 */
	public static final void sendJavascript(final String type, final String statement) {
		if (TextUtils.isEmpty(type)) {
			for (BWebView webView : cachedWebView.values()) {
				webView.sendJavascript(statement);
			}
		} else {
			BWebView webView = cachedWebView.get(type);
			if (webView != null) {
				webView.sendJavascript(statement);
			}
		}
	}

	/**
	 * return a cached Webview, if there is no such Webview, then create a new
	 * one
	 * 
	 * @param relativeUrl
	 *            : the relative url root path is asset/www/
	 * @return
	 */
	public static final BWebView getWebview(String relativeUrl) {
		BWebView webView = cachedWebView.get(relativeUrl);
		if (webView == null) {
			final Context context = AQUtility.getContext();
			webView = new BWebView(context);
			String url;
			final File htmlCache = BUtilities.getHTMLFolder();
			if (USE_ASSET || htmlCache == null || !new File(htmlCache, relativeUrl).exists()) {
				url = "file:///android_asset/www/" + relativeUrl;
			} else {
				url = String.format("file://%s/%s", htmlCache.getAbsolutePath(), relativeUrl);
			}
			webView.loadUrl(url);
			cachedWebView.put(relativeUrl, webView);
		}
		return webView;
	}

	public static final boolean isWebdoEngineReady() {
		return resourceReady;
	}

	/**
	 * This could take some time to finish, please don't call this function in
	 * UI thread
	 * 
	 * @param ctx
	 */
	private static final boolean updateResource(final Context ctx) {
		// Step 1: check if there are html in sdcard
		// Step 2: check if there are download htmls.zip
		// Step 3: check if there are updates from server
		final File htmlFolder = BUtilities.getHTMLFolder();
		if (htmlFolder == null || !htmlFolder.isDirectory()) {
			return false;
		}
		if (htmlFolder.list().length == 0) {
			// then copy assert to sdcard
			try {
				final String root = "www";
				final String[] files = ctx.getAssets().list(root);
				for (String file : files) {
					BIOUtilities.copyAssertToSDCard(ctx, String.format("%s%s%s", root, File.separator, file), htmlFolder);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			final File zipFile = new File(AQUtility.getTempDir(), "htmls.zip");
			if (zipFile.exists()) {
				InputStream is = null;
				try {
					is = new FileInputStream(zipFile);
					BIOUtilities.unZipFolder(is, htmlFolder.getAbsolutePath());
					BLog.i(TAG, "htmls.zip unziped sucessfully");
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
				zipFile.delete();
			} else {
				// get zip from server async
				final String url = "http://192.168.1.105/htmls.zip";
				BLog.i(TAG, "downloading htmls.zip from " + url);
				AjaxCallback<File> callback = new AjaxCallback<File>() {
					@Override
					public void callback(String url, File object, AjaxStatus status) {
						if (object != null) {
							object.renameTo(zipFile);
							BLog.i(TAG, "htmls.zip download sucessfully");
							String lastModified = status.getHeader("Last-Modified");
							BUtilities.setPref(BConstants.PRE_KEY_LAST_MODIFIED_HTML, lastModified);
						} else if (status.getCode() == 304) {
							BLog.i(TAG, "htmls.zip is already up to date");
						} else {
							BLog.e(TAG, "htmls.zip download failed");
						}
					}
				};
				callback.url(url).type(File.class).uiCallback(false);// .targetFile(zipFile);
				String lastModifiedStr = BUtilities.getPref(BConstants.PRE_KEY_LAST_MODIFIED_HTML);
				if (!TextUtils.isEmpty(lastModifiedStr)) {
					callback.header("If-Modified-Since", lastModifiedStr);
				}
				callback.async(ctx);
			}
		}
		return true;
	}
}
