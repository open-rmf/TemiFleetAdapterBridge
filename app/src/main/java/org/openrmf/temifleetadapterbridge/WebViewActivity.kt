package org.openrmf.temifleetadapterbridge
import android.os.Bundle
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
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
        val url = intent.getStringExtra(TELEPRESENCE_ID)

        webView.webChromeClient = object: WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }
        }
        webView.loadUrl(url)
    }
}