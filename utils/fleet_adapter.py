from aiohttp import web
import socketio


ROBOT_STATE_EVENT = "robot_state"

sio = socketio.AsyncServer(cors_allowed_origins="*")
app = web.Application()
sio.attach(app)


@sio.event
def connect(sid, headers, environ):
    try:
        robot_name = headers["HTTP_ROBOT_NAME"]
        print(f"connect {sid} to room {robot_name}")
        sio.enter_room(sid, robot_name)
    except Exception as e:
        print(e)


@sio.event
def disconnect(sid):
    print('disconnect ', sid)


@sio.event
async def robot_state(sid, data):
    for room in sio.rooms(sid):
        await sio.emit('robot_state', {'data': data}, room=room)
    print('robot_state: ', data)


@sio.event
async def goToPosition(sid, data):
    for room in sio.rooms(sid):
        await sio.emit('goToPosition', {'data': data}, room=room)
    print('goToPosition: ', data)


if __name__ == '__main__':
    web.run_app(app, host="0.0.0.0", port=8008)
