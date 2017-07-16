#! /bin/bash

mkdir androidsdk
cd androidsdk
wget https://dl.google.com/android/repository/sdk-tools-linux-3859397.zip -O sdk.zip
unzip sdk.zip
rm sdk.zip
yes | ./tools/bin/sdkmanager --licenses
echo sdk.dir=`pwd` >> ../local.properties
cd ..