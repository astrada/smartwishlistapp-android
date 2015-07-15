Smart Wish List
===============

This is the Android client for
[SmartWishList.net](https://www.smartwishlist.net).

The app lets you receive notifications when a price of a product in your wish
list drops below your target price.

You can install this app from [Google Play Store](https://play.google.com/store/apps/details?id=net.smartwishlist.smartwishlistapp).

How to build
------------

This app was developed with [Android Studio](http://developer.android.com/sdk/index.html).


### Dependencies

* [Volley](https://developer.android.com/training/volley/index.html)

### Get source and compile (Linux)

1. Clone `smartwishlistapp-android` repository:

        $ git clone https://github.com/astrada/smartwishlistapp-android.git

1. Go to the root project directory:

        $ cd smartwishlistapp-android

1. Clone `volley` repository (from smartwishlistapp-android root):

        $ git clone https://android.googlesource.com/platform/frameworks/volley

1. Create or edit `~/.gradle/gradle.properties` inserting your values:

        LocalApiUrlProp=
        LocalWebSiteUrlProp=
        DebugClientIdProp=
        DebugTokenProp=
        DebugDefaultRegionProp=
        DebugHasAccountProp=
        DebugNotificationsEnabledProp=
        GcmSenderIdProp=
        KeyAlias=
        KeyPassword=
        StoreFile=
        StorePassword=

1. Compile using gradle (debug target):

        $ ./gradlew assembleDebug

1. (or) Compile using gradle (release target):

        $ ./gradlew assembleRelease

