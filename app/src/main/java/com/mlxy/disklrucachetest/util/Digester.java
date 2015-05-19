package com.mlxy.disklrucachetest.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Digester {
    /** 对数据作MD5加密。 */
    public static String hashUp(String src) {
        String hash = null;

        try {
            byte[] md5 = MessageDigest.getInstance("md5").digest(src.getBytes());

            StringBuilder builder = new StringBuilder();
            for (byte b : md5) {
                if ((b & 0xff) < 0x10) {
                    builder.append("0");
                }

                builder.append(Integer.toHexString(b & 0xff));
            }
            hash = builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hash;
    }
}
