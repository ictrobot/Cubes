zip -q -r -9 source.zip * -x ".git"

mv assets/version assets/version.tmp
cat assets/version.tmp | sed -e "s/%BUILD_NUMBER%/$BUILD_NUMBER/" >> assets/version
rm -rf assets/version.tmp