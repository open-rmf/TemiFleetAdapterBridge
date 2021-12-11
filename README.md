# Temi Fleet Adapter Bridge

## Setup
```bash
git clone git@github.com:open-rmf/TemiFleetAdapterBridge
# Open in Android Studio and build
adb connect [robot-ip]:5555
adb push 

# Permissions
adb shell pm grant org.openrmf.temifleetadapterbridge android.permission.CAMERA
adb shell pm grant org.openrmf.temifleetadapterbridge android.permission.RECORD_AUDIO

# Remote view
apt install scrcpy
scrcpy --max-size 1024

# Teleop
pipenv shell
usermod -aG input $USER
python3 mock_fleet_adapter.py
python3 teleop.py

```
