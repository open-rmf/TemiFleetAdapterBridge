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

while 1:
    events = get_key()
    for event in events:
        if event.code == "KEY_END":
            print("STOP")
            sio.emit("stopMovement", json.dumps(
                copy.deepcopy(msg.STOPMOVEMENT_DEFINITION)))
        elif event.code == "KEY_W":
            print("DRIVE")
            json_msg = copy.deepcopy(msg.SKIDJOY_DEFINITION)
            json_msg["x"] = DRIVE_SPEED
            sio.emit("skidJoy", json.dumps(json_msg))
        elif event.code == "KEY_S":
            print("REVERSE")
            json_msg = copy.deepcopy(msg.SKIDJOY_DEFINITION)
            json_msg["x"] = DRIVE_SPEED * -1
            sio.emit("skidJoy", json.dumps(json_msg))
        elif event.code == "KEY_A":
            print("TURN LEFT")
            json_msg = copy.deepcopy(msg.TURNBY_DEFINITION)
            json_msg["degrees"] = TURN_SENSITIVITY
            json_msg["speed"] = TURN_SPEED
            sio.emit("turnBy", json.dumps(json_msg))
        elif event.code == "KEY_D":
            print("TURN RIGHT")
            json_msg = copy.deepcopy(msg.TURNBY_DEFINITION)
            json_msg["degrees"] = -TURN_SENSITIVITY
            json_msg["speed"] = TURN_SPEED
            sio.emit("turnBy", json.dumps(json_msg))
        elif event.code == "KEY_K":
            print("TILT DOWN")
            json_msg = copy.deepcopy(msg.TILTBY_DEFINITION)
            json_msg["degrees"] = -TILT_SENSITIVITY
            json_msg["speed"] = TILT_SPEED
            sio.emit("tiltBy", json.dumps(json_msg))
        elif event.code == "KEY_I":
            print("TILT UP")
            json_msg = copy.deepcopy(msg.TILTBY_DEFINITION)
            json_msg["degrees"] = TILT_SENSITIVITY
            json_msg["speed"] = TILT_SPEED
            sio.emit("tiltBy", json.dumps(json_msg))
        elif event.code == "KEY_LEFTALT":
            if event.state != 1:
                continue
            print("ENTER ROOM")
            json_msg = copy.deepcopy(msg.TELEPRESENCE_DEFINITION)
            input("WebRTC Room, Press Enter to continue")
            id = input("Enter Room to enter:").strip()
            if not id:
                continue
            json_msg["id"] = id
            sio.emit("telepresence", json.dumps(json_msg))
        elif event.code == "KEY_LEFTCTRL":
            if event.state != 1:
                continue
            print("GOTO WAYPOINT")
            json_msg = copy.deepcopy(msg.GOTO_DEFINITION)
            input("GoTo Waypoint, Press Enter to continue")
            location = input("Enter Waypoint to goto:").strip()
            if not location:
                continue
            json_msg["location"] = location
            sio.emit("goTo", json.dumps(json_msg))
        else:
            print(event.code)
            pass
