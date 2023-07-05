package com.lycoo.commons.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.screensaver.ScreensaverConstants;

/**
 * xxx
 *
 * Created by lancy on 2017/6/23
 */
public class CommonDbOpenHelper extends SQLiteOpenHelper {

    // SQL: 创建屏保表
    private static final String SQL_SCREENSAVER_IMAGE_TABLE_CREATE = "CREATE TABLE "
            + ScreensaverConstants.SCREENSAVER_IMAGE_TABLE.TABLE_NAME + " ("
            + ScreensaverConstants.SCREENSAVER_IMAGE_TABLE.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ScreensaverConstants.SCREENSAVER_IMAGE_TABLE.COLUMN_NAME + " TEXT,"
            + ScreensaverConstants.SCREENSAVER_IMAGE_TABLE.COLUMN_URL + " TEXT,"
            + ScreensaverConstants.SCREENSAVER_IMAGE_TABLE.COLUMN_MD5 + " TEXT,"
            + ScreensaverConstants.SCREENSAVER_IMAGE_TABLE.COLUMN_SIZE + " LONG,"
            + ScreensaverConstants.SCREENSAVER_IMAGE_TABLE.COLUMN_CREATEDATE + " TEXT"
            + ");";
    // SQL: 删除屏保表
    private static final String SQL_SCREENSAVER_IMAGE_TABLE_DROP = "DROP TABLE IF EXISTS " + ScreensaverConstants.SCREENSAVER_IMAGE_TABLE.TABLE_NAME;


    public CommonDbOpenHelper(Context context) {
        super(context, CommonConstants.DB_NAME, null, CommonConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_SCREENSAVER_IMAGE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_SCREENSAVER_IMAGE_TABLE_DROP);

        onCreate(db);
    }
}
