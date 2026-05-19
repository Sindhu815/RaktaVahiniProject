#!/bin/sh

APP_HOME=$(dirname "$0")
LOCAL_GRADLE="$HOME/.gradle/wrapper/dists/gradle-8.7-bin"

if [ -d "$LOCAL_GRADLE" ]; then
  for candidate in "$LOCAL_GRADLE"/*/gradle-8.7/bin/gradle; do
    if [ -x "$candidate" ]; then
      exec "$candidate" "$@"
    fi
  done
fi

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

exec java -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
