#!/bin/sh
PGENERATOR_VERSION="0.3.0"
PGENERATOR_NAME="pgenerator"
PGENERATOR_JAR="target/$PGENERATOR_NAME-$PGENERATOR_VERSION-jar-with-dependencies.jar"

if [ -f $PGENERATOR_JAR ]
then
    java -jar $PGENERATOR_JAR "$@"
else
    echo "Please run \"mvn package\" to build Plugin Generator first."
fi

