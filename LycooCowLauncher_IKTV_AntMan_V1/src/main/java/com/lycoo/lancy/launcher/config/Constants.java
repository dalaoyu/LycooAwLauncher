package com.lycoo.lancy.launcher.config;

/**
 * 常量定义
 *
 * Created by lancy on 2018/5/7
 */
public class Constants {
    public static final int POLYMERIC_PAGE_NUMBER = 100;
    public static final int POLYMERIC_PAGE_ITEM_COUNT = 12;

    public static final String SP_DESKTOP = "sp_desktop";
    public static final String DESKTOP_INITIALIZED = "desktop_initialized";
    public static final String DANCE_DOWNLOAD_ITEM = "danceDownloadItem";

    public static final int DOCK_NUMBER = 1000;
    public static final int DOCK_ITEM_COUNT = 7;

    public static final String SP_PUBLIC = "sp_public";
    public static final String INTERFACE_TEST_COUNT = "interface_test_count";

    /* 电池管理(CustomBatteryManager) ************************************************************************** */
    /**
     * 充电状态
     */
    public static final int BATTERY_CHARGE = 0;
    /**
     * 低电量
     */
    public static final int BATTERY_00 = 1;
    /**
     * 1格
     */
    public static final int BATTERY_01 = 2;
    /**
     * 2格
     */
    public static final int BATTERY_02 = 3;
    /**
     * 3格
     */
    public static final int BATTERY_03 = 4;
    /**
     * 4格
     */
    public static final int BATTERY_04 = 5;
    /**
     * 5格
     */
    public static final int BATTERY_05 = 6;

    /**
     * 充满
     */
    public static final int BATTERY_FULL = 100;

    public static final int BATTERY_LEVEL_4 = 4;
    public static final int BATTERY_LEVEL_5 = 5;
    public static final String UPDATE_BATTERY_STATE_ACTION = "com.lycoo.UPDATE_BATTERY_STATE";
    public static final String EXTRA_BATTERY_STATE = "batteryState";
    public static final String EXTRA_BATTERY_VOLTAGE = "batteryVoltage";

    /**
     * 欢迎词视频文件
     */
    public static final String SALUTATORY_FILE = "/system/media/salutatory.mp3";
    public static final String SALUTATORY_BG = "/system/media/bg_salutatory.png";
    /**
     * 广场舞下载路径
     */
    public static final String PROPERTY_DANCE_DOWNLOAD_DEVICE = "persist.sys.dance.device";
    /**
     * 更新广场舞下载路径ACTION
     */
    public static final String ACTION_UPDATE_DANCE_DOWNLOAD_DEVICE = "com.lycoo.action.update_dance_download_device";
    /**
     * 更新广场舞下载路径参数
     */
    public static final String DEVICE = "device";

    /**
     * 客户model: 联信浩东
     */
    public static final String MODEL_LXHD = "LXHD";
    /**
     * 客户model: 翔声通
     */
    public static final String MODEL_XIANGSHENGTONG = "XIANGSHENGTONG";
    /**
     * 客户model: 上琪
     */
    public static final String MODEL_SHANGQI = "SHANGQI";
    /**
     * 客户model: 符氏
     */
    public static final String MODEL_GAV = "FUSHI";
//    public static final String MODEL_GAV = "GAV";
    /**
     * 客户model: 符氏-QBA
     */
    public static final String MODEL_QBA = "QBA";
    /**
     * 客户model: 符氏-HCKE
     */
    public static final String MODEL_HCKE = "HCKE";
    /**
     * 客户model: 丽超
     */
    public static final String MODEL_LICHAO = "LICHAO";
    /**
     * 客户model: 亿维
     */
    public static final String MODEL_YIWEI = "YIWEI";
    /**
     * 客户model: 商进
     */
    public static final String MODEL_SHANGJIN= "SHANGJIN";
    /**
     * 客户model: 华威
     */
    public static final String MODEL_HUAWEI = "HUAWEI";
    /**
     * 客户model: 奋威
     */
    public static final String MODEL_FENWEI = "FENWEI";
    /**
     * 客户model: 欧柏
     */
    public static final String MODEL_OUBO = "OUBO";
    /**
     * 客户model: 铭都
     */
    public static final String MODEL_MINGDU = "MINGDU";
    /**
     * 客户model: 广州现代
     */
    public static final String MODEL_XIANDAI = "XIANDAI";
    /**
     * 客户model: 广州乐好
     */
    public static final String MODEL_LEHAO = "Lohao";
    /**
     * 客户model: 业翔
     */
    public static final String MODEL_YEXIANG = "YEXIANG";
    /**
     * 客户model: 明歌
     */
    public static final String MODEL_MINGGE = "MINGGE";
    /**
     * 客户model: 创辉
     */
    public static final String MODEL_JBA = "JBA";

    public static final String MODEL_BOK = "BOK";
    /**
     * 客户model: 狮乐
     */
    public static final String MODEL_SHILE = "SHILE";

    /**
     * 客户model: 愛翔
     */
    public static final String MODEL_AIXIANG = "AIXIANG_IKTV_K";

    /**
     * 客户model: 山凌
     */
    public static final String MODEL_SHANLING = "SHANGLING";
    /**
     * 客户model: ailipu
     */
    public static final String MODEL_AILIPU = "AILIPU";

    /**
     * 客户model: 同创
     */
    public static final String MODEL_TONGCHUANG = "TONGCHUANG";
    /**
     * 客户model: WEITUO
     */
    public static final String MODEL_WEITUO = "WEITUO";
    /**
     * 客户model: 爱博声
     */
    public static final String MODEL_AIBOSHENG = "AIBOSHENG";
    /**
     * 客户model: 蒂索纳
     */
    public static final String MODEL_DISUONA = "DISUONA";

    /**
     * 客户model: 同创 SS508
     */
    public static final String MODEL_SS508 = "SS508";
    public static final String PACKAGE_NAME_KTV = "com.lycoo.lancy.ktv";
    public static final String PACKAGE_NAME_WIFI_PATH = "com.android.settings/.wifi.WifiSettings";

    public static final String PROPERTY_MEDIA_BOOT_DISABLE = "sys.lycoo.media.boot_disable";

    public static final String PERSIST_SYS_BT_LYRICS                             = "persist.sys_bt_lyrics";

}
