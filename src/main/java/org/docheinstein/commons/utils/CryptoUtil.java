package org.docheinstein.commons.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Provides utilities for encryption/decryption and encoding/decoding.
 */
public class CryptoUtil {

    public static class SHA256 {
        /**
         * Encodes a message using SHA-256 and converting the bytes to
         * string using Base64 notation.
         * @param message the message
         * @return the Base64 string of the encoded SHA-256 message
         */
        public static String encodeBase64(String message) {
            if (!StringUtil.isValid(message))
                return null;

            try {
                return java.util.Base64.getEncoder()
                    .encodeToString(
                        MessageDigest.getInstance("SHA-256").digest(message.getBytes())
                    );

            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("SHA-256 not supported");
            }
        }
    }

    public static class Base64 {
        /**
         * Encode a message to Base64.
         * @param message the message
         * @return the encoded Base64 string of the message
         */
        public static String encode(String message) {
            return java.util.Base64.getEncoder().encodeToString(message.getBytes());
        }

        /**
         * Decode a message from Base64.
         * @param message the message
         * @return the decoded string of the Base64 message
         */
        public static String decode(String message) {
            return new String(java.util.Base64.getDecoder().decode(message));
        }
    }
}
