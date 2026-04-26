@echo off
set GRADLE_VERSION=9.4.1
echo This lightweight project wrapper is optimized for GitHub Linux runners.
echo On Windows, install Gradle %GRADLE_VERSION% or build from Android Studio, then run: gradle %*
gradle %*
