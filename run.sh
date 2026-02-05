#!/bin/bash
# Discogs Random Picker - Unix/Linux Launch Script
# Version 2.0

# Set the JAR file path
JAR_FILE="target/discogs-random-picker-1.0-SNAPSHOT.jar"

# Check if JAR exists
if [ ! -f "$JAR_FILE" ]; then
    echo "Building project..."
    mvn clean package
    if [ $? -ne 0 ]; then
        echo "Build failed!"
        exit 1
    fi
fi

# Run the application with all arguments
java -jar "$JAR_FILE" "$@"
