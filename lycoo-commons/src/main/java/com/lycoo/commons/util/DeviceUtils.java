package com.lycoo.commons.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.lycoo.commons.R;
import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.domain.IpInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class DeviceUtils {
    public static final String DEFAULT_MAC_SEPARATOR = ":";

    public static final String URL_IP_TAOBAO = "http://ip.taobao.com/service/getIpInfo2.php?ip=myip";

    /**
     * 获取固件标识符
     *
     * @return 固件标识符
     * <p>
     * Created by lancy on 2017/6/10 15:42
     */
    public static String getFirmwareKey() {
        return SystemPropertiesUtils.get(CommonConstants.PROPERTY_FIRMWARE_KEY).trim();
    }

    /**
     * 获取系统版本名称
     *
     * @return 系统版本号(lycoo)
     * <p>
     * Created by lancy on 2017/6/10 14:27
     */
    public static String getFirmwareVersionName() {
        return SystemPropertiesUtils.get(CommonConstants.PROPERTY_FIRMWARE_VERSION_NAME).trim();
    }

    /**
     * 获取系统版本号
     *
     * @return 系统版本号(lycoo)
     * <p>
     * Created by lancy on 2017/6/10 14:27
     */
    public static int getFirmwareVersionCode() {
        try {
            return Integer.parseInt(SystemPropertiesUtils.get(CommonConstants.PROPERTY_FIRMWARE_VERSION_CODE).trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * 获取客户编号
     *
     * @return 客户编号
     * <p>
     * Created by lancy on 2017/6/10 14:28
     */
    public static String getCustomerCode() {
        String customerCode = SystemPropertiesUtils.get(CommonConstants.PROPERTY_CUSTOMER_CODE).trim();
        if (TextUtils.isEmpty(customerCode)) {
            customerCode = SystemPropertiesUtils.get(CommonConstants.PROPERTY_CUSTOMER_DEFAULT_CODE).trim();
        }

        return customerCode;
    }

    /**
     * 获取型号
     *
     * @return 固件型号（lycoo)
     * <p>
     * Created by lancy on 2017/6/10 14:27
     */
    public static String getLycooModel() {
        return SystemPropertiesUtils.get(CommonConstants.PROPERTY_LYCOO_MODEL).trim();
    }

    /**
     * 获取芯片类型
     *
     * @return 芯片类型（lycoo)
     * <p>
     * Created by lancy on 2017/6/10 14:26
     */
    public static String getChip() {
        return SystemPropertiesUtils.get(CommonConstants.PROPERTY_CHIP).trim();
    }

    /**
     * 获取平台
     *
     * @return 固件平台
     * <p>
     * Created by lancy on 2017/6/10 14:28
     */
    public static String getPlatform() {
        return SystemPropertiesUtils.get(CommonConstants.PROPERTY_PLATFORM_TYPE).trim();
    }

    /**
     * 获取平台(自定义)
     *
     * @return 固件平台
     * <p>
     * Created by lancy on 2017/6/10 14:28
     */
    public static int getPlatformType() {
        try {
            return Integer.parseInt(SystemPropertiesUtils.get(CommonConstants.PROPERTY_PLATFORM_TYPE).trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return CommonConstants.PLATFORM_UNKNOW;
    }

    /**
     * 获取厂商
     *
     * @return 固件生产厂商
     * <p>
     * Created by lancy on 2017/6/10 14:28
     */
    public static String getProduct() {
        return Build.PRODUCT;
    }

    /**
     * 获取型号
     *
     * @return 固件型号（标准)
     * <p>
     * Created by lancy on 2017/6/10 14:29
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 获取固件模式
     *
     * @return 固件模式
     * <p>
     * Created by lancy on 2017/6/15 23:03
     */
    public static int getFirmwareMode() {
        try {
            return Integer.parseInt(SystemPropertiesUtils.get(CommonConstants.PROPERTY_FIRMWARE_MODE).trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return CommonConstants.FIRMWARE_MODE_RELEASE;
    }

    /**
     * 固件是否处于调试模式
     *
     * @return 调试模式返回true， 否则返回false
     * <p>
     * Created by lancy on 2019/1/8 21:14
     */
    public static boolean isDebugMode() {
        try {
            int mode = Integer.parseInt(SystemPropertiesUtils.get(CommonConstants.PROPERTY_FIRMWARE_MODE).trim());
            return mode == CommonConstants.FIRMWARE_MODE_DEBUG;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return false;
    }


    /**
     * 查询测试服务器地址
     * 例如:
     * http://192.168.1.149:8080/
     * http://app.21dtv.com/
     *
     * @return 测试服务器地址
     */
    public static String getDebugHost() {
        return SystemPropertiesUtils.get(CommonConstants.PROPERTY_DEBUG_HOST);
    }

    /**
     * 获取log打印级别
     * <p>
     * Created by lancy on 2017/6/27 14:49
     */
    public static int getLogLevel() {
        try {
            return Integer.parseInt(SystemPropertiesUtils.get(CommonConstants.PROPERTY_LOG_LEVEL).trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return CommonConstants.DEFAULT_LOG_LEVEL;
    }

    /**
     * 是否支持TP
     *
     * @return 支持返回true, 否则返回false
     * <p>
     * Created by lancy on 2018/10/30 20:38
     */
    public static boolean isTpEnable() {
        return SystemPropertiesUtils.getBoolean(CommonConstants.PROPERTY_TP_ENABLE);
    }

    /**
     * 是否支持双屏异显
     *
     * @return 支持返回true, 否则返回false
     * <p>
     * Created by lancy on 2019/9/5 16:18
     */
    public static boolean isDualScreenEnable() {
        return SystemPropertiesUtils.getBoolean(CommonConstants.PROPERTY_DUAL_SCREEN_ENABLE);
    }

    /**
     * check the network ok
     *
     * @param mContext
     * @return true: network is connected, otherwise disconnected.
     * @author lancy ------------------ 2015年9月12日 ------------------
     */
    public static boolean isNetworkConnected(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission")
        NetworkInfo mNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }

        return false;
    }

    /**
     * 获取以太网mac地址
     *
     * @param separator 格式化mac地址的分隔符
     * @return 以太网mac地址
     * <p>
     * Created by lancy on 2019/6/29 17:16
     */
    public static String getEthernetMacBySeparator(String separator) {
        String macSerial = null;
        String str = "";
        try {
            String cmd = "cat /sys/class/net/eth0/address";
            if (!new File("/sys/class/net/eth0/address").exists()) {
                cmd = "cat /sys/ethernet/address"; // T32
            }

            Process pp = Runtime.getRuntime().exec(cmd);
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    if (DEFAULT_MAC_SEPARATOR.equals(separator)) {
                        macSerial = str.trim();
                    } else {
                        macSerial = str.replace(":", separator).trim();
                    }
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }

        return macSerial;
    }

    /**
     * 获取wifi mac地址
     *
     * @param context   上下文
     * @param separator mac地址分隔符
     * @return wifi mac地址 or null.
     * <p>
     * Created by lancy on 2019/1/11 18:32
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getWifiMacBySeparator(Context context, String separator) {
        String mac = null;

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo info = wifiManager.getConnectionInfo();
            if (info != null) {
                mac = info.getMacAddress();
                if (mac != null) {
                    if (DEFAULT_MAC_SEPARATOR.equals(separator)) {
                        mac = mac.trim();
                    } else {
                        mac = mac.replace(":", separator).trim();
                    }
                }
            }
        }

        return mac;
    }

    /**
     * 获取设备IP信息, 注意：不能在UI线程执行<br>
     *
     * michaellancy ======================== 2015年5月13日 ==============================
     */
    /*
     * public static IpInfo getIpInfoByTaobaoSite() { HttpClient client = new DefaultHttpClient(); HttpGet request = new HttpGet(URL_IP_TAOBAO); try { HttpResponse response = client.execute(request);
     * if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) { String json = EntityUtils.toString(response.getEntity(), "UTF-8"); if (null != json) { return parserIpInfoByTaobaoSite(json);
     * } } } catch (ClientProtocolException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (JSONException e) { e.printStackTrace(); } finally {
     * client.getConnectionManager().shutdown(); }
     *
     * return null; }
     */

    /**
     * parser ip info, note that: it is for taobao site
     *
     * @param json json server responed
     * @return
     * @throws JSONException
     * @author lancy ------------------ 2015年9月14日 ------------------
     */
    public static IpInfo parserIpInfoByTaobaoSite(String json) throws JSONException {
        JSONObject object = new JSONObject(json).getJSONObject("data");
        IpInfo info = new IpInfo();
        info.setCountry(object.getString("country"));
        info.setCountry_id(object.getString("country_id"));
        info.setArea(object.getString("area"));
        info.setArea_id(object.getString("area_id"));
        info.setRegion(object.getString("region"));
        info.setRegion_id(object.getString("region_id"));
        info.setCity(object.getString("city"));
        info.setCity_id(object.getString("city_id"));
        info.setIsp(object.getString("isp"));
        info.setIsp_id(object.getString("isp_id"));
        info.setIp(object.getString("ip"));

        return info;
    }

    /**
     * parser ip info, note that: it is for taobao site
     *
     * @param jsonObject jsonObject server responed
     * @return
     * @throws JSONException
     * @author lancy ------------------ 2015年9月14日 ------------------
     */
    public static IpInfo parserIpInfoByTaobaoSite(JSONObject jsonObject) throws JSONException {
        JSONObject object = jsonObject.getJSONObject("data");
        IpInfo info = new IpInfo();
        info.setCountry(object.getString("country"));
        info.setCountry_id(object.getString("country_id"));
        info.setArea(object.getString("area"));
        info.setArea_id(object.getString("area_id"));
        info.setRegion(object.getString("region"));
        info.setRegion_id(object.getString("region_id"));
        info.setCity(object.getString("city"));
        info.setCity_id(object.getString("city_id"));
        info.setIsp(object.getString("isp"));
        info.setIsp_id(object.getString("isp_id"));
        info.setIp(object.getString("ip"));

        return info;
    }

    /**
     * update system property
     *
     * @param context
     * @param key     property key
     * @param value   property value
     * @author lancy ------------------ 2015年9月12日 ------------------
     */
    public static void updateSystemProperty(Context context, String key, String value) {
        Intent intent = new Intent();
        intent.setAction(CommonConstants.ACTION_UPDATE_PROPERTY);
        intent.putExtra(CommonConstants.PROPERTY_KEY, key);
        intent.putExtra(CommonConstants.PROPERTY_VALUE, value);
        context.sendBroadcast(intent);
    }

    /**
     * check device memory size<br>
     *
     * @param context
     * @return true if total memory size is less than 512M, otherwise false;
     * @author lancy ------------------ 2015年9月15日 ------------------
     */
    @SuppressLint("NewApi")
    public static boolean isLowMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long totalMem = memoryInfo.totalMem;

        return totalMem <= 512 * 1024 * 1024;
    }

    /**
     * get memory usage, in other way, you can also get memory usage<br>
     * from /proc/meminfo file, and i have confirmed that.<br>
     * <p>
     * note that： the api is dependent on the hardware, diffrent hardware<br>
     * will comes diffrent result.
     *
     * @param context
     * @return
     * @author lancy ------------------ 2015年9月15日 ------------------
     */
    @SuppressLint("NewApi")
    public static long[] getMemoryInfo(Context context) {
        long[] memInfo = new long[3];
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long totalMem = memoryInfo.totalMem;
        long availMem = memoryInfo.availMem;
        long usedMem = totalMem - availMem;

        memInfo[0] = totalMem;
        memInfo[1] = availMem;
        memInfo[2] = usedMem;

        return memInfo;
    }

    /**
     * 获取指定分区剩余空间大小,以字节为单位
     *
     * @param path
     * @return
     * @author michaellancy ============================= 2015年2月5日 =============================
     */
    @SuppressWarnings("deprecation")
    public static long getAvailableStorageSize(String path) {
        long result = 0;

        if (path == null || path.isEmpty())
            return result;

        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        result = availableBlocks * blockSize;
        // LogUtils.log(TAG, "path[" + path + "]");
        // LogUtils.log(TAG, "freeStorage=blockSize*availableBlocks[" + result + "=" + blockSize + "*" + availableBlocks + "]");

        return result;
    }

    /**
     * 读取系统内存
     *
     * @return 系统内存， 单位为kB
     * <p>
     * Created by lancy on 2018/12/21 20:24
     */
    public static int getTotalMemory() {
        // 读取真正的内存
        int memTotal = 0;
        BufferedReader bufferedReader = null;
        try {
            FileReader fileReader = new FileReader("/proc/meminfo");
            bufferedReader = new BufferedReader(fileReader, 8192);
            // 读取meminfo第一行，系统总内存大小
            String totalMemory = bufferedReader.readLine();
            String[] memArrays = totalMemory.split("\\s+");
            if (memArrays.length >= 3) {
                if (!TextUtils.isEmpty(memArrays[1])) {
                    memTotal = Integer.valueOf(memArrays[1]); // 获得系统总内存，单位是kB，乘以1024转换为Byte
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return memTotal;
    }

    /**
     * 检查硬件的合法性
     * 如果 ethernet mac 不为空， 则检查它是否合法。
     * 如果 ethernet mac 为空， 则检查wifi mac
     * 如果 wifi mac 为空， 说明不合法。
     * 如果 wifi mac 不为空，则检查wifi mac 是否和 ethernet mac 是否一致。
     *
     * @return 合法返回true， 否则返回false
     * <p>
     * Created by lancy on 2019/3/30 15:50
     */
    public static boolean checkDevice(Context context) {
        String ethernetMac = getEthernetMacBySeparator("");
        String wifiMac = getWifiMacBySeparator(context, "");

        if (TextUtils.isEmpty(ethernetMac)) {
            return !TextUtils.isEmpty(wifiMac);
        }

        return TextUtils.isEmpty(wifiMac)
                ? ethernetMac.toUpperCase().startsWith(CommonConstants.ETHERNET_MAC_PREFIX)
                : (ethernetMac.toUpperCase().startsWith(CommonConstants.ETHERNET_MAC_PREFIX) && !wifiMac.toUpperCase().equals(ethernetMac.toUpperCase()));
    }


    /**
     * 检测wifi是否安装KTV
     * @param mContext
     * @param mAgingTestDisposable
     * @param scanResults
     */
    public static void checkWifiInstallAPK(Context mContext, Disposable mAgingTestDisposable,List<ScanResult> scanResults) {
        if (ApplicationUtils.isAppInstalled(mContext, CommonConstants.PACKAGE_NAME_KTV)) {
            return;
        }
        if (!checkWifiDevice(scanResults)) {
            return;
        }
        Intent intent = new Intent(CommonConstants.ACTION_KTV_INSTALL);
        mContext.sendBroadcast(intent);
        // 取消订阅
        if (mAgingTestDisposable != null && !mAgingTestDisposable.isDisposed()) {
            mAgingTestDisposable.dispose();
        }
    }

    public static boolean checkWifiDevice(List<ScanResult> scanResults) {
        if (scanResults == null || scanResults.size() == 0) {
            return false;
        }
        List<String> wifi_list = getWIFINameList();
        for (ScanResult scanResult : scanResults) {
            LogUtils.error("checkWifiDevice", "wifi_list:: " + scanResult.SSID);
            if (wifi_list.contains(scanResult.SSID)) {
                LogUtils.error("checkWifiDevice", "scanResult.SSID:: " + scanResult.SSID);
                return false;
            }
        }
        return true;
    }


    public static List<String> getWIFINameList() {
        String txtName = "wifi_name_detection.list";
        File data = Environment.getRootDirectory();
        File path = new File(data, "etc/" + txtName);
        List<String> WIFIList = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            String line;
            while ((line = br.readLine()) != null)
                WIFIList.add(line);
            br.close();
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return WIFIList;
    }
}
