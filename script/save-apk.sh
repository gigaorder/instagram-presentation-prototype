#!/bin/bash
VERSION=`cat ../gradle.properties | grep VERSION | sed 's/VERSION=//'`
FOLDER_PATH="/var/jenkins_home/files/feed2wall/apk/$VERSION"

if ! [[ -d "$FOLDER_PATH" ]]
  then
    mkdir ${FOLDER_PATH}
    cp ./app/build/bakApk/app-${VERSION}.apk ${FOLDER_PATH}/app.apk
fi