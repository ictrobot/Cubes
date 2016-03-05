![Cubes](/assets/assets/logo.png)
=============
<a href="https://ictrobot.duckdns.org/jenkins/job/Cubes/"><img src="https://ictrobot.duckdns.org/jenkins/buildStatus/icon?job=Cubes" alt="Build status" align="right"></a>
Summer project started in 2014 by Ethan Jones.

[***License***](/LICENSE)

[***Credits***](/CREDITS.md)

Building
--------

To customize the build, make a file called 'build.properties' in this folder and put the following lines:


To build signed android artifacts:
```
ANDROID_KEYSTORE_FILE=[Keystore file]
ANDROID_KEYSTORE_PASSWORD=[Keystore password]
ANDROID_KEYSTORE_KEY_ALIAS=[Key alias]
ANDROID_KEYSTORE_KEY_PASSWORD=[Key password]
```

To specify maven repo path and authentication:
```
MAVEN_REPO_PATH=[Path]
MAVEN_REPO_USERNAME=[Username]
MAVEN_REPO_PASSWORD=[Password]
```
