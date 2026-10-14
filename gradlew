#!/usr/bin/env bash
set -euo pipefail

APP_HOME="$(cd "$(dirname "$0")" && pwd)"
GRADLE_VERSION="${GRADLE_VERSION:-8.10.2}"
GRADLE_HOME="$APP_HOME/.gradle/bootstrap/gradle-$GRADLE_VERSION"
GRADLE_BIN="$GRADLE_HOME/bin/gradle"

if [ ! -x "$GRADLE_BIN" ]; then
  EXISTING_GRADLE="$(find "$HOME/.gradle/wrapper/dists" -path '*/bin/gradle' -type f 2>/dev/null | sort -V | tail -n 1 || true)"
  if [ -n "$EXISTING_GRADLE" ] && [ -x "$EXISTING_GRADLE" ]; then
    exec "$EXISTING_GRADLE" "$@"
  fi
fi

if [ ! -x "$GRADLE_BIN" ]; then
  DIST_DIR="$APP_HOME/.gradle/bootstrap"
  DIST_ZIP="$DIST_DIR/gradle-$GRADLE_VERSION-bin.zip"
  mkdir -p "$DIST_DIR"
  if [ ! -f "$DIST_ZIP" ]; then
    curl -fsSL "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" -o "$DIST_ZIP"
  fi
  unzip -q "$DIST_ZIP" -d "$DIST_DIR"
fi

exec "$GRADLE_BIN" "$@"
