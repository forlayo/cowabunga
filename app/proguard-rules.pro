# RxJava
-keep class rx.** {*; }
-dontwarn rx.internal.util.unsafe.**

-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}

# Support
-dontwarn android.support.**
-dontwarn android.support.design.**
-dontwarn android.support.v7.**
-keep class android.support.design.** { *; }
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }
-keep class com.android.vending.billing.**

# Logs
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int w(...);
    public static int d(...);
}

# Retrolambda
-dontwarn java.lang.invoke.*
