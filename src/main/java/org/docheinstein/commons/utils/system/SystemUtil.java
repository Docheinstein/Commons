package org.docheinstein.commons.utils.system;

import org.docheinstein.commons.utils.types.StringUtil;

import java.util.Locale;

public class SystemUtil {
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
