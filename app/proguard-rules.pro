# ProGuard rules for E-ink Calendar

# Keep application classes
-keep class com.eink.calendar.** { *; }

# Keep Kotlin classes
-keepclassmembers class com.eink.calendar.** {
    <init>(...);
}

# Keep data classes
-keep class com.eink.calendar.domain.model.** { *; }
-keep class com.eink.calendar.data.** { *; }

# Keep viewmodels
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep Hilt injected classes
-keep class dagger.hilt.** { *; }
-keep class hilt_aggregated_deps.** { *; }

# Keep enums
-keepclassmembers enum ** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep data class generated methods
-keepclassmembers class **$*Impl {
    *;
}

# Remove logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Retrofit
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Gson
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Android X
-keep class androidx.** { *; }
-dontwarn androidx.**

# Remove build-in assertion errors
-assumevalues class androidx.databinding.GeneratedBindings {
    *;
}

# Optimization
-optimizationpasses 5
-dontobfuscate
