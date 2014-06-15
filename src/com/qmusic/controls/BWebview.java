package com.qmusic.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.webkit.WebView;

import com.qmusic.uitls.BLog;

public class BWebview extends WebView {
	public BWebview(Context context) {
		super(context);
	}

	public BWebview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public InputConnection onCreateInputConnection(EditorInfo ei) {
		return new MyConnection(super.onCreateInputConnection(ei));
	}

	private class MyConnection extends InputConnectionWrapper {
		public MyConnection(InputConnection wrapped) {
			super(wrapped, false);
		}

		@Override
		public boolean deleteSurroundingText(int beforeLength, int afterLength) {
			BLog.d("BWebview", "beforeLength:" + beforeLength + ";afterLength:" + afterLength);
			return super.deleteSurroundingText(beforeLength, afterLength);
		}

		@Override
		public boolean commitText(CharSequence text, int newCursorPosition) {
			BLog.d("BWebview", "commitText:" + text + ";newCursorPosition:" + newCursorPosition);
			return super.commitText(text, newCursorPosition);
		}

		@Override
		public boolean performEditorAction(int action) {
			BLog.d("BWebview", "action:" + action);
			return true;
		}

		@Override
		public boolean sendKeyEvent(KeyEvent event) {
			// int i = KeyEvent.KEYCODE_DEL;
			BLog.d("BWebview", "event:" + event.getKeyCode());
			return super.sendKeyEvent(event);
		}
	}
}
