import msg_definitions as msg
import socketio
import copy
import json
import os
import sys
from inputs import get_key

# Temporary fix for relative imports
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
sys.path.append(os.path.dirname(SCRIPT_DIR))

DRIVE_SPEED = 0.4
TURN_SPEED = 0.6
TILT_SPEED = 0.5
TILT_SENSITIVITY = 7
TURN_SENSITIVITY = 7


sio = socketio.Client()


@sio.event
def robot_state(data):
    print('robot_state: ', data)


@sio.event
def battery_status(data):
    print('battery_statua: ', data)


sio.connect('http://localhost:8008', headers={"robot_name": "temi_0"})
pause_stdout = False
def _print(msg):
    if not pause_stdout:
        print(msg)

while 1:
    events = get_key()
    for event in events:
        if event.code == "KEY_END":
            _print("STOP")
            sio.emit("stopMovement", json.dumps(
                copy.deepcopy(msg.STOPMOVEMENT_DEFINITION)))
        elif event.code == "KEY_UP":
            _print("DRIVE")
            json_msg = copy.deepcopy(msg.SKIDJOY_DEFINITION)
            json_msg["x"] = DRIVE_SPEED
            sio.emit("skidJoy", json.dumps(json_msg))
        elif event.code == "KEY_DOWN":
            _print("REVERSE")
            json_msg = copy.deepcopy(msg.SKIDJOY_DEFINITION)
            json_msg["x"] = DRIVE_SPEED * -1
            sio.emit("skidJoy", json.dumps(json_msg))
        elif event.code == "KEY_LEFT":
            _print("TURN LEFT")
            json_msg = copy.deepcopy(msg.TURNBY_DEFINITION)
            json_msg["degrees"] = TURN_SENSITIVITY
            json_msg["speed"] = TURN_SPEED
            sio.emit("turnBy", json.dumps(json_msg))
        elif event.code == "KEY_RIGHT":
            _print("TURN RIGHT")
            json_msg = copy.deepcopy(msg.TURNBY_DEFINITION)
            json_msg["degrees"] = -TURN_SENSITIVITY
            json_msg["speed"] = TURN_SPEED
            sio.emit("turnBy", json.dumps(json_msg))
        elif event.code == "KEY_PAGEDOWN":
            _print("TILT DOWN")
            json_msg = copy.deepcopy(msg.TILTBY_DEFINITION)
            json_msg["degrees"] = -TILT_SENSITIVITY
            json_msg["speed"] = TILT_SPEED
            sio.emit("tiltBy", json.dumps(json_msg))
        elif event.code == "KEY_PAGEUP":
            _print("TILT UP")
            json_msg = copy.deepcopy(msg.TILTBY_DEFINITION)
            json_msg["degrees"] = TILT_SENSITIVITY
            json_msg["speed"] = TILT_SPEED
            sio.emit("tiltBy", json.dumps(json_msg))
        elif event.code == "KEY_RIGHTALT":
            if event.state != 1:
                continue
            _print("ENTER ROOM")
            pause_stdout = True
            json_msg = copy.deepcopy(msg.TELEPRESENCE_DEFINITION)
            input("WebRTC Room, Press Enter to continue")
            room_id = input("Enter Room to enter:").strip()
            if id:
                json_msg["id"] = room_id
                sio.emit("telepresence", json.dumps(json_msg))
                input(f"going to {room_id}, press enter to continue")
            pause_stdout = False
        elif event.code == "KEY_RIGHTCTRL":
            if event.state != 1:
                continue
            _print("GOTO WAYPOINT")
            pause_stdout = True
            json_msg = copy.deepcopy(msg.GOTO_DEFINITION)
            input("GoTo Waypoint, Press Enter to continue")
            location = input("Enter Waypoint to goto:").strip()
            if location:
                json_msg["location"] = location
                sio.emit("goTo", json.dumps(json_msg))
                input(f"going to {location}, press enter to continue")
            pause_stdout = False
        else:
            pass
