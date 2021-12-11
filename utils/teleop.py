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


sio.connect('http://localhost:8008', headers={"robot_name": "temi_0"})

while 1:
    events = get_key()
    for event in events:
        if event.code == "KEY_ENTER":
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
        elif event.code == "KEY_LEFTSHIFT":
            print("KEY_LEFTSHIFT")
            json_msg = copy.deepcopy(msg.TILTBY_DEFINITION)
            json_msg["degrees"] = -TILT_SENSITIVITY
            json_msg["speed"] = TILT_SPEED
            sio.emit("tiltBy", json.dumps(json_msg))
        elif event.code == "KEY_LEFTCTRL":
            print("KEY_LEFTCTRL")
            json_msg = copy.deepcopy(msg.TILTBY_DEFINITION)
            json_msg["degrees"] = TILT_SENSITIVITY
            json_msg["speed"] = TILT_SPEED
            sio.emit("tiltBy", json.dumps(json_msg))
        elif event.code == "KEY_SPACE":
            if event.state != 1:
                continue
            print("KEY_SPACE")
            json_msg = copy.deepcopy(msg.WEBVIEW_DEFINITION)
            url = input("Enter URL to view:")
            json_msg["url"] = url
            sio.emit("webView", json.dumps(json_msg))
        else:
            pass
