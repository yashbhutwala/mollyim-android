#!/usr/bin/env bash
set -euo pipefail

# Allow overriding or auto-detecting adb
ADB_PATH="${ADB_PATH:-$(command -v adb)}"

# Check if adb exists
if [ ! -f "$ADB_PATH" ]; then
    echo "Error: adb not found at $ADB_PATH"
    exit 1
fi

# Check if device is connected
DEVICE_ID="${1:-${ANDROID_SERIAL:-}}"
if [ -z "$DEVICE_ID" ]; then
  # Use the first non-header device
  DEVICE_ID=$("$ADB_PATH" devices | sed 1d | awk '{print $1; exit}')
fi
if ! $ADB_PATH devices | grep -q "$DEVICE_ID"; then
    echo "Error: Device $DEVICE_ID not found. Please make sure your phone is connected and USB debugging is enabled."
    exit 1
fi

# Build and install the app
echo "Building and installing the app..."
./gradlew :app:installProdFossWebsiteDebug --stacktrace

# Check if build was successful
if [ $? -ne 0 ]; then
    echo "Error: Build failed"
    exit 1
fi

# Get the package name from the device
echo "Using device: $DEVICE_ID"
PACKAGE_NAME="${APPLICATION_ID:-$(grep applicationId app/build.gradle \
  | head -n1 | cut -d '"' -f2)}"
if ! "$ADB_PATH" -s "$DEVICE_ID" shell pm path "$PACKAGE_NAME" >/dev/null; then
  echo "Error: Package $PACKAGE_NAME not found on device"
  exit 1
fi

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