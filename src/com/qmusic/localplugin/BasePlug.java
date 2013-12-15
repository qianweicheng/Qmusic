package com.qmusic.localplugin;

import android.content.Context;

public abstract class BasePlug {
	public String plugName() {
		return this.getClass().getSimpleName();
	}

	abstract void init(Context ctx);

	abstract void destory();
}
