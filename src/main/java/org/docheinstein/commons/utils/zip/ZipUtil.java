package org.docheinstein.commons.utils.zip;

import org.docheinstein.commons.utils.file.FileUtil;
import org.docheinstein.commons.internal.DocCommonsLogger;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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

        if (!target.exists()) {
            L.out("Creating directory: " + target.getAbsolutePath());
            if (!target.mkdirs()) {
                throw new IOException("Failed to create root directory needed for zip extraction");
            }
        }

        final ZipInputStream zipStream = new ZipInputStream(source);
        ZipEntry zipEntry;

        while ((zipEntry = zipStream.getNextEntry()) != null) {
            final String entryName = zipEntry.getName();

            final File outputFile = new File(target, entryName);
            if (!entryName.endsWith(File.separator)) {
                // Extract file
                L.out("Unzipping file to: " + outputFile.getAbsolutePath());

                // Ensure that file exits
                outputFile.mkdirs();

                try (OutputStream targetStream = new FileOutputStream(outputFile)) {
                    FileUtil.copy(zipStream, targetStream);
                }
            }
            else {
                // Create directories
                L.out("Creating directory: " + outputFile.getAbsolutePath());
                if (!outputFile.exists()) {
                    if (!outputFile.mkdirs()) {
                        L.out("Creation of directory has failed while extracting zip");
                        throw new IOException("Failed to create directories needed for zip extraction");
                    }
                }
            }
        }
    }

    /**
     * Unzip a file to a target directory.
     * @param source the zip file
     * @param target the target directory
     * @throws IOException if the extraction fails
     */
    public static void unzip(File source,
                             File target) throws IOException {
        unzip(new FileInputStream(source), target);
    }

    /**
     * Zips a directory to a target zip file.
     * @param source the source directory
     * @param target the output zip file
     * @throws IOException if the compression fails
     */
    public static void zip(File source, File target) throws IOException {
        if (!source.exists()) {
            throw new RuntimeException(
                "Zip extraction failed, source file doesn't exists ("
                + source.getAbsolutePath() + ")");
        }

        FileOutputStream targetStream = new FileOutputStream(target);
        ZipOutputStream zipOut = new ZipOutputStream(targetStream);

        Path sourcePath = source.toPath();

        Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {

                Path targetFile = sourcePath.relativize(file);
                L.out("Zipping file to: " + file);
                zipOut.putNextEntry(new ZipEntry(targetFile.toString()));

                byte[] bytes = Files.readAllBytes(file);
                zipOut.write(bytes, 0, bytes.length);
                zipOut.closeEntry();

                return FileVisitResult.CONTINUE;
            }
        });

        zipOut.close();
    }
}
