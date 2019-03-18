# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep public class com.longfocus.webcorepresence.dashboard.Registration {
    <fields>;
    <methods>;
}
-keep public class com.longfocus.webcorepresence.dashboard.client.* {
    <fields>;
    <methods>;
}
-keep public class com.longfocus.webcorepresence.dashboard.js.* {
    <fields>;
    <methods>;
}
-keep public class com.longfocus.webcorepresence.smartapp.request.* {
    <fields>;
    <methods>;
}
-keep public class com.longfocus.webcorepresence.smartapp.response.* {
    <fields>;
    <methods>;
}

-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}