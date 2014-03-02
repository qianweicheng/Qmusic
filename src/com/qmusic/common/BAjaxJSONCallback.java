package com.qmusic.common;

import java.io.File;
import java.io.FileInputStream;

import org.json.JSONObject;

import android.text.TextUtils;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.util.AQUtility;
import com.qmusic.uitls.BLog;

public class BAjaxJSONCallback extends AjaxCallback<JSONObject> {
	static final String TAG = BAjaxJSONCallback.class.getSimpleName();

	public BAjaxJSONCallback() {
		this.header("x-author", "weicheng.qian@hotmail.com");
		this.header("Cookie", BUser.getUser().getString(BUser.FIELD_TOKEN));
		this.type(JSONObject.class);
	}

	public final JSONObject getCache() {
		JSONObject json = null;
		try {
			File cacheFile = AQUtility.getCacheFile(AQUtility.getCacheDir(AQUtility.getContext()), this.getUrl());
			if (cacheFile != null && cacheFile.exists() && cacheFile.length() > 0) {
				byte[] data = AQUtility.toBytes(new FileInputStream(cacheFile));
				String str = new String(data, getEncoding());
				if (!TextUtils.isEmpty(str)) {
					BLog.i(TAG, "from cache:" + this.getUrl());
					json = new JSONObject(str);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return json;
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
