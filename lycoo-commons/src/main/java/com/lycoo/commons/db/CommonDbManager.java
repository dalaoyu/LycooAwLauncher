package com.lycoo.commons.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lycoo.commons.screensaver.ScreensaverConstants;
import com.lycoo.commons.entity.ScreensaverImage;
import com.lycoo.commons.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 公共数据库辅助类
 *
 * Created by lancy on 2017/6/23
 */
public class CommonDbManager {

    /**
     * desktop item 操作锁
     */
    private static final Object mScreensaverLock = new Object();

    private CommonDao mCommonDao;

    private static CommonDbManager mCommonDbManager;

    public static CommonDbManager getInstance(Context context) {
        if (mCommonDbManager == null) {
            mCommonDbManager = new CommonDbManager(context);
        }

        return mCommonDbManager;
    }

    public CommonDbManager(Context context) {
        mCommonDao = new CommonDao(context);
    }

    public void saveScreensaverImages(List<ScreensaverImage> screensaverImages) {
        if (CollectionUtils.isEmpty(screensaverImages)) {
            return;
        }

        SQLiteDatabase db = mCommonDao.getWritableDatabase();
        synchronized (mScreensaverLock) {
            db.beginTransaction();
            try {
                for (ScreensaverImage image : screensaverImages) {
                    ContentValues values = new ContentValues();
                    values.put(ScreensaverConstants.SCREENSAVER_IMAGE_TABLE.COLUMN_NAME, image.getName());
                    values.put(ScreensaverConstants.SCREENSAVER_IMAGE_TABLE.COLUMN_URL, image.getUrl());
                    values.put(ScreensaverConstants.SCREENSAVER_IMAGE_TABLE.COLUMN_MD5, image.getMd5());
                    values.put(ScreensaverConstants.SCREENSAVER_IMAGE_TABLE.COLUMN_SIZE, image.getSize());
                    values.put(ScreensaverConstants.SCREENSAVER_IMAGE_TABLE.COLUMN_CREATEDATE, image.getCreateDate());
                    mCommonDao.insert(db,
                            ScreensaverConstants.SCREENSAVER_IMAGE_TABLE.TABLE_NAME,
                            values
                    );
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    public void removeAllScreensaverImages() {
        SQLiteDatabase db = mCommonDao.getWritableDatabase();
        synchronized (mScreensaverLock) {
            try {
                mCommonDao.delete(db,
                        ScreensaverConstants.SCREENSAVER_IMAGE_TABLE.TABLE_NAME,
                        null,
                        null);
            } finally {
            }
        }

    }

    public List<String> getScreensaverImageColumn(String column) {
        SQLiteDatabase db = mCommonDao.getReadableDatabase();

        List<String> columns = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mCommonDao.query(db,//
                    ScreensaverConstants.SCREENSAVER_IMAGE_TABLE.TABLE_NAME, //
                    new String[]{column},//
                    null,//
                    null,//
                    null,//
                    null,//
                    null,//
                    null);

            if (cursor == null)
                return columns;

            String queryColumn;
            while (cursor.moveToNext()) {
                queryColumn = cursor.getString(cursor.getColumnIndex(column));
                if (queryColumn != null && !queryColumn.isEmpty()) {
                    columns.add(queryColumn);
                }
            }
            return columns;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
