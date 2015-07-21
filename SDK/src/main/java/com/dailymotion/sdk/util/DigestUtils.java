package com.dailymotion.sdk.util;


import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

public class DigestUtils {
    /**
     * Get MD5 hash of a string
     * @param str the string to hash
     * @return a string which contains hex character
     */
    public static String getMd5Hash(String str, boolean capitalized) {
        try {
            byte hash[] = java.security.MessageDigest.getInstance("MD5").digest(str.getBytes());
            
            BigInteger bi = new BigInteger(1, hash);
            String result = bi.toString(16);
            if (result.length() % 2 != 0) {
                result = "0" + result;
            }
            if(capitalized) { result = result.toUpperCase(); }
            return result;  
            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Get MD5 hash of a string
     * @param str the string to hash
     * @return a string which contains hex character (not capitalized)
     */
    public static String getMd5Hash(String str) {
        return getMd5Hash(str, false);
    }

    /**
     * Get MD5 hash of a string
     * @return a string which contains hex character
     */
    public static byte[] getMd5Hash(byte [] bytes) {
        try {
            byte hash[] = java.security.MessageDigest.getInstance("MD5").digest(bytes);

            return hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
