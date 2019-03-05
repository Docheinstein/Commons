package org.docheinstein.commons.system;

import org.docheinstein.commons.internal.DocCommonsLogger;
import org.docheinstein.commons.types.StringUtil;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Provides utilities for the system
 */
public class SystemUtil {
    private static final DocCommonsLogger L = DocCommonsLogger.createForTag("{SYSTEM_UTIL}");

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

    private static final String[] BAD_NAME_PREFIXES = new String[] { "vm", "lo" };

    // https://www.techrepublic.com/blog/data-center/mac-address-scorecard-for-common-virtual-machine-platforms/
    private static final String[] BAD_MAC_PREFIXES = new String[] {
        "00-50-56", "00-0C-29", "00-05-69", // VMware ESX 3, Server, Workstation, Player
        "00-03-FF", // Microsoft Hyper-V, Virtual Server, Virtual PC
        "00-1C-42", // Parallells Desktop, Workstation, Server, Virtuozzo
        "00-0F-4B", // Virtual Iron 4
        "00-16-3E", // Red Hat Xen, Oracle VM, XenSource, Novell Xen
        "08-00-27"  // Sun xVM VirtualBox
    };

    /**
     * Returns the first valid mac address for this device.
     * <p>
     * MAC address if virtual machines or loopback networks are skipped.
     * @return the first valid mac address of this device
     * @throws SocketException if the network interfaces can't be retrieved
     */
    public static String getMacAddress() throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces =
            NetworkInterface.getNetworkInterfaces();

        String aValidMac = null;

        // Tries to grab a MAC that doesn't refer to a VM or a Loopback,
        // if the method fails, repeat and grab the first valid one.

        while (networkInterfaces.hasMoreElements()) {
            boolean badName = false;
            boolean badMac = false;

            NetworkInterface network = networkInterfaces.nextElement();
            String name = network.getName();

            L.out("Network name: " + network.getName());

            // Check bad name
            for (String badNamePrefix : BAD_NAME_PREFIXES)
                badName |= name.startsWith(badNamePrefix);

            if (badName) {
                L.out("|__ skipping since contains bad name prefix");
                continue;
            }

            byte[] mac = network.getHardwareAddress();

            if (mac == null) {
                L.out("|__ skipping since MAC is null");
                continue;
            }

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s",
                    mac[i],
                    (i < mac.length - 1) ? "-" : ""));
            }

            aValidMac = sb.toString();

            L.out("|__ interface has a valid MAC: " + aValidMac);

            // Check bad MAC
            for (String badMacPrefix : BAD_MAC_PREFIXES)
                badMac |= aValidMac.startsWith(badMacPrefix);

            if (badMac) {
                L.out("|__ skipping since contains bad MAC prefix");
                continue;
            }

            L.out("|__ hurray, found a good MAC address: " + aValidMac);

            return aValidMac;
        }

        L.out("Can't find a good MAC address that is neither a loopback, a" +
               " vm or some bad stuff. Returning the following MAC: [" + aValidMac + "]");

        return aValidMac;
    }

    private final static Map<OSType, String> SHUTDOWN_COMMANDS = new HashMap<>();
    private final static Map<OSType, String> REBOOT_COMMNANDS = new HashMap<>();

    static {
        SHUTDOWN_COMMANDS.put(OSType.Linux, "shutdown -h now");
        SHUTDOWN_COMMANDS.put(OSType.Windows, "shutdown.exe -s -t 0");
        SHUTDOWN_COMMANDS.put(OSType.Mac, "shutdown -h now");

        REBOOT_COMMNANDS.put(OSType.Linux, "reboot -h now");
        REBOOT_COMMNANDS.put(OSType.Windows, "shutdown -r -t 0");
        REBOOT_COMMNANDS.put(OSType.Mac, "reboot -h now");
    }

    /**
     * Shutdowns the system, invoking the appropriate command for this OS.
     * @throws IOException if {@link Runtime#exec(String)} fails
     */
    public static void shutdown() throws IOException {
        L.out("Operating system is shutting down...");
        execCommand(SHUTDOWN_COMMANDS);
    }


    /**
     * Reboots the system, invoking the appropriate command for this OS.
     * @throws IOException if {@link Runtime#exec(String)} fails
     */
    public static void reboot() throws IOException {
        L.out("Operating system is rebooting down...");
        execCommand(REBOOT_COMMNANDS);
    }

    /**
     * Executes the command appropriate for the current OS.
     * @param os2cmd a map that associated operating systems to the command to invoke
     * @throws IOException if {@link Runtime#exec(String)} fails
     */
    private static void execCommand(Map<OSType, String> os2cmd) throws IOException {
        OSType os = getCurrentOperatingSystemType();
        if (os == null)
            return;

        String cmd = os2cmd.get(os);

        if (!StringUtil.isValid(cmd))
            return;

        Runtime.getRuntime().exec(cmd);
    }
}
