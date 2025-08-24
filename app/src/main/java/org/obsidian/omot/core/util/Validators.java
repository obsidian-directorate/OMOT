package org.obsidian.omot.core.util;

public class Validators {

    public static boolean isCodenameValid(String codename) {
        return codename != null && codename.length() >= 3 && codename.length() <= 20;
    }

    public static boolean isClearanceValid(String code) {
        return code != null && (code.equals("BETA") || code.equals("ALPHA")
                || code.equals("OMEGA") || code.equals("SHADOW"));
    }

    public static boolean isCipherKeyStrong(String cipherKey) {
        if (cipherKey == null) return false;
        return cipherKey.length() >= 8;
    }
}