package com.qmusic.localplugin;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.qmusic.uitls.BLog;

public abstract class PluginManager {
	static final String TAG = PluginManager.class.getSimpleName();
	static Map<String, BasePlug> plugins;

	public static void init(Context ctx) {
		plugins = new HashMap<String, BasePlug>();
		// BasePlug plug = new LockScreenPlug();
		// plugins.put(plug.plugName(), plug);
		for (BasePlug p : plugins.values()) {
			BLog.i(TAG, "initing plugin:" + p.plugName());
			p.init(ctx);
		}
	}

	public static void destory() {
		for (BasePlug plug : plugins.values()) {
			BLog.i(TAG, "destorying plugin:" + plug.plugName());
			plug.destory();
		}
	}

	public static final BasePlug getPlugin(String pluginName) {
		return plugins.get(pluginName);
	}
}
