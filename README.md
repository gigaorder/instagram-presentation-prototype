## Update flow
<img src="https://i.imgur.com/KnqeCog.png" />

We gonna use a node server called [tinker server](https://github.com/longnguyen2/tinker-server) to serves patch apk file and to notify android devices whenever there is a new patch.  
To communicate between server and device, we use [Google FCM](https://firebase.google.com/docs/cloud-messaging) (firebase cloud messaging).  
Follow these steps to implement tinker patch:

## Import google-services.json
To run the project you need to create a firebase project and generate <b>google-service.json</b> for android app:
1. Go to [Firebase console](https://console.firebase.google.com/), click <b>Add project</b>  
    <img src="https://i.imgur.com/uNXKg5k.png" width="400" />
2. Enter your project name and finish creating project
3. In dashboard page, click on android icon  
    <img src="https://i.imgur.com/mpudbyL.png" width="400" />
4. Register app and download config file, then put it inside <b>app</b> folder of the project  
    <img src="https://i.imgur.com/GYPTry9.png" width="400" />

## Setup
1. Add tinker-gradle-plugin as a dependency in your main `build.gradle` in the root of your project  
Gradle version should be <b>3.1.3</b> to avoid warning message when building project (because tinker does not support for java 8 or higher)

    ```gradle
    buildscript {
        dependencies {
            classpath 'com.android.tools.build:gradle:3.1.3'
            classpath 'com.tencent.tinker:tinker-patch-gradle-plugin:1.9.14'
        }
    }
    ```

2. Then copy [tinker.gradle](https://github.com/gigaorder/instagram-presentation-prototype/blob/tinker-staging/instagram-app/app/tinker.gradle) to your app directory, 
    ```
    --project
        | -- app
              | --  tinker.gradle
              | --  build.gradle
    ```

    and apply to app's `build.gradle`
    ```gradle
    ...
    ...
    apply from: 'tinker.gradle'
    ```
3. In `gradle.properties` add field named <b>VERSION</b> (eg. VERSION=1.02) and TOPIC for firebase topic (eg. TOPIC=instagramPatching)  
 Then add <b>versionName</b> and <b>TOPIC</b> to default config in `build.gradle`
    ```gradle
    android {
        ...
        defaultConfig {
            ...
            versionName VERSION
            ...
            buildConfigField"String","TOPIC","\"${TOPIC}\""
        }
        ...
    }
    ```
4. Sync project
5. If your app has a class that subclasses android.app.Application, then you need to modify that class, and move all its implements to [InstagramApplicationLike](https://github.com/gigaorder/instagram-presentation-prototype/blob/tinker-staging/instagram-app/app/src/main/java/com/demo/instagram_presentation/InstagramApplicationLike.java) rather than Application:

    ```java
    -public class App extends Application {
    +public class InstagramApplicationLike extends DefaultApplicationLike {
    ```
    
    Use `tinker-android-anno` to generate your `Application` automatically, you just need to add an annotation for your [InstagramApplicationLike](https://github.com/gigaorder/instagram-presentation-prototype/blob/tinker-staging/instagram-app/app/src/main/java/com/demo/instagram_presentation/InstagramApplicationLike.java) class:
    
    ```java
    @DefaultLifeCycle(
    application = "com.demo.instagram_presentation.InstagramApplication",             //application name to generate
    flags = ShareConstants.TINKER_ENABLE_ALL)                                         //tinkerFlags above
    public class InstagramApplicationLike extends DefaultApplicationLike
    ```

6. Before update patch using tinker, you need to call `install` method first. For example, in [InstagramApplicationLike](https://github.com/gigaorder/instagram-presentation-prototype/blob/tinker-staging/instagram-app/app/src/main/java/com/demo/instagram_presentation/InstagramApplicationLike.java):
    ```java
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        LoadReporter loadReporter = new DefaultLoadReporter(appLike.getApplication());
        PatchReporter patchReporter = new DefaultPatchReporter(appLike.getApplication());
        PatchListener patchListener = new DefaultPatchListener(appLike.getApplication());
        AbstractPatch upgradePatchProcessor = new UpgradePatch();
    
        TinkerInstaller.install(appLike,
            loadReporter, patchReporter, patchListener,
            DefaultTinkerResultService.class, upgradePatchProcessor);
    }
    ```
## Patching using firebase messaging service
1. Add `classpath 'com.google.gms:google-services:4.2.0'` in `build.gradle` in the root project
    ```gradle
    buildscript {
        dependencies {
            ...
            classpath 'com.google.gms:google-services:4.2.0'
        }
    }
    ```
2. In app's `build.gradle`, add `apply plugin: 'com.google.gms.google-services'` at the bottom
3. Add firebase plugin
    ```gradle
    dependencies {
        ...
        implementation 'com.google.firebase:firebase-messaging:20.0.0'
        implementation 'com.google.firebase:firebase-core:17.2.0'
    }
    ```
4. Sync project
5. In `MainActivity` register the firebase topic that has been configured before to communicate with server
    ```java
    FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.TOPIC);
    ```
6. Create a class PatchingService extends FirebaseMessagingService
7. Override `onMessageReceived` method, this is where devices communicate with tinker server  
    You should call patching method here:
    ```java
    //apkPath specify where the patch apk stored in device
    TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), apkPath); 
    ```
   PatchingService example: [PatchingService](https://github.com/gigaorder/instagram-presentation-prototype/blob/tinker-staging/app/src/main/java/com/demo/instagram_presentation/hotfix_plugin/PatchingService.java)
8. Finally add service with `MESSAGING_EVENT` intent-filter in `AndroidManifest.xml`:
    ```xml
    <application>
        ...
        <service
            android:name=".hotfix_plugin.PatchingService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
    </application>
    ```

## Build tinker patch
<b>Before build</b>: By default each time you run project or build project apk, [tinker.gradle](https://github.com/gigaorder/instagram-presentation-prototype/blob/tinker-staging/instagram-app/app/tinker.gradle) script will help move all resource files to `app/build/bakApk` directory and name it according to source version (eg: 1.02).  
Remember to store these files <b>somewhere</b> else because you will need them in the next step.

<img src="https://i.imgur.com/axvvfd5.png" width="400"/>
  
1. Tinker will build update patch based on the original resource files (.apk, -R, -mapping) which means for each version you have to build a corresponding update patch so that android devices can apply patch without error.  

    Move resource files that are needed to build patch to <b>originalBuild</b> folder and rename them to app.apk, app-R.txt, app-mapping.txt …
    ```
    --project
        | -- app
        | -- originalBuild/
                | --  app-R.txt
                | --  app.apk
    ```
2. Run command `./gradlew tinkerPatchDebug` in project root directory  
or run `tinkerPatchDebug` in android studio's gradle tab  
   <img src="https://i.imgur.com/xq7RbRi.png" width="400"/>

3. Setup [tinker-server](https://github.com/longnguyen2/tinker-server)
4. Install `sshpass`
    - mac: `brew install https://raw.githubusercontent.com/kadwanev/bigboybrew/master/Library/Formula/sshpass.rb`
    - ubuntu: `sudo apt-get install sshpass`
    - centos: `yum install sshpass`
5. open file `ssh.cfg` in root project and config to your host server  
For example:
    ```
    domain=192.168.1.1
    usr=root
    pwd=root
    serverDir=/home/root/tinker-server
    ```
    - <b>domain</b>: host domain
    - <b>usr</b>: host username
    - <b>pwd</b>: host password
    - <b>src</b>: path to android project

6. Run command `./copyPatch -v <version> -t <topic>` in root folder of project to automatically upload patch to server.
    Note: `version` is original apk version, `topic` is firebase topic
    For example: `./copyPatch -v 1.02 -t instagramPatching`      
    <span style="color: red">You need to repeat step 1-6 for every version's patch.</span>
7. Finally, if you want to notify devices to update new patch, run command `./notifyUpdate <topic>` (`topic` is firebase topic) in root folder
    For example `./notifyUpdate instagramPatching`