#!/bin/bash
if ! [[ -z "$(ls -A /var/jenkins_home/files/feed2wall/apk)" ]]; then
  FOLDER_LIST=(/var/jenkins_home/files/feed2wall/apk/*/)
  FOLDER_LIST=("${FOLDER_LIST[@]%/}")
  FOLDER_LIST=("${FOLDER_LIST[@]##*/}")

  for VERSION_TO_PATCH in "${FOLDER_LIST[@]}"
  do
    mkdir -p ./originalBuild
    rm ./originalBuild/app.apk
    cp /var/jenkins_home/files/feed2wall/apk/${VERSION_TO_PATCH}/app.apk ./originalBuild/app.apk
    ./gradlew tinkerPatchDebug
    ./copyPatch -v ${VERSION_TO_PATCH} -t instagramPatching
    ./notifyUpdate instagramPatching
  done
else
   echo "No original builds are found, patching won't happen"
fi