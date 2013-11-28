package com.qmusic.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qmusic.R;
import com.qmusic.uitls.BLog;

public final class FragmentUserGuide extends Fragment implements OnClickListener {
	static final String TAG = FragmentUserGuide.class.getSimpleName();
	public static final int[] tutorials = new int[] { R.drawable.splash, R.drawable.splash };
	int index;

	public static FragmentUserGuide newInstance(int index) {
		FragmentUserGuide f = new FragmentUserGuide();
		Bundle b = new Bundle();
		b.putInt("index", index);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args;
		if (savedInstanceState != null) {
			args = savedInstanceState;
		} else {
			args = getArguments();
		}
		if (args != null) {
			index = args.getInt("index");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("index", index);
	}

	/**
	 * Create the view for this fragment, using the arguments given to it.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_user_guide, null);
		ImageView tutorial = (ImageView) layout.findViewById(R.id.fragment_user_guide_img);
		if (index > -1 && index < tutorials.length) {
			tutorial.setImageResource(tutorials[index]);
		} else {
			BLog.i(TAG, "index is large than the tutorials lenght");
			tutorial.setImageResource(tutorials[0]);
		}
		return layout;
	}

	@Override
	public void onClick(View v) {
		Activity ctx = getActivity();
		Intent intent = new Intent(ctx, SplashActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(SplashActivity.RE_LOGIN, true);
		startActivity(intent);
		ctx.finish();
	}
}