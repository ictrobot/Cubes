zip -q -r -9 source.zip * -x ".git"

mv android/assets/version android/assets/version.tmp
cat android/assets/version.tmp | sed -e "s/%BUILD_NUMBER%/$BUILD_NUMBER/" >> android/assets/version
rm -rf android/assets/version.tmp