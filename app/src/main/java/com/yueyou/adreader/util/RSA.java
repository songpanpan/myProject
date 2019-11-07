package com.yueyou.adreader.util;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by zy on 2017/3/28.
 */

public class RSA {
    public static  byte[] mPrivateKeyBit;
    public static  byte[] mPublicKeyBit;
   public static void createKeys(){
       try {
           KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
           keyPairGen.initialize(1024);
           KeyPair keyPair = keyPairGen.generateKeyPair();
           RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
           RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
           mPublicKeyBit = publicKey.getEncoded();
           mPrivateKeyBit = privateKey.getEncoded();
           String str = "";
           for(int i = 0; i < mPrivateKeyBit.length; i++)
               str += mPrivateKeyBit[i] + ",";
           String str1 = "";
           for(int i = 0; i < mPublicKeyBit.length; i++)
               str1 += mPublicKeyBit[i] + ",";
           String str2 = str1;
           str2 = "333";
           //String privateKeyStr = Base64.encodeToString(privateKey.getEncoded(), Base64.DEFAULT);
           //String publicKeyStr = Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
       } catch (Exception e) {
           e.printStackTrace();
       }
   }

   public static String sign(String content, byte[] privateKeyBit){
       try {
           PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(privateKeyBit);
           KeyFactory keyf = KeyFactory.getInstance("RSA");
           PrivateKey priKey = keyf.generatePrivate(priPKCS8);
           java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");
           signature.initSign(priKey);
           signature.update(content.getBytes("utf-8"));
           byte[] signed = signature.sign();
           return Base64.encodeToString(signed, Base64.DEFAULT);
       } catch (Exception e) {
           e.printStackTrace();
       }
       return null;
   }

   public static boolean verify(String content, byte[] publicKeyBit, String sign){
       try{
           KeyFactory keyf = KeyFactory.getInstance("RSA");
           PublicKey publicKey = keyf.generatePublic(new X509EncodedKeySpec(publicKeyBit));
           java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");
           signature.initVerify(publicKey);
           signature.update(content.getBytes("utf-8"));
           byte[] signBit = Base64.decode(sign, Base64.DEFAULT);
           boolean bverify = signature.verify(signBit);
           return  bverify;
       } catch (Exception e) {
           e.printStackTrace();
       }
       return false;
   }
}
