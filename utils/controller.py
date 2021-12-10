import socketio
import copy
import json
import os
import sys

# Temporary fix for relative imports
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
sys.path.append(os.path.dirname(SCRIPT_DIR))

import msg_definitions as msg

sio = socketio.Client()


@sio.event
def robot_state(data):
    print('robot_state received with ', data)


goto_msg = copy.deepcopy(msg.GOTOPOSITION_DEFINITION)
goto_msg["x"] = 0.0
goto_msg["y"] = 0.0
goto_msg["yaw"] = 0.0
goto_msg["tiltAngle"] = 0.0

sio.connect('http://localhost:8008', headers={"robot_name": "temi_0"})
sio.emit("goToPosition", json.dumps(goto_msg))
