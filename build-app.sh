#!/bin/bash

# RunTrack Build Script
# Fixes KAPT issues and builds the app

echo "🔧 Fixing build issues and building RunTrack with OpenStreetMap..."
echo ""

cd /home/ubuntu/Projects/run-track

# Stop any existing Gradle daemons to clear state
echo "1️⃣ Stopping Gradle daemons..."
./gradlew --stop

# Clean the project
echo ""
echo "2️⃣ Cleaning project..."
./gradlew clean

# Build the app
echo ""
echo "3️⃣ Building debug APK..."
./gradlew assembleDebug

# Check if build was successful
if [ $? -eq 0 ]; then
    echo ""
    echo "✅ BUILD SUCCESSFUL!"
    echo ""
    echo "📱 APK Location:"
    echo "   app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "🚀 To install on device/emulator:"
    echo "   adb install app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "   Or run:"
    echo "   ./gradlew installDebug"
else
    echo ""
    echo "❌ BUILD FAILED"
    echo "Check the error messages above"
fi
