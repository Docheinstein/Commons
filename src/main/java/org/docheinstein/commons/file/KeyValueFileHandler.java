package org.docheinstein.commons.file;

import org.docheinstein.commons.internal.DocCommonsLogger;
import org.docheinstein.commons.logger.DocLogger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Represents an handler of a file that contains (key, value) pairs, one for each line.
 * <p>
 * The separator can be decided, the default form is {key}={value}
 */
public class KeyValueFileHandler {
    private static final DocCommonsLogger L =
        DocCommonsLogger.createForTag("{KEY_VALUE_FILE_HANDLER}");

    private static final String DEFAULT_KEY_VALUE_SEPARATOR = "=";

    private final File mFile;

    private final String mSeparator;

    /**
     * Initializes the handler for the given file using the default
     * separator (i.e. =)
     * @param file the file to wrap
     */
    public KeyValueFileHandler(File file) {
        this(file, DEFAULT_KEY_VALUE_SEPARATOR);
    }

    /**
     * Initializes the handler for the given file using the given separator
     * @param file the file to wrap
     * @param keyvalSeparator the separator between key and value
     */
    public KeyValueFileHandler(File file, String keyvalSeparator) {
        mFile = file;
        mSeparator = keyvalSeparator;
    }

    /**
     * Reads a single key from the file; implies a file opening.
     * @param key the key for which retrieve the value
     * @return the value of the key
     */
    public String read(String key) {
        return readAll().get(key);
    }

    /**
     * Reads all the key-value pairs of the file.
     * @return a map containing the key-value assignments
     */
    public Map<String, String> readAll() {
        Map<String, String> keyvalMap = new HashMap<>();
        Scanner scanner;

        try {
            scanner = new Scanner(mFile);
        } catch (FileNotFoundException e) {
            return keyvalMap;
        }

        while (scanner.hasNextLine()) {
            String l = scanner.nextLine();
            String[] ls = l.split(Pattern.quote(mSeparator));
            if (ls.length == 2) {
                keyvalMap.put(ls[0], ls[1]);
            } else {
                L.out("Unexpected tokens found while reading file: " + mFile.getAbsolutePath());
            }
        }

        return keyvalMap;
    }

    /**
     * Writes a single key to the file using the given value; implies a file opening.
     * <p>
     * The method doesn't check for duplicate keys, those are simply appended.
     * @param key the key for which write the value
     * @param value the value of the key
     */
    public void write(String key, String value) {
        Map<String, String> keyvalMap = new HashMap<>();
        keyvalMap.put(key, value);
        writeAll(keyvalMap);
    }

    /**
     * Writes all the key-values assignments in the given map to the file
     * @param keyvalMap the map containing the key-value assignments.
     */
    public void writeAll(Map<String, String> keyvalMap) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter out = null;

        try {
            fw = new FileWriter(mFile, true);
            bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);
        } catch (IOException e) {
            L.out("Error occurred while writing (key, value) for file " + mFile.getAbsolutePath());
            return;
        }

        for (Map.Entry<String, String> keyval : keyvalMap.entrySet()) {
            out.println(keyval.getKey() + mSeparator + keyval.getValue());
        }

        out.close();
    }
}
