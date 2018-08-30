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
import java.util.List;
import java.util.Map;

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
        private Map<String, List<String>> mHeaderFields;

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

        public Map<String, List<String>> getHeaderFields() {
            return mHeaderFields;
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

    private HttpURLConnection mConnection = null;

    // Defaults
    private RequestMethod mMethod = RequestMethod.GET;
    private ContentType mContentType = null;
    private boolean mRedirect = true;

    private String mURI = null;
    private String mOutData = null;
    private String mEncodedUserPass = null;

    private String mUserAgent = null;
    private String mAccept = null;



    /**
     * Creates an http requester.
     * @return an http requester
     */
    public static HttpRequester createRequest() {
        return new HttpRequester();
    }

    /**
     * Creates an http requester for the given request method.
     * @param method the request method
     * @return an http requester
     */
    public static HttpRequester createRequest(HttpRequester.RequestMethod method) {
        return createRequest().method(method);
    }

    /**
     * Creates an http requester for the request method 'HEAD'.
     * @return an http requester
     */
    public static HttpRequester head() {
        return createRequest(HttpRequester.RequestMethod.HEAD);
    }

    /**
     * Creates an http requester for the request method 'GET'.
     * @return an http requester
     */
    public static HttpRequester get() {
        return createRequest(HttpRequester.RequestMethod.GET);
    }

    /**
     * Creates an http requester for the request method 'POST'.
     * @return an http requester
     */
    public static HttpRequester post() {
        return createRequest(HttpRequester.RequestMethod.POST);
    }

    /**
     * Creates an http requester for the request method 'DELETE'.
     * @return an http requester
     */
    public static HttpRequester delete() {
        return createRequest(HttpRequester.RequestMethod.DELETE);
    }

    /**
     * Creates an http requester for the request method 'PUT'.
     * @return an http requester
     */
    public static HttpRequester put() {
        return createRequest(HttpRequester.RequestMethod.PUT);
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
     * Whether the connection should automatically follow the 'location'
     * header or not.
     * @param redirect whether allow redirection
     * @return
     */
    public HttpRequester allowRedirect(boolean redirect) {
        mRedirect = redirect;
        return this;
    }

    /**
     * Sets the 'User-Agent' header.
     * @param userAgent the user agent string
     * @return the requester
     */
    public HttpRequester userAgent(String userAgent) {
        mUserAgent = userAgent;
        return this;
    }

    /**
     * Sets the 'Accept' header.
     * @param accept the accept string
     * @return the requester
     */
    public HttpRequester accept(String accept) {
        mAccept = accept;
        return this;
    }

    /**
     * Returns the underlying connection.
     * @return the underlying connection.
     */
    public HttpURLConnection getUnderlyingConnection() {
        return mConnection;
    }

    /**
     * Actually createRequest the underlying {@link HttpURLConnection} that can be
     * retrieved via {@link #getUnderlyingConnection()}
     * @return
     */
    public HttpRequester initialized() {
        if (!StringUtil.isValid(mURI) || mMethod == null || mContentType == null)
            L.out("Can't send request, please build HttpRequester with every mandatory field");

        L.out("Initializing with URI: " + mURI);

        URL url = null;
        try {
            url = new URL(mURI);
            mConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    /**
     * Sends a request for the built requester and returns a response object.
     * @return the response of this request
     */
    public Response send() {
        if (mConnection == null)
            initialized();

        Response resp = new Response();

        try {
            // Cache
            mConnection.setUseCaches(false);

            // Method
            mConnection.setRequestMethod(mMethod.name);

            // Headers

            // Content-Type
            if (mContentType != null)
                mConnection.setRequestProperty("Content-Type", mContentType.name);


            // User-Agent
            if (StringUtil.isValid(mUserAgent))
                mConnection.setRequestProperty("User-Agent", mUserAgent);

            // Accept
            if (StringUtil.isValid(mAccept))
                mConnection.setRequestProperty("Accept", mAccept);

            // Redirect
            mConnection.setInstanceFollowRedirects(mRedirect);

            if (StringUtil.isValid(mEncodedUserPass)) {
                mConnection.setRequestProperty("Authorization", "Basic " + mEncodedUserPass);
            }

            // Content
            if (StringUtil.isValid(mOutData)) {
                mConnection.setDoOutput(true);
                DataOutputStream dataOut = new DataOutputStream(mConnection.getOutputStream());
                dataOut.writeBytes(mOutData);
                dataOut.close();
            }

            mConnection.connect();

            resp.mResponseCode = mConnection.getResponseCode();
            resp.mContentLength = mConnection.getContentLengthLong();
            resp.mHeaderFields = mConnection.getHeaderFields();

            InputStream is;

            if (resp.mResponseCode >= 200 && resp.mResponseCode < 400)
                is = mConnection.getInputStream();
            else
                is = mConnection.getErrorStream();

            String line;
            StringBuilder sb;
            BufferedReader br;

            if (is != null) {
                // Read standard in
                sb = new StringBuilder();
                InputStreamReader isr = new InputStreamReader(is);
                br = new BufferedReader(isr);

                if ((line = br.readLine()) != null)
                    sb.append(line); // Avoid the last \n by handling the first
                // line apart from the others

                while ((line = br.readLine()) != null) {
                    sb.append("\n");
                    sb.append(line);
                }

                resp.mResponseBody = sb.toString();
                br.close();
                isr.close();
                is.close();
            }

            mConnection.disconnect();

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