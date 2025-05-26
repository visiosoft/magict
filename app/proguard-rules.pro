# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep JSON-related classes
-keep class upworksolutions.themagictricks.data.TricksResponse { *; }
-keep class upworksolutions.themagictricks.model.Trick { *; }
-keep class upworksolutions.themagictricks.data.TrickDataProvider { *; }

# Keep Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep assets
-keep class **.R$* { *; }
-keepclassmembers class **.R$* { *; }

# Prevent minification of JSON files in assets
-keep class upworksolutions.themagictricks.data.** { *; }
-keepclassmembers class upworksolutions.themagictricks.data.** { *; }

# Keep all JSON files in assets directory
-keep class upworksolutions.themagictricks.data.TricksResponse
-keep class upworksolutions.themagictricks.data.CategoriesResponse
-keep class upworksolutions.themagictricks.data.QuotesResponse

# Keep all model classes that are used in JSON parsing
-keep class upworksolutions.themagictricks.model.** { *; }
-keepclassmembers class upworksolutions.themagictricks.model.** { *; }

# Keep all data provider classes
-keep class upworksolutions.themagictricks.data.** { *; }
-keepclassmembers class upworksolutions.themagictricks.data.** { *; }

# Exclude specific JSON files from minification
-keep class upworksolutions.themagictricks.data.TricksResponse
-keep class upworksolutions.themagictricks.data.OfflineTricksResponse
-keepclassmembers class upworksolutions.themagictricks.data.TricksResponse { *; }
-keepclassmembers class upworksolutions.themagictricks.data.OfflineTricksResponse { *; }

# Keep JSON file names
-keepclassmembers class upworksolutions.themagictricks.data.** {
    public static final String tricks_json;
    public static final String offlinetricks_json;
}