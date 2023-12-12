package srimalar.core.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public abstract class EncryptDecrypt {
    private static final Logger log = LoggerFactory.getLogger(EncryptDecrypt.class);

    private static MessageDigest messageDigest;

    private static MessageDigest getMessageDigest256() throws NoSuchAlgorithmException {
        if (messageDigest == null) {
            messageDigest = MessageDigest.getInstance("SHA-256");
        }

        return messageDigest;
    }

    private static SecretKey getSecretEncryptionKey(String keyText) {
        return new SecretKeySpec(getKey(keyText), "AES");
    }

    private static byte[] getKey(String keyStr) {
        byte[] key = null;

        try {
            key = keyStr.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = getMessageDigest256();
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return key;
    }

    public static byte[] hexToBytes(String content) {
        int len = content.length();
        byte[] data = new byte[len / 2];

        for(int i = 0; i < len; i += 2) {
            data[i / 2] = (byte)((Character.digit(content.charAt(i), 16) << 4) + Character.digit(content.charAt(i + 1), 16));
        }

        return data;
    }

    public static String bytesToHex(byte[] byteArray) {
        StringBuilder builder = new StringBuilder();
        for (byte data : byteArray) {
            builder.append(String.format("%02x", data));
        }
        return builder.toString();
    }

    public static String getSHA256(String base) {
        try {
            byte[] hash = getMessageDigest256().digest(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            byte[] var3 = hash;
            int var4 = hash.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                byte b = var3[var5];
                String hex = Integer.toHexString(255 & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception var8) {
            throw new RuntimeException(var8);
        }
    }

    public static String encryptHmacSha256(String key, String text) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            return encodeBase64(mac.doFinal(text.getBytes()));
        } catch (Exception var4) {
            if (log.isDebugEnabled()) {
                log.error("Encrypt HMAC sha256 error", var4);
            } else {
                log.error("ERROR : ENCRYPTION Hmac Sha256 " + var4.getMessage());
            }
        }
        return null;
    }

    public static String encryptPKCS5Padding(String keyCode, String content) {
        if (keyCode != null && content != null) {
            try {
                SecretKey secKey = getSecretEncryptionKey(keyCode);
                Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                aesCipher.init(1, secKey);
                return bytesToHex(aesCipher.doFinal(content.getBytes()));
            } catch (Exception var4) {
                if (log.isDebugEnabled()) {
                    log.error("Encrypt HMAC sha256 error", var4);
                } else {
                    log.error("ERROR : ENCRYPTION Hmac Sha256 " + var4.getMessage());
                }
                log.error("ERROR: Encrypt PKCS5Padding", var4);
            }
        }

        return null;
    }

    public static String decryptPKCS5Padding(String keyCode, String content) {
        if (keyCode != null && content != null) {
            try {
                byte[] contentBytes = hexToBytes(content);
                SecretKey secKey = getSecretEncryptionKey(keyCode);
                Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                aesCipher.init(2, secKey);
                byte[] bytePlainText = aesCipher.doFinal(contentBytes);
                return new String(bytePlainText, StandardCharsets.UTF_8);
            } catch (Exception var6) {
                if (log.isDebugEnabled()) {
                    log.error("ERROR: decrypt PKCS5 padding ", var6);
                } else {
                    log.error("ERROR: decrypt PKCS5 padding ," + var6.getMessage());
                }
            }
        }
        return null;
    }

    public static String decodeBase64(String value) {
        return new String(decodeBase64Byte(value), StandardCharsets.UTF_8);
    }

    public static byte[] decodeBase64Byte(String value) {
        return Base64.getDecoder().decode(value);
    }

    public static String decodeBase64(byte[] bytes) {
        return new String(Base64.getDecoder().decode(bytes), StandardCharsets.UTF_8);
    }

    public static String encodeBase64(String value) {
        return encodeBase64(value.getBytes());
    }

    public static String encodeBase64(byte[] value) {
        return Base64.getEncoder().encodeToString(value);
    }

    public static String decodeURIComponent(String txt) {
        if (txt != null) {
            return URLDecoder.decode(txt, StandardCharsets.UTF_8);
        }
        return null;
    }

    public static String encodeURIComponent(String txt) {
        String result;
        try {
            result = URLEncoder.encode(txt, "UTF-8").replaceAll("\\+", "%20").replaceAll("\\%21", "!").replaceAll("\\%27", "'").replaceAll("\\%28", "(").replaceAll("\\%29", ")").replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException var3) {
            result = txt;
        }

        return result;
    }

}
