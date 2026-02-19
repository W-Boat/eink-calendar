#!/bin/bash

# Script to initialize Gradle Wrapper
# This downloads the gradle-wrapper.jar needed for the project

set -e

GRADLE_VERSION="8.2"
GRADLE_HOME="gradle/wrapper"
JAR_FILE="$GRADLE_HOME/gradle-wrapper.jar"

if [ -f "$JAR_FILE" ]; then
    echo "Gradle Wrapper JAR already exists"
    exit 0
fi

echo "Downloading Gradle Wrapper..."
mkdir -p "$GRADLE_HOME"

# Download gradle-wrapper.jar from Gradle repository
GRADLE_WRAPPER_URL="https://raw.githubusercontent.com/gradle/gradle/v${GRADLE_VERSION}/gradle/wrapper/gradle-wrapper.jar"

if command -v curl &> /dev/null; then
    curl -sSL "$GRADLE_WRAPPER_URL" -o "$JAR_FILE"
elif command -v wget &> /dev/null; then
    wget -q "$GRADLE_WRAPPER_URL" -O "$JAR_FILE"
else
    echo "Error: curl or wget required to download Gradle Wrapper"
    exit 1
fi

echo "Gradle Wrapper JAR downloaded successfully"
ls -lh "$JAR_FILE"
