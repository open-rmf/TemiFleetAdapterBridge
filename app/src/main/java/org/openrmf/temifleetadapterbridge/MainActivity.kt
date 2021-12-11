package org.openrmf.temifleetadapterbridge
import org.openrmf.temifleetadapterbridge.BuildConfig

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity

import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter

import com.robotemi.sdk.Robot
import org.openrmf.temifleetadapterbridge.databinding.ActivityMainBinding
import com.robotemi.sdk.Robot.Companion.getInstance
import com.robotemi.sdk.navigation.listener.OnCurrentPositionChangedListener
import com.robotemi.sdk.navigation.model.Position
import org.json.JSONObject
import java.util.*
import java.util.Collections.singletonList
import java.util.Collections.singletonMap
import org.json.JSONException
import androidx.core.content.IntentCompat







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

        robot = getInstance()
        robot.addOnCurrentPositionChangedListener(this)

        val options = IO.Options.builder()
            .setExtraHeaders(
                singletonMap("robot_name",
                singletonList(robot_name))
            ).build()
        mSocket = IO.socket(BuildConfig.FLEET_ADAPTER_WS_URL, options)

        // WebSocket Listener callbacks
        mSocket.on("goToPosition") {
            for (item in it) {
                try {
                    val jsonData = JSONObject(JSONObject(item.toString()).getString("data"))
                    val x = jsonData.getString("x").toFloat()
                    val y = jsonData.getString("y").toFloat()
                    val tiltAngle = jsonData.getString("tiltAngle").toInt()
                    val yaw = jsonData.getString("yaw").toFloat()

                    val position = Position(x, y, yaw, tiltAngle)
                    Log.e("goToPosition", position.toString())
                    robot.goToPosition(position)
                } catch (e: RuntimeException) {
                    Log.e("goToPosition", e.toString())
                }
            }
        }

        mSocket.on("tiltBy") {
            for (item in it) {
                try {
                    val jsonData = JSONObject(JSONObject(item.toString()).getString("data"))
                    val degrees = jsonData.getString("degrees").toInt()
                    val speed = jsonData.getString("speed").toFloat()


                    Log.e(
                        "tiltBy", "Degrees: " + degrees.toString() +
                                " Speed: " + speed.toString()
                    )
                    robot.tiltBy(degrees, speed)
                } catch (e: RuntimeException) {
                    Log.e("tiltBy", e.toString())
                }
            }
        }

        mSocket.on("turnBy") {
            for (item in it) {
                try {
                    val jsonData = JSONObject(JSONObject(item.toString()).getString("data"))
                    val degrees = jsonData.getString("degrees").toInt()
                    val speed = jsonData.getString("speed").toFloat()


                    Log.e(
                        "turnBy", "Degrees: " + degrees.toString() +
                                " Speed: " + speed.toString()
                    )
                    robot.turnBy(degrees, speed)
                } catch (e: RuntimeException) {
                    Log.e("turnBy", e.toString())
                }
            }
        }

        mSocket.on("skidJoy") {
            for (item in it) {
                try {
                    val jsonData = JSONObject(JSONObject(item.toString()).getString("data"))
                    val x = jsonData.getString("x").toFloat()
                    val y = jsonData.getString("y").toFloat()


                    Log.e(
                        "skidJoy", "x: " + x.toString() +
                                " y: " + y.toString()
                    )
                    robot.skidJoy(x, y)
                } catch (e: RuntimeException) {
                    Log.e("skidJoy", e.toString())
                }
            }
        }

        mSocket.on("stopMovement") {
            try {
                Log.e("stopMovement", "Stop")
                robot.stopMovement()
            } catch (e: RuntimeException) {
                Log.e("stopMovement", e.toString())
            }
        }

        mSocket.on("webView") {

            for (item in it) {
                try {
                    val jsonData = JSONObject(JSONObject(item.toString()).getString("data"))
                    val url = jsonData.getString("url")

                    Log.e("webView", "url: " + url.toString())
                    emitWebViewIntent(url)
                } catch (e: RuntimeException) {
                    Log.e("skidJoy", e.toString())
                }
            }
        }

        mSocket.connect()
    }

    // Temi callbacks
    private fun emitWebViewIntent(url: String) {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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
