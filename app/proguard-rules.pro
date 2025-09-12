# Keep security-relarted classes and methods from being obfuscated
-keep class org.obsidiandirectorate.omot.security.** { *; }
-keepclassmembers class org.obsidiandirectorate.omot.security.** { *; }

# Keep logging for security events
-keepclassmembers class * {
    private static final *** LOG;
}