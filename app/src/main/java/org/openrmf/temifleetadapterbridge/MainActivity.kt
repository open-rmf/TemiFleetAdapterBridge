package org.openrmf.temifleetadapterbridge
import org.openrmf.temifleetadapterbridge.BuildConfig

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.openrmf.temifleetadapterbridge.databinding.ActivityMainBinding
import io.socket.client.IO
import io.socket.client.Socket

const val WEBVIEW_URL = "com.openrmf.WEBVIEW_URL"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mSocket: Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mSocket = IO.socket(BuildConfig.FLEET_ADAPTER_WS_URL)
        mSocket.connect()
    }

    fun emitWebViewIntent(v: View?) {
        val intent = Intent(this, WebViewActivity::class.java)
        val url = "https://www.google.com"
        intent.putExtra(WEBVIEW_URL, url)
        startActivity(intent)
    }
}
