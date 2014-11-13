![Cubes](/assets/assets/logo.png)
=============
<a href="https://travis-ci.org/ictrobot/Cubes"><img src="https://travis-ci.org/ictrobot/Cubes.svg?branch=master" alt="Build status" align="right"></a>
Summer project started in 2014 by Ethan Jones.

[***License***](/LICENSE)

[***Credits***](/CREDITS.md)

Building
--------

To customize the build, make a file called 'build.properties' in this folder and put the following lines:


To build signed android artifacts:
```
CUBES_KEYSTORE_FILE=[Keystore file]
CUBES_KEYSTORE_PASSWORD=[Keystore password]
CUBES_KEY_ALIAS=[Key alias]
CUBES_KEY_PASSWORD=[Key password]
```

To specify where the maven repo is:
```
CUBES_MAVEN_REPO=[Path here]
```
