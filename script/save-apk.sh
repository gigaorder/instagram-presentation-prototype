#!/bin/bash
if ! [[ -d "$FOLDER_PATH" ]]; then
  mkdir ${FOLDER_PATH}
  cp ./app/build/bakApk/app-${VERSION}.apk ${FOLDER_PATH}/app.apk
fi