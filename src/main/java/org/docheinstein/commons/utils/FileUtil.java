package org.docheinstein.commons.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Provides utilities for files.
 */
public class FileUtil {
    /**
     * Copies a file from a resource to a target output stream.
     * @param source the resource to copy
     * @param target the output destination
     * @throws IOException if the the copy fails
     */
    public static void copy(InputStream source,
                            OutputStream target) throws IOException {
        final int bufferSize = 4096;
        final byte[] buffer = new byte[bufferSize];

        int nextCount;
        while ((nextCount = source.read(buffer)) >= 0) {
            target.write(buffer, 0, nextCount);
        }
    }

    /**
     * Renames the a file/directory.
     * @param src the source path of the file to rename
     * @param dst the destination path of the renamed file
     * @return whether the file has been renamed successfully
     */
    public static boolean rename(String src, String dst) {
        return rename(new File(src), new File(dst));
    }

    /**
     * Renames the a file/directory.
     * @param src the source path of the file to rename
     * @param dst the destination path of the renamed file
     * @return whether the file has been renamed successfully
     */
    public static boolean rename(File src, File dst) {
        return move(src, dst);
    }

    /**
     * Moves a file/directory from a source path to a destination path.
     * @param src the source path of the file to move
     * @param dst the destination path of the moved file
     * @return whether the file has been moved successfully
     */
    public static boolean move(String src, String dst) {
        return move(new File(src), new File(dst));
    }

    /**
     * Moves a file/directory from a source path to a destination path.
     * @param src the source path of the file to move
     * @param dst the destination path of the moved file
     * @return whether the file has been moved successfully
     */
    public static boolean move(File src, File dst) {
        return exists(src) && exists(dst) && src.renameTo(dst);
    }

    /**
     * Returns whether a file/directory exists.
     * @param path the path of the file
     * @return whether the file exists
     */
    public static boolean exists(String path) {
        return exists(new File(path));
    }

    /**
     * Returns whether a file/directory exists.
     * @param path the path of the file
     * @return whether the file exists
     */
    public static boolean exists(File path) {
        return path != null && path.exists();
    }

    /**
     * Deletes a file at the given location.
     * This methods work also for empty directory, if the directory
     * has some content it must be deleted before invoke this method.
     * @param path the path of the file to delete
     * @return whether the file has been deleted successfully
     *
     * @see #deleteRecursive(String)
     */
    public static boolean delete(String path) {
        return delete(new File(path));
    }

    /**
     * Deletes a file at the given location.
     * This methods work also for empty directory, if the directory
     * has some content it must be deleted before invoke this method.
     * @param path the path of the file to delete
     * @return whether the file has been deleted successfully
     *
     * @see #deleteRecursive(File)
     */
    public static boolean delete(File path) {
        return exists(path) && path.delete();
    }

    /**
     * Deletes a directory recursively.
     * @param path the path of the directory to delete
     * @return whether the directory has been deleted successfully
     */
    public static boolean deleteRecursive(String path) {
        return deleteRecursive(new File(path));
    }

    /**
     * Deletes a directory recursively.
     * @param path the path of the directory to delete
     * @return whether the directory has been deleted successfully
     */
    public static boolean deleteRecursive(File path) {
        if (!exists(path))
            return false;

        boolean deleteOk = true;

        if (path.isDirectory()) {
            File fs[] = path.listFiles();
            if (fs != null) {
                for (File f : fs) {
                    deleteOk = deleteOk && deleteRecursive(f);
                }
            }
        }
        return deleteOk && path.delete();
    }
}
