#rm -rf android/build/apk/*.apk
#rm -rf desktop/build/libs/*.jar

mv android/AndroidManifest.xml android/AndroidManifest.old
cat android/AndroidManifest.old | sed -e "s/%versionName%/${major}.${minor}.${point}.${build}/" >> android/AndroidManifest.xml
rm -rf android/AndroidManifest.old
mv android/AndroidManifest.xml android/AndroidManifest.old
cat android/AndroidManifest.old | sed -e "s/1234/$BUILD_NUMBER/" >> android/AndroidManifest.xml
rm -rf android/AndroidManifest.old