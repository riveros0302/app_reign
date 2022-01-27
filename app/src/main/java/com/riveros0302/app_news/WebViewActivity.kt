package com.riveros0302.app_news

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val webview = findViewById<WebView>(R.id.idWebView)
        val url = getIntent().getStringExtra("url")

        val setting = webview.settings
        setting.javaScriptEnabled = true

        webview.loadUrl(url.toString())
    }
}