package org.osd.omot_app.security;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Debug;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for detecting security threats like root access, debuggers, and unofficial installations.
 */
public class SecurityChecker {

    private static final String TAG = "SecurityChecker";

    // Common paths where root binaries might be found
    private static final List<String> ROOT_INDICATORS = Arrays.asList(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
    );

    // Common package names of root management apps
    private static final List<String> ROOT_APP_PACKAGES = Arrays.asList(
            "com.noshufou.android.su",
            "com.thirdparty.superuser",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.zachspong.temprootremovejb",
            "com.ramdroid.appquarantine"
    );

    /**
     * Checks if the device is rooted using multiple detection methods.
     */
    public static boolean isDeviceRooted(Context context) {
        return checkRootBinaries() || checkRootApps(context) || checkSuperuserApk() || checkDangerousProps();
    }

    /**
     * Checks for common root binary files.
     */
    private static boolean checkRootBinaries() {
        for (String path : ROOT_INDICATORS) {
            if (new File(path).exists()) {
                Log.w(TAG, "Root binary detected: " + path);
                return true;
            }
        }
        return false;
    }

    /**
     * Check for known root management applications.
     */
    private static boolean checkRootApps(Context context) {
        PackageManager pm = context.getPackageManager();
        for (String packageName : ROOT_APP_PACKAGES) {
            try {
                pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                Log.w(TAG, "Root management app detected: " + packageName);
                return true;
            } catch (PackageManager.NameNotFoundException e) {
                // App not found, which is good
            }
        }
        return false;
    }

    /**
     * Checks for the presence of Superuser.apk.
     */
    private static boolean checkSuperuserApk() {
        String[] paths = {
                "/system/app/Superuser.apk",
                "/system/app/superuser.apk",
                "/system/app/SuperUser.apk",
                "/system/app/superuser/Superuser.apk"
        };

        for (String path : paths) {
            if (new File(path).exists()) {
                Log.w(TAG, "Superuser.apk detected: " + path);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks for dangerous system properties that indicates root access.
     */
    private static boolean checkDangerousProps() {
        String buildTags = Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            Log.w(TAG, "Test keys detected in build tags - possible custom ROM");
            return true;
        }
        return false;
    }

    /**
     * Checks if a debugger is attached to the process.
     */
    public static boolean isDebuggerAttached() {
        boolean isDebuggerActivated = Debug.isDebuggerConnected();
        if (isDebuggerActivated) {
            Log.w(TAG, "Debugger detected attached to process");
        }
        return isDebuggerActivated;
    }

    /**
     * Checks if the app is installed from an official source (Google Play Store).
     * Note: This is a basic check and can be bypassed.
     */
    public static boolean isInstalledFromOfficialStore(Context context) {
        try {
            String installerPackage =
                    context.getPackageManager().getInstallerPackageName(context.getPackageName());
            boolean isPlayStore = "com.android.vending".equals(installerPackage) || ("com.google" +
                    ".android.feedback").equals(installerPackage);
            
            if (isPlayStore) {
                Log.w(TAG, "App not installed from Play Store. Installer: " + installerPackage);

                // For development, don't treat this as a threat if we're on emulator or debug build
                if (isRunningOnEmulator() || isAppDebuggable(context)) {
                    Log.i(TAG, "Allowing non-Play Store installation for development");
                    return true;
                }
            }
            return isPlayStore;
        } catch (Exception e) {
            Log.e(TAG, "Error checking installation source", e);
            return false; // Err on the side of caution
        }
    }

    /**
     * Checks if the app is running in an emulator.
     */
    public static boolean isRunningOnEmulator() {
        return Build.FINGERPRINT.startsWith("generic") ||
                Build.FINGERPRINT.startsWith("unknown") ||
                Build.MODEL.contains("google_sdk") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK built for x86") ||
                Build.MANUFACTURER.contains("Genymotion") ||
                (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||
                "google_sdk".equals(Build.PRODUCT);
    }

    /**
     * Checks if the app is debuggable (should be false in release builds).
     */
    public static boolean isAppDebuggable(Context context) {
        boolean isDebuggable =
                (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        if (isDebuggable) {
            Log.w(TAG, "App is running in debug mode");
        }
        return isDebuggable;
    }

    /**
     * Checks if the current build is a debug build.
     * This is a safer alternative to BuildConfig.DEBUG that doesn't require import.
     */
    public static boolean isDebugBuild(Context context) {
        try {
            // Use reflection to check BuildConfig without importing it
            Class<?> buildConfig = Class.forName(context.getPackageName() + ".BuildConfig");
            boolean isDebug = buildConfig.getField("DEBUG").getBoolean(null);
            Log.d(TAG, "BuildConfig.DEBUG: " + isDebug);
            return isDebug;
        } catch (Exception e) {
            // If we can't access BuildConfig, fall back to the debuggable flag
            Log.d(TAG, "Cannot access BuildConfig, using debuggable flag instead");
            return isAppDebuggable(context);
        }
    }

    /**
     * Comprehensive security check that combines all detection methods.
     * @return true if any security threat is detected, false if the environment is secure.
     */
    public static boolean detectSecurityThreats(Context context, boolean isDevelopmentEnvironment) {
        boolean threatDetected = false;
        
        if (isDeviceRooted(context)) {
            Log.e(TAG, "ROOT ACCESS DETECTED - Security threat!");
            threatDetected = true;
        }

        if (isDebuggerAttached()) {
            // Allow debugger in development environment
            if (!isDevelopmentEnvironment) {
                Log.e(TAG, "DEBUGGER DETECTED - Security threat!");
                threatDetected = true;
            } else {
                Log.w(TAG, "Debugger detected but allowed in development environment");
            }
        }

        if (isAppDebuggable(context)) {
            // Allow debugger in development environment
            if (!isDevelopmentEnvironment) {
                Log.w(TAG, "App running in debug mode - Potential security risk");
                threatDetected = true;
            } else {
                Log.i(TAG, "App running in debug mode - Allowed in development");
            }
        }
        
        if (!isInstalledFromOfficialStore(context)) {
            // Allow unofficial installations in development environment
            if (!isDevelopmentEnvironment) {
                Log.w(TAG, "Unofficial installation source detected - Potential security risk");
                threatDetected = true;  // Treat this as a threat for sensitive apps
            } else {
                Log.i(TAG, "Unofficial installation - Allowed in development");
            }
        }

        if (isRunningOnEmulator()) {
            // Allow emulator in development environment
            if (!isDevelopmentEnvironment) {
                Log.w(TAG, "Running on emulator - Environment not trusted");
                threatDetected = true;
            } else {
                Log.i(TAG, "Running on emulator - Allowed in development");
            }
        }

        return threatDetected;
    }

    /**
     * Performs a security self-check and takes appropriate action if threats are detected.
     * @return true if the environment is secure, false if threats were detected and handled.
     */
    public static boolean performSecurityCheck(Context context,
                                               SecurePreferencesManager securePreferences) {
        boolean isDevelopment = isDebugBuild(context) || isRunningOnEmulator();
        boolean hasThreats = detectSecurityThreats(context, isDevelopment);

        if (hasThreats && !isDevelopment) {
            Log.e(TAG, "SECURITY BREACH DETECTED - Initiating protective measures");

            // Wipe sensitive data
            if (securePreferences != null) {
                securePreferences.clearAllPreferences();
                Log.i(TAG, "Sensitive data wiped due to security threat");
            }

            return false;
        }

        if (hasThreats) {
            Log.w(TAG, "Security threats detected but allowed in development environment");
        } else {
            Log.i(TAG, "Security check passed - Environment is secure");
        }

        return true;
    }
}