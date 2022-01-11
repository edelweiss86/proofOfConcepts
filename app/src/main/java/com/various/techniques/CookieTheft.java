package com.various.techniques;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CookieTheft extends AppCompatActivity implements View.OnClickListener {

    WebView webview;
    EditText urlTextView;
    Button loadUrlBtn;
    WebViewClient webViewClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cookie_theft);
        webview = (WebView) findViewById(R.id.webView);
        urlTextView =(EditText) findViewById(R.id.urlHolder);
        urlTextView.setOnClickListener(this);
        loadUrlBtn = (Button) findViewById(R.id.loadUrl);
        loadUrlBtn.setOnClickListener(this);
        webViewClient = new CookieThiefWebClient();


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == loadUrlBtn.getId()){
            loadUrl_(urlTextView.getText().toString());
        }
        else if(id == urlTextView.getId()){
            System.out.println("Text clicked");
            urlTextView.setText("");
        }
    }
    public  void loadUrl_(String url){
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(webViewClient);
        webview.loadUrl(url);

    }
}