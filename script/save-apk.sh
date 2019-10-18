#!/bin/bash
if ! [[ -d "$ORIGINAL_APK_FOLDER_PATH" ]]; then
  mkdir ${ORIGINAL_APK_FOLDER_PATH}
  cp ./app/build/bakApk/app-${BUILD_VERSION}.apk ${ORIGINAL_APK_FOLDER_PATH}/app.apk
fi