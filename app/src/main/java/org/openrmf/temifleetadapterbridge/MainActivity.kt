package org.openrmf.temifleetadapterbridge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
//import com.robotemi.sdk.BatteryData

import io.socket.client.IO
import io.socket.client.Socket

import org.openrmf.temifleetadapterbridge.databinding.ActivityMainBinding
//import com.robotemi.sdk.Robot
//import com.robotemi.sdk.Robot.Companion.getInstance
//import com.robotemi.sdk.listeners.OnBatteryStatusChangedListener
//import com.robotemi.sdk.navigation.listener.OnCurrentPositionChangedListener
//import com.robotemi.sdk.navigation.model.Position
import org.json.JSONObject
import java.util.Collections.singletonList
import java.util.Collections.singletonMap

import org.jitsi.meet.sdk.*
import android.content.Context
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.jitsi.meet.sdk.*
import timber.log.Timber
import java.net.MalformedURLException
import java.net.URL


const val TELEPRESENCE_ID = "com.openrmf.TELEPRESENCE_ID"
const val ROBOT_STATE_EVENT = "robot_state"
const val BATTERY_STATUS_EVENT = "battery_status"

//class MainActivity : AppCompatActivity(), OnCurrentPositionChangedListener, OnBatteryStatusChangedListener {
class MainActivity : AppCompatActivity() {

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onBroadcastReceived(intent)
        }
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var mSocket: Socket
//    private lateinit var robot: Robot
    private val robot_name = BuildConfig.ROBOT_NAME
    private val serverURL = URL(BuildConfig.VIDEOROOM_URL)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerForBroadcastMessages()

        // Temi init
//        robot = getInstance()
//        robot.addOnCurrentPositionChangedListener(this)
//        robot.addOnBatteryStatusChangedListener(this)

        // WebSockets init
        val options = IO.Options.builder()
            .setExtraHeaders(
                singletonMap("robot_name",
                singletonList(robot_name))
            ).build()
        mSocket = IO.socket(BuildConfig.FLEET_ADAPTER_WS_URL, options)

        mSocket.on("disconnect") {
            try {
                val location = "home base"

                Timber.tag("disconnect").i("Going back to base on fleet adapter disconnect.")
//                robot.goTo(location)
            } catch (e: RuntimeException) {
                Timber.tag("disconnect").e(e.toString())
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

//                    val position = Position(x, y, yaw, tiltAngle)
//                    Log.e("goToPosition", position.toString())
////                    robot.goToPosition(position)
                } catch (e: RuntimeException) {
                    Timber.tag("goToPosition").e(e.toString())
                }
            }
        }

        mSocket.on("tiltBy") {
            for (item in it) {
                try {
                    val jsonData = JSONObject(JSONObject(item.toString()).getString("data"))
                    val degrees = jsonData.getString("degrees").toInt()
                    val speed = jsonData.getString("speed").toFloat()


                    Timber.tag( "tiltBy").i(
                        "Degrees: %s Speed: %s", degrees.toString(), speed.toString() )
//                    robot.tiltBy(degrees, speed)
                } catch (e: RuntimeException) {
                    Timber.tag("tiltBy").e(e.toString())
                }
            }
        }

        mSocket.on("turnBy") {
            for (item in it) {
                try {
                    val jsonData = JSONObject(JSONObject(item.toString()).getString("data"))
                    val degrees = jsonData.getString("degrees").toInt()
                    val speed = jsonData.getString("speed").toFloat()


                    Timber.tag("turnBy").i(
                        "Degrees: %s Speed: %s", degrees.toString(), speed.toString())
//                    robot.turnBy(degrees, speed)
                } catch (e: RuntimeException) {
                    Timber.tag("turnBy").e(e.toString())
                }
            }
        }

        mSocket.on("skidJoy") {
            for (item in it) {
                try {
                    val jsonData = JSONObject(JSONObject(item.toString()).getString("data"))
                    val x = jsonData.getString("x").toFloat()
                    val y = jsonData.getString("y").toFloat()


                    Timber.tag("skidJoy").i("x: %s y: %s", x.toString(), y.toString())
//                    robot.skidJoy(x, y)
                } catch (e: RuntimeException) {
                    Timber.tag("skidJoy").e(e.toString())
                }
            }
        }

        mSocket.on("stopMovement") {
            try {
                Timber.tag("stopMovement").i("Stop")
//                robot.stopMovement()
            } catch (e: RuntimeException) {
                Timber.tag("stopMovement").e(e.toString())
            }
        }

        mSocket.on("telepresence") {

            for (item in it) {
                try {
                    val jsonData = JSONObject(JSONObject(item.toString()).getString("data"))
                    val id = jsonData.getString("id")
                    LaunchJitsi(id)

                    Timber.tag("telepresence").i("id: %s", id.toString().trim())
                } catch (e: RuntimeException) {
                    Timber.tag("telepresence").e(e.toString())
                }
            }
        }

        mSocket.on("telepresenceEnd") {

            Timber.tag("telepresenceEnd").i("Ending Call")
            hangUp()
        }

        mSocket.on("goTo") {

            for (item in it) {
                try {
                    val jsonData = JSONObject(JSONObject(item.toString()).getString("data"))
                    val location = jsonData.getString("location")

                    Timber.tag("goTo").i("location: %s", location.toString().trim())
//                    robot.goTo(location)
                } catch (e: RuntimeException) {
                    Timber.tag("goTo").e(e.toString())
                }
            }
        }

        mSocket.connect()
    }

    // Temi callbacks
//    override fun onCurrentPositionChanged(position: Position) {
//        Log.i("onCurrentPositionChanged",
//            position.toString())
//
//        var msg = JSONObject()
//        msg.put("x", position.x)
//        msg.put("y", position.y)
//        msg.put("yaw", position.yaw)
//        msg.put("tiltAngle", position.tiltAngle)
//        mSocket.emit(ROBOT_STATE_EVENT, msg.toString())
//    }
//
//    override fun onBatteryStatusChanged(batteryData: BatteryData?) {
//        Log.i("onBatteryStatusChanged",
//            batteryData.toString())
//        var msg = JSONObject()
//        msg.put("level", batteryData?.level)
//        msg.put("isCharging", batteryData?.isCharging)
//        mSocket.emit(BATTERY_STATUS_EVENT, msg.toString())
//    }
    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    fun onButtonClick(v: View?) {
        val editText = findViewById<EditText>(R.id.room_edittext)
        val text = editText.text.toString()
        LaunchJitsi(text)
    }

    fun LaunchJitsi(text: String) {
        if (text.length > 0) {
            val options = JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                .setRoom(text)
                //.setToken("MyJWT")
                .setAudioMuted(true)
                .setWelcomePageEnabled(false)
                .setConfigOverride("requireDisplayName", false)
                .build()
            JitsiMeetActivity.launch(this, options)
        }
    }

    private fun registerForBroadcastMessages() {
        val intentFilter = IntentFilter()

        /* This registers for every possible event sent from JitsiMeetSDK
           If only some of the events are needed, the for loop can be replaced
           with individual statements:
           ex:  intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.action);
                intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.action);
                ... other events
         */
        for (type in BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.action)
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter)
    }

    // Example for handling different JitsiMeetSDK events
    private fun onBroadcastReceived(intent: Intent?) {
        if (intent != null) {
            val event = BroadcastEvent(intent)
            when (event.getType()) {
                BroadcastEvent.Type.CONFERENCE_JOINED -> Timber.i("Conference Joined with url%s", event.getData().get("url"))
                BroadcastEvent.Type.PARTICIPANT_JOINED -> Timber.i("Participant joined%s", event.getData().get("name"))
            }
        }
    }

    // Example for sending actions to JitsiMeetSDK
    private fun hangUp() {
        val hangupBroadcastIntent: Intent = BroadcastIntentHelper.buildHangUpIntent()
        LocalBroadcastManager.getInstance(org.webrtc.ContextUtils.getApplicationContext()).sendBroadcast(hangupBroadcastIntent)
    }
}
