#!/bin/bash
echo "========================================================"
echo "📦 Building MoneyMind AI Universal Mobile APK..."
echo "========================================================"

./gradlew assembleDebug

echo "========================================================"
echo "✅ Universal Mobile APK generated successfully!"
echo "📍 Location: app/build/outputs/apk/debug/MoneyMindAI-v1.0.0-debug.apk"
echo "========================================================"
