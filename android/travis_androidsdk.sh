#! /bin/bash

mkdir androidsdk
cd androidsdk
curl https://dl.google.com/android/repository/sdk-tools-linux-3859397.zip -o sdk.zip
unzip sdk.zip
rm sdk.zip
yes | ./tools/bin/sdkmanager --licenses
echo sdk.dir=`pwd` >> ../local.properties
cd ..