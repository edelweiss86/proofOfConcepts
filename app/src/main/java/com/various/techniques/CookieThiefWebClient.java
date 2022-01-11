package com.various.techniques;

import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CookieThiefWebClient extends WebViewClient {


    @Override
    public void onPageFinished(WebView view, String url) {

        super.onPageFinished(view, url);
        System.out.println("Finished loading page:"+url);
        String cookie = CookieManager.getInstance().getCookie(url);
        Log.i("Cookie:", cookie);
    }
}
