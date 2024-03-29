package arcanelux.library.activity;

import org.apache.http.util.EncodingUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import arcanelux.library.R;

public class BaseWebViewActivity extends BaseActionBarActivity {
	private WebView wv;
	private String url;
	private String method, postData;
	
	public void setWebView(WebView wv) {
		this.wv = wv;
		wv.getSettings().setJavaScriptEnabled(true);
		wv.setWebViewClient(new WebViewClientClass());
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public void setPostData(String postData) {
		this.postData = postData;
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
