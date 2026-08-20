#!/usr/bin/env sh

# Resolve links: $0 may be a link
PRG="$0"
while [ -h "$PRG" ]; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done
SAVED="`pwd`"
CDPATH=""
cd "`dirname \"$PRG\"`/" >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null

JAVACMD="java"
if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
fi

CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

exec "$JAVACMD" -Xmx1024m -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
