package com.yueyou.adreader.util;

import android.util.Base64;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by zy on 2017/3/28.
 */
public class DES {
    public static String encryption(String content, String desKey) {
        try {
            SecureRandom sr = new SecureRandom();
            DESKeySpec dks = new DESKeySpec(desKey.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
            byte[] bytes = cipher.doFinal(content.getBytes());
            String base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
            return base64;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryption(String content, String desKey){
        try {
            byte[] bytes = Base64.decode(content, Base64.DEFAULT);
            SecureRandom sr = new SecureRandom();
            DESKeySpec dks = new DESKeySpec(desKey.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
            byte[] ret = cipher.doFinal(bytes);
            String str = new String(ret, "UTF-8");
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
