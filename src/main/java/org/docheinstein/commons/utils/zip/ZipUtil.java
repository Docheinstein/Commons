package org.docheinstein.commons.utils.zip;

import org.docheinstein.commons.utils.file.FileUtil;
import org.docheinstein.commons.internal.DocCommonsLogger;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Provides utilities for .zip files (actually unzip only).
 */
public class ZipUtil {

    private static final DocCommonsLogger L = DocCommonsLogger.createForTag("{ZIP_UTIL}");

    /**
     * Unzip a resource to a target directory.
     * @param source the zip stream
     * @param target the target directory
     * @throws IOException if the extraction fails
     */
    public static void unzip(InputStream source,
                             File target) throws IOException {
        final ZipInputStream zipStream = new ZipInputStream(source);
        ZipEntry zipEntry;

        while ((zipEntry = zipStream.getNextEntry()) != null) {
            final String name = zipEntry.getName();

            final File entryFile = new File(target, name);

            if (!name.endsWith(File.separator)) {
                // Extract file
                L.out("Extracting file: " + entryFile.getAbsolutePath());
                try (OutputStream targetStream = new FileOutputStream(entryFile)) {
                    FileUtil.copy(zipStream, targetStream);
                }
            }
            else {
                // Create directories
                L.out("Creating directory: " + entryFile.getAbsolutePath());
                if (!entryFile.exists())
                    if (!entryFile.mkdirs()) {
                        L.out("Creation of directory has failed while extracting zip");
                        throw new IOException("Failed to create directories needed for zip extraction");
                    }
            }
        }
    }
}
