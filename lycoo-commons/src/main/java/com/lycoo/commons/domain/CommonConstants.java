package com.lycoo.commons.domain;

/**
 * 常量定义
 * 请不要随意更改，如果必须要改，请同步修改引用
 */
public class CommonConstants {
    public static final String CUSTOM_FONT_FLFBLS = "fonts/flfbls.ttf";
    public static final String SP_COMMON = "common";
    public static final String UPDATETIME = "updateTime";
    public static final String DEF_UPDATETIME = "1970-01-01 00:00:00";

    public static final String STYLE_COLOR = "styleColor";
    public static final String STYLE_COLOR_TAG = "styleEnable";
    public static final int STYLE_COLOR_COUNT = 4;
    public static final int STYLE_COLOR_DEFAULT = 0;
    public static final int STYLE_COLOR_ORANGE = 1;
    public static final int STYLE_COLOR_GREEN = 2;
    public static final int STYLE_COLOR_BLUE = 3;

    public static final String ETHERNET_MAC_PREFIX = "0A0A";
    public static final String WIFI_MAC_PREFIX = "0C0A";

    /**
     * 默认同时下载的最大数
     */
    public static final int DEFAULT_DOWNLOAD_COUNT = 3;

    public static final int DAY_OF_WEEK_MONDAY = 1;
    public static final int DAY_OF_WEEK_TUESDAY = 2;
    public static final int DAY_OF_WEEK_WEDNESDAY = 3;
    public static final int DAY_OF_WEEK_THURDAY = 4;
    public static final int DAY_OF_WEEK_FRIDAY = 5;
    public static final int DAY_OF_WEEK_SATURDAY = 6;
    public static final int DAY_OF_WEEK_SUNDAY = 7;

    /* 平台定义 =================================================================================================== */
    public static final int PLATFORM_UNKNOW = 0x00;
    public static final int PLATFORM_V40 = 0x01;
    public static final int PLATFORM_H3 = 0x02;

    public static final int PLATFORM_RK3128_DEPRECATED = 0x04;
    public static final int PLATFORM_RK3128 = 0x40;
    public static final int PLATFORM_RK3128H = 0x41;
    public static final int PLATFORM_RK3229 = 0x42;
    public static final int PLATFORM_RK3368 = 0x43;

    public static final String BRAND_T361 = "T361";

    /* Lycoo property定义================================================================================================== */

    /**
     * 替换开机动画 （自定义)
     */
    public static final String PROPERTY_REPLACE_BAM = "sys.lycoo.replace.bam.trigger";
    /**
     * 重置开机动画 （自定义)
     */
    public static final String PROPERTY_RESET_BAM = "sys.lycoo.reset.bam.trigger";
    /**
     * 客户编码 （自定义)
     */
    public static final String PROPERTY_CUSTOMER_CODE = "persist.sys.customer.code";
    /**
     * 客户编码（默认）
     */
    public static final String PROPERTY_CUSTOMER_DEFAULT_CODE = "ro.customer.default.code";
    /**
     * 芯片类型（自定义)
     */
    public static final String PROPERTY_PLATFORM_TYPE = "ro.lycoo.platform";
    /**
     * 平台类型（自定义)
     */
    public static final String PROPERTY_CHIP = "ro.lycoo.chip";
    /**
     * 固件型号（自定义)
     */
    public static final String PROPERTY_LYCOO_MODEL = "ro.lycoo.model";
    /**
     * 固件标识符（自定义)
     */
    public static final String PROPERTY_FIRMWARE_KEY = "ro.lycoo.firmware.key";
    /**
     * 系统版本名称（自定义)
     */
    public static final String PROPERTY_FIRMWARE_VERSION_NAME = "ro.lycoo.firmware.verion.name";
    /**
     * 系统版本号（自定义)
     */
    public static final String PROPERTY_FIRMWARE_VERSION_CODE = "ro.lycoo.firmware.verion.code";
    /**
     * 固件平台
     */
    public static final String PROPERTY_PLATFORM = "ro.board.platform";
    /**
     * 固件模式 （自定义)
     */
    public static final String PROPERTY_FIRMWARE_MODE = "persist.sys.firmware.mode";
    /**
     * log打印级别 （自定义)
     */
    public static final String PROPERTY_LOG_LEVEL = "persist.sys.log.level";
    /**
     * 调试服务器地址（自定义), 例如：http://192.168.1.149:8080/
     */
    public static final String PROPERTY_DEBUG_HOST = "persist.sys.debug.host";
    /**
     * 蓝牙是否使能(true: 使能， false:使能)
     */
    public static final String PROPERTY_BLUETOOTH_ENABLE = "ro.lycoo.bluetooth.enable";
    /**
     * 系统配置默认桌面的包名
     */
    public static final String PROPERTY_DEFAULT_LAUNCHER_PACKAGENAME = "ro.lycoo.defaultlauncherpackage";
    /**
     * 系统配置默认桌面的启动类名
     */
    public static final String PROPERTY_DEFAULT_LAUNCHER_CLASSNAME = "ro.lycoo.defaultlauncherclass";
    /**
     * 系统属性： 允许同时下载的最大数
     */
    public static final String PROPERTY_MAX_DOWNLOAD_COUNT = "persist.sys.dance.max.count";
    /**
     * 系统属性： 是否支持TP
     */
    public static final String PROPERTY_TP_ENABLE = "persist.sys.tp.enable";
    /**
     * 系统属性： 是否显示悬浮back
     */
    public static final String PROPERTY_FLOATED_BACK_ENABLE = "persist.sys.floated_back.enable";
    /**
     * 系统属性： MYKTV是否允许运行
     */
    public static final String PROPERTY_MYKTV_ENABLE = "sys.lycoo.myktv.enable";
    /**
     * 系统属性：是否启用欢迎词功能
     */
    public static final String PROPERTY_SALUTATORY_ENABLE = "persist.sys.salutatory.enable";
    /**
     * 欢迎词是否播放完成, 防止桌面重启之后重复播放
     */
    public static final String PROPERTY_SALUTATORY_FINISHED = "sys.lycoo.salutatory_finished";
    /**
     * 系统属性：是否启用开机启动KTV
     */
    public static final String PROPERTY_BOOT_KTV = "persist.sys.boot_ktv.enable";
    /**
     * 系统属性：是否启用开机启动收费页面
     */
    public static final String PROPERTY_BOOT_CHARGE = "persist.sys.boot_charge.enable";
    /**
     * KTV是否启动完成, 防止桌面重启之后重复播放
     */
    public static final String PROPERTY_BOOTKTV_FINISHED = "sys.lycoo.booktv_finished";
    /**
     * 系统属性：是否启用欢迎视频功能
     */
    public static final String PROPERTY_BOOT_VAIDEO_ENABLE = "persist.sys.boot_video.enable";
    /**
     * 系统属性：是否打开line_in
     */
    public static final String PROPERTY_BOOT_MODEL_LINE_IN = "persist.sys.model.line_in";
    /**
     * 欢迎视频是否播放完成, 防止桌面重启之后重复播放
     */
    public static final String PROPERTY_BOOT_VAIDEO_FINISHED = "sys.lycoo.boot_video_finished";
    /**
     * 标记是否已经启动完成， 在发送完ACTION_BOOT_COMPLETED广播之后，设置此属性
     */
    public static final String PROPERTY_BOOT_COMPLETED = "sys.lycoo.boot_completed";
    /**
     * 系统属性：是否支持双系统
     */
    public static final String PROPERTY_DUAL_BOOT_ENABLE = "persist.sys.dual_boot.enable";
    /**
     * 系统属性：是否支持DSP调节
     */
    public static final String PROPERTY_DSP_ADJUST_ENABLE = "persist.sys.dsp_adjust.enable";
    /**
     * 系统属性：是否支持双屏异显
     */
    public static final String PROPERTY_DUAL_SCREEN_ENABLE = "persist.sys.dual_screen.enable";

    /**
     * 是否打开蓝牙歌词功能
     */
    public static final String PROPERTY_BT_LYRICS = "sys.lycoo.bt.lyrics";

    // ACTION 定义=========================================================================================================
    /**
     * 自定义开机广播
     */
    public static final String ACTION_BOOT_COMPLETED = "com.lycoo.action.BOOT_COMPLETED";

    public static final String ACTION_CLEAN_DATABASES = "com.lycoo.action.DATABASES";

    // 快捷键属性定义=========================================================================================================
    /**
     * 直播（自定义)
     */
    public static final String PROPERTY_SHORTCUT_KEY_TV = "persist.sys.key.tv";
    /**
     * 点播（自定义)
     */
    public static final String PROPERTY_SHORTCUT_KEY_VOD = "persist.sys.key.vod";
    /**
     * 音乐（自定义)
     */
    public static final String PROPERTY_SHORTCUT_KEY_MUSIC = "persist.sys.key.music";
    /**
     * 游戏（自定义)
     */
    public static final String PROPERTY_SHORTCUT_KEY_GAME = "persist.sys.key.game";
    /**
     * 应用（自定义)
     */
    public static final String PROPERTY_SHORTCUT_KEY_APPS = "persist.sys.key.apps";
    /**
     * 设置（自定义)
     */
    public static final String PROPERTY_SHORTCUT_KEY_SETUP = "persist.sys.key.setup";
    /**
     * 文件管理器（自定义)
     */
    public static final String PROPERTY_SHORTCUT_KEY_FILE_MANAGER = "persist.sys.key.file_manager";
    /**
     * 一键清理（自定义)
     */
    public static final String PROPERTY_SHORTCUT_KEY_CLEAR = "persist.sys.key.clear";
    /**
     * 接口测试
     */
    public static final String PROPERTY_SHORTCUT_KEY_INTERFACE_TEST = "persist.sys.key.interface_test";
    /**
     * 老化测试
     */
    public static final String PROPERTY_SHORTCUT_KEY_AGING_TEST = "persist.sys.key.aging_test";
    /**
     * 老化KTV
     */
    public static final String PACKAGE_AGING_APP = "com.lycoo.agingtest";
    public static final String ACTION_AGING_APP = "com.lycoo.action.aning.test";
    public static final String PACKAGE_AGING_APP_ACTIVITY = "com.lycoo.agingtest.MainActivity";

    /**
     * 老化KTV
     */
    public static final String PROPERTY_SHORTCUT_KEY_AGING_KTV = "persist.sys.key.aging_ktv";

    /**
     * 更多应用是否显示KTV图标
     */
    public static final String PROPERTY_SHOW_KTV_APP_ICON = "persist.sys.ktv_icon.show";

    /* 作假属性 ============================================================================================================ */
    /**
     * FLASH 容量是否作假
     */
    public static final String PROPERTY_FLASH_FEIGNED = "ro.lycoo.flash.feigned";
    /**
     * DDR 容量是否作假
     */
    public static final String PROPERTY_DDR_FEIGNED = "ro.lycoo.ddr.feigned";
    /**
     * FLASH 作假容量， 单位G
     */
    public static final String PROPERTY_FLASH_SIZE = "ro.lycoo.flash.size";
    /**
     * DDR 作假容量， 单位M
     */
    public static final String PROPERTY_DDR_SIZE = "ro.lycoo.ddr.size";

    /**
     * 系统属性(仅用于设置显示)：DDR作假
     */
    public static final String PROPERTY_SETUP_DDR_FEIGNED = "ro.lycoo.setup.ddr.feigned";
    /**
     * 系统属性（仅用于设置显示）：DDR作假大小，单位为M
     */
    public static final String PROPERTY_SETUP_DDR_FEIGNED_SIZE = "ro.lycoo.setup.ddr.size";
    /**
     * 系统属性(仅用于设置显示)：FLASH作假
     */
    public static final String PROPERTY_SETUP_FLASH_FEIGNED = "ro.lycoo.setup.flash.feigned";
    /**
     * 系统属性(仅用于设置显示)：FLASH作假大小，单位为G
     */
    public static final String PROPERTY_SETUP_FLASH_FEIGNED_SIZE = "ro.lycoo.setup.flash.size";

    /* 固件-应用信息 ========================================================================================================= */
    /**
     * 固件keyCode
     */
    public static final String FIRMWARE_KEY = "firmwareKey";
    /**
     * 应用keyCode,在AndroidManifest.xml中配置
     */
    public static final String APP_KEY = "appKey";
    /**
     * 设备以太网mac地址
     */
    public static final String MAC = "mac";
    /**
     * 设备wifi mac地址
     */
    public static final String WIFI_MAC = "wifiMac";
    /**
     * 设备客户编码
     */
    public static final String CUSTOMER_CODE = "customerCode";
    /**
     * 版本名称：
     */
    public static final String VERSION_NAME = "versionName";
    /**
     * 版本号，升级用
     */
    public static final String VERSION_CODE = "versionCode";
    /**
     * 应用包名
     */
    public static final String PACKAGENAME = "packageName";
    /**
     * 所属公司编号
     */
    public static final String COMPANY_NUMBER = "companyNumber";
    /**
     * 序列号
     */
    public static final String SN = "sn";
    /**
     * MYKTV授权码
     */
    public static final String DYNAMIC_CODE = "dynamicCode";
    /**
     * 固件模式-量产模式
     */
    public static final int FIRMWARE_MODE_RELEASE = 0;
    /**
     * IKTV激活码
     */
    public static final String ACTIVATE_CODE = "activateCode";
    /**
     * 固件模式-调试模式
     */
    public static final int FIRMWARE_MODE_DEBUG = 1;
    /**
     * log级别
     */
    public static final int DEFAULT_LOG_LEVEL = 3;

    /* LycooPropertyUpdate ===================================================================================================== */
    // 请不要随意更改名称，如果要改必须和LycooPropertyUpdate同步修改
    /**
     * 启动LycooPropertyUpdate action
     */
    public static final String ACTION_UPDATE_PROPERTY = "com.lycoo.ACTION_UPDATE_PROPERTY";
    public static final String PROPERTY_KEY = "propertyKey";
    public static final String PROPERTY_VALUE = "propertyValue";
    /**
     * 属性名最大长度
     */
    public static final int PROPERTY_KEY_MAX_LENGTH = 31;
    /**
     * 属性值最大长度
     */
    public static final int PROPERTY_VALUE_MAX_LENGTH = 91;

    /* LycooPackageInstaller ================================================================================================= */
    /**
     * 广播action: 开始执行
     */
    public static final String ACTION_PACKAGEINSTALL = "com.lycoo.ACTION_PACKAGEINSTALL";
    /**
     * 执行模式
     */
    public static final String EXECUTE_MODE = "executeMode";
    /**
     * 执行数据
     */
    public static final String EXECUTE_DATA = "executeData";
    /**
     * 执行模式：安装
     */
    public static final int MODE_INSTALL = 0;
    /**
     * 执行模式：卸载
     */
    public static final int MODE_UNINSTALL = 1;

    /**
     * 广播action：执行完成发送
     */
    public static final String ACTION_PACKAGEINSTALL_COMPLETE = "com.lycoo.ACTION_PACKAGEINSTALL_COMPLETE";
    /**
     * 执行结果
     */
    public static final String PACKAGEINSTALL_RESULTCODE = "resultCode";
    /**
     * 执行应用包名
     */
    public static final String PACKAGEINSTALL_PACKAGENAME = "packageName";
    /**
     * 安装文件
     */
    public static final String PACKAGEINSTALL_FILE = "file";
    /**
     * 安装成功
     */
    public static final int INSTALL_SUCCEEDED = 1;
    /**
     * 安装失败
     */
    public static final int INSTALL_FAILED_INVALID_APK = -2;
    public static final int INSTALL_FAILED_INVALID_URI = -3;
    public static final int INSTALL_FAILED_INSUFFICIENT_STORAGE = -4;
    /**
     * 卸载成功
     */
    public static final int DELETE_SUCCEEDED = 1;
    /**
     * 卸载失败
     */
    public static final int DELETE_FAILED_DEVICE_POLICY_MANAGER = -2;

    /* preInstaller ======================================================================================================== */
    public static final String PREINSTALL_DIR = "/system/preinstall";
    public static final String KEY_PREINSTALL_COMPLETED = "persist.sys.lycoo.preinstall";
    public static final String LYCOO_PREINSTALLER_PACKAGENAME = "com.lycoo.lancy.preinstaller";
    public static final String LYCOO_PREINSTALLER_LUANCH_CLASSNAME = "com.lycoo.lancy.preinstaller.MainActivity";
    public static final String ACTION_PREINSTALL = "com.lycoo.ACTION_PREINSTALL";
    public static final String KEY_PREINSTALL = "preinstall";
    public static final String EXTRA_PREINSTALL_BEGIN = "preinstall_begin";
    public static final String EXTRA_PREINSTALL_COMPLETED = "preinstall_completed";
    /*
    public static final String PREINSTALL_DIR 								= "/system/preinstall";
    public static final String KEY_PREINSTALL_COMPLETED 					= "persist.sys.preinstall.ok";
    public static final String PREINSTALL_COMPLETED 						= "ok";
    public static final String LYCOO_PREINSTALLER_PACKAGENAME 				= "com.lycoo.lancy.preinstaller";
    public static final String LYCOO_PREINSTALLER_LUANCH_CLASSNAME 			= "com.lycoo.lancy.preinstaller.MainActivity";
    public static final String ACTION_PREINSTALL 							= "com.lycoo.ACTION_PREINSTALL";
    public static final String KEY_PREINSTALL 								= "preinstall";
    public static final String EXTRA_PREINSTALL_BEGIN 						= "preinstall_begin";
    public static final String EXTRA_PREINSTALL_COMPLETED 					= "preinstall_completed";
    */
    // PackageInstall
    public static final String LYCOO_PACKAGEINSTALLER_PACKAGENAME = "com.lycoo.lancy.packageinstaller";
    public static final String LYCOO_PACKAGEINSTALLER_LUANCH_CLASSNAME = "com.lycoo.lancy.packageinstaller.MainActivity";

    //webview
    public static final String LYCOO_LAUNCHER_CHARGE_WEBVIEW = "http://kge.cn9441.cn";

    // wallPaper
    /**
     * 绝对路径为：/sdcard/Wallpaper
     */
    public static final String WALLPAPER_DIR_LOCATION_SDCARD = "Wallpaper";
    public static final String WALLPAPER_DIR_LOCATION_SYSTEM = "/system/media/images/wallpapers";

    /* 服务器返回参数 ==================================================================================================== */
    public static final String RESPONSE_STATUS_CODE = "statusCode";
    public static final String RESPONSE_MESSAGE = "message";
    public static final String RESPONSE_DATA = "data";
    public static final int STATUS_CODE_ERROR = 0;
    public static final int STATUS_CODE_SUCCESS = 1;

    /* 天气预报 ======================================================================================================== */
    public static final String ACTION_GET_WEATHER = "com.lycoo.keily.getweather";
    public static final String ACTION_RECEIVE_WEATHER = "com.lycoo.receive.weather";
    public static final String ACTION_SIM_STATE_CHANGED  = "android.intent.action.SIM_STATE_CHANGED";
    public static final String KEY_WEATHER = "weather";
    public static final String KEY_WEATHER_CITY = "city";
    public static final String KEY_WEATHER_TEMP1 = "temp1";

    /* LycooOtaUpdate =================================================================================================== */
    public static final String ACTION_INSTALL_OTA_PACKAGE = "com.lycoo.ACTION_INSTALL_OTA_PACKAGE";
    public static final String PACKAGE_FILE = "packageFile";

    /*  数据库 ============================================================================================================ */
    public static final String DB_NAME = "commons.db";
    public static final int DB_VERSION = 1;

    /*  应用广告=========================================================================================================== */
    public static final int APPVERTISING_IMAGE_SPLASH = 0;
    public static final int APPVERTISING_IMAGE_EXIT = 1;
    public static final int APPVERTISING_IMAGE_BUFFERING = 2;
    public static final int APPVERTISING_IMAGE_PAUSE = 3;
    public static final int APPVERTISING_IMAGE_PROGRAM_LIST = 4;
    public static final int APPVERTISING_IMAGE_PROGRAM_EPG = 5;
    public static final int APPVERTISING_IMAGE_LOGO = 6;

    /* 电池管理(CustomBatteryManager) ====================================================================================== */
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

    public static final int BATTERY_LEVEL_4 = 4;
    public static final int BATTERY_LEVEL_5 = 5;
    public static final String UPDATE_BATTERY_STATE_ACTION = "com.lycoo.UPDATE_BATTERY_STATE";
    public static final String EXTRA_BATTERY_STATE = "batteryState";
    public static final String EXTRA_BATTERY_VOLTAGE = "batteryVoltage";

    /* ******************************************************************************************************************
     * 授权管理
     * *******************************************************************************************************************/
    /**
     * 默认激活码
     */
    public static final String DEFAULT_ACTIVATE_CODE = "00000000000000000000000000000000";

    /* *******************************************************************************************************************
     * 标记文件
     * *******************************************************************************************************************/
    /**
     * 标记文件文件目录
     */
    public static final String MARK_FILE_DIR = "MarkFiles";

    /**
     * 开机视频播放完成标记文件
     */
    public static final String FILE_BOOT_VIDEO_FINISH = "boot_video_finish";

    /**
     * 是否已校验MAC
     */
    public static final String MAC_CHECK_COMPLETE = "mac_check_complete";

    public static final String DEFAULT_ETHERNET_MAC_PREFIX = "0A:0A";

    public static final String PROPERTY_SOFT_TYPE = "ro.lycoo.softtype";
    public static final String PROPERTY_DSP_FIRMWARE = "ro.lycoo.dspfirmware";
    /**
     * 固件是中英版本 en 英文
     */
    public static final String PROPERTY_LANGUAGE = "persist.product.locale.language";

    public static final String PACKAGE_NAME_KTV = "com.lycoo.lancy.ktv";

    public static final String ACTION_KTV_INSTALL ="action.lycoo.ktv_install";

    public static final String PROPERTY_LAUNCHER_IS_HANSHENG                        = "persist.lycoo.launcher.hansheng";
    /**
     * 系统属性：是否打开收费系统
     */
    public static final String PROPERTY_CHARGING_ENABLE = "persist.sys.charging_system";
    /**
     * 系统属性：开机视频使用是几
     */
    public static final String PROPERTY_VIDEO_ENABLE_NUM = "persist.sys.video_enable_num";

}
