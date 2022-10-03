#!/bin/bash

SCRIPT=$(realpath -s "$0")
SCRIPTPATH=$(dirname "$SCRIPT")

GRADLEW=$SCRIPTPATH/gradlew

if [[ $# -eq 0 ]]
then
    $SCRIPTPATH/gradlew run
else
    $SCRIPTPATH/gradlew run --args="$*"
fi
