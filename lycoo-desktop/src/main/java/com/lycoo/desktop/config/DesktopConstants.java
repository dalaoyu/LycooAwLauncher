package com.lycoo.desktop.config;

/**
 * xxx
 *
 * Created by lancy on 2017/12/14
 */

public class DesktopConstants {
    /* 坑位类型定义. BEGIN ********************************************************************************************** */
    /** 专门页面 */
    public static final int SPECIALIZED_PAGE                                        = 0;
    /** 专门应用, 系统中固定，不可卸载的应用，例如设置系列的应用 */
    public static final int SPECIALIZED_APP                                         = 1;
    /** 可替换应用 */
    public static final int REPLACEABLE_APP                                         = 2;
    /** 可配置应用 */
    public static final int CONFIG_APP                                              = 3;
    /** 网络站点   */
    public static final int WEBSITE                                                 = 4;
    /** 视窗广告 */
    public static final int ADVERTISEMENT                                           = 5;
    /** 定制坑位：自己处理自己的事件 */
    public static final int CUSTOM_ITEM                                             = 6;


    // 爱奇艺类型
    /** 爱奇艺类型 */
    public static final int QIYI_ITEM                                               = 201;
    /** 爱奇艺频道 */
    public static final int QIYI_CHANNEL                                            = 20;
    /** 爱奇艺轮播 */
    public static final int QIYI_EXTRUDE_RECOMMENDATION                             = 21;
    /** 爱奇艺普通推荐 */
    public static final int QIYI_COMMON_RECOMMENDATION                              = 22;
    /** 爱奇艺主题推荐 */
    public static final int QIYI_SUBJECT_RECOMMENDATION                             = 23;
    /** 爱奇艺特殊PAGE,例如搜索， 历史， 收藏， VIP视频等 */
    public static final int QIYI_SPECIALIZED_PAGE                                   = 24;

    // 容器类型
    /** 容器类型 */
    public static final int CONTAINER_ITEM                                          = 200;
    /** 直播大厅 */
    public static final int TV_CONTAINER                                            = 30;
    /** 影视大厅 */
    public static final int AOD_CONTAINER                                           = 31;
    /** 音乐大厅 */
    public static final int MUSIC_CONTAINER                                         = 32;
    /** 游戏大厅 */
    public static final int GAME_CONTAINER                                          = 33;
    /** 教育大厅 */
    public static final int EDUCATION_CONTAINER                                     = 34;
    /** 应用大厅 */
    public static final int APP_CONTAINER                                           = 35;
    /** 设置大厅 */
    public static final int SETUP_CONTAINER                                         = 36;
    /** 工具大厅 */
    public static final int TOOLS_CONTAINER                                         = 37;
    /** 精选大厅 */
    public static final int EXTRUDE_RECOMMENDATION_CONTAINER                        = 38;
    /** 推荐大厅 */
    public static final int COMMON_RECOMMENDATION_CONTAINER                         = 39;

    // 播视广场舞
    /** 播视广场舞类型 */
    public static final int BOOSJ_DANCE_ITEM                                        = 300;
    /** 分类类别 */
    public static final int BOOSJ_DANCE_CLASSIFICATION                              = 301;
    /** 今日头条 */
    public static final int BOOSJ_DANCE_EXCHANGE                                    = 302;
    /** 人气视频 */
    public static final int BOOSJ_DANCE_RECOMMEND                                   = 303;
    /** 独家精品 */
    public static final int BOOSJ_DANCE_EXCLUSIVE                                   = 304;
    /** 广场舞活动 */
    public static final int BOOSJ_DANCE_ACTIVITY                                    = 305;
    /** 舞曲 */
    public static final int BOOSJ_DANCE_MUSIC                                       = 306;
    /** 原厂导师 */
    public static final int BOOSJ_DANCE_GOLD_TEACHER                                = 307;
    /** 知名舞队 */
    public static final int BOOSJ_DANCE_DAREN                                       = 308;
    /** 养生健康 */
    public static final int BOOSJ_DANCE_HEALTH                                      = 309;
    /** 舞友广场 */
    public static final int BOOSJ_DANCE_SQUARE                                      = 310;
    /** 搜索*/
    public static final int BOOSJ_DANCE_SEARCH                                      = 311;

    // IKTV ITEM
    public static final int IKTV_ITEM                                        = 400;
    public static final int IKTV_ITEM_SINGERS                                = 400;
    public static final int IKTV_ITEM_HOT_NEW_SONGS                          = 401;
    public static final int IKTV_ITEM_LOCAL_SONGS                            = 402;
    public static final int IKTV_ITEM_SONGS                                  = 403;
    public static final int IKTV_ITEM_FAVORITE_SONGS                         = 404;
    public static final int IKTV_ITEM_LANGUAGE                               = 405;
    public static final int IKTV_ITEM_TOPIC                                  = 406;
    public static final int IKTV_ITEM_VARIETY                                = 407;
    public static final int IKTV_ITEM_TIKTOK_SONGS                           = 408;
    public static final int IKTV_ITEM_RADITIONAL_OPERA                       = 409;
    public static final int IKTV_ITEM_BL                                     = 410;
    public static final int HEALTH_SCYD                                      = 411;

    public static final int JSYX_RADITIONAL_OPERA                            = 412;

    /* Action 定义 BEGIN ********************************************************************************************** */
    public static final String ACTION_BROWSE_CONTAINER_ITEMS                        = "com.lycoo.action.BROWSE_CONTAINER_ITEMS";

    public static final String ACTION_TV_CONTAINER                                  = "com.lycoo.action.TV_CONTAINER";
    public static final String ACTION_AOD_CONTAINER                                 = "com.lycoo.action.AOD_CONTAINER";
    public static final String ACTION_MUSIC_CONTAINER                               = "com.lycoo.action.MUSIC_CONTAINER";
    public static final String ACTION_GAME_CONTAINER                                = "com.lycoo.action.GAME_CONTAINER";
    public static final String ACTION_EDUCATION_CONTAINER                           = "com.lycoo.action.EDUCATION_CONTAINER";
    public static final String ACTION_APP_CONTAINER                                 = "com.lycoo.action.APP_CONTAINER";
    public static final String ACTION_SETUP_CONTAINER                               = "com.lycoo.action.SETUP_CONTAINER";
    public static final String ACTION_TOOLS_CONTAINER                               = "com.lycoo.action.TOOLS_CONTAINER";
    public static final String ACTION_EXTRUDE_RECOMMENDATION_CONTAINER              = "com.lycoo.action.EXTRUDE_RECOMMENDATION_CONTAINER";
    public static final String ACTION_COMMON_RECOMMENDATION_CONTAINER               = "com.lycoo.action.COMMON_RECOMMENDATION_CONTAINER";

    public static final String ACTION_LAUNCH_KTV = "com.lycoo.action.START_KTV";
    public static final String PACK_BL_KTV="com.lutongnet.kalaok2";
    public static final String PACK_SCYD_JS="com.lutongnet.ott.health";
    public static final String KEY_BL_CODE="targetCode";
    public static final String BL_CODE="12081";
    public static final String Opera_CODE="KQJX_album";
    public static final String KEY_BL_TYPE="targetType";
    public static final String BL_TYPE="billboard";
    public static final String Opera_TYPE="album";
    public static final String KEY_SCYD_SOURCE="source";
    public static final String SCYD_SOURCE="jiangsu_sarft_qing";
    public static final String KEY_SCYD_PAGETYPE="pageType";
    public static final String SCYD_PAGETYPE="dzxljh";
    public static final String KEY_ITEM_TYPE = "itemType";
    /* Action 定义 END   ********************************************************************************************** */

    /* 坑位类型定义. END   ********************************************************************************************** */
    public static final String DEF_UPDATETIME                                       = "1970-01-01 00:00:00";

    public static final String DB_NAME 												= "Desktop.db";
    public static final int DB_VERSION 												= 1;

    // 桌面坑位表
    public static class DESKTOP_ITEM_TABLE {
        public static final String TABLE_NAME 										= "desktopItem";
        public static final String COLUMN_ID 										= "_id";
        public static final String COLUMN_TAG 										= "tag";
        public static final String COLUMN_TYPE 										= "type";
        public static final String COLUMN_LABEL 									= "label";
        public static final String COLUMN_IMAGE_URL                                 = "imageUrl";
        public static final String COLUMN_ICON_URL                                  = "iconUrl";
        public static final String COLUMN_ICON_VISIBLE 								= "iconVisible";
        public static final String COLUMN_UPDATETIME 								= "updateTime";
        public static final String COLUMN_CLASSNAME                                 = "className";
        public static final String COLUMN_ACTION                                    = "action";
        public static final String COLUMN_PACKAGENAME 								= "packageName";
        public static final String COLUMN_APPVERSION    							= "appVersion";
        public static final String COLUMN_APPURL 									= "appUrl";
        public static final String COLUMN_APPMD5 									= "appMd5";
        public static final String COLUMN_WEBSITE_URL                               = "websiteUrl";
        public static final String COLUMN_QIYI_DATA 								= "qiyiData";
        public static final String COLUMN_PARAM1 							        = "param1";
        public static final String COLUMN_PARAM2 							        = "param2";
        public static final String COLUMN_PARAM3 							        = "param3";
    }

    public static final String TYPE 										        = "type";
    public static final int UNKNOW_TYPE 										    = -1;
    public static final String PARAM1 							                    = "param1";
    public static final String PARAM2 							                    = "param2";
    public static final String PARAM3 							                    = "param3";

    public static class CONTAINER_ITEM_TABLE {
        public static final String TABLE_NAME 										= "containerItem";
        public static final String COLUMN_ID 										= "_id";
        public static final String COLUMN_CONTAINER_TYPE 							= "containerType";
        public static final String COLUMN_PACKAGENAME 								= "packageName";
    }

    // 爱奇艺使能开关属性
    public static final String PROPERTY_QIYI_SWITCH                                 = "persist.lycoo.qiyi.enable";


}
