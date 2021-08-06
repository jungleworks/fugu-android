## Fugu is an open source, private cloud, Slack-alternative from https://jungleworks.com/fugu/
___

# Android application for FuguChat

### Make the following changes in the mentioned files

##### /app/build.gradle
```gradle
    def appName = 'FuguChat' // Change your App Name here
    defaultConfig {
        applicationId "com.officechat" // Change your Product ID here
        ...
        manifestPlaceholders = [fabric_key: ''] // Add your crashlytics fabric key here
        manifestPlaceholders = [branchKey: ""] // Add your live branch key here
        ...
    }
    ...
    buildTypes {
        debug {
            ...
            manifestPlaceholders = [branchKey: ""] // Add your test branch key here
        }
    }
```

##### app/src/main/java/com/skeleton/mvp/constant/FuguAppConstant.java
```java
    String LIVE_SERVER = "https://openapi.fuguchat.com/api/"; // Change 'openapi.fuguchat.com' to the api pointing of your fugu-server
    String LIVE_SERVER_OC = "https://openapi.fuguchat.com/api/"; // Change 'openapi.fuguchat.com' to the api pointing of your fugu-server
    String SOCKET_LIVE_SERVER = "https://openapi.fuguchat.com"; // Change 'openapi.fuguchat.com' to the api pointing of your fugu-server

    String CONFERENCING_LIVE_NEW = "https://meet.jit.si"; // Change this to your own jitsi-meet instance url

    String DOMAIN_URL_LIVE = "fuguchat.com"; // Change your domain name here (Can be found in domain_credentials.domain table)

    String APP_NAME_SHORT = "Fugu"; // Replace with your AppName
```

##### app/src/main/AndroidManifest.xml
```xml
    <application
        android:name=".MyApplication"
        android:finishOnTaskLaunch="true"
        .... >
        ...
        <activity
            android:name=".activity.MainActivity"
            android:label="@string/app_name"
            android:launchMode="standard"
            android:screenOrientation="portrait">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:scheme="fuguchat" />      <!--Change this scheme to your appname-->
            </intent-filter>
        </activity>
        ...
        <activity
            android:name=".activity.VideoConfActivity"
            android:launchMode="singleTask"
            android:resizeableActivity="true"
            ... >
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.BROWSABLE" />
                <category android:name="android.intent.category.DEFAULT" />
                <data
                    android:scheme="https"
                    android:host="meet.jit.si" />     <!-- Change this to your own jitsi-meet instance url -->
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data
                    tools:ignore="AppLinkUrlError"
                    android:scheme="fuguchat"/>        <!-- Change this to your app name -->

            </intent-filter>
        </activity>
        ...
        <meta-data
            android:name="io.fabric.ApiKey"
            android:value="Add your crashlytics fabric key here" /> <!-- Add your crashlytics fabric key here if needed -->
```

##### app/src/main/res/values/strings.xml
```java
    <!DOCTYPE resources
        [<!ENTITY appname "FuguChat"> // Place your app's full name
        <!ENTITY fugu "Fugu">] // Place short name here
        >
```

##### app/src/main/res/values-hdpi/strings.xml
```java
    <!DOCTYPE resources
        [<!ENTITY appname "FuguChat"> // Place your app's full name
        <!ENTITY fugu "Fugu">] // Place short name here
        >
```

##### app/src/main/res/values-mdpi/strings.xml
```java
    <!DOCTYPE resources
        [<!ENTITY appname "FuguChat"> // Place your app's full name
        <!ENTITY fugu "Fugu">] // Place short name here
        >
```

##### app/src/main/res/values-xhdpi/strings.xml
```java
    <!DOCTYPE resources
        [<!ENTITY appname "FuguChat"> // Place your app's full name
        <!ENTITY fugu "Fugu">] // Place short name here
        >
```

##### app/src/main/res/values-xxhdpi/strings.xml
```java
    <!DOCTYPE resources
        [<!ENTITY appname "FuguChat"> // Place your app's full name
        <!ENTITY fugu "Fugu">] // Place short name here
        >
```
