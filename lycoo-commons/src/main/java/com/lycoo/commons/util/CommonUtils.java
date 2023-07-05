package com.lycoo.commons.util;

import android.content.Context;
import android.content.Intent;

import com.lycoo.commons.domain.CommonConstants;

import java.util.ArrayList;

/**
 * 工具类
 * 只适合懿美莱科技
 *
 * Created by lancy on 2017/3/9
 */

public class CommonUtils {

    /**
     * 发送安装应用广播
     *
     * @param context 上下文
     * @param data    安装包信息(apk路径)
     *
     *                Created by lancy on 2017/6/15 22:39
     */
    public static void sendInstallPackageBroadcast(Context context, ArrayList<String> data) {
        Intent intent = new Intent();
        intent.setAction(CommonConstants.ACTION_PACKAGEINSTALL);
        intent.putExtra(CommonConstants.EXECUTE_MODE, CommonConstants.MODE_INSTALL);
        intent.putStringArrayListExtra(CommonConstants.EXECUTE_DATA, data);
        context.sendBroadcast(intent);
    }

    /**
     * 发送卸载应用广播
     *
     * @param context 上下文
     * @param data    要卸载应用的包名
     *
     *                Created by lancy on 2017/6/15 22:38
     */
    public static void sendUnInstallPackageBroadcast(Context context, ArrayList<String> data) {
        Intent intent = new Intent();
        intent.setAction(CommonConstants.ACTION_PACKAGEINSTALL);
        intent.putExtra(CommonConstants.EXECUTE_MODE, CommonConstants.MODE_UNINSTALL);
        intent.putStringArrayListExtra(CommonConstants.EXECUTE_DATA, data);
        context.sendBroadcast(intent);
    }

    /**
     * 发送安装ota升级包广播
     *
     * @param context     上下文
     * @param packageFile 升级包
     *
     *                    Created by lancy on 2017/7/4 0:23
     */
    public static void sendInstallOtaPackageBroadcast(Context context, String packageFile) {
        Intent intent = new Intent();
        intent.setAction(CommonConstants.ACTION_INSTALL_OTA_PACKAGE);
        intent.putExtra(CommonConstants.PACKAGE_FILE, packageFile);
        context.sendBroadcast(intent);
    }
}
