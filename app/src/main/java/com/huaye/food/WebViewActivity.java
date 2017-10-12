package com.huaye.food;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebViewActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		WebView view = new WebView(this);
		setContentView(view);
		view.loadUrl("http://dining.nd.edu/locations-menus/");
	}
}
