package org.docheinstein.commons.utils.http;

import org.docheinstein.commons.internal.DocCommonsLogger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Entity responsible for download resources via HTTP.
 */
public class HttpDownloader {
    private static final DocCommonsLogger L = DocCommonsLogger.createForTag("{HTTP_DOWNLOADER}");

    private static final String DEFAULT_USER_AGENT =
        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1";

    /**
     * Whether there is need to continue the download.
     * <p>
     * False if download is aborted.
     **/
    private boolean mDownloadEnabled = true;

    /**
     * Interface used for listen to download progress.
     */
    public interface DownloadObserver {
        /**
         * Called on download's progress.
         * <p>
         * The byte delay between consecutive calls of this method is defined
         * by the parameter passed to {@link #download(String, String, String,
         * DownloadObserver, long)}
         * @param downloadedBytes the downloaded byte amount
         */
        void onProgress(long downloadedBytes);

        /**
         * Called when the download is finished successfully.
         * <p>
         * This method is not call if the download is aborted.
         */
        void onEnd();
    }

    /**
     * Download a resource from an url.
     * @param urlString the url to download
     * @param outputPath the output path where the download will be put
     * @throws IOException if the download fails
     *
     * @see #download(String, String, DownloadObserver, int)
     * @see #download(String, String, String, DownloadObserver, long)
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
     *
     * @see #download(String, String, String, DownloadObserver, long)
     */
    public void download(String urlString,
                                String outputPath,
                                DownloadObserver observer,
                                int bytesBetweenCallbacks) throws IOException {
        download(
            urlString,
            outputPath,
            DEFAULT_USER_AGENT,
            observer,
            bytesBetweenCallbacks);
    }

    /**
     * Download a resource from an url.
     * @param urlString the url to download
     * @param outputPath the output path where the download will be put
     * @param userAgent the user agent string to use for the request
     * @param observer an optional observer used for listen to download progress
     * @param bytesBetweenCallbacks the amount of bytes between each callback
     *                              of {@link DownloadObserver#onProgress(long)}
     * @throws IOException if the download fails
     *
     * @see #download(String, String, String, DownloadObserver, long)
     */
    public void download(String urlString,
                                String outputPath,
                                String userAgent,
                                DownloadObserver observer,
                                long bytesBetweenCallbacks) throws IOException {

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
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setRequestMethod("GET");

            connection.connect();
            int code = connection.getResponseCode();

            if (code < 200 || code >= 400) {
                outputPath = "download-error-" + System.currentTimeMillis() + ".txt";
                is = connection.getErrorStream();
            }
            else
                is = connection.getInputStream();

            fos = new FileOutputStream(outputPath);

            byte[] buffer = new byte[BUFFER_SIZE];
            long lastCallbackLength = 0;
            long totalLength = 0;
            int len;

            // Download and write to the local file until data is available
            while (mDownloadEnabled && (len = is.read(buffer)) > 0) {
                totalLength += len;

                fos.write(buffer, 0, len);

                // Notify download progression
                if (observer != null) {
                    if (totalLength - lastCallbackLength > bytesBetweenCallbacks) {
                        lastCallbackLength = totalLength;
                        observer.onProgress(totalLength);
                    }
                }
            }
            connection.disconnect();

            // Notify the observer about download end (if the download is not aborted)
            if (mDownloadEnabled && observer != null)
                observer.onEnd();
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
