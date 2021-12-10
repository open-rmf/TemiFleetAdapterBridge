package org.openrmf.temifleetadapterbridge
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val webView = findViewById<View>(R.id.webview) as WebView
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true

        webView.clearFocus()
        webSettings.setBuiltInZoomControls(false);
        webView.setOnTouchListener(null)
        webView.isClickable = false
        webView.isEnabled = false

        val intent = intent
        val url = intent.getStringExtra(WEBVIEW_URL)
        webView.loadUrl(url)
    }
}