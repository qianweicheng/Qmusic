package com.qmusic.controls;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class BTabFragment extends Fragment {
	public String TAG;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = getClass().getSimpleName();
	}

	public void onSelected() {
	}
}
