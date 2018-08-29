package org.docheinstein.commons.utils.http;

import org.docheinstein.commons.internal.DocCommonsLogger;
import org.docheinstein.commons.utils.crypto.CryptoUtil;
import org.docheinstein.commons.utils.types.StringUtil;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Entity able to perform HTTP request using different {@link RequestMethod}
 * and eventually basic authentication.
 */
public class HttpRequester {

    private static final DocCommonsLogger L = DocCommonsLogger.createForTag("{HTTP_REQUESTER}");

    /**
     * The response obtained from a requester request.
     */
    public static class Response {
        private Integer mResponseCode;
        private String mResponseBody;
        private long mContentLength;
        private HttpURLConnection mConnection;

        /**
         * Returns whether the requests has been performed successfully.
         * @return whether the requests has been performed successfully
         */
        public boolean hasBeenPerformed() {
            return mResponseCode != null;
        }

        /**
         * Returns the response code of the request.
         * @return the response code of the request
         */
        public int getResponseCode() {
            return mResponseCode;
        }

        /**
         * Returns the response body of the request.
         * @return the response body of the request
         */
        public String getResponseBody() {
            return mResponseBody;
        }

        /**
         * Returns the 'Content-Length' header of the response.
         * @return the 'Content-Length' header of the response
         */
        public long getContentLength() {
            return mContentLength;
        }

        /**
         * Returns the underlying connection.
         * @return the underlying connection.
         */
        public HttpURLConnection getUnderlyingConnection() {
            return mConnection;
        }
    }

    /**
     * HTTP request method.
     */
    public enum RequestMethod {
        GET("GET"),
        POST("POST"),
        DELETE("DELETE"),
        PUT("PUT"),
        HEAD("HEAD");

        private String name;

        RequestMethod(String method) {
            name = method;
        }
    }

    /**
     * 'Content-Type' header types.
     */
    public enum ContentType {
        PLAIN("text/plain"),
        JSON("application/json");

        private String name;

        ContentType(String contentType) {
            name = contentType;
        }
    }

    // Defaults
    private RequestMethod mMethod = RequestMethod.GET;
    private ContentType mContentType = ContentType.PLAIN;

    private String mURI;
    private String mOutData;
    private String mEncodedUserPass;

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

    /**
     * Sets the given uri.
     * @param uri an uri
     * @return the requester
     */
    public HttpRequester uri(String uri) {
        mURI = uri;
        return this;
    }

    /**
     * Sets the given method.
     * @param method an uri
     * @return the requester
     */
    public HttpRequester method(RequestMethod method) {
        mMethod = method;
        return this;
    }

    /**
     * Sets the 'Basic Authentication' header for the given
     * plain user and password.
     * @param plainUser a plain username
     * @param plainPass a plain password
     * @return the requester
     *
     * @see #basicAuth(String)
     */
    public HttpRequester basicAuth(String plainUser, String plainPass) {
        basicAuth(CryptoUtil.Base64.encode(plainUser + ":" + plainPass));
        return this;
    }

    /**
     * Sets the 'Basic Authentication' header for the given
     * encoded user and password.
     * @param encodedUserPass an already encoded user:pass
     * @return the requester
     *
     * @see #basicAuth(String, String)
     */
    public HttpRequester basicAuth(String encodedUserPass) {
        mEncodedUserPass = encodedUserPass;
        return this;
    }

    /**
     * Sets the body.
     * @param contentType the content type of the body
     * @param bodyData the body string
     * @return the requester
     */
    public HttpRequester body(ContentType contentType, String bodyData) {
        mContentType = contentType;
        mOutData = bodyData;
        return this;
    }

    /**
     * Sends a request for the built requester and returns a response object.
     * @return the response of this request
     */
    public Response send() {
        Response resp = new Response();

        try {
            if (!StringUtil.isValid(mURI) || mMethod == null || mContentType == null)
                L.out("Can't send request, please build HttpRequester with every mandatory field");

            L.out("Will send request to: " + mURI);

            URL url = new URL(mURI);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Method
            conn.setRequestMethod(mMethod.name);

            // Headers
            conn.setRequestProperty("Content-Type", mContentType.name);

            if (StringUtil.isValid(mEncodedUserPass)) {
                conn.setRequestProperty("Authorization", "Basic " + mEncodedUserPass);
            }

            // Content
            if (StringUtil.isValid(mOutData)) {
                conn.setDoOutput(true);
                DataOutputStream dataOut = new DataOutputStream(conn.getOutputStream());
                dataOut.writeBytes(mOutData);
                dataOut.close();
            }


            resp.mResponseCode = conn.getResponseCode();
            resp.mContentLength = conn.getContentLengthLong();
            resp.mConnection = conn;

            InputStream is;

            if (resp.mResponseCode >= 200 && resp.mResponseCode < 400)
                is = conn.getInputStream();
            else
                is = conn.getErrorStream();

            String line;
            StringBuilder sb;
            BufferedReader br;

            if (is != null) {
                // Read standard in
                sb = new StringBuilder();
                br = new BufferedReader(new InputStreamReader(is));

                if ((line = br.readLine()) != null)
                    sb.append(line); // Avoid the last \n by handling the first
                // line apart from the others

                while ((line = br.readLine()) != null) {
                    sb.append("\n");
                    sb.append(line);
                }

                resp.mResponseBody = sb.toString();
                br.close();
            }

            return resp;
        } catch (ProtocolException e) {
            L.out("Protocol exception, not a valid request method: " + mMethod);
        } catch (MalformedURLException e) {
            L.out("Malformed URL exception, not a valid URI: " + mURI);
        } catch (IOException e) {
            L.out("IO exception, can't perform HTTP request");
        }
        return resp;
    }

    /**
     * Trust all the certificates for every HTTP connection established
     * (from this requester and from other classes too).
     */
    public static void enableTrustAllSocketFactory() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

        try {
            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = (hostname, session) -> true;

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            L.out("Failed to set trust all socket factory!");
        }
    }
}