package org.openrmf.temifleetadapterbridge
import org.openrmf.temifleetadapterbridge.BuildConfig

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity

import io.socket.client.IO
import io.socket.client.Socket

import com.robotemi.sdk.Robot
import org.openrmf.temifleetadapterbridge.databinding.ActivityMainBinding
import com.robotemi.sdk.Robot.Companion.getInstance
import com.robotemi.sdk.navigation.listener.OnCurrentPositionChangedListener
import com.robotemi.sdk.navigation.model.Position
import org.json.JSONObject
import java.util.*
import java.util.Collections.singletonList
import java.util.Collections.singletonMap

const val WEBVIEW_URL = "com.openrmf.WEBVIEW_URL"
const val ROBOT_STATE_EVENT = "robot_state"

class MainActivity : AppCompatActivity(), OnCurrentPositionChangedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mSocket: Socket
    private lateinit var robot: Robot
    private val robot_name = BuildConfig.ROBOT_NAME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val options = IO.Options.builder()
            .setExtraHeaders(
                singletonMap("robot_name",
                singletonList(robot_name))
            ).build()
        mSocket = IO.socket(BuildConfig.FLEET_ADAPTER_WS_URL, options)
        mSocket.connect()

        robot = getInstance()
        robot.addOnCurrentPositionChangedListener(this)
    }

    fun emitWebViewIntent(v: View?) {
        val intent = Intent(this, WebViewActivity::class.java)
        val url = "https://www.google.com"
        intent.putExtra(WEBVIEW_URL, url)
        startActivity(intent)
    }

    override fun onCurrentPositionChanged(position: Position) {
        Log.i("onCurrentPositionChanged",
            position.toString())

        var msg = JSONObject()
        msg.put("x", position.x)
        msg.put("y", position.y)
        msg.put("yaw", position.yaw)
        msg.put("tiltAngle", position.tiltAngle)
        mSocket.emit(ROBOT_STATE_EVENT, msg.toString())
    }
}
