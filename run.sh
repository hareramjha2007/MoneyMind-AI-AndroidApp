#!/usr/bin/env bash

# ==============================================================================
# MoneyMind AI — Understand your money. Improve your future.
# One-Step Emulator Launch & App Build Script
# ==============================================================================

set -e

# Export standard Android SDK paths
export ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
export PATH="$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools:$ANDROID_HOME/tools/bin:$HOME/.local/bin:$PATH"

echo "========================================================"
echo "🚀 Starting MoneyMind AI Build & Deployment Pipeline..."
echo "========================================================"

# 1. Check for running devices/emulators
RUNNING_DEVICE=$(adb devices | grep -v "List of devices" | grep "device" | awk '{print $1}' | head -n 1)

if [ -n "$RUNNING_DEVICE" ]; then
    echo "✅ Found active device/emulator: $RUNNING_DEVICE"
else
    echo "⚡ No active emulator found. Detecting available AVDs..."
    AVD_NAME=$(emulator -list-avds 2>/dev/null | head -n 1)

    if [ -z "$AVD_NAME" ]; then
        if command -v android >/dev/null 2>&1; then
            AVD_NAME=$(android emulator list 2>/dev/null | grep -v "Error" | head -n 1)
        fi
    fi

    if [ -z "$AVD_NAME" ]; then
        echo "❌ Error: No Android Virtual Device (AVD) found!"
        echo "Please create an emulator in Android Studio or using 'android emulator create'."
        exit 1
    fi

    echo "📲 Starting Android Emulator: '$AVD_NAME'..."
    emulator -avd "$AVD_NAME" -no-snapshot-load > /dev/null 2>&1 &
    
    echo "⏳ Waiting for emulator to boot up..."
    adb wait-for-device
    
    until [ "$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" = "1" ]; do
        sleep 2
    done
    echo "✅ Emulator '$AVD_NAME' is online and ready!"
    RUNNING_DEVICE=$(adb devices | grep -v "List of devices" | grep "device" | awk '{print $1}' | head -n 1)
fi

# 2. Build Debug APK using Gradle wrapper
echo "========================================================"
echo "📦 Building MoneyMind AI Debug APK with Gradle..."
echo "========================================================"

./gradlew :app:assembleDebug

APK_PATH="app/build/outputs/apk/debug/MoneyMindAI-v1.0.0-debug.apk"
if [ ! -f "$APK_PATH" ]; then
    APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
fi

# 3. Install & Launch on emulator
echo "========================================================"
echo "📲 Deploying MoneyMind AI to Emulator ($RUNNING_DEVICE)..."
echo "========================================================"

if [ -f "$APK_PATH" ]; then
    echo "Installing APK..."
    adb -s "$RUNNING_DEVICE" install -r "$APK_PATH"
    echo "Launching com.finly.app.MainActivity..."
    adb -s "$RUNNING_DEVICE" shell am start -n "com.finly.app/com.finly.app.MainActivity"
    echo "========================================================"
    echo "🎉 MoneyMind AI is now running on your emulator!"
    echo "========================================================"
else
    echo "❌ Error: APK artifact not found at $APK_PATH"
    exit 1
fi
