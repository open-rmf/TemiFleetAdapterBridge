# Temi Fleet Adapter Bridge

## Setup
```bash
git clone git@github.com:open-rmf/TemiFleetAdapterBridge
# Open in Android Studio and build
adb connect [robot-ip]:5555
adb push 

# Remote view
apt install scrcpy
scrcpy --max-size 1024

# Teleop
pip3 install inputs
usermod -aG input $USER
```
