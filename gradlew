#!/usr/bin/env sh
set -eu
GRADLE_VERSION="9.4.1"
CACHE_DIR="${HOME:-.}/.gradle/famy-wrapper"
DIST_DIR="$CACHE_DIR/gradle-$GRADLE_VERSION"
BIN="$DIST_DIR/bin/gradle"
if [ ! -x "$BIN" ]; then
  mkdir -p "$CACHE_DIR"
  ZIP="$CACHE_DIR/gradle-$GRADLE_VERSION-bin.zip"
  URL="https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
  if command -v curl >/dev/null 2>&1; then
    curl -L --fail -o "$ZIP" "$URL"
  elif command -v wget >/dev/null 2>&1; then
    wget -O "$ZIP" "$URL"
  else
    echo "curl or wget is required to download Gradle $GRADLE_VERSION" >&2
    exit 1
  fi
  rm -rf "$DIST_DIR"
  unzip -q "$ZIP" -d "$CACHE_DIR"
fi
exec "$BIN" "$@"
