package com.qmusic.service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.qmusic.uitls.BLog;

public class BAccessibilityService extends AccessibilityService {
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		BLog.e("BAccessibilityService", "" + event.getClassName());
		AccessibilityNodeInfo nodeInfo = event.getSource();
		nodeInfo.performAction(nodeInfo.getActions());
		nodeInfo.recycle();
	}

	@Override
	public void onInterrupt() {

	}

}
