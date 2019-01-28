package org.docheinstein.commons.utils.file;

import org.docheinstein.commons.internal.DocCommonsLogger;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Provides utilities for files.
 */
public class FileUtil {

    private static final DocCommonsLogger L = DocCommonsLogger.createForTag("{FILE_UTIL}");

    private static final String ENDL = System.getProperty("line.separator");

    /**
     * Checks whether the given folder exists, and if not tries to create the
     * directory via {@link File#mkdirs()}
     * @param folder the folder
     * @return whether the folders have been created
     */
    public static boolean ensureFolderExistence(File folder) {
        return exists(folder) || folder.mkdirs();
    }

    /**
     * Checks whether the given file exists, and if not tries to create the
     * directory via {@link File#createNewFile()} ()}
     * @param file the file
     * @return whether the file has been created
     */
    public static boolean ensureFileExistence(File file) {
        return ensureFileExistence(file, f -> {
            try {
                return f.createNewFile();
            } catch (IOException e) {
                return false;
            }
        });
    }

    /**
     * Checks whether the given file exists, and if not tries to create the
     * directory using the given file creator
     * @param file the file
     * @param fileCreator the creator responsible for create the file
     * @return whether the file has been created
     */
    public static boolean ensureFileExistence(File file,
                                               Function<File, Boolean> fileCreator) {
        return exists(file) || fileCreator.apply(file);
    }

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
        return src.renameTo(dst);
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
     * @param file the path of the file
     * @return whether the file exists
     */
    public static boolean exists(File file) {
        return file != null && file.exists();
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
     * @param file the file
     * @param content the content to write
     * @return true if the file has been written successfully
     */
    public static boolean write(File file, String content) {
        return file != null && write(file.getAbsolutePath(), content);
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
     * Reads the content of a file at the given path.
     * @param file the file to read
     * @return the content to the file
     */
    public static String readFile(File file) {
        return readFile(file, ENDL);
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
            if (line != null) {
                stringBuilder.append(line);

                while ((line = bufferedReader.readLine()) != null) {
                    // L.debug(LOGGER_TAG, "Reading line:" + line);
                    stringBuilder.append(lineSeparator);
                    stringBuilder.append(line);
                }
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

    /**
     * Merges the inputs file into a single output file by concatenate them.
     * <p>
     * The merge operation doesn't read line per line, but instead transfers the
     * byte content directly.
     * @param output the output file
     * @param inputs the input files
     * @see #mergeFiles(File, File...)
     */
    public static void mergeFiles(String output, String ...inputs) {
        Path outPath = Paths.get(output);
        try {
            FileChannel outChannel = FileChannel.open(
                outPath,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE
            );

            for (String input : inputs) {

                Path inPath = Paths.get(input);
                FileChannel inChannel = FileChannel.open(inPath, StandardOpenOption.READ);

                long transferred = 0;
                long inputSize = inChannel.size();

                L.out("Transferring content from " + inPath);
                while (transferred < inputSize)
                    transferred += inChannel.transferTo(transferred,
                        inputSize - transferred /*  remaining bytes */,
                        outChannel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Merges the inputs file into a single output file by concatenate them.
     * <p>
     * The merge operation doesn't read line per line, but instead transfers the
     * byte content directly.
     * @param output the output file
     * @param inputs the input files
     * @see #mergeFiles(String, String...)
     */
    public static void mergeFiles(File output, File ...inputs) {
        String[] strs = new String[inputs.length];
        for (int i = 0; i < inputs.length; i++)
            strs[i] = inputs[i].getAbsolutePath();

        mergeFiles(output.getAbsolutePath(), strs);
    }
}
