# R8 / ProGuard rules for MoneyMind AI by Hastradar

# Preserve Compose annotations & UI state
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# Preserve Room Database Models & SQLCipher
-keep class com.finly.core.data.local.entity.** { *; }
-keep class net.sqlcipher.** { *; }
-dontwarn net.sqlcipher.**

# Preserve Google Gemini AI SDK models
-keep class com.google.ai.client.generativeai.** { *; }
-dontwarn com.google.ai.client.generativeai.**

# Preserve Dagger Hilt DI bindings
-keep class * extends dagger.hilt.internal.UnstableApi
-keepclassmembers,allowobfuscation class * {
    @dagger.hilt.InstallIn <fields>;
}

# Keep Coroutines
-keepclassmembers class kotlinx.coroutines.** { *; }
