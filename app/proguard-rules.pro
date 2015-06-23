# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /opt/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# Needed by Fabric Crashalytics
-keepattributes SourceFile,LineNumberTable

# Needed by WebView with JS
-keepclassmembers class net.smartwishlist.smartwishlistapp.WebAppInterface {
    public *;
}

-dontwarn sun.misc.Unsafe
