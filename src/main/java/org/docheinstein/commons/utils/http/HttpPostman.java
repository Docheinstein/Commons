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

public class HttpPostman {

    private static final DocCommonsLogger L = DocCommonsLogger.createForTag("{HTTP_POSTMAN}");

    /**
     * The response obtained from a postman request.
     */
    public static class Response {
        private Integer mResponseCode;
        private String mResponseBody;
        private int mContentLength;

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
        public int getContentLength() {
            return mContentLength;
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
     * Creates an http postman.
     * @return an http postman
     */
    public static HttpPostman create() {
        return new HttpPostman();
    }

    /**
     * Creates an http postman for the given request method.
     * @param method the request method
     * @return an http postman
     */
    public static HttpPostman create(HttpPostman.RequestMethod method) {
        return create().method(method);
    }

    /**
     * Creates an http postman for the request method 'HEAD'.
     * @return an http postman
     */
    public static HttpPostman head() {
        return create(HttpPostman.RequestMethod.HEAD);
    }

    /**
     * Creates an http postman for the request method 'GET'.
     * @return an http postman
     */
    public static HttpPostman get() {
        return create(HttpPostman.RequestMethod.GET);
    }

    /**
     * Creates an http postman for the request method 'POST'.
     * @return an http postman
     */
    public static HttpPostman post() {
        return create(HttpPostman.RequestMethod.POST);
    }

    /**
     * Creates an http postman for the request method 'DELETE'.
     * @return an http postman
     */
    public static HttpPostman delete() {
        return create(HttpPostman.RequestMethod.DELETE);
    }

    /**
     * Creates an http postman for the request method 'PUT'.
     * @return an http postman
     */
    public static HttpPostman put() {
        return create(HttpPostman.RequestMethod.PUT);
    }

    /**
     * Creates an http postman for the request method 'HEAD' for the
     * given uri.
     * @param uri an uri
     * @return an http postman
     */
    public static HttpPostman head(String uri) {
        return head().uri(uri);
    }

    /**
     * Creates an http postman for the request method 'GET' for the
     * given uri.
     * @param uri an uri
     * @return an http postman
     */
    public static HttpPostman get(String uri) {
        return get().uri(uri);
    }

    /**
     * Creates an http postman for the request method 'POST' for the
     * given uri.
     * @param uri an uri
     * @return an http postman
     */
    public static HttpPostman post(String uri) {
        return post().uri(uri);
    }

    /**
     * Creates an http postman for the request method 'DELETE' for the
     * given uri.
     * @param uri an uri
     * @return an http postman
     */
    public static HttpPostman delete(String uri) {
        return delete().uri(uri);
    }

    /**
     * Creates an http postman for the request method 'PUT' for the
     * given uri.
     * @param uri an uri
     * @return an http postman
     */
    public static HttpPostman put(String uri) {
        return put().uri(uri);
    }

    /**
     * Sets the given uri.
     * @param uri an uri
     * @return the postman
     */
    public HttpPostman uri(String uri) {
        mURI = uri;
        return this;
    }

    /**
     * Sets the given method.
     * @param method an uri
     * @return the postman
     */
    public HttpPostman method(RequestMethod method) {
        mMethod = method;
        return this;
    }

    /**
     * Sets the 'Basic Authentication' header for the given
     * plain user and password.
     * @param plainUser a plain username
     * @param plainPass a plain password
     * @return the postman
     *
     * @see #basicAuth(String)
     */
    public HttpPostman basicAuth(String plainUser, String plainPass) {
        basicAuth(CryptoUtil.Base64.encode(plainUser + ":" + plainPass));
        return this;
    }

    /**
     * Sets the 'Basic Authentication' header for the given
     * encoded user and password.
     * @param encodedUserPass an already encoded user:pass
     * @return the postman
     *
     * @see #basicAuth(String, String)
     */
    public HttpPostman basicAuth(String encodedUserPass) {
        mEncodedUserPass = encodedUserPass;
        return this;
    }

    /**
     * Sets the body.
     * @param contentType the content type of the body
     * @param bodyData the body string
     * @return the postman
     */
    public HttpPostman body(ContentType contentType, String bodyData) {
        mContentType = contentType;
        mOutData = bodyData;
        return this;
    }

    /**
     * Sends a request for the built postman and returns a response object.
     * @return the response of this request
     */
    public Response send() {
        Response resp = new Response();

        try {
            if (!StringUtil.isValid(mURI) || mMethod == null || mContentType == null)
                L.out("Can't send request, please build HttpPostman with every mandatory field");

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
            resp.mContentLength = conn.getContentLength();

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
     * (from this postman and from other classes too).
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