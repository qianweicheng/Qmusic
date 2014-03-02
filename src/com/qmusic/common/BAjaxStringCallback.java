package com.qmusic.common;

import java.io.File;
import java.io.FileInputStream;

import android.text.TextUtils;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.util.AQUtility;
import com.qmusic.uitls.BLog;

public class BAjaxStringCallback extends AjaxCallback<String> {
	static final String TAG = BAjaxStringCallback.class.getSimpleName();

	public BAjaxStringCallback() {
		this.header("x-author", "weicheng.qian@hotmail.com");
		this.header("Cookie", BUser.getUser().getString(BUser.FIELD_TOKEN));
		this.type(String.class);
	}

	public final String getCache() {
		String str = null;
		try {
			File cacheFile = AQUtility.getCacheFile(AQUtility.getCacheDir(AQUtility.getContext()), this.getUrl());
			if (cacheFile.exists() && cacheFile.length() > 0) {
				byte[] data = AQUtility.toBytes(new FileInputStream(cacheFile));
				str = new String(data, getEncoding());
				if (!TextUtils.isEmpty(str)) {
					BLog.i(TAG, "from cache:" + this.getUrl());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return str;
	}

	public final void deleteCache() {
		try {
			File cacheFile = AQUtility.getCacheFile(AQUtility.getCacheDir(AQUtility.getContext()), this.getUrl());
			if (cacheFile.exists() && cacheFile.isFile()) {
				cacheFile.delete();
			} else {
				BLog.i(TAG, "delete failed");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
