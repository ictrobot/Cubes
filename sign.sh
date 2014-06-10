#!  /bin/bash

#Align Debug
echo " "
echo "Aligning Debug APK"
$ANDROID_HOME/tools/zipalign -v 4 android/build/apk/android-debug-unaligned.apk android/build/apk/android-debug-aligned.apk

#Sign Release
cp android/build/apk/android-release-unsigned.apk android/build/apk/android-release-signed.apk
echo " "
echo "Signing Release APK"
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore /keys/EthanJonesApps.keystore -storepass $KEY_STORE_PASS android/build/apk/android-release-signed.apk release 
#Align Release
echo " "
echo "Aligning Release APK"
$ANDROID_HOME/tools/zipalign -v 4 android/build/apk/android-release-signed.apk android/build/apk/android-release-signed-aligned.apk