package com.qmusic.service;

import com.qmusic.uitls.BLog;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class BAccessibilityService extends AccessibilityService {
	@Override
	public void onCreate() {
		super.onCreate();
		getServiceInfo().flags = AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE;
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
