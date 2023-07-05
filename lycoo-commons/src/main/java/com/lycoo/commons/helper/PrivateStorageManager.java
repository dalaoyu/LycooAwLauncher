package com.lycoo.commons.helper;

import android.text.TextUtils;

import com.allwinnertech.dragonsn.jni.ReadPrivateJNI;
import com.lycoo.commons.domain.CommonConstants;
import com.rockchip.keily.jni.ReadPrivate;

/**
 * 私有分区管理者
 *
 * Created by lancy on 2019/1/8
 */
public class PrivateStorageManager {

    private static PrivateStorageManager mPrivateStorageManager;
    private ReadPrivateJNI mReadPrivateJNI;

    public PrivateStorageManager() {
        if (DeviceManager.isH3() || DeviceManager.isV40()) {
            mReadPrivateJNI = new ReadPrivateJNI();
        }
    }

    public static PrivateStorageManager getInstance() {
        if (mPrivateStorageManager == null) {
            synchronized (PrivateStorageManager.class) {
                if (mPrivateStorageManager == null) {
                    mPrivateStorageManager = new PrivateStorageManager();
                }
            }
        }
        return mPrivateStorageManager;
    }

    /**
     * 读私有分区数据
     *
     * @param key 名称
     * @return 私有分区中名称对应的值
     *
     * Created by lancy on 2019/1/8 17:43
     */
    public String read(String key) {
        String value = "";
        if (TextUtils.isEmpty(key)) {
            return value;
        }

        if (DeviceManager.isH3() || DeviceManager.isV40()) {
            value = mReadPrivateJNI.native_get_parameter(key);
        } else if (DeviceManager.isRK3128()) {
            if (key.equals(CommonConstants.SN)) {
                value = ReadPrivate.readAuthorizeCode();
            } else if (key.equals(CommonConstants.DYNAMIC_CODE)) {
                value = ReadPrivate.readDynamicCode();
            } else if (key.equals(CommonConstants.ACTIVATE_CODE)) {
                value = ReadPrivate.readActivateCode();
            } else if (key.equals(CommonConstants.CUSTOMER_CODE)) {
                // TODO: 2019/9/18 3128未实现读 “客户码”
            }
        }

        return value;
    }

    /**
     * 读私有分区数据
     *
     * @param key      名称
     * @param defValue 默认值
     * @return 私有分区中名称对应的值
     *
     * Created by lancy on 2019/1/8 17:43
     */
    public String read(String key, String defValue) {
        String value = read(key);
        if (TextUtils.isEmpty(value)) {
            value = defValue;
        }

        return value;
    }

    /**
     * 写私有分区
     *
     * @param key   名称
     * @param value 值
     *
     *              Created by lancy on 2019/1/8 17:44
     */
    public void write(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }

        if (DeviceManager.isH3() || DeviceManager.isV40()) {
            mReadPrivateJNI.native_set_parameter(key, value);
        } else if (DeviceManager.isRK3128()) {
            if (key.equals(CommonConstants.DYNAMIC_CODE)) {
                ReadPrivate.writeDynamicCode(value);
            } else if (key.equals(CommonConstants.ACTIVATE_CODE)) {
                ReadPrivate.writeActivateCode(value);
            } else if (key.equals(CommonConstants.CUSTOMER_CODE)) {
                // TODO: 2019/9/18 3128未实现写 “客户码”
            }
        }
    }

    /**
     * 释放资源
     *
     * Created by lancy on 2019/1/8 17:45
     */
    /*
    public void release() {
        if (DeviceManager.isH3() || DeviceManager.isV40()) {
            if (mReadPrivateJNI != null) {
                mReadPrivateJNI.native_release();
            }
        }

        if (mPrivateStorageManager != null) {
            mPrivateStorageManager = null;
        }
    }
    */


}
