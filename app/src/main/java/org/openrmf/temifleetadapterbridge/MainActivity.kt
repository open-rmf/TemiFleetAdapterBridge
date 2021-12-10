package org.openrmf.temifleetadapterbridge

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.openrmf.temifleetadapterbridge.databinding.ActivityMainBinding
import android.widget.Toast

const val WEBVIEW_URL = "com.openrmf.WEBVIEW_URL";
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun onConnect(v: View?) {
        val intent = Intent(this, WebViewActivity::class.java);
        val url = "https://www.google.com"
        intent.putExtra(WEBVIEW_URL, url);
        startActivity(intent)
    }
}