#!/bin/bash

# Set the path to adb
ADB_PATH="/Users/yashbhutwala/Library/Android/sdk/platform-tools/adb"

# Check if adb exists
if [ ! -f "$ADB_PATH" ]; then
    echo "Error: adb not found at $ADB_PATH"
    exit 1
fi

# Check if device is connected
DEVICE_ID="48181FDAP0041B"
if ! $ADB_PATH devices | grep -q "$DEVICE_ID"; then
    echo "Error: Device $DEVICE_ID not found. Please make sure your phone is connected and USB debugging is enabled."
    exit 1
fi

# Build and install the app
echo "Building and installing the app..."
./gradlew installDebug --stacktrace

# Check if build was successful
if [ $? -ne 0 ]; then
    echo "Error: Build failed"
    exit 1
fi

# Get the package name from the device
echo "Using device: $DEVICE_ID"
PACKAGE_NAME=$($ADB_PATH -s "$DEVICE_ID" shell pm list packages | grep -i "molly" | head -n 1 | cut -d':' -f2)

if [ -z "$PACKAGE_NAME" ]; then
    echo "Error: Could not find the app package name. Please make sure the app is installed."
    exit 1
fi

echo "Launching app with package name: $PACKAGE_NAME"
$ADB_PATH -s "$DEVICE_ID" shell monkey -p "$PACKAGE_NAME" -c android.intent.category.LAUNCHER 1

if [ $? -eq 0 ]; then
    echo "App launched successfully!"
else
    echo "Error: Failed to launch the app"
    exit 1
fi