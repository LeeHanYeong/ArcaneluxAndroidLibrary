package arcanelux.library.activity;

import org.apache.http.util.EncodingUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import arcanelux.library.R;

public class BaseWebViewActivity extends BaseActionBarActivity {
	protected WebView wv;
	protected String method, postData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.basewebview);

		Intent intent = getIntent();
		String url = intent.getStringExtra("url");
		String title = intent.getStringExtra("title");
		int iconRes = intent.getIntExtra("iconRes", 0);
		method = intent.getStringExtra("method");
		postData = intent.getStringExtra("postData");

		mActionBar.setTitle(title);
		mActionBar.setIcon(iconRes);

		wv = (WebView) findViewById(R.id.wvBaseWebView);
		wv.getSettings().setJavaScriptEnabled(true);
		try{
			if(method == null || method.equals("POST") || method.equals("post")){
				wv.postUrl(url, EncodingUtils.getBytes(postData, "BASE64"));
			} else{
				wv.loadUrl(url);
			}	
		} catch(Exception e){
			wv.loadUrl(url);
		}

		wv.setWebViewClient(new WebViewClientClass());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
		if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) { 
			wv.goBack(); 
			return true; 
		} 
		return super.onKeyDown(keyCode, event);
	}

	private class WebViewClientClass extends WebViewClient { 
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) { 
			try{
				if(method == null || method.equals("POST") || method.equals("post")){
					wv.postUrl(url, EncodingUtils.getBytes(postData, "BASE64"));
				} else{
					wv.loadUrl(url);
				}	
			} catch(Exception e){
				wv.loadUrl(url);
			}
			return true; 
		} 
	}
	
	
}
