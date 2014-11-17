package com.qmusic.test;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.qmusic.R;
import com.qmusic.activities.BaseActivity;
import com.qmusic.common.BAppHelper;
import com.qmusic.controls.CommonTitle;
import com.qmusic.uitls.BLog;

public class Test2Activity extends BaseActivity implements OnClickListener {
	ListView listView;
	List<Integer> data;
	MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test2);
		CommonTitle commonTitle = (CommonTitle) findViewById(R.id.activity_test2_title);
		commonTitle.setRightImgCallback(this);
		listView = (ListView) findViewById(R.id.activity_test2_list);
		data = new ArrayList<Integer>();
		for (int i = 0; i < 10; i++) {
			data.add(i);
		}
		adapter = new MyAdapter();
		listView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.common_title_right_img) {
			if (data.size() > 0) {
				data.remove(0);
			}
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onBackPressed() {
		BAppHelper.exit(this, true);
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return data == null ? 0 : data.size();
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
			BLog.i(TAG, "Position:" + position);
			TextView txtTitle;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
			}
			txtTitle = (TextView) convertView;
			txtTitle.setText("Item " + data.get(position));
			return convertView;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			return data.get(position) % 2;
		}
	}
}
