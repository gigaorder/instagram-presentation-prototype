#!/bin/bash
VERSION=`cat ../gradle.properties | grep VERSION | sed 's/VERSION=//'`
FOLDER_PATH="/var/jenkins_home/files/feed2wall/apk/$VERSION"

cp ${FOLDER_PATH}/app.apk ./originalBuild/app.apk