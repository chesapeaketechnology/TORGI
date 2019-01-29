package org.sofwerx.torgi.util;

import android.content.Context;
import android.content.pm.PackageManager;

public class PackageUtil {
    public final static String PACKAGE_LOGGER = "org.sofwerx.torgi.logger";

    public static boolean doesPackageExist(Context context, String packageToFind){
        if ((context != null) && (packageToFind != null)){
            PackageManager pm = context.getPackageManager();
            try {
                pm.getPackageInfo(packageToFind, PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
            return true;
        } else
            return false;
    }

    public static boolean isSensorAppInstalled(Context context) {
        return doesPackageExist(context, PACKAGE_LOGGER);
    }
}
