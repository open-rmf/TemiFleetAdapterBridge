package org.openrmf.temifleetadapterbridge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.robotemi.sdk.BatteryData

import io.socket.client.IO
import io.socket.client.Socket

import com.robotemi.sdk.Robot
import org.openrmf.temifleetadapterbridge.databinding.ActivityMainBinding
import com.robotemi.sdk.Robot.Companion.getInstance
import com.robotemi.sdk.listeners.OnBatteryStatusChangedListener
import com.robotemi.sdk.navigation.listener.OnCurrentPositionChangedListener
import com.robotemi.sdk.navigation.model.Position
import org.json.JSONObject
import java.util.Collections.singletonList
import java.util.Collections.singletonMap


const val TELEPRESENCE_ID = "com.openrmf.TELEPRESENCE_ID"
const val ROBOT_STATE_EVENT = "robot_state"
const val BATTERY_STATUS_EVENT = "battery_status"

class MainActivity : AppCompatActivity(), OnCurrentPositionChangedListener, OnBatteryStatusChangedListener {

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
        robot.addOnBatteryStatusChangedListener(this)

        val options = IO.Options.builder()
            .setExtraHeaders(
                singletonMap("robot_name",
                singletonList(robot_name))
            ).build()
        mSocket = IO.socket(BuildConfig.FLEET_ADAPTER_WS_URL, options)

        // WebSocket Listener callbacks

        mSocket.on("disconnect") {
            try {
                val location = "home base"

                Log.e("disconnect", "Going back to base on fleet adapter disconnect.")
                robot.goTo(location)
            } catch (e: RuntimeException) {
                Log.e("disconnect", e.toString())
            }
        }
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

        mSocket.on("telepresence") {

            for (item in it) {
                try {
                    val jsonData = JSONObject(JSONObject(item.toString()).getString("data"))
                    val id = jsonData.getString("id")

                    Log.e("telepresence", "id: " + id.toString().trim())
                    emitTelepresenceIntent(id)
                } catch (e: RuntimeException) {
                    Log.e("telepresence", e.toString())
                }
            }
        }

        mSocket.on("goTo") {

            for (item in it) {
                try {
                    val jsonData = JSONObject(JSONObject(item.toString()).getString("data"))
                    val location = jsonData.getString("location")

                    Log.e("goTo", "location: " + location.toString().trim())
                    robot.goTo(location)
                } catch (e: RuntimeException) {
                    Log.e("goTo", e.toString())
                }
            }
        }

        mSocket.connect()
    }

    // Temi callbacks
    private fun emitTelepresenceIntent(id: String) {
        val intent = Intent(this, ConnectActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(TELEPRESENCE_ID, id)
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

    override fun onBatteryStatusChanged(batteryData: BatteryData?) {
        Log.i("onBatteryStatusChanged",
            batteryData.toString())
        var msg = JSONObject()
        msg.put("level", batteryData?.level)
        msg.put("isCharging", batteryData?.isCharging)
        mSocket.emit(BATTERY_STATUS_EVENT, msg.toString())
    }
}
