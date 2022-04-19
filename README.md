# PictureInPicture in Android (Kotlin)

## Introduction

As of Android O, activities can launch in [Picture-in-Picture (PiP)](https://developer.android.com/guide/topics/ui/picture-in-picture) mode. PiP is a special type of multi-window mode mostly used for video playback.
To enable pip in your activity you can , Inline `set android:supportsPictureInPicture` to Inline `true` in the manifest. (Beginning with the O Developer Preview, you do not need to Inline `set android:resizeableActivity` to Inline `true` if you are supporting PIP mode, either on Android TV or on other Android devices; you only need to Inline `setrandroid:resizeableActivity` if your activity supports other multi-window modes.)

## Pre-requisites
- Android SDK 26
- Android Build Tools v26.0.2
