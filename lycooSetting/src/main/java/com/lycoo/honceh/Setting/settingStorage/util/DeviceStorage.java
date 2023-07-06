package com.lycoo.honceh.Setting.settingStorage.util;


import android.app.ActivityManager;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import com.lycoo.commons.helper.DeviceManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class DeviceStorage {
    private StorageManager mStorageManager;
    private List<Object> getVolumeInfo;
    private long UsedBytes = 0;
    private long TotalBytes = 0;
    private Context context;

    public DeviceStorage(Context context) {
        this.context = context;
        init();
    }

    private long getAllStorage() {
        try {
            Method getPrimaryStorageSize = mStorageManager.getClass().getMethod("getPrimaryStorageSize");
            long invoke = (long) getPrimaryStorageSize.invoke(mStorageManager);
            return invoke;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    private static String[] units = {"B", "KB", "MB", "GB", "TB"};

    /**
     * 单位转换
     */
    public static String getUnit(float size, int mode) {
        int index = 0;
        while (size > mode && index < 4) {
            size = size / mode;
            index++;
        }
        return String.format(Locale.getDefault(), " %.2f %s", size, units[index]);
    }

    private void init() {
        mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Method getVolumes = StorageManager.class.getDeclaredMethod("getVolumes");//6.0
            getVolumeInfo = (List<Object>) getVolumes.invoke(mStorageManager);
            long total = 0L, used = 0L;
            int unit = 1024;
            String TAG = "queryTAG";
            for (Object obj : getVolumeInfo) {

                Method getType = obj.getClass().getDeclaredMethod("getType");
                int type = (int) getType.invoke(obj);

                Log.d(TAG, "type: " + type);
                if (type == 1 || type == 2) {//TYPE_PRIVATE  TYPE_EMULATED

//                    //获取内置内存总大小
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0也可以不做这个判断
//                        unit = 1000;
//                        //8.0 以后使用
//                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {//7.1.1
//                        //5.0 6.0 7.0没有
//                        Method getPrimaryStorageSize = StorageManager.class.getMethod("getPrimaryStorageSize");
//                        totalSize = (long) getPrimaryStorageSize.invoke(mStorageManager);
//                    }
                    Method getFsUuid = obj.getClass().getDeclaredMethod("getFsUuid");
                    String fsUuid = (String) getFsUuid.invoke(obj);
                    Method file = obj.getClass().getDeclaredMethod("getPath");
                    File f = (File) file.invoke(obj);
                    final long totalSize=getTotalSize(type,fsUuid,f);
//                    if(type==1 && Objects.equals(fsUuid, null)){
//                        totalSize =getAllStorage()-f.getTotalSpace();
//                    }else {
//                        if(f!=null){
//                            totalSize = f.getTotalSpace();
//                        }
//                    }
                    long systemSize = 0L;
                    Method isMountedReadable = obj.getClass().getDeclaredMethod("isMountedReadable");
                    boolean readable = (boolean) isMountedReadable.invoke(obj);
                    if (readable) {
//                        Method file = obj.getClass().getDeclaredMethod("getPath");
//                        File f3 = (File) file.invoke(obj);

//                        if (totalSize == 0) {
//                            totalSize = f.getTotalSpace();
//                        }
                        String _msg = "剩余总存储：" + getUnit(f.getTotalSpace(), unit) + "\n可用存储：" + getUnit(f.getFreeSpace(), unit) + "\n已用存储：" + getUnit(f.getTotalSpace() - f.getFreeSpace(), unit);
                        Log.d(TAG, _msg);
                        systemSize = totalSize - f.getTotalSpace();
                        used += (totalSize - f.getFreeSpace());
                        total += totalSize;
                    }
                    Log.d(TAG, "totalSize = " + getUnit(totalSize, unit) + " ,used(with system) = " + getUnit(used, unit) + " ,free = " + getUnit(totalSize - used, unit));

                }
//                else if (type == 0) {//TYPE_PUBLIC
//                    //外置存储
//                    Method isMountedReadable = obj.getClass().getDeclaredMethod("isMountedReadable");
//                    boolean readable = (boolean) isMountedReadable.invoke(obj);
//                    if (readable) {
//                        Method file = obj.getClass().getDeclaredMethod("getPath");
//                        File f = (File) file.invoke(obj);
//                        used += f.getTotalSpace() - f.getFreeSpace();
//                        total += f.getTotalSpace();
//                    }
//                }
//                else if (type == 2) {//TYPE_EMULATED
//
//                }
            }
            Log.d(TAG, "总存储 total = " + getUnit(total, unit) + " ,已用 used(with system) = " + getUnit(used, unit) + "\n可用 available = " + getUnit(total - used, unit));
            TotalBytes=total;
            UsedBytes=used;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getUsedBytes() {
        return UsedBytes;
    }

    public long getTotalBytes() {
        return TotalBytes;
    }

    /**
     * API 26 android O
     * 获取总共容量大小，包括系统大小
     * create by Honceh 23/6/14
     */
    public long getTotalSize(String fsUuid) {
        try {
            UUID id;
            if (fsUuid == null) {
                id = StorageManager.UUID_DEFAULT;
            } else {
                id = UUID.fromString(fsUuid);
            }
            StorageStatsManager stats = context.getSystemService(StorageStatsManager.class);
            return stats.getTotalBytes(id);
        } catch (NoSuchFieldError | NoClassDefFoundError | NullPointerException | IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * File总容量
     * @param type
     * @param fsUuid
     * @param f
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * create by Honceh 23/6/14
     */
    private long getTotalSize(int type,String fsUuid,File f) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        long totalSize=0;
        long sEmulatedlStorage=0;
        if(type==1 && Objects.equals(fsUuid, null)){
            for (Object vol : getVolumeInfo) {
                Method getType = vol.getClass().getDeclaredMethod("getType");
                int Type = (int) getType.invoke(vol);
                Method method = vol.getClass().getDeclaredMethod("getPath");
                File file = (File) method.invoke(vol);
                if(Type==2){
                    sEmulatedlStorage=file.getTotalSpace();
                }
            }
            totalSize =getAllStorage()-sEmulatedlStorage;
            return totalSize;
        }else {
            if(f==null){
               return 0;
            }
            totalSize = f.getTotalSpace();
            return totalSize;
        }
    }

    /**
     * 获取挂载设备的大下
     * @param mountPoint
     * @return
     * create by Honceh 23/6/14
     */
    public static String getStorageSize(String mountPoint) {
        StatFs statFs = new StatFs(mountPoint);
        long blockSize = statFs.getBlockSizeLong();
        long totalBlocks = statFs.getBlockCountLong();
        return getUnit(blockSize * totalBlocks,1024);
    }

    /**
     * 根据挂载点判断是什么设备
     * @param mountPoint
     * @return
     * create by Honceh 23/6/14
     */
    public static String getDevices(String mountPoint){
        String trim = mountPoint.trim();
        if(trim.equals("/storage/udiskh") || trim.equals("/storage/udisk1") || trim.equals("/storage/udisk") || trim.equals("/storage/udiskh1")){
            return "U盘";
        }else if(DeviceManager.isExternalCard(mountPoint)){
            return "TF卡";
        }else {
            return "硬盘";
        }
    }

}
