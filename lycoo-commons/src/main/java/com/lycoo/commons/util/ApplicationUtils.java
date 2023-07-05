package com.lycoo.commons.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.lycoo.commons.domain.CommonConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用工具类，主要用来获取应用的信息
 * <p>
 * Created by lancy on 2017/3/12
 */
@SuppressLint("NewApi")
public class ApplicationUtils {
    private static final String TAG = ApplicationUtils.class.getSimpleName();

    /**
     * check the app is running in top
     *
     * @param context
     * @param packageName the app's packageName
     * @return true: the app is running now, otherwise false
     *
     * Created by lancy on 2017/6/8 18:15
     */
    @SuppressWarnings("deprecation")
    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(100);
        if (list == null)
            return false;

        for (RunningTaskInfo info : list) {
            if (info == null)
                continue;

            if (info.topActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查应用是否安装在系统中
     *
     * @param context     上下午
     * @param packageName 要检测应用的包名
     * @return 如果系统中安装了此应用返回true， 否则返回false
     *
     * Created by lancy on 2019/1/9 13:28
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }

        PackageInfo packageInfo = null;
        try {
            packageInfo = context
                    .getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (NameNotFoundException e) {
        }

        return packageInfo != null;
    }

    /**
     * get the app running VersionCode
     *
     * @param context
     * @return the app versionCode, otherwise -1.
     *
     * Created by lancy on 2017/6/8 18:15
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager mPackageManager = context.getPackageManager();
            PackageInfo packageInfo = mPackageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * get the app VersionCode
     *
     * @param context
     * @param packageName the app's packageName
     * @return the app versionCode, otherwise -1.
     *
     * Created by lancy on 2017/6/8 18:16
     */
    public static int getVersionCode(Context context, String packageName) {
        try {
            PackageManager mPackageManager = context.getPackageManager();
            PackageInfo packageInfo = mPackageManager.getPackageInfo(packageName, 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * get the app running versionName
     *
     * @param context
     * @return the app's versionName, otherwise "0.0"
     *
     * Created by lancy on 2017/6/8 18:16
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager mPackageManager = context.getPackageManager();
            PackageInfo packageInfo = mPackageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return "0.0";
    }

    /**
     * get the app versionName
     *
     * @param context
     * @param packageName the app's packageName
     * @return the app's versionName, otherwise "0.0"
     *
     * Created by lancy on 2017/6/8 18:16
     */
    public static String getVersionName(Context context, String packageName) {
        try {
            PackageManager mPackageManager = context.getPackageManager();
            PackageInfo packageInfo = mPackageManager.getPackageInfo(packageName, 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "0.0";
    }

    /**
     * get the app running label
     *
     * @param context
     * @return the app running label, otherwise invalidLabel
     *
     * Created by lancy on 2017/6/8 18:16
     */
    public static String getAppName(Context context) {
        try {
            PackageManager mPackageManager = context.getPackageManager();
            PackageInfo packageInfo = mPackageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.applicationInfo.name;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return "invalidLabel";
    }

    public static String getAppLabel(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            return (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return "invalidLabel";
    }

    public static String getAppLabel(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            return (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return "invalidLabel";
    }

    public static Drawable getAppIcon(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);
            return info.loadIcon(packageManager);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * get the metaData of the application<br>
     * 1. appKey配置在AndroidManifest.xml的application标签中：<meta-data android:name="appKey" android:value="jarTest" /><br>
     * 2. appKey必须包含英文字母，可以含有下划线数字等，切记不能为纯数字<br>
     *
     * @param context
     * @param key     此标记必须为appKey， 不能更改,否则会与服务无法正确对应
     * @return the specified key's value, otherwise "";
     *
     * Created by lancy on 2017/6/8 18:16
     */
    public static String getApplicationMetaData(Context context, String key) {
        String value;
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            value = appInfo.metaData.getString(key);
            if (null == value) {
                value = "";
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            value = "";
        }

        return value;
    }

    /**
     * uninstall app by system PacageInstaller
     *
     * @param context
     * @param packageName the app's packageName
     *
     *                    Created by lancy on 2017/6/8 18:16
     */
    public static void uninstallApp(Context context, String packageName) {
        Uri packageURI = Uri.parse("package:" + packageName);
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        context.startActivity(uninstallIntent);
    }

    /**
     * install app by system PacageInstaller
     *
     * @param context
     * @param uri     the app's path Uri
     *
     *                Created by lancy on 2017/6/8 18:16
     */
    public static void installApp(Context context, Uri uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * get running app's packageName
     *
     * @param context
     * @return Created by lancy on 2017/6/8 18:16
     */
    @SuppressWarnings("deprecation")
    public static String getTopAppPackageName(Context context) {
        String packageName = "";
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1);
            if (!CollectionUtils.isEmpty(runningTasks)) {
                ComponentName componentName = runningTasks.get(0).topActivity;
                if (componentName != null) {
                    packageName = componentName.getPackageName();
                }
            }
        }
        return packageName;
    }

    /**
     * get top activity's class name
     *
     * @param context
     * @return Created by lancy on 2017/6/8 18:17
     */
    @SuppressWarnings("deprecation")
    public static String getTopActivity(Context context) {
        String topActivity = "";
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName componentName = activityManager.getRunningTasks(1).get(0).topActivity;
        if (null != componentName) {
            topActivity = componentName.getClassName();
        }

        return topActivity;
    }

    /**
     * get apps that can launcher in system
     *
     * @param mContext
     * @return Created by lancy on 2017/6/8 18:17
     */
    public static List<ResolveInfo> getAllLauncherResolveInfos(Context mContext) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return mContext.getPackageManager().queryIntentActivities(intent, 0);
    }

    /**
     * 获取指定应用的ResolveInfo
     *
     * @param context     上下文
     * @param packageName 应用包名
     * @return 指定应用的ResolveInfo
     *
     * Created by lancy on 2018/6/20 10:48
     */
    public static ResolveInfo getResolveInfo(Context context, String packageName) {
        ResolveInfo resolveInfo = null;
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(packageName);
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
        if (resolveInfos != null && resolveInfos.size() > 0) {
            resolveInfo = resolveInfos.get(0);
        }
        return resolveInfo;
    }

    /**
     * get app's level
     *
     * @param appInfo
     * @return 1：user app 0:system app
     *
     * Created by lancy on 2017/6/8 18:17
     */
    public static int categorizeAppByLevel(ApplicationInfo appInfo) {
        // 1. 系统程序，但是用户自己升级了，所以就变成了user app
        if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return 1;
        }
        // 2. 用户自己按安装的app
        else if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return 1;
        }
        // 3. System app
        return 0;
    }

    /**
     * 启动应用
     *
     * @param context     上下文
     * @param packageName 应用包名
     * @param className   启动Activity全类名
     *
     *                    Created by lancy on 2019/8/11 15:41
     */
    public static void openApplication(Context context, String packageName, String className) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, className));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 启动应用
     *
     * @param context 上下文
     * @param uri     rui
     *
     *                Created by lancy on 2019/8/11 15:42
     */
    public static void openApplication(Context context, Uri uri) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(uri);
        context.startActivity(intent);
    }

    /**
     * Open app by PackageName or PackageName/LauncherClassName
     *
     * @param compnent App info
     *
     *                 Created by lancy on 2017/6/8 18:17
     */

    /**
     * @param context     上下文
     * @param packageName 包名/包名+类名
     *
     *                    Created by lancy on 2018/8/14 11:27
     */
    public static void openApplication(Context context, String packageName) {
        if (packageName != null && !packageName.isEmpty()) {
            if (packageName.contains("/.")) {
                String[] split = packageName.split("/");
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(split[0], split[0] + split[1]));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else if (packageName.contains("/")) {
                String[] split = packageName.split("/");
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(split[0], split[1]));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }

    /**
     * 启动服务
     *
     * Created by lancy on 2019/8/11 15:43
     */
    public static void startService(Context context, String action) {
        if (TextUtils.isEmpty(action)) {
            return;
        }
        context.startService(new Intent(action));
    }

    /**
     * 清理内存,杀死后台进程
     * <p>
     * Created by lancy on 2016/8/6
     */
    public static void cleanMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = activityManager.getRunningAppProcesses();
        if (null == runningAppProcessInfos || runningAppProcessInfos.isEmpty())
            return;

        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcessInfos) {
            String[] pkgList = processInfo.pkgList;
            if (processInfo.importance >= ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) {
                for (int i = 0; i < pkgList.length; i++) {
                    LogUtils.debug(TAG, "----- kill process's packageName = " + pkgList[i]);
                    activityManager.killBackgroundProcesses(pkgList[i]);
                }
            }
        }
    }

    /**
     * U盘安装KTV
     * <p>
     * Created by ovo on 2023/2/7
     */
    public static void installedUsbKtv(Context context, String path){
        if (isAppInstalled(context,"com.lycoo.lancy.ktv")){
            return;
        }
        ArrayList<String> data = new ArrayList<>();
        for (File file : new File(path).listFiles()){
            if (file.getName().contains("LycooKTV_SuperMan")){
/*                Intent intentUsb = new Intent(Intent.ACTION_VIEW);
                intentUsb.setDataAndType( Uri.parse("file://" + file),"application/vnd.android.package-archive");
                intentUsb.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentUsb);*/
                data.add(file.getPath());
                new Handler().postDelayed(() -> {
                    CommonUtils.sendInstallPackageBroadcast(context, data);
                }, 1000);
            }
        }
    }

    public static void sendEmptyMessage(Handler hander, int what) {
        hander.sendEmptyMessage(what);
    }

    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void showInstallDialog(Intent intent, Context context) {
        String packageName = intent.getStringExtra(CommonConstants.PACKAGEINSTALL_PACKAGENAME);
        if (packageName.equals("com.lycoo.lancy.ktv")){
            showNormalDialog(context);
        }
    }
    private static void showNormalDialog(Context context){
        AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
        normalDialog.setTitle("温馨提示");
        normalDialog.setMessage("激活完成！！！");
        normalDialog.setPositiveButton("确定",
                (dialog, which) -> {
                });
        normalDialog.setNegativeButton("关闭",
                (dialog, which) -> {
                });
        // 显示
        normalDialog.show();
    }
}
