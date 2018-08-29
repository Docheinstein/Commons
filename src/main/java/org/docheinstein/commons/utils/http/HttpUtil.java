package org.docheinstein.commons.utils.http;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Provides utilities for request and download resources over http connection.
 */
public class HttpUtil {

    /**
     * Interface used for listen to download progress.
     */
    public interface DownloadObserver {
        /**
         * Called on download's progress.
         * The byte delay between consecutive calls of this method is defined
         * by the parameter passed to {@link #download(String, String, String,
         * DownloadObserver, int)}
         * @param downloadedBytes
         */
        void onProgress(long downloadedBytes);
    }

    /**
     * Download a resource from an url.
     * @param urlString the url to download
     * @param outputPath the output path where the download will be put
     * @throws IOException if the download fails
     *
     * @see #download(String, String, DownloadObserver, int)
     * @see #download(String, String, String, DownloadObserver, int)
     */
    public static void download(String urlString,
                                String outputPath) throws IOException {

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
     * @see #download(String, String, String, DownloadObserver, int)
     */
    public static void download(String urlString,
                                String outputPath,
                                DownloadObserver observer,
                                int bytesBetweenCallbacks) throws IOException {
        download(
            urlString,
            outputPath,
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1",
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
     * @see #download(String, String, String, DownloadObserver, int)
     */
    public static void download(String urlString,
                                String outputPath,
                                String userAgent,
                                DownloadObserver observer,
                                int bytesBetweenCallbacks) throws IOException {

        final int BUFFER_SIZE = 4096;

        URL url = new URL(urlString);
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestProperty("User-Agent", userAgent);
            urlConn.setRequestMethod("GET");

            urlConn.connect();
            int code = urlConn.getResponseCode();

            if (code < 200 || code >= 400) {
                outputPath = "download-error-" + System.currentTimeMillis() + ".txt";
                is = urlConn.getErrorStream();
            }
            else
                is = urlConn.getInputStream();

            fos = new FileOutputStream(outputPath);

            byte[] buffer = new byte[BUFFER_SIZE];
            long lastCallbackLength = 0;
            long totalLength = 0;
            int len;

            // Download and write to the local file until data is available
            while ((len = is.read(buffer)) > 0) {
                totalLength += len;

                fos.write(buffer, 0, len);

                if (observer != null) {
                    if (totalLength - lastCallbackLength > bytesBetweenCallbacks) {
                        lastCallbackLength = totalLength;
                        observer.onProgress(totalLength);
                    }
                }
            }
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
     * Creates an http requester.
     * @return an http requester
     */
    public static HttpRequester create() {
        return new HttpRequester();
    }

    /**
     * Creates an http requester for the given request method.
     * @param method the request method
     * @return an http requester
     */
    public static HttpRequester create(HttpRequester.RequestMethod method) {
        return create().method(method);
    }

    /**
     * Creates an http requester for the request method 'HEAD'.
     * @return an http requester
     */
    public static HttpRequester head() {
        return create(HttpRequester.RequestMethod.HEAD);
    }

    /**
     * Creates an http requester for the request method 'GET'.
     * @return an http requester
     */
    public static HttpRequester get() {
        return create(HttpRequester.RequestMethod.GET);
    }

    /**
     * Creates an http requester for the request method 'POST'.
     * @return an http requester
     */
    public static HttpRequester post() {
        return create(HttpRequester.RequestMethod.POST);
    }

    /**
     * Creates an http requester for the request method 'DELETE'.
     * @return an http requester
     */
    public static HttpRequester delete() {
        return create(HttpRequester.RequestMethod.DELETE);
    }

    /**
     * Creates an http requester for the request method 'PUT'.
     * @return an http requester
     */
    public static HttpRequester put() {
        return create(HttpRequester.RequestMethod.PUT);
    }

    /**
     * Creates an http requester for the request method 'HEAD' for the
     * given uri.
     * @param uri an uri
     * @return an http requester
     */
    public static HttpRequester head(String uri) {
        return head().uri(uri);
    }

    /**
     * Creates an http requester for the request method 'GET' for the
     * given uri.
     * @param uri an uri
     * @return an http requester
     */
    public static HttpRequester get(String uri) {
        return get().uri(uri);
    }

    /**
     * Creates an http requester for the request method 'POST' for the
     * given uri.
     * @param uri an uri
     * @return an http requester
     */
    public static HttpRequester post(String uri) {
        return post().uri(uri);
    }

    /**
     * Creates an http requester for the request method 'DELETE' for the
     * given uri.
     * @param uri an uri
     * @return an http requester
     */
    public static HttpRequester delete(String uri) {
        return delete().uri(uri);
    }

    /**
     * Creates an http requester for the request method 'PUT' for the
     * given uri.
     * @param uri an uri
     * @return an http requester
     */
    public static HttpRequester put(String uri) {
        return put().uri(uri);
    }
}
