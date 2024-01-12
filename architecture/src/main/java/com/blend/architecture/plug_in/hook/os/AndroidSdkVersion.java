package com.blend.architecture.plug_in.hook.os;

import android.os.Build;

/**
 * 对于系统版本的判断
 */
public class AndroidSdkVersion {

    /**
     * API Level 21 --- Android 5.0
     * API Level 22 --- Android 5.1
     * API Level 23 --- Android 6.0
     * API Level 24 --- Android 7.0
     * API Level 25 --- Android 7.1.1
     * API Level 26 --- Android 8.0
     * API Level 27 --- Android 8.1
     * API Level 28 --- Android 9.0
     */

    /**
     * 判断当前系统版本 26 27 28
     * @return
     */
    public static boolean isAndroidOS_26_27_28_30() {
        int V = Build.VERSION.SDK_INT;
        if ((V > 26 || V == 26) && (V < 30 || V == 30)) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前系统版本 21 22 23 24 25 以及 21版本之下的
     * @return
     */
    public static boolean isAndroidOS_21_22_23_24_25() {
        int V = Build.VERSION.SDK_INT;
        if (V < 26) {
            return true;
        }
        return false;
    }
}
