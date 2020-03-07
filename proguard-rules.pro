# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/anton/android-sdk-linux/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

### Keep Mockito
-dontwarn com.google.errorprone.annotations.*
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-dontwarn okhttp3.internal.platform.*
-dontwarn com.appsflyer.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }
-keep class greenely.greenely.** { *; }
-keep class com.shockwave.**
-keep class io.intercom.android.** { *; }
-keep class com.intercom.** { *; }

## Kepp Chart Animations
-keep public class com.github.mikephil.charting.animation.* {
    public protected *;
}

### Moshi
-keepclassmembers class ** {
  @com.squareup.moshi.FromJson *;
  @com.squareup.moshi.ToJson *;
}
-dontwarn javax.annotation.**
