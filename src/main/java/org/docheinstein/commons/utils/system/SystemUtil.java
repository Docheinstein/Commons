package org.docheinstein.commons.utils.system;

import org.docheinstein.commons.utils.types.StringUtil;

import java.util.Locale;

/**
 * Provides utilities for the system
 */
public class SystemUtil {

    /**
     * Returns the current operating system type this JVM runs on.
     * @return the current operating system
     */
    public static OSType getCurrentOperatingSystemType() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

        if (!StringUtil.isValid(osName))
            return OSType.Other;

        if (osName.contains("mac") || osName.contains("darwin"))
            return OSType.Mac;

        if (osName.contains("win"))
            return OSType.Windows;

        if (osName.contains("nux"))
            return OSType.Linux;

        return OSType.Other;
    }
}
