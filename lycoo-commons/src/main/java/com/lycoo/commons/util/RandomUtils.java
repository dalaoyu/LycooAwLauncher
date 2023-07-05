package com.lycoo.commons.util;

import java.util.Random;
import java.util.UUID;

/**
 * 随机数生成器<br>
 * 说明：<br>
 * Random strGen = new Random();
 * strGen.nextInt(10);
 * 输出结果为：0~9
 * <p>
 * Created by lancy on 2017/4/10 13:43
 */
public class RandomUtils {

    /**
     * 生成公司唯一标识符
     * 使用Random生成一个32位的字符串作为公司唯一标识符，字符由[a-z, A-Z, 0-9]组成
     *
     * @return 公司唯一标识符（32位）
     *
     * Created by lancy on 2017/4/20 15:14
     */
    public static String generateCompanyCode() {
        Random strGen = new Random();
        char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
        char[] randBuffer = new char[32];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[strGen.nextInt(62)];
        }

        return new String(randBuffer);
    }

    /**
     * 生成固件唯一标识符
     *
     * Created by lancy on 2017/4/10 13:43
     */
    public static String generateFirmwareCode() {
        Random strGen = new Random();
        char[] numbersAndLetters = ("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
        char[] randBuffer = new char[32];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[strGen.nextInt(36)];
        }

        return new String(randBuffer);
    }

    /**
     * 生成应用唯一标识符
     * <p>
     * Created by lancy on 2017/4/10 13:42
     */
    public static String generateAppCode() {
        Random strGen = new Random();
        char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz").toCharArray();
        char[] randBuffer = new char[32];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[strGen.nextInt(36)];
        }

        return new String(randBuffer);
    }

    /**
     * 生成应用唯一标识符
     *
     * Created by lancy on 2019/9/2 16:18
     */
    public static String generateSn(int len) {
        Random strGen = new Random();
        char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
        char[] randBuffer = new char[18];
        for (int i = 0; i < randBuffer.length; i++) {
            if (i == 3) {
                randBuffer[i] = 'L';
            } else if (i == 6) {
                randBuffer[i] = 'Y';
            } else if (i == 9) {
                randBuffer[i] = 'C';
            } else if (i == 12) {
                randBuffer[i] = 'O';
            } else if (i == 15) {
                randBuffer[i] = 'O';
            } else {
                randBuffer[i] = numbersAndLetters[strGen.nextInt(62)];
            }
        }
        return new String(randBuffer);
    }

    /**
     * 生成客户码
     * <p>
     * Created by lancy on 2017/4/10 13:42
     */
    public static String generateCustomerCode() {
        Random strGen = new Random();
        char[] numbersAndLetters = ("0123456789").toCharArray();
        char[] randBuffer = new char[7];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[strGen.nextInt(10)];
        }

        return new String(randBuffer);
    }

    public static String randomName() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成一个随机名字，当前毫秒数+三位随机数
     * <p>
     * Created by lancy on 2017/4/10 13:42
     */
    public static String generateRandomName() {
        // 取当前时间的长整形值包含毫秒
        long millis = System.currentTimeMillis();
        // long millis = System.nanoTime();
        // 加上三位随机数
        Random random = new Random();
        int end3 = random.nextInt(1000);
        // 如果不足三位前面补0
        String str = millis + String.format("%03d", end3);

        return str;
    }

    /**
     * 生成一个随机Id，当前毫秒数+二位随机数
     * <p>
     * created by lancy. 2017年2月14日--------------------------------
     */
    public static long generateRandomId() {
        // 取当前时间的长整形值包含毫秒
        long millis = System.currentTimeMillis();
        // long millis = System.nanoTime();
        // 加上两位随机数
        Random random = new Random();
        int end2 = random.nextInt(100);
        // 如果不足两位前面补0
        String str = millis + String.format("%02d", end2);
        long id = new Long(str);
        return id;
    }

    public static String generateAuthorizationCode() {
        Random strGen = new Random();
        char[] numbersAndLetters = ("0123456789").toCharArray();
        char[] randBuffer = new char[12];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[strGen.nextInt(10)];
        }

        return new String(randBuffer);
    }

    /**
     * 随机字符串16位
     * @return
     */
    public static String generateRandomNumber() {
        Random strGen = new Random();
        char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
        char[] randBuffer = new char[16];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[strGen.nextInt(62)];
        }

        return new String(randBuffer);
    }

}
