package com.qmusic.extplugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import com.qmusic.uitls.BLog;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

public class PluginLoader {
	static final String TAG = PluginLoader.class.getSimpleName();
	private static Map<String, IPlugin> plugins;
	private static Map<String, String> pluginsLoaded;
	static {
		plugins = new HashMap<String, IPlugin>();
		pluginsLoaded = new HashMap<String, String>();
	}

	public static final String[] getFunctions(String pluginId) {
		String[] functions = null;
		if (plugins.containsKey(pluginId)) {
			IPlugin plugin = plugins.get(pluginId);
			functions = plugin.getFunctions();
		} else {
			BLog.e(TAG, "plugin not exists:" + pluginId);
		}
		return functions;
	}

	public static final void execute(String pluginId, String functionName, Object params) {
		if (plugins.containsKey(pluginId)) {
			IPlugin plugin = plugins.get(pluginId);
			plugin.invoke(functionName, params);
		} else {
			BLog.e(TAG, "plugin not exists:" + pluginId);
		}
	}

	/**
	 * 每次升级插件的时候，必须同时删除缓存文件，否则程序会崩溃
	 * 
	 * @param pluginId
	 */
	@SuppressWarnings("rawtypes")
	@SuppressLint("NewApi")
	public static final String loadPlugin(Context ctx, String jarPath, String classPath) {
		String pluginId = "";
		final String key = jarPath + classPath;
		if (pluginsLoaded.containsKey(key)) {
			pluginId = pluginsLoaded.get(key);
			return pluginId;
		}
		try {
			Class mmsmsClass;
			if (Build.VERSION.SDK_INT >= 14) {
				BaseDexClassLoader cl = new BaseDexClassLoader(jarPath, ctx.getFilesDir(), null, ctx.getClassLoader());
				mmsmsClass = cl.loadClass(classPath);
			} else {
				DexClassLoader cl = new DexClassLoader(jarPath, ctx.getFilesDir().getAbsolutePath(), null,
						ctx.getClassLoader());
				mmsmsClass = cl.loadClass(classPath);
			}
			IPlugin plugin = (IPlugin) mmsmsClass.newInstance();
			pluginId = plugin.pluginId();
			plugin.onLoad();
			plugins.put(pluginId, plugin);
			pluginsLoaded.put(key, pluginId);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return pluginId;
	}

	public static final void removeCache(Context ctx, String fileName) {
		File folder = ctx.getFilesDir();
		File[] dexes = folder.listFiles();
		int index = fileName.lastIndexOf(".");
		String name = fileName;
		if (index > 0) {
			name = fileName.substring(0, index) + ".dex";
		} else {
			name += ".dex";
		}
		for (File dex : dexes) {
			if (dex.getName().equals(name)) {
				dex.delete();
				break;
			}
		}
	}

	public static final void removePlugin(String pluginId) {
		if (plugins.containsKey(pluginId)) {
			IPlugin plugin = plugins.remove(pluginId);
			Set<String> keys = pluginsLoaded.keySet();
			String keyId = null;
			for (String key : keys) {
				String value = pluginsLoaded.get(key);
				if (value.equals(pluginId)) {
					keyId = key;
					break;
				}
			}
			if (keyId != null) {
				pluginsLoaded.remove(keyId);
			}
			plugin.onUnload();
		} else {
			BLog.e(TAG, "plugin not exists:" + pluginId);
		}
	}
}
