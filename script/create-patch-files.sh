#!/bin/bash
FOLDER_LIST=`ls -d */`

for FOLDER in "${FOLDER_LIST[@]}"
do
  VERSION_TO_PATCH=`echo ${FOLDER} | sed 's/\///g'`
  mkdir -p ./originalBuild
  cp /var/jenkins_home/files/feed2wall/apk/${VERSION_TO_PATCH}/app.apk ./originalBuild/app.apk
  ./gradlew tinkerPatchDebug
  ./copyPatch -v ${VERSION_TO_PATCH} -t instagramPatching
done