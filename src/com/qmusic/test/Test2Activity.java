package com.qmusic.test;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.qmusic.R;
import com.qmusic.activities.BaseActivity;
import com.qmusic.uitls.BLog;
import com.qmusic.volley.RequestImageManager;

public class Test2Activity extends BaseActivity implements OnClickListener {
	ListView listView;
	MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test2);
		listView = (ListView) findViewById(R.id.activity_test2_list);
		adapter = new MyAdapter();
		listView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {

	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return Constants.IMAGES.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// BLog.i(TAG, "Position:" + position);
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.item_simple_text_with_icon2, parent, false);
			}
			final RequestImageManager manager = RequestImageManager.getInstance();
			final ImageLoader imageLoader = manager.getImageLoader();

			final ImageView imgView = (ImageView) convertView.findViewById(R.id.item_icon);
			if (imgView.getTag() != null) {
				ImageContainer imageContainer = (ImageContainer) imgView.getTag();
				imageContainer.cancelRequest();
			}
			final ImageContainer imageContainer = imageLoader.get(Constants.IMAGES[position], new ImageListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					imgView.setImageResource(R.drawable.b_loader_1);
				}

				@Override
				public void onResponse(ImageContainer response, boolean isImmediate) {
					if (response.getBitmap() != null) {
						imgView.setImageBitmap(response.getBitmap());
						if (isImmediate) {
							BLog.d(TAG, "From L1:" + response.getRequestUrl());
						}
					} else {
						imgView.setImageResource(R.drawable.icon);
					}
				}
			});
			imgView.setTag(imageContainer);
			return convertView;
		}
	}
}
