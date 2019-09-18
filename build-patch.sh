#!/bin/sh

if [ -z "$1" ]
then
	echo "Provide server public path"
else
	rm -rf ./app/build/outputs/apk/tinkerPatch/debug
	./gradlew tinkerPatchDebug
	rm $1/patch_signed_7zip.apk
	mv ./app/build/outputs/apk/tinkerPatch/debug/patch_signed_7zip.apk $1/
fi
