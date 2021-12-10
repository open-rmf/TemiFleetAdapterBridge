import socketio

sio = socketio.Client()


@sio.event
def robot_state(data):
    print('message received with ', data)


sio.connect('http://localhost:8008', headers={"robot_name": "temi_0"})
