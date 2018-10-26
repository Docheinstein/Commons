package org.docheinstein.commons.utils.http;

import org.docheinstein.commons.internal.DocCommonsLogger;
import org.docheinstein.commons.utils.file.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Entity responsible for download resources via HTTP.
 */
public class HttpDownloader {
    private static final DocCommonsLogger L = DocCommonsLogger.createForTag("{HTTP_DOWNLOADER}");

    private static final String DEFAULT_USER_AGENT =
        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1";

    private static final boolean DEFAULT_RESUME_IF_EXISTS = true;

    /**
     * Whether there is need to continue the download.
     * <p>
     * False if download is aborted.
     **/
    private boolean mDownloadEnabled = true;

    private String mUserAgent = DEFAULT_USER_AGENT;
    private boolean mResumeIfExists = DEFAULT_RESUME_IF_EXISTS;
    private Consumer<HttpURLConnection> mConnectionInitializer = null;

    /**
     * Interface used for listen to download progress.
     */
    public interface DownloadObserver {
        /**
         * Called on download's progress.
         * <p>
         * The byte delay between consecutive calls of this method is defined
         * by the parameter passed to {@link #download(String, String, DownloadObserver, int)}
         * @param downloadedBytes the downloaded byte amount
         */
        void onProgress(long downloadedBytes);
    }

    /**
     * Uses the given user agent instead of the default one.
     * @param userAgent the user agent to use for the HTTP request
     * @return this downloader
     */
    public HttpDownloader userAgent(String userAgent) {
        mUserAgent = userAgent;
        return this;
    }

    /**
     * Whether the download of the resource should be resumed in case the output
     * file already exists (and the downloaded has been stopped for whatever reason).
     * <p>
     * This works only if the remote site supports the 'Range' HTTP header.
     * @param resume whether resume an already existing download
     * @return this downloader
     */
    public HttpDownloader resumeIfExists(boolean resume) {
        mResumeIfExists = resume;
        return this;
    }

    /**
     * Sets a custom initializer that can perform additional action
     * on the connection before proceed with the download.
     * @param connectionInitializer the initializer
     * @return this downloader
     */
    public HttpDownloader connectionInitializer(Consumer<HttpURLConnection> connectionInitializer) {
        mConnectionInitializer = connectionInitializer;
        return this;
    }

    /**
     * Download a resource from an url.
     * @param urlString the url to download
     * @param outputPath the output path where the download will be put
     * @throws IOException if the download fails
     *
     * @see #download(String, String, DownloadObserver, int)
     */
    public void download(String urlString,
                         String outputPath) throws IOException {
        download(
            urlString,
            outputPath,
            null,
            0
        );
    }

    /**
     * Download a resource from an url.
     * @param urlString the url to download
     * @param outputPath the output path where the download will be put
     * @param observer an optional observer used for listen to download progress
     * @param bytesBetweenCallbacks the amount of bytes between each callback
     *                              of {@link DownloadObserver#onProgress(long)}
     * @throws IOException if the download fails
     */
    public void download(String urlString,
                         String outputPath,
                         DownloadObserver observer,
                         int bytesBetweenCallbacks) throws IOException {

        if (!mDownloadEnabled) {
            L.out("Download is not enabled, doing nothing");
            return;
        }

        final int BUFFER_SIZE = 4096;

        URL url = new URL(urlString);
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", mUserAgent);
            connection.setRequestMethod("GET");

            // Performs additional initializations
            if (mConnectionInitializer != null)
                mConnectionInitializer.accept(connection);

            boolean append = false;

            if (mResumeIfExists) {
                File outputFile = new File(outputPath);

                if (FileUtil.exists(outputFile)) {
                    long alreadyDownloadedBytes = outputFile.length();
                    L.out("The output file already exists; trying to resuming it from byte "
                        + alreadyDownloadedBytes);

                    connection.setRequestProperty(
                        "Range",
                        "bytes=" + alreadyDownloadedBytes  + "-");

                    append = true;
                }
            }

            connection.connect();
            int code = connection.getResponseCode();

            if (code < 200 || code >= 400) {
                outputPath += "-download-error-" + System.currentTimeMillis() + ".txt";
                is = connection.getErrorStream();
            }
            else
                is = connection.getInputStream();

            fos = new FileOutputStream(outputPath, append);

            byte[] buffer = new byte[BUFFER_SIZE];
            long lastCallbackLength = 0;
            long totalLength = 0;
            int len;

            // Download and write to the local file until data is available
            while (mDownloadEnabled && (len = is.read(buffer)) > 0) {
                totalLength += len;

                fos.write(buffer, 0, len);

                // Notify download progression
                if (totalLength - lastCallbackLength > bytesBetweenCallbacks &&
                    observer != null) {
                    lastCallbackLength = totalLength;
                    observer.onProgress(totalLength);
                }

            }
            connection.disconnect();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
        }
    }

    /**
     * Whether this entity should or should not download resource.
     * <p>
     * If this is set to false while a download is in progress it will be
     * aborted.
     * @param downloadEnabled whether enable download
     */
    public void enableDownload(boolean downloadEnabled) {
        mDownloadEnabled = downloadEnabled;
    }
}
