package com.rockchip.keily.jni;

/**
 * Created by keily on 2016/11/1.
 */

public class ReadPrivate {

    /**
     * 读取授权码（sn）
     *
     * Created by lancy on 2019/9/18 2:42
     */
    public native static String readAuthorizeCode();

    /**
     * 读激活码（MYKTV）
     *
     * @return 私有分区保存的MYKTV激活码
     *
     * Created by lancy on 2019/9/18 2:45
     */
    public native static String readDynamicCode();

    /**
     * 写激活码到私有分区(MYKTV)
     *
     * @param dynamicCode MYKTV激活成功后返回给客户端的动态码
     *
     *                    Created by lancy on 2019/9/18 2:44
     */
    public native static boolean writeDynamicCode(String dynamicCode);

    /**
     * 读激活码（IKTV）
     *
     * @return 私有分区保存的IKTV激活码
     *
     * Created by lancy on 2019/9/18 2:45
     * .
     *
     */
    public native static String readActivateCode();

    /**
     * 写激活码到私有分区（IKTV）
     *
     * @param activateCode IKTV激活成功后返回给客户端的动态码
     *
     *                     Created by lancy on 2019/9/18 2:46
     */
    public native static boolean writeActivateCode(String activateCode);

    static {
        System.loadLibrary("privatedatajni");
    }
}
