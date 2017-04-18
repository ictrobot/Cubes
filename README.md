![Cubes](/assets/assets/logo.png)
=============
A simple voxel game featuring single player, multiplayer and endless procedurally generated terrain.

This branch stores the code for the cut down browser version. [Try it here](https://cubes.ethanjones.me/play/)

[***License***](/LICENSE) | [***Credits***](/CREDITS.md) | [***Changes***](/CHANGES.md) | [***Builds***](https://cubes.ethanjones.me/)

Screenshots
-------
![Screenshot 1](/screenshots/screenshot1.png)
![Screenshot 2](/screenshots/screenshot2.png)

Mods
--------
For sample json and lua mods, see mod_sample/json and mod_sample/lua respectively. [CubesSampleMod](https://github.com/ictrobot/CubesSampleMod) is a sample java mod, which uses [CubesModPlugin](https://github.com/ictrobot/CubesModPlugin), a gradle plugin to build .cm files automatically. [CubesEquationTerrainGenerator](https://github.com/ictrobot/CubesEquationTerrainGenerator) is an example of a mod that actually does something (add terrain generators based off mathematical expressions).  

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
