package com.qmusic.extplugin;

public interface IPlugin {
	String[] getFunctions();

	String pluginId();

	String pluginInfo();

	void invoke(String function, Object params);

	void onLoad();

	void onUnload();
}
