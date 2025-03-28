# 保留 Annotation 不混淆
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

# Room 数据库
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.**

# ViewModel and LiveData
-keep class androidx.lifecycle.** { *; }
-keep class * extends androidx.lifecycle.ViewModel
-keep class * extends androidx.lifecycle.AndroidViewModel
-dontwarn androidx.lifecycle.**

# MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }
-dontwarn com.github.mikephil.charting.**

# Apache Commons CSV
-keep class org.apache.commons.csv.** { *; }
-dontwarn org.apache.commons.csv.**

# 保留自定义的实体类
-keep class com.bloodpressure.data.** { *; }
-keep class com.bloodpressure.model.** { *; }

# 保留 Android 四大组件
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference 