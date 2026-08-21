#!/bin/bash
echo "========================================================"
echo "📦 Building CapitalCurb AI Universal Mobile APK..."
echo "========================================================"

./gradlew assembleDebug

echo "========================================================"
echo "✅ Universal Mobile APK generated successfully!"
echo "📍 Location: app/build/outputs/apk/debug/CapitalCurbAI-v1.0.0-debug.apk"
echo "========================================================"
