package org.docheinstein.commons.utils.file;

import java.io.*;
import java.util.Scanner;

/**
 * Provides utilities for files.
 */
public class FileUtil {

    private static final String ENDL = System.getProperty("line.separator");

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

    /**
     * Writes the given content to a file.
     * @param path the path of the file
     * @param content the content to write
     * @return true if the file has been written successfully
     */
    public static boolean write(String path, String content) {
        try {
            PrintWriter w = new PrintWriter(path, "UTF-8");
            w.print(content);
            w.close();
            return true;
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            return false;
        }
    }


    /**
     * Reads the resource at the given path.
     * @param path the resource path of the resource
     * @param clazz the class from where the resource is loaded
     * @return the read resource as a string
     */
    public static String readResource(String path, Class clazz) {
        return readResource(clazz.getResourceAsStream(path));
    }

    /**
     * Reads the given resource.
     * @param res the input stream of the resource to read
     * @return the read resource as a string
     */
    public static String readResource(InputStream res) {
        // https://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
        Scanner s = new Scanner(res).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * Reads the content of a file at the given path.
     * @param path the path of the file to read
     * @return the content to the file
     */
    public static String readFile(String path) {
        return readFile(path, ENDL);
    }

    /**
     * Reads the content of a file at the given path using the
     * given line separator.
     * @param path the path of the file to read
     * @param lineSeparator the line separator to use
     * @return the content to the file
     */
    public static String readFile(String path, String lineSeparator) {
        return readFile(new File(path), lineSeparator);
    }

    /**
     * Reads the content of a file using the given line separator.
     * @param file the file to read
     * @param lineSeparator the line separator to use
     * @return the content to the file
     */
    public static String readFile(File file, String lineSeparator) {
        if (!file.exists())
            return null;

        FileReader reader = null;
        BufferedReader bufferedReader = null;

        try {
            reader = new FileReader(file);
            bufferedReader = new BufferedReader(reader);

            String line;

            StringBuilder stringBuilder = new StringBuilder();

            // Read the first line outside the cycle in order not to add ENDL at the EOF
            line = bufferedReader.readLine();
            stringBuilder.append(line);

            while ( (line = bufferedReader.readLine()) != null) {
                // L.debug(LOGGER_TAG, "Reading line:" + line);
                stringBuilder.append(lineSeparator);
                stringBuilder.append(line);

            }
            return stringBuilder.toString();
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
                if (reader != null)
                    reader.close();
            }
            catch (IOException e) {
                return null;
            }
        }
    }
}
