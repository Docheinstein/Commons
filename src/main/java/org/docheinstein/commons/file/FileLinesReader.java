package org.docheinstein.commons.file;

import org.docheinstein.commons.internal.DocCommonsLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Reads a file line by line.
 */
public abstract class FileLinesReader {
    private static final DocCommonsLogger L = DocCommonsLogger.createForTag("{FILE_LINES_READER}");

    private File mFile;

    /**
     * Creates a lines reader for the given file.
     * @param file the file to read
     */
    public FileLinesReader(File file) {
        mFile = file;
    }

    /**
     * Creates a lines reader for the file at the given path
     * @param path the path of the file to read
     */
    public FileLinesReader(String path) {
        this(new File(path));
    }

    /**
     * Reads the file line by line.
     * <p>
     * When a new line is read, {@link #readLine(String)} is called in order
     * to handle the line.
     */
    public void readLineByLine() {
        if (!mFile.exists())
            return;

        FileReader reader = null;
        BufferedReader bufferedReader = null;

        try {
            reader = new FileReader(mFile);
            bufferedReader = new BufferedReader(reader);

            String line;

            while ((line = bufferedReader.readLine()) != null &&
                    readLine(line));

        } catch (IOException e) {
            L.out("Error occurred while reading lines of file " + mFile + "; " +
                    e.getMessage());
        }
        finally {
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
                if (reader != null)
                    reader.close();
            }
            catch (IOException e) {
                L.out("Error occurred while closing file " + mFile + "; " +
                    e.getMessage());
            }
        }
    }

    /**
     * Handle the reading of a line.
     * @param line the line read from the file
     * @return whether the line reading should continue
     */
    protected abstract boolean readLine(String line);
}
