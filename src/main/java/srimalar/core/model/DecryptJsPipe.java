package srimalar.core.model;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class DecryptJsPipe {
    private static final String DEFAULT_IV = "7aff6d6cb4b47804d896a6b0ee893df9";
    private final int keySize;
    private final int iterationCount;
    private final Cipher cipher;
    private String ivText, saltText;

    public DecryptJsPipe() {
        this(128, 1000);
    }

    public DecryptJsPipe(String ivText, String saltText) {
        this(128, 1000);
        if(ivText == null) {
            ivText = DEFAULT_IV;
        }
        this.ivText = ivText;
        this.saltText = saltText;
    }

    public DecryptJsPipe(int keySize, int iterationCount) {
        this.keySize = keySize;
        this.iterationCount = iterationCount;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypted(String passPhrase, String ivSaltAndEncrypted) {
        if(passPhrase == null) {
            throw new RuntimeException("1005, Invalid encryption would not be processed.");
        }
        if(ivSaltAndEncrypted == null) {
            throw new RuntimeException("1006, Invalid encryption would not be processed.");
        }
        String[] result = ivSaltAndEncrypted.split("::");
        if(2 == result.length) {
            DecryptJsPipe pipe = new DecryptJsPipe(null, result[0].trim());
            return pipe.decrypt(passPhrase, result[1].trim());
        }  else if(3 == result.length) {
            DecryptJsPipe pipe = new DecryptJsPipe(result[0].trim(), result[1].trim());
            return pipe.decrypt(passPhrase, result[2].trim());
        }
        throw new RuntimeException("1007, Invalid encryption would not be processed.");
    }

    public void setIvText(String ivText) {
        this.ivText = ivText;
    }

    public void setSaltText(String saltText) {
        this.saltText = saltText;
    }

    public String decrypt(String passphrase, String ciphertext) {
        if(ivText == null) {
            throw new NullPointerException("DecryptJsPipe: IV text should not be null.");
        }
        if(saltText == null) {
            throw new NullPointerException("DecryptJsPipe: salt text should not be null.");
        }
        try {
            SecretKey key = generateKey(saltText, passphrase);
            byte[] decrypted = doFinal(key, ivText, base64(ciphertext));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    public String decrypt(String salt, String iv, String passphrase, String ciphertext) {
        try {
            SecretKey key = generateKey(salt, passphrase);
            byte[] decrypted = doFinal(key, iv, base64(ciphertext));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    private byte[] doFinal(SecretKey key, String iv, byte[] bytes) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(hex(iv)));
            return cipher.doFinal(bytes);
        } catch (InvalidKeyException
               | InvalidAlgorithmParameterException
               | IllegalBlockSizeException
               | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    private SecretKey generateKey(String salt, String passphrase) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), hex(salt), iterationCount, keySize);
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return null;
        }
    }

    public static byte[] base64(String str) {
        return Base64.getDecoder().decode(str);
    }

    public static byte[] hex(String str) {
        try {
            return Hex.decodeHex(str.toCharArray());
        } catch (org.apache.commons.codec.DecoderException e) {
            throw new RuntimeException(e);
        }
    }

    private void test2(String[] args) {
        System.out.println(DecryptJsPipe.decrypted("H7bb0VoRN2XMCkXK", "44e28c23f43febd68017be4ed5165eb2::MGm/jQQ1qpLM6rl2W2gEiguh4Smdue4JAErXuyERraceonxEBSE9QgcC5kCcR4Q3NOMpkbdZBpk3acj8Fbrjvw=="));
    }

    private void test3(String[] args) {
        System.out.println(DecryptJsPipe.decrypted("H7bb0VoRN2XMCkXK", "7aff6d6cb4b47804d896a6b0ee893df9::44e28c23f43febd68017be4ed5165eb2::MGm/jQQ1qpLM6rl2W2gEiguh4Smdue4JAErXuyERraceonxEBSE9QgcC5kCcR4Q3NOMpkbdZBpk3acj8Fbrjvw=="));
    }
}
