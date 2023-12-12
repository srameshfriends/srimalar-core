package srimalar.core.model;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.Random;

public class EncryptJsPipe {
    private final byte[] keyBytes;
    private final String keyBase64;
    private Cipher cipher;

    /*
    * Key length is bounded by 16 bit*/
    public EncryptJsPipe() {
        keyBytes = new byte[16];
        new Random().nextBytes(keyBytes);
        keyBase64 = Base64.getEncoder().encodeToString(keyBytes);
    }

    public EncryptJsPipe(String base64Key) {
        if(base64Key == null) {
            throw new RuntimeException("2001, EncryptJsPipe: Base64 string should not be null.");
        }
        this.keyBytes = Base64.getDecoder().decode(base64Key);
        this.keyBase64 = base64Key;
    }

    public static String generateKey() {
        byte[] bytes = new byte[16];
        new Random().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public String getKeyBase64() {
        return keyBase64;
    }

    private Cipher getCipher()  {
        if(cipher == null) {
            try {
                SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
                cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        }
        return cipher;
    }

    public String encrypt(String text) {
        if(text == null) {
            throw new IllegalArgumentException("DecryptJsPipe encrypt text should not be null.");
        }
        try {
            byte[] b1 = text.getBytes();
            byte[] encryptedValue = getCipher().doFinal(b1);
            return Base64.getEncoder().encodeToString(encryptedValue);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EncryptJsPipe that = (EncryptJsPipe) o;
        return keyBase64.equals(that.keyBase64);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyBase64);
    }

    @Override
    public String toString() {
        return keyBase64;
    }

    private void test(String[] args) {
        String key  = EncryptJsPipe.generateKey();
        EncryptJsPipe jsPipe = new EncryptJsPipe(key);
        String result = jsPipe.encrypt("Hi Ramesh, Welcome to your contribution!");
        System.out.println(result);
        System.out.println(jsPipe.getKeyBase64());
    }
}
