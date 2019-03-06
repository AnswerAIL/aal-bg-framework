package com.answer.bdframework.algorithm;

import static com.answer.bdframework.common.LoggerCommon.info;
import static com.answer.bdframework.common.LoggerCommon.error;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.util.Random;

/**
 * Created by L.Answer on 2018-07-31 11:09
 *
 * default encrypt algorithum for type String{@link com.answer.bdframework.entity.BDType#String}
 */
public class Algorithum3DES extends AlgorithmAbs {
    /**
     * encrypt method
     * @param source 加密字符串
     * @param salt 长度必须大等于24位
     * */
    @Override
    public String encrypt(String source, String salt) throws Exception {
        if (salt == null) {
            salt = generateSalt();
            info(Thread.currentThread().getStackTrace()[1], "random generation's salt is 【"+ salt +"】");
        }
        if (salt.length() < 24)
            error(Thread.currentThread().getStackTrace()[1], "salt's length must be egt 24.");
        DESedeKeySpec dks = new DESedeKeySpec(salt.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey securekey = keyFactory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, securekey);
        byte[] b = cipher.doFinal(source.getBytes());

        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(b);
    }

    /**
     * decrypt method
     * @param source 加密字符串
     * @param salt 长度必须大等于24位
     * */
    @Override
    public String decrypt(String source, String salt) throws Exception {
        if (salt.length() < 24)
            error(Thread.currentThread().getStackTrace()[1], "salt's length must be egt 24.");
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytesrc = decoder.decodeBuffer(source);
        DESedeKeySpec dks = new DESedeKeySpec(salt.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey securekey = keyFactory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, securekey);
        byte[] retByte = cipher.doFinal(bytesrc);
        return new String(retByte);
    }


    private String generateSalt() {
        char[] chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 24; i++) {
            int index = random.nextInt(62);
            stringBuilder.append(chars[index]);
        }
        return stringBuilder.toString();
    }
}