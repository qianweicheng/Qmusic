package com.qmusic.test;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.androidquery.util.AQUtility;
import com.qmusic.R;
import com.qmusic.activities.BaseActivity;
import com.qmusic.uitls.BAppHelper;

public class Test2Activity extends BaseActivity {
	public static final String TAG = Test2Activity.class.getSimpleName();
	ListView listView;
	static final String[] URLS = new String[] { "http://m.baidu.com", "http://google.com", "http://douban.com",
			"http://www.zhihu.com", "http://appi.ifeng.com/" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test2);
		listView = (ListView) findViewById(R.id.activity_test2_list);
		listView.setAdapter(new MyAdapter());
	}

	@Override
	public void onBackPressed() {
		BAppHelper.exit(this, true);
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return URLS.length * 10;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				WebView webView = new WebView(Test2Activity.this);
				AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
						AQUtility.dip2pixel(Test2Activity.this, 200));
				webView.setLayoutParams(layoutParams);
				convertView = webView;
			}
			String url = URLS[0];
			((WebView) convertView).loadUrl(url);
			// ((WebView) convertView).loadData(HTML, "text", "UTF-8");
			return convertView;
		}

	}

	static final String HTML = "<html><body><div>balbalb</div>"
			+ "<a href='http://imn.baidu.com/coolsite/index.php?ssid=0&amp;from=0&amp;bd_page_type=1&amp;uid=0&amp;pu=sz%40224_220%2Cta%40middle___3_537&amp;itj=215'>酷站</a>"
			+ "</body></html>";
}
