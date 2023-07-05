package com.lycoo.commons.helper;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.util.CollectionUtils;
import com.lycoo.commons.util.DeviceUtils;
import com.lycoo.commons.util.FileUtils;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.util.SystemPropertiesUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 设备管理器
 *
 * Created by lancy on 2017/10/28
 */
public class DeviceManager {
    private static final String TAG = DeviceManager.class.getSimpleName();
    // DEBUG-LOG **************************************************
    private static final boolean DEBUG_STATFS = true;
    // DEBUG-LOG **************************************************
    private static final boolean DEBUG_USB = true;
    //是否隐藏USB图标
    public static final String PERSIST_SYS_HIDDEN_USB                             = "persist.sys_hidden_usb";

    private static int mPlatformType;

    static {
        // TODO: 2018/5/21 modify when release
        mPlatformType = DeviceUtils.getPlatformType();
    }

    /**
     * 当前平台是否为全志V40
     *
     * @return 如果是V40返回true, 否则返回false
     *
     * Created by lancy on 2018/5/21 11:36
     */
    public static boolean isV40() {
        return mPlatformType == CommonConstants.PLATFORM_V40;
    }

    /**
     * 当前平台是否为全志H3/H2
     *
     * @return 如果是全志H3/H2返回true, 否则返回false
     *
     * Created by lancy on 2018/11/20 18:02
     */
    public static boolean isH3() {
        return mPlatformType == CommonConstants.PLATFORM_H3;
    }


    /**
     * 是否为全志平台
     *
     * @return 如果是全志平台返回true， 否则返回false
     *
     * Created by lancy on 2019/1/8 22:03
     */
    public static boolean isAllwinnerPlatform() {
        return isV40() || isH3();
    }


    /**
     * 当前平台是否为RK3128
     *
     * @return 如果是RK3128返回true, 否则返回false
     *
     * Created by lancy on 2018/5/21 11:36
     */
    public static boolean isRK3128() {
        return mPlatformType == CommonConstants.PLATFORM_RK3128 || mPlatformType == CommonConstants.PLATFORM_RK3128_DEPRECATED;
    }

    /**
     * 当前平台是否为RK3128H
     *
     * @return 如果是RK3128H 返回true, 否则返回false
     *
     * Created by lancy on 2018/6/23 16:15
     */
    public static boolean isRK3128H() {
        return mPlatformType == CommonConstants.PLATFORM_RK3128H;
    }

    /**
     * 当前平台是否为RK3229
     *
     * @return 如果是RK3229 返回true, 否则返回false
     *
     * Created by lancy on 2018/7/19 21:04
     */
    public static boolean isRK3229() {
        return mPlatformType == CommonConstants.PLATFORM_RK3229;
    }

    /**
     * 当前平台是否为RK3368
     *
     * @return 如果是RK3368 返回true, 否则返回false
     *
     * Created by lancy on 2019/2/13 11:03
     */
    public static boolean isRK3368() {
        return mPlatformType == CommonConstants.PLATFORM_RK3368;
    }

    /**
     * 枚举系统所有可用的挂载点
     *
     * @param context
     * @return 当前系统中可用设备挂载点绝对路径
     *
     * Created by lancy on 2017/10/27 14:01
     */
    public static List<String> getDevices(Context context) {
        List<String> devicePaths = null;
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Activity.STORAGE_SERVICE);
            Method method = storageManager.getClass().getMethod("getVolumePaths");
            String[] paths = (String[]) method.invoke(storageManager);
            if (paths != null && paths.length > 0) {
                devicePaths = Arrays.asList(paths);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return devicePaths;
    }

    /**
     * 检查设备是否挂载到系统中
     *
     * @param context    上下文
     * @param mountPoint 设备挂载点（绝对路径）
     * @return true: 设备已挂载， false: 设备未挂载
     *
     * Created by lancy on 2017/10/27 14:03
     */
    public static boolean isDeviceMounted(Context context, String mountPoint) {
        if (TextUtils.isEmpty(mountPoint)) {
            return false;
        }
        // RK3128需要判断上一级
        if ((isRK3128() || isRK3128H() || isRK3368()) && isUsb(mountPoint)) {
            mountPoint = mountPoint.substring(0, mountPoint.lastIndexOf("/"));
        }

        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Method method = storageManager.getClass().getMethod("getVolumeState", String.class);
            String state = (String) method.invoke(storageManager, mountPoint);
            return Environment.MEDIA_MOUNTED.equals(state);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 获取系统中已挂载的设备
     *
     * @param context 上下文
     * @return 系统中已挂载设备的绝对路径
     *
     * Created by lancy on 2017/10/27 14:27
     */
    public static List<String> getMountedDevices(Context context) {
        List<String> mountedPaths = new ArrayList<>();
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Activity.STORAGE_SERVICE);
            Method getVolumePathsMethod = storageManager.getClass().getMethod("getVolumePaths");
            String[] volumePaths = (String[]) getVolumePathsMethod.invoke(storageManager);
            if (volumePaths != null && volumePaths.length > 0) {
                Method getVolumeStateMethod = storageManager.getClass().getMethod("getVolumeState", String.class);
                for (String path : volumePaths) {
//                    LogUtils.debug(TAG, "path : " + path);
                    String state = (String) getVolumeStateMethod.invoke(storageManager, path);
                    if (!TextUtils.isEmpty(path) && state != null && !state.isEmpty() && Environment.MEDIA_MOUNTED.equals(state)) {
                        LogUtils.debug(TAG, "mounted path : " + path);
                        // RK3128 ====================================================================
                        if (isRK3128() || isRK3128H() || isRK3368()) {
                            // USB
                            if (isUsb(path)) {
                                File[] files = new File(path).listFiles();
                                if (files != null && files.length > 0) {
                                    for (File file : files) {
                                        if (DEBUG_USB) {
                                            LogUtils.debug(TAG, "*******************************************************************************************************");
                                            LogUtils.debug(TAG, "* usb file : " + file.getPath());
                                            LogUtils.debug(TAG, "* canRead : " + file.canRead() + ", canWrite: " + file.canWrite() + ", canExecute : " + file.canExecute());
                                            LogUtils.debug(TAG, "*******************************************************************************************************");
                                        }
                                        String filePath = file.getPath();
                                        if (!TextUtils.isEmpty(filePath)
                                                && file.isDirectory()
                                                && file.canRead() && file.canWrite() && file.canExecute()) {
                                            List<String> attributes = FileUtils.getFileAttributes(file);
                                            LogUtils.debug(TAG, "attributes: " + attributes);
                                            if (!CollectionUtils.isEmpty(attributes) && attributes.size() == 3
                                                    && (attributes.get(1).equals("system") || attributes.get(1).equals("root"))) {
                                                mountedPaths.add(filePath);
                                            }
                                        }
                                    }
                                }
                            } else {
                                mountedPaths.add(path);
                            }
                        }
                        // V40 ========================================================================
                        else if (isV40() || isH3()) {
                            mountedPaths.add(path);
                        } else {
                            mountedPaths.add(path);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mountedPaths;
    }

    /**
     * 获取挂载的设备
     * 例如某个硬盘可能有几个分区， 每个厂商的处理方式不同
     * 全志  ：每个分区都当做一个外设， 挂载和移除的时候会发对应分区个数的广播
     * 瑞星微： 以3128为例， 它会当做是一个外设， 每个分区对应不同的文件夹， 挂载和移除的时候会只发一次广播
     *
     * @param path 设备挂载点
     * @return 挂载设备的硬盘分区
     *
     * Created by lancy on 2018/5/21 11:52
     */
    @SuppressWarnings("unchecked")
    public static List<String> getMountedDevices(String path) {
        LogUtils.debug(TAG, "getMountedDevices, path : " + path);
        if (TextUtils.isEmpty(path)) {
            return Collections.EMPTY_LIST;
        }

        List<String> partitions = new ArrayList<>();
        if ((DeviceManager.isRK3128() || isRK3128H())
                && DeviceManager.isUsb(path)) {
            File file = new File(path);
            if (file.exists() && file.canRead()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File dir : files) {
                        if (DEBUG_USB) {
                            LogUtils.info(TAG, "mounted usb dir : " + dir.getPath());
                            LogUtils.info(TAG, "canRead: " + dir.canRead() + ", canWrite: " + dir.canWrite() + ", canExecute: " + dir.canExecute());
                        }
                        if (dir.isDirectory() && dir.canRead() && dir.canWrite() && dir.canExecute()) {
                            List<String> attributes = FileUtils.getFileAttributes(file);
                            LogUtils.debug(TAG, "attributes: " + attributes);
                            if (!CollectionUtils.isEmpty(attributes) && attributes.size() == 3
                                    && (attributes.get(1).equals("system") || attributes.get(1).equals("root"))) {
                                partitions.add(dir.getPath());
                            }
                        }
                    }
                }
            }
        } else {
            partitions.add(path);
        }

        return partitions;
    }

    /**
     * 检查目标设备剩余空间
     *
     * @param path      目标设备路径
     * @param limitSize 临界值, 例如1G：1 * 1024 * 1024* 1024
     * @return 如果剩余空间 > 临界值， 则返回true， 否则返回false
     *
     * Created by lancy on 2018/4/28 16:46
     */
    public static boolean checkSpace(String path, long limitSize) {
        StatFs statFs = new StatFs(path);
        long availableSize = statFs.getAvailableBytes();
        if (DEBUG_STATFS) {
            LogUtils.debug(TAG, "totalSize = " + statFs.getTotalBytes());
            LogUtils.debug(TAG, "freeBytes = " + statFs.getFreeBytes());
        }
        LogUtils.debug(TAG, "availableSize = " + statFs.getAvailableBytes());
        LogUtils.debug(TAG, "limitSize     = " + limitSize);
        // 存储空间不足1G不允许下载
        return availableSize > limitSize;
    }

    /**
     * 挂载设备是否为本地存储
     *
     * @param mountPoint 挂载点
     * @return 返回true如果为本地设备， 否则返回false
     *
     * Created by lancy on 2018/5/21 11:19
     */
    public static boolean isInternalCard(String mountPoint) {
        if (isRK3128() || isRK3128H() || isRK3229()) {
            return !StringUtils.isEmpty(mountPoint) && mountPoint.startsWith("/storage/emulated");
        } else if (isV40()) {
            return !StringUtils.isEmpty(mountPoint) && mountPoint.contains("sdcard");
        } else if (isH3()) {
            return !StringUtils.isEmpty(mountPoint) && mountPoint.startsWith("/storage/emulated/0");
        } else if (isRK3368()) {
            return !StringUtils.isEmpty(mountPoint) && mountPoint.startsWith("/storage/emulated/0");
        }

        return false;
    }

    /**
     * 挂载设备是否为TF Card
     *
     * @param mountPoint 挂载点
     * @return 返回true如果为TF Card， 否则返回false
     *
     * Created by lancy on 2018/5/21 11:19
     */
    public static boolean isExternalCard(String mountPoint) {
        if (isRK3128() || isRK3128H()) {
            return !StringUtils.isEmpty(mountPoint) && mountPoint.contains("external_sd");
        } else if (isV40() || isH3()) {
            return !StringUtils.isEmpty(mountPoint) && !mountPoint.contains("sdcard") && mountPoint.contains("card");
        }

        return false;
    }

    /**
     * 挂载设备是否为USB设备
     *
     * @param mountPoint 挂载点
     * @return 返回true如果为USB设备， 否则返回false
     *
     * Created by lancy on 2018/5/21 11:19
     */
    public static boolean isUsb(String mountPoint) {
        if (isRK3128() || isRK3128H() || isRK3368()) {
            return !StringUtils.isEmpty(mountPoint) && mountPoint.contains("usb_storage");
        } else if (isV40() || isH3()) {
            if (Build.MODEL.contains("TEMEISHENG") || SystemPropertiesUtils.getBoolean(PERSIST_SYS_HIDDEN_USB,false)){//特美声不需要首页显示默认硬盘图标 ic_usb
                return !StringUtils.isEmpty(mountPoint) && ((mountPoint.contains("usbhost") || mountPoint.contains("udisk") ) && !mountPoint.contains("udisk51") && !mountPoint.contains("udisk52"));
            }else {
                return !StringUtils.isEmpty(mountPoint) && ((mountPoint.contains("usbhost") || mountPoint.contains("udisk") ) || mountPoint.contains("udisk51") && !mountPoint.contains("udisk52"));
            }
        }

        return false;
    }

    /**
     * 获取usb数量
     *
     * @param context 上下文
     * @return 当前系统挂载的USB设备数量
     *
     * Created by lancy on 2018/11/21 20:11
     */
    public static int getMountedUsbSize(Context context) {
        int size = 0;
        List<String> devices = getMountedDevices(context);
        if (!CollectionUtils.isEmpty(devices)) {
            for (String device : devices) {
                if (isUsb(device)) {
                    size++;
                }
            }
        }

        return size;
    }

    /**
     * 获取当前系统挂载usb设备
     *
     * @param context 上下文
     * @return 系统挂载usb设备
     *
     * Created by lancy on 2018/11/21 20:11
     */
    public static List<String> getMountedUsbDevices(Context context) {
        List<String> devices = getMountedDevices(context);
        List<String> usbDevices = new ArrayList<>();
        if (!CollectionUtils.isEmpty(devices)) {
            for (String device : devices) {
                if (isUsb(device)) {
                    usbDevices.add(device);
                }
            }
        }
        return usbDevices;
    }

    /**
     * 获取挂载的TFCard数量
     *
     * @param context 上下文
     * @return TFCard数量
     *
     * Created by lancy on 2018/12/21 19:49
     */
    public static int getMountedTFCardSize(Context context) {
        int size = 0;
        List<String> devices = getMountedDevices(context);
        if (!CollectionUtils.isEmpty(devices)) {
            for (String device : devices) {
                if (isExternalCard(device)) {
                    size++;
                }
            }
        }
        return size;
    }

}
