# CUE Audio Integration Demo
This demo project shows you how to integrate the CUE Audio Live Event SDK into your application.

## Setup
First, make sure you get CUE Audio Maven credentials for your company or project. Add credentials and ULR to Maven bucket to `local.properties` at root of project:

```properties
com.cueaudio.maven.bucket=https://cueaudio.jfrog.io/cueaudio/libs-release-local
com.cueaudio.maven.username=<myusername>
com.cueaudio.maven.password=<mypassword>
```

Secondly, check the CUE Audio Maven repository `build.gradle` file at root of project and add the following code if it's not present:
```groovy
// Reading Maven credentials
Properties properties = new Properties()
properties.load(project.rootProject.file('local.properties').newDataInputStream())

allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url "https://jitpack.io"
        }
        maven {
            url = properties.getProperty("com.cueaudio.maven.bucket")
            credentials {
                username = properties.getProperty("com.cueaudio.maven.username")
                password = properties.getProperty("com.cueaudio.maven.password")
            }
        }
    }
}
```

## Linking
After CUE Audio Maven setup finished, you can add CUE library as dependency of your `app` project:
```groovy
dependencies {
    implementation 'com.cueaudio:cuelive:3.+'
}
```
#### If CUE has provided you with a customed-library, use it as dependency artifact id: `implementation 'com.cueaudio:custom:3.0.0'`

> Note: always use the latest version of CUE library to get most recent features and fixes

> **Note: If you are using Android X, use version 3.+ instead of 2.+.**

## Integration
1. In your Manifest, include:
```xml
<activity android:name="com.cueaudio.live.CUEActivity"
             android:theme="@style/CUEAppTheme" />
```

1. If you are using a custom design library, in app `gradle` file, replace the line: `implementation project(':cue-design')` with the name of the client design library: `implementation project(':myClient-design')`.

1. Finally, in your fragment or activity, start `CUEActivity` and we'll handle the rest.

  ```java
  final Intent i = new Intent(this, CUEActivity.class);
  i.putExtra(CUEActivity.EXTRA_CUE_ENABLE_NAVIGATION_MENU, true);
  startActivity(i);
  ```

CUEActivity.EXTRA_CUE_ENABLE_NAVIGATION_MENU activity extra flag regulates whether in-built navigation menu will be shown or not. This extra is optional. By default it is set to true.

> **Note: If you are building your own GUI library from scratch, in your `res/values/strings.xml` file, include the resource:
`<string name="cue_client_id">{ClientId}</string>`**

## Configuration
CUE parameters can be overwritten and customized for your application. You can overwrite these parameters in `strings.xml` and `colors.xml` files in your project.

> Note: If you overwrite CUE parameters in your project and also include a themed SDK other than the default `cuelive`, this may cause incorrect behavior. Therefore, always ensure that, if you overwrite critical values like `apiKey` in your project, these values are set correctly for your client.

### Initiating triggers from push notifications or on an app start
If you want to initiate the trigger from Push Notification or on an app start, use the `CUEActivity.EXTRA_CUE_TRIGGER` extra while setting the `CUEActivity` intent:
```java
i.putExtra(CUEActivity.EXTRA_CUE_TRIGGER, "311.65.106");
```

### Trivia prizes overriding
If you want to disable the Trivia prizes for loser/winner users, use next flags while launching `CUEActivity`:
```java
i.putExtra(CUEActivity.EXTRA_IGNORE_TRIVIA_LOSER_PRIZE, true);
i.putExtra(CUEActivity.EXTRA_IGNORE_TRIVIA_WINNER_PRIZE, true);
```

### Trivia events broadcast receiver
Trivia completed / canceled events are sent within LocalBroadcastReceiver with an actions:
`CUETriviaBroadcastReceiver.ACTION_TRIVIA_COMPLETE`/`CUETriviaBroadcastReceiver.ACTION_TRIVIA_CANCELED`
Use next code to subscribe for events:
```java
private final CUETriviaBroadcastReceiver triviaEventReceiver = new CUETriviaBroadcastReceiver() {
    @Override
    public void onTriviaComplete(int score, int maxScore) {
        Log.d(TAG, "onTriviaComplete: score=$score maxScore=$maxScore");
    }

    @Override
    public void onTriviaCanceled() {
        Log.d(TAG, "onTriviaCanceled");
    }
};

final IntentFilter filter = new IntentFilter();
filter.addAction(CUETriviaBroadcastReceiver.ACTION_TRIVIA_COMPLETE);
filter.addAction(CUETriviaBroadcastReceiver.ACTION_TRIVIA_CANCELED);
context.registerReceiver(triviaEventReceiver, filter);
```
Don't forget to unsubbscribe when you don't need to listen for event anymore:
```java
context.unregisterReceiver(triviaEventReceiver);
```

### API Key
The `cue_client_api_key` key is what uniquely identifies a client. It is 32 alpha-numeric characters. This API Key must be the same for a client on both iOS and Android platforms.

The API Key also lets CUE modify the branding of a client remotely (on versions 2.3.0+). This way, if you are including the CUE SDK as a feature flag in multiple apps, you can simply use the `-Default` theme and do not need to provide custom branding in advance. Once a client purchases CUE services, we can update the SDK to a custom theme remotely. All you need to do is:

(1) Ensure that the same unique API Key overwrites the default API Key within your `strings.xml` file within your project:

```
<string name="cue_client_api_key">my_32_alpha_numeric_string</string>
```

(2) Fetch the CUE Theme associated with that API Key. You can do this in `Application.onCreate()`:

``` java
CUEController.fetchCueTheme(this);
```

If you store your API Key remotely rather than hardcode each client, you can pass the key in as a parameter. First, fetch the correct theme when you launch your app:

``` java
CUEController.fetchCueTheme(this, <#myClientApiKey#>);
```

Next, launch this client's GUI:

```
final Intent i = new Intent(this, CUEActivity.class);
        i.putExtra(CUEActivity.EXTRA_CUE_API_KEY, <#myClientApiKey#>);
        startActivity(i);
```

Note: **If you pull your client API Key from the server**, it should be cached (e.g., `UserDefaults`) so that the user can access the light show even without a network connection.

### Using EXTRA_CUE_PRIVACY flag

You can pass optional EXTRA_CUE_PRIVACY flag to prevent collecting and sending to the server any user information. SDK initialization in this case looks like that:

```java
    private void launchApp() {
        final Intent i = new Intent(this, CUEActivity.class);
        i.putExtra(CUEActivity.EXTRA_CUE_PRIVACY, true);
        startActivity(i);
    }
```

### CUEActivity theme
If you need different navigation bar color or controls primary color for `CUEActivity`, override colors in `colors.xml`:

``` xml
<color name="cueColorPrimary">#ffffff</color>
<color name="colorPrimaryDark">#1a1e22</color>
<color name="colorAccent">#40c6ff</color>
```

Alternatively the whole theme may be overridden by creating new one with `CUEAppTheme` as parent:
``` xml
<style name="MyAppTheme" parent="@style/CUEAppTheme">
    <!-- Customize your theme here. -->
</style>
```
and set to `CUEActivity` in `AndroidManifest.xml`
```xml
<activity
    android:name="com.cueaudio.live.CUEActivity"
    android:theme="@style/MyAppTheme" />
```

> Note: For more information read [Developers Android documentation](https://developer.android.com/guide/topics/ui/look-and-feel/themes)

### Primary Color
Overwrite in `colors.xml` with the following:

```
<color name="cue_primary_color">#00AEFF</color>
```

`cue_primary_color` is the theme color of the CUE SDK. This can be configured by CUE remotely as long as this app utilizes a unique API Key.

### Navigation Header Images
The image at the top of the CUE navigation menu. Overwrite by including an image titled `cue_header.png` in your `drawables` directory.

Include or overwrite the image or resource `cue_header_background.jpg` to change the background of the navigation header.

### Display name on onboarding

Overwrite the name that appears on the first page of the CUE Onboarding by specifying `cue_app_name` in your `R.strings`.

### Facebook Fresco image library
If you use the Facebook library Fresco within your project and initialize Fresco at the start of your application, set `cue_use_fresco` string resource to `false`:
```xml
<string name="cue_use_fresco">false</string>
```
This ensures that Fresco is not initialized twice.

### Sentry
If you are using [Sentry](https://sentry.io) in your project, you will need to initialize Sentry with the CueAudio library.
To initialize Sentry use method below with your own Sentry DNS key:
 ```kotlin
  CUEInit.initSentry(dns: String)
 ```

# QA

## Manual Script

* Once the CUE SDK is integrated and your project successfully compiles, launch the CUE portion of your app.

* On the homescreen, please press the camera button to take a photo. Next, hold the button for at least three seconds to take a video. The photo and video should save successfully to your photos.

* In the navigation menu, make sure the correct menu items are listed.
	* Live
	* Demo
	* Help
	* Info
	* Exit (iOS only)

* Next, go to `Demo`. Complete the demo for all the services listed, which can be up to three items: `Light Show`, `Selfie Cam`, and `Trivia`.

* For `Light Show`, your device should play an audio file and execute a demo light show in sync with the music, using the smartphone torch and screen.

* For `Selfie Cam`, your device should initiate a countdown sequence while displaying the front-facing camera feed. Upon the countdown reaching `0`, you should be able to successfully **Save**, **Retake**, and **Share** your selfie. Please try all three features, although it is not necessary to actually share the selfie. You can simply ensure that the OS-specific share activity successfully launches.

* For `Trivia`, your device should show a list of rules for a few seconds before launching a trivia game. Upon the completion of the game, your rank will be calculated. Since this is a demo game and not a live game, a generic "loser" message will be displayed after ~45 seconds.

* Finally, if CUE has provided you specific, additional audio files to test, please play these from a nearby computer or speaker while your CUE app is open to `Live`. Your device should sync to the music. These custom tracks are client-specific, so a track prepared for one client will not work for another client unless explicitly designed to by CUE. To confirm that this track is for the correct client, make sure that client-specific information is displayed during the light show, such as the client's color scheme or logo.

## Update Instructions

Simply make sure the right version of CUE is specified in your `build.gradle` file:

```groovy
dependencies {
    implementation 'com.cueaudio:cuelive:3.+'
}
```

## Troubleshooting

### Error: Invoke-customs are only supported starting with Android O (--min-api 26)
**FIX:** Add the support for Java 1.8 to app's build.gradle:
```groovy
android {
    ...

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```
