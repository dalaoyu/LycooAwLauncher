package com.lycoo.commons.util;

import android.os.Build;
import android.util.Base64;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 授权，验证，加解密辅助类
 *
 * Created by lancy on 2017/6/19
 */
public class SecurityUtils {
    private static final int DEFAULT_REVERSE_LENGTH = 50;

    public static byte[] desEncrypt(String transformation, String passwordKey, String ivKey, String data) {
        try {
            byte[] tmp = Base64.decode(data, Base64.DEFAULT);

            Cipher cipher = Cipher.getInstance(transformation);
            SecretKeySpec keyspec = new SecretKeySpec(passwordKey.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(ivKey.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            return cipher.doFinal(tmp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean verifySpecialstr(String key) {
        String host = Build.HOST;
        return key.equals(host);
    }

    /**
     * 校验
     * 如果keys里边包含Build.HOST则代表校验成功， 否则校验失败
     *
     * @param keys key集合
     * @return 校验成功返回true， 否则返回false
     *
     * Created by lancy on 2018/6/21 10:58
     */
    public static boolean verifySpecialstr(String[] keys) {
        String host = Build.HOST;
        if (keys != null && keys.length > 0) {
            for (String key : keys) {
                if (key.equals(host)) {
                    return true;
                }
            }
        }
        // TODO: 2018/5/18 发布的时候修改
        return false;
    }

    /**
     * 加密解密文件
     *
     * @param file          要加密或解密的文件
     * @param reverseLength 文件加密或解密的长度， 例如reverseLength == 100， 则表示对文件的前100个字节进行加密或者解密
     * @return 加密成功返回true， 否则返回false
     *
     * Created by lancy on 2018/6/21 11:07
     */
    public static boolean encryptAndDecrypt(String file, int reverseLength) {
        return encryptAndDecrypt(new File(file), reverseLength);
    }

    /**
     * 加密解密文件
     *
     * @param file          要加密或解密的文件
     * @param reverseLength 文件加密或解密的长度， 例如reverseLength == 100， 则表示对文件的前100个字节进行加密或者解密
     * @return 加密成功返回true， 否则返回false
     *
     * Created by lancy on 2018/6/21 11:07
     */
    public static boolean encryptAndDecrypt(File file, int reverseLength) {
        if (reverseLength <= 1) {
            reverseLength = DEFAULT_REVERSE_LENGTH;
        }

        int len = reverseLength;
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            long totalLen = raf.length();

            if (totalLen < reverseLength) {
                len = (int) totalLen;
            }

            FileChannel channel = raf.getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, reverseLength);
            byte tmp;
            for (int i = 0; i < len; ++i) {
                byte rawByte = buffer.get(i);
                tmp = (byte) (rawByte ^ i);
                buffer.put(i, tmp);
            }
            buffer.force();
            buffer.clear();
            channel.close();
            raf.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 十六进制GBK转String
     * @param s
     * @return
     */
    public static String toStringHex2(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
                        i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "GBK");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    /**
     * @param: [content]
     * @return: int
     * @description: 十六进制转十进制
     */
    public static int covert(String numStr) {
        String content = numStr.toUpperCase(Locale.ROOT);
        int number = 0;
        String[] HighLetter = {"A", "B", "C", "D", "E", "F"};
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i <= 9; i++) {
            map.put(i + "", i);
        }
        for (int j = 10; j < HighLetter.length + 10; j++) {
            map.put(HighLetter[j - 10], j);
        }
        String[] str = new String[content.length()];
        for (int i = 0; i < str.length; i++) {
            str[i] = content.substring(i, i + 1);
        }
        for (int i = 0; i < str.length; i++) {
            number += map.get(str[i]) * Math.pow(16, str.length - 1 - i);
        }
        return number;
    }


}
