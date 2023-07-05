package com.lycoo.commons.screensaver;

/**
 * 屏保常量
 *
 * Created by lancy on 2018/1/4
 */
public class ScreensaverConstants {

    public static final String SP_NAME                                      = "sp_screensaver";
    public static final String DEFAULT_UPDATETIME                           = "1970-01-01 00:00:00";
    public static final int DEFAULT_SILENT_TIME                             = 60 * 1000;
    public static final int DEFAULT_PERIOD                                  = 5;

    public static final String NAME                                         = "name";
    public static final String SHOW                                         = "show";
    public static final String PERIOD                                       = "period";
    public static final String UPDATETIME                                   = "updateTime";

    // 屏保
    public static class SCREENSAVER_IMAGE_TABLE {
        public static final String TABLE_NAME 								= "screensaver_image";
        public static final String COLUMN_ID 								= "_id";
        public static final String COLUMN_NAME 								= "name";
        public static final String COLUMN_URL 								= "url";
        public static final String COLUMN_MD5 								= "md5";
        public static final String COLUMN_SIZE 								= "size";
        public static final String COLUMN_CREATEDATE 						= "createDate";
    }
}
