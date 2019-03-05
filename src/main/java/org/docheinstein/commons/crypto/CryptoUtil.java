package org.docheinstein.commons.crypto;

import org.docheinstein.commons.types.StringUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Provides utilities for encryption/decryption and encoding/decoding.
 */
public class CryptoUtil {

    // Encoding

    public static class Base64 {
        /**
         * Encode bytes to Base64.
         * @param bytes the bytes
         * @return the encoded Base64 string of the bytes
         */
        public static String encode(byte[] bytes) {
            return java.util.Base64.getEncoder().encodeToString(bytes);
        }


        /**
         * Encode a message to Base64.
         * @param message the message
         * @return the encoded Base64 string of the message
         */
        public static String encode(String message) {
            return encode(message.getBytes());
        }

        /**
         * Decode a message from Base64 to bytes[].
         * @param message the message
         * @return the decoded bytes of the Base64 message
         */
        public static byte[] decodeToBytes(String message) {
            return java.util.Base64.getDecoder().decode(message);
        }

        /**
         * Decode a message from Base64.
         * @param message the message
         * @return the decoded string of the Base64 message
         */
        public static String decodeToString(String message) {
            return new String(decodeToBytes(message));
        }
    }

    // One way hash

    public static class SHA256 {
        /**
         * Encodes a message using SHA-256 to bytes.
         * @param message the message
         * @return the bytes of the encoded SHA-256 message
         */
        public static byte[] encodeToBytes(String message) {
            if (!StringUtil.isValid(message))
                return null;
            try {
                return MessageDigest.getInstance("SHA-256")
                    .digest(message.getBytes());
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("SHA-256 not supported");
            }
        }

        /**
         * Encodes the message using SHA-256 to base64 string.
         * @param message the message
         * @return the base64 representation of the encoded message
         */
        public static String encodeToBase64(String message) {
            byte[] bs = encodeToBytes(message);
            return bs == null ? null : Base64.encode(bs);
        }

        /**
         * Encodes the message using SHA-256 to java string.
         * @param message the message
         * @return the string representation of the encoded message
         */
        public static String encodeToString(String message) {
            byte[] bs = encodeToBytes(message);
            return bs == null ? null : new String(bs);
        }
    }

    public static class MD5 {

        /**
         * Encodes the given message using MD5.
         * @param message the message to encode
         * @return the message encoded using MD5
         */
        public static String encode(String message) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(message.getBytes());
                byte[] digest = md.digest();
                StringBuilder sb = new StringBuilder();

                for (byte b : digest)
                    sb.append(String.format("%02x", b & 0xFF));

                return sb.toString();
            } catch (Exception ex) {
                throw new RuntimeException("MD5 encoding failed");
            }
        }
    }

    // Two way cryptography

    public static class AES {

        /**
         * Encrypts the given plain message using AES to base64 string.
         * @param message the message to encrypt
         * @param initVector the AES init vector
         * @param key the AES key
         * @return the encrypted message in base64
         */
        public static String encryptToBase64(String message, String initVector, String key) {
            try {
                IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
                SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);

                return Base64.encode(cipher.doFinal(message.getBytes()));
            } catch (Exception ex) {
                throw new RuntimeException("AES encryption failed");
            }
        }

        /**
         * Decrypts the given message encrypted using AES in base64 to the
         * original plain message.
         * @param encryptedMessage the message to decrypt
         * @param initVector the AES init vector
         * @param key the AES key
         * @return the decrypted message
         */
        public static String decryptFromBase64(String encryptedMessage, String initVector, String key) {
            try {
                IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
                SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);

                byte[] decryptedData = cipher.doFinal(
                    Base64.decodeToBytes(encryptedMessage)
                );
                return new String(decryptedData);

            } catch (Exception ex) {
                throw new RuntimeException("AES decryption failed");
            }
        }

//
//        public static byte[] encryptToBytes(String message, String initVector, String key) {
//            return encryptToBytes(message.getBytes(), initVector, key);
//        }
//
//        public static String encryptToString(byte[] messageBytes, String initVector, String key) {
//            return new String(encryptToBytes(messageBytes, initVector, key));
//        }
//
//        public static String encryptToString(String message, String initVector, String key) {
//            return Base64.encode(encryptToBytes(message, initVector, key));
//        }
//
//        public static String encryptToBase64(byte[] messageBytes, String initVector, String key) {
//            return Base64.encode(encryptToBytes(messageBytes, initVector, key));
//        }
//
//        public static String encryptToBase64(String message, String initVector, String key) {
//            return Base64.encode(encryptToBytes(message, initVector, key));
//        }
//
//
//
//        public static byte[] decryptToBytes(byte[] encryptedBytes, String initVector, String key) {
//            try {
//                IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
//                SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
//
//                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
//                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
//
//                return cipher.doFinal(encryptedBytes);
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                throw new RuntimeException("AES decryption failed");
//            }
//        }
//
//        public static byte[] decryptToBytes(String encryptedMessage, String initVector, String key) {
//            return decryptToBytes(encryptedMessage.getBytes(), initVector, key);
//        }
//
//        public static String decryptToString(byte[] encryptedBytes, String initVector, String key) {
//            return new String(decryptToBytes(encryptedBytes, initVector, key));
//        }
//
//        public static String decryptToString(String encryptedMessage, String initVector, String key) {
//            return new String(decryptToBytes(encryptedMessage, initVector, key));
//        }
//
//        public static String decrypt(String encryptedMessage, String initVector, String key) {
//            try {
//                IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
//                SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
//
//                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
//                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
//
//                byte[] decryptedData = cipher.doFinal(
//                    Base64.decodeToBytes(encryptedMessage)
//                );
//                return new String(decryptedData);
//
//            } catch (Exception ex) {
//                throw new RuntimeException("AES decryption failed");
//            }
//        }
    }

    public static class RSA {

        /**
         * Creates a public key from its base64 representation.
         * <p>
         * Uses X509 as key spec.
         * @param publicKeyBase64 the public key string in base64
         * @return the public key associated with the given string
         */
        public static PublicKey publicKeyFromBase64(String publicKeyBase64) {
            try {
                return KeyFactory.getInstance("RSA").generatePublic(
                    new X509EncodedKeySpec(
                        Base64.decodeToBytes(publicKeyBase64)
                    )
                );
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("RSA setup failed");
            }
        }
        
        /**
         * Creates a private key from its base64 representation.
         * <p>
         * Uses PKCS8E as key spec.
         * @param privateKeyBase64 the private key string in base64
         * @return the private key associated with the given string
         */
        public static PrivateKey privateKeyFromBase64(String privateKeyBase64) {
            try {
                return KeyFactory.getInstance("RSA").generatePrivate(
                    new PKCS8EncodedKeySpec(
                        Base64.decodeToBytes(privateKeyBase64)
                    )
                );
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("RSA setup failed");
            }
        }

        /**
         * Encrypts a message with RSA using the given public key.
         * @param message the message to encrypt
         * @param publicKeyBase64 the base64 string representation of the public key
         * @return the encrypted message in base64
         */
        public static String encryptToBase64(String message, String publicKeyBase64) {
            return encryptToBase64(message, publicKeyFromBase64(publicKeyBase64));
        }
        
        /**
         * Encrypts a message using RSA using the given public key.
         * @param message the message to encrypt
         * @param publicKey the public key
         * @return the encrypted message in base64
         */
        public static String encryptToBase64(String message, PublicKey publicKey) {
            try {
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);

                return Base64.encode(cipher.doFinal(message.getBytes()));
            } catch (Exception ex) {
                throw new RuntimeException("RSA encryption failed");
            }
        }

        /**
         * Decrypts a message with RSA using the given private key.
         * @param message the message to decrypt in base64 
         * @param privateKeyBase64 the base64 string representation of the private key
         * @return the decrypted message
         */
        public static String decryptFromBase64(String message, String privateKeyBase64) {
            return decryptFromBase64(message, privateKeyFromBase64(privateKeyBase64));
        }

        /**
         * Decrypts a message with RSA using the given private key.
         * @param encryptedMessage the message to decrypt in base64
         * @param privateKey the base64 string representation of the private key
         * @return the decrypted message
         */
        public static String decryptFromBase64(String encryptedMessage, PrivateKey privateKey) {
            try {
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);

                return new String(
                    cipher.doFinal(Base64.decodeToBytes(encryptedMessage))
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException("RSA decryption failed");
            }
        }

//        public static byte[] encrypt(String message, PublicKey publicKey) {
//            return encrypt(message.getBytes(), publicKey);
//        }
//
//        public static String encryptToString(byte[] messageBytes, PublicKey publicKey) {
//            return new String(encrypt(messageBytes, publicKey));
//        }
//
//        public static String encryptToString(String message, PublicKey publicKey) {
//            return new String(encrypt(message, publicKey));
//        }
//
//        public static String encryptToBase64(byte[] messageBytes, PublicKey publicKey) {
//            return Base64.encode(encrypt(messageBytes, publicKey));
//        }
//
//        public static String encryptToBase64(String message, PublicKey publicKey) {
//            return Base64.encode(encrypt(message, publicKey));
//        }



//        public static byte[] decrypt(String encryptedMessage, PrivateKey privateKey) {
//            return decrypt(encryptedMessage.getBytes(), privateKey);
//        }
//
//        public static String decryptToString(byte[] encryptedData, PrivateKey privateKey) {
//            return new String(decrypt(encryptedData, privateKey));
//        }
//
//        public static String decryptToString(String encryptedMessage, PrivateKey privateKey) {
//            return new String(decrypt(encryptedMessage, privateKey));
//        }

    }

}
