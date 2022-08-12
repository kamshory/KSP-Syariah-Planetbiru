package com.example.planetbiru.utility;

import android.util.Base64;
import android.util.Log;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {

    public enum DataType {
        HEX,
        BASE64
    }
    private static String TAG = "AESUtil";
    private static final String CIPHER_ALGORITHM = "AES/ECB/NoPadding";
    private static final String KEY_ALGORITHM = "AES";

    private final int IV_SIZE = 0;

    private int iterationCount = 1989;
    private int keySize = 256;

    private int saltLength;

    private final DataType dataType = DataType.BASE64;

    private Cipher cipher;
    public static SecretKeySpec setKey(byte [] key){
        key = Arrays.copyOf(key, 16);
        SecretKeySpec secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
        return secretKey;
    }

    public static String encrypt(final String secret, final String strToEncrypt) {
        try {
            byte[] key = fromHex(secret);
            SecretKeySpec secretKey = setKey(key);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return toBase64(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(final String secret, final String strToDecrypt) {
        try {
            byte[] key = fromHex(secret);
            SecretKeySpec secretKey = setKey(key);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(fromBase64(strToDecrypt)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] fromBase64(String str) {
        return Base64.decode(str, 0);
    }

    private static String toBase64(byte[] ba) {
        return new String(Base64.encode(ba, 0));
    }

    private static byte[] fromHex(String str) {
        byte[] ans = new byte[str.length() / 2];
        for (int i = 0; i < ans.length; i++) {
            int index = i * 2;

            // Using parseInt() method of Integer class
            int val = Integer.parseInt(str.substring(index, index + 2), 16);
            ans[i] = (byte)val;
        }
        return ans;
    }

    private static String toHex(byte[] ba) {
        BigInteger n = new BigInteger(ba);
        String hexa = n.toString(16);
        return hexa;
    }

    private byte[] doFinal(int mode, SecretKey secretKey, String iv, byte[] bytes) {
        try {
            cipher.init(mode, secretKey, new IvParameterSpec(fromHex(iv)));
            return cipher.doFinal(bytes);
        } catch (InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
                | InvalidKeyException e) {
            Log.d(TAG, e.getMessage());
        }
        return null;
    }

    private static byte[] generateRandom(int length) {
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[length];
        random.nextBytes(randomBytes);
        return randomBytes;
    }

}