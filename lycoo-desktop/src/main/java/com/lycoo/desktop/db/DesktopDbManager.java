package com.lycoo.desktop.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lycoo.commons.util.LogUtils;
import com.lycoo.desktop.bean.DesktopContainerItemInfo;
import com.lycoo.desktop.bean.DesktopItemInfo;
import com.lycoo.desktop.bean.DockItemInfo;
import com.lycoo.desktop.config.DesktopConstants;
import com.lycoo.desktop.config.DockConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作中间件
 *
 * Created by lancy on 2017/12/14
 */
public class DesktopDbManager {
    private static final String TAG = DesktopDbManager.class.getSimpleName();

    private DesktopDao mDesktopDao;

    private static DesktopDbManager mInstance;

    /**
     * desktop item 坑位锁
     */
    private static final Object DESKTOPITEM_LOCK = new Object();
    private static final Object DOCKITEM_LOCK = new Object();
    private static final Object DESKTOP_CONTAINER_ITEM_LOCK = new Object();
    private List<DockItemInfo> dockItemInfos;

    public static DesktopDbManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DesktopDbManager.class) {
                if (mInstance == null) {
                    mInstance = new DesktopDbManager(context);
                }
            }
        }
        return mInstance;
    }

    public DesktopDbManager(Context context) {
        mDesktopDao = new DesktopDao(context);
    }

    public void test() {
        SQLiteDatabase rdb1 = mDesktopDao.getReadableDatabase();
        SQLiteDatabase rdb2 = mDesktopDao.getReadableDatabase();
        SQLiteDatabase rdb3 = mDesktopDao.getReadableDatabase();
        LogUtils.debug(TAG, "rdb1 = " + rdb1.hashCode());
        LogUtils.debug(TAG, "rdb2 = " + rdb2.hashCode());
        LogUtils.debug(TAG, "rdb3 = " + rdb3.hashCode());

        SQLiteDatabase wdb1 = mDesktopDao.getWritableDatabase();
        SQLiteDatabase wdb2 = mDesktopDao.getWritableDatabase();
        SQLiteDatabase wdb3 = mDesktopDao.getWritableDatabase();
        LogUtils.debug(TAG, "wdb1 = " + rdb1.hashCode());
        LogUtils.debug(TAG, "wdb2 = " + rdb2.hashCode());
        LogUtils.debug(TAG, "wdb3 = " + rdb3.hashCode());

        mDesktopDao.closeDatabase(rdb1);

        wdb2.beginTransaction();

        rdb2.beginTransaction();
        rdb1.beginTransaction();

        mDesktopDao.closeDatabase(wdb1);

        wdb2.beginTransaction();
        wdb1.beginTransaction();
    }

    public void saveDesktopItemInfos(List<DesktopItemInfo> itemInfoss) {
        SQLiteDatabase db = mDesktopDao.getWritableDatabase();

        synchronized (DESKTOPITEM_LOCK) {
            db.beginTransaction();
            try {
                for (DesktopItemInfo itemInfo : itemInfoss) {
                    ContentValues values = new ContentValues();
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_TAG, itemInfo.getTag());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_TYPE, itemInfo.getType());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_LABEL, itemInfo.getLabel());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_IMAGE_URL, itemInfo.getImageUrl());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_ICON_URL, itemInfo.getIconUrl());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_ICON_VISIBLE, itemInfo.isIconVisible());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_UPDATETIME, itemInfo.getUpdateTime());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_CLASSNAME, itemInfo.getClassName());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_ACTION, itemInfo.getAction());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PACKAGENAME, itemInfo.getPackageName());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_APPVERSION, itemInfo.getAppVersion());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_APPURL, itemInfo.getAppUrl());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_APPMD5, itemInfo.getAppMd5());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_WEBSITE_URL, itemInfo.getWebsiteUrl());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_QIYI_DATA, itemInfo.getQiyiData());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_UPDATETIME, itemInfo.getUpdateTime());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PARAM1, itemInfo.getParam1());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PARAM2, itemInfo.getParam2());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PARAM3, itemInfo.getParam3());
                    mDesktopDao.insert(db,
                            DesktopConstants.DESKTOP_ITEM_TABLE.TABLE_NAME,
                            values
                    );
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    public void removeAllDesktopItemInfos() {
        SQLiteDatabase db = mDesktopDao.getWritableDatabase();
        synchronized (DESKTOPITEM_LOCK) {
            try {
                mDesktopDao.delete(db,
                        DesktopConstants.DESKTOP_ITEM_TABLE.TABLE_NAME,
                        null,
                        null);
            } finally {
            }
        }
    }

    public void updateDesktopItemInfo(DesktopItemInfo itemInfo) {
        SQLiteDatabase db = mDesktopDao.getReadableDatabase();

        synchronized (DESKTOPITEM_LOCK) {
            db.beginTransaction(); // 开启事务
            try {
                ContentValues values = new ContentValues();
                values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_TAG, itemInfo.getTag());
                values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_TYPE, itemInfo.getType());
                values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_LABEL, itemInfo.getLabel());
                values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_IMAGE_URL, itemInfo.getImageUrl());
                values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_ICON_URL, itemInfo.getIconUrl());
                values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_ICON_VISIBLE, itemInfo.isIconVisible());
                values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_UPDATETIME, itemInfo.getUpdateTime());
                values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_CLASSNAME, itemInfo.getClassName());
                values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_ACTION, itemInfo.getAction());
                values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PACKAGENAME, itemInfo.getPackageName());
                values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_APPVERSION, itemInfo.getAppVersion());
                values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_APPURL, itemInfo.getAppUrl());
                values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_APPMD5, itemInfo.getAppMd5());
                values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_WEBSITE_URL, itemInfo.getWebsiteUrl());
                values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_QIYI_DATA, itemInfo.getQiyiData());
                values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_UPDATETIME, itemInfo.getUpdateTime());
                values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PARAM1, itemInfo.getParam1());
                values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PARAM2, itemInfo.getParam2());
                values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PARAM3, itemInfo.getParam3());

                mDesktopDao.update(db,
                        DesktopConstants.DESKTOP_ITEM_TABLE.TABLE_NAME,
                        values,
                        DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_TAG + " = ?",
                        new String[]{String.valueOf(itemInfo.getTag())}
                );
                db.setTransactionSuccessful(); // 提交
            } finally {
                db.endTransaction();
            }
        }
    }

    public synchronized void updateDesktopItemInfos(List<DesktopItemInfo> desktopItemInfos) {
        SQLiteDatabase db = mDesktopDao.getReadableDatabase();

        synchronized (DESKTOPITEM_LOCK) {
            db.beginTransaction(); // 开启事务
            try {
                for (DesktopItemInfo itemInfo : desktopItemInfos) {
                    ContentValues values = new ContentValues();
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_TAG, itemInfo.getTag());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_TYPE, itemInfo.getType());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_LABEL, itemInfo.getLabel());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_IMAGE_URL, itemInfo.getImageUrl());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_ICON_URL, itemInfo.getIconUrl());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_ICON_VISIBLE, itemInfo.isIconVisible());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_UPDATETIME, itemInfo.getUpdateTime());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_CLASSNAME, itemInfo.getClassName());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_ACTION, itemInfo.getAction());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PACKAGENAME, itemInfo.getPackageName());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_APPVERSION, itemInfo.getAppVersion());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_APPURL, itemInfo.getAppUrl());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_APPMD5, itemInfo.getAppMd5());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_WEBSITE_URL, itemInfo.getWebsiteUrl());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_QIYI_DATA, itemInfo.getQiyiData());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_UPDATETIME, itemInfo.getUpdateTime());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PARAM1, itemInfo.getParam1());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PARAM2, itemInfo.getParam2());
                    values.put(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PARAM3, itemInfo.getParam3());

                    mDesktopDao.update(db,
                            DesktopConstants.DESKTOP_ITEM_TABLE.TABLE_NAME,
                            values,
                            DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_TAG + " = ?",
                            new String[]{String.valueOf(itemInfo.getTag())}
                    );
                }
                db.setTransactionSuccessful(); // 提交
            } finally {
                db.endTransaction();
            }
        }
    }

    public List<DesktopItemInfo> getDesktopItemInfos(String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDesktopDao.getReadableDatabase();

        List<DesktopItemInfo> list = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = mDesktopDao.query(db,
                    DesktopConstants.DESKTOP_ITEM_TABLE.TABLE_NAME,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null,
                    null);

            if (cursor == null)
                return list;

            DesktopItemInfo itemInfo;
            while (cursor.moveToNext()) {
                itemInfo = new DesktopItemInfo();
                itemInfo.setId(cursor.getInt(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_ID)));
                itemInfo.setTag(cursor.getInt(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_TAG)));
                itemInfo.setType(cursor.getInt(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_TYPE)));
                itemInfo.setLabel(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_LABEL)));
                itemInfo.setImageUrl(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_IMAGE_URL)));
                itemInfo.setIconUrl(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_ICON_URL)));
                itemInfo.setIconVisible(cursor.getInt(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_ICON_VISIBLE)) != 0);
                itemInfo.setUpdateTime(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_UPDATETIME)));
                itemInfo.setClassName(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_CLASSNAME)));
                itemInfo.setPackageName(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PACKAGENAME)));
                itemInfo.setAction(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_ACTION)));
                itemInfo.setAppVersion(cursor.getInt(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_APPVERSION)));
                itemInfo.setAppUrl(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_APPURL)));
                itemInfo.setAppMd5(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_APPMD5)));
                itemInfo.setWebsiteUrl(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_WEBSITE_URL)));
                itemInfo.setQiyiData(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_QIYI_DATA)));
                itemInfo.setParam1(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PARAM1)));
                itemInfo.setParam2(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PARAM2)));
                itemInfo.setParam3(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PARAM3)));
                list.add(itemInfo);
            }
            return list;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public DesktopItemInfo getDesktopItemInfo(String selection, String[] selectionArg) {
        SQLiteDatabase db = mDesktopDao.getReadableDatabase();

        DesktopItemInfo itemInfo = null;
        Cursor cursor = null;
        try {
            cursor = mDesktopDao.query(db,//
                    DesktopConstants.DESKTOP_ITEM_TABLE.TABLE_NAME, //
                    null,//
                    selection, //
                    selectionArg,//
                    null,//
                    null,//
                    null,//
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                itemInfo = new DesktopItemInfo();
                itemInfo.setId(cursor.getInt(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_ID)));
                itemInfo.setTag(cursor.getInt(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_TAG)));
                itemInfo.setType(cursor.getInt(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_TYPE)));
                itemInfo.setLabel(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_LABEL)));
                itemInfo.setImageUrl(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_IMAGE_URL)));
                itemInfo.setIconUrl(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_ICON_URL)));
                itemInfo.setIconVisible(cursor.getInt(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_ICON_VISIBLE)) != 0);
                itemInfo.setUpdateTime(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_UPDATETIME)));
                itemInfo.setClassName(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_CLASSNAME)));
                itemInfo.setPackageName(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PACKAGENAME)));
                itemInfo.setAction(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_ACTION)));
                itemInfo.setAppVersion(cursor.getInt(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_APPVERSION)));
                itemInfo.setAppUrl(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_APPURL)));
                itemInfo.setAppMd5(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_APPMD5)));
                itemInfo.setWebsiteUrl(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_WEBSITE_URL)));
                itemInfo.setQiyiData(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_QIYI_DATA)));
                itemInfo.setParam1(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PARAM1)));
                itemInfo.setParam2(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PARAM2)));
                itemInfo.setParam3(cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PARAM3)));
            }
            return itemInfo;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public String getDesktopItemUpdateTime(String tag) {
        SQLiteDatabase db = mDesktopDao.getReadableDatabase();

        String updateTime = DesktopConstants.DEF_UPDATETIME;
        Cursor cursor = null;
        try {
            cursor = mDesktopDao.query(db,//
                    DesktopConstants.DESKTOP_ITEM_TABLE.TABLE_NAME, //
                    null,//
                    DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_TAG + " = ?", //
                    new String[]{tag},//
                    null,//
                    null,//
                    null,//
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                updateTime = cursor.getString(cursor.getColumnIndex(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_UPDATETIME));
            }
            return updateTime;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<String> getDesktopItemsColumn(String column) {
        SQLiteDatabase db = mDesktopDao.getReadableDatabase();

        List<String> columns = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDesktopDao.query(db,//
                    DesktopConstants.DESKTOP_ITEM_TABLE.TABLE_NAME, //
                    new String[]{column},//
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);

            if (cursor == null) {
                return columns;
            }

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

    // ContainerItemInfo =========================================================================================

    /**
     * 保存容器子坑位信息
     *
     * @param itemInfos 容器子坑位信息
     *
     *                  Created by lancy on 2018/6/20 16:46
     */
    public void saveDesktopContainerItemInfos(List<DesktopContainerItemInfo> itemInfos) {
        SQLiteDatabase db = mDesktopDao.getWritableDatabase();

        synchronized (DESKTOP_CONTAINER_ITEM_LOCK) {
            db.beginTransaction();
            try {
                for (DesktopContainerItemInfo itemInfo : itemInfos) {
                    ContentValues values = new ContentValues();
                    values.put(DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_CONTAINER_TYPE, itemInfo.getContainerType());
                    values.put(DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_PACKAGENAME, itemInfo.getPackageName());
                    mDesktopDao.insert(db,
                            DesktopConstants.CONTAINER_ITEM_TABLE.TABLE_NAME,
                            values
                    );
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    /**
     * 保存容器子坑位信息
     *
     * @param itemInfos 容器子坑位信息
     *
     *                  Created by lancy on 2018/6/20 16:46
     */
    public void saveDesktopContainerItemInfo(DesktopContainerItemInfo containerItemInfo) {
        SQLiteDatabase db = mDesktopDao.getWritableDatabase();

        synchronized (DESKTOP_CONTAINER_ITEM_LOCK) {
            db.beginTransaction();
            try {
                ContentValues values = new ContentValues();
                values.put(DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_CONTAINER_TYPE, containerItemInfo.getContainerType());
                values.put(DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_PACKAGENAME, containerItemInfo.getPackageName());
                mDesktopDao.insert(db,
                        DesktopConstants.CONTAINER_ITEM_TABLE.TABLE_NAME,
                        values
                );
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    /**
     * 删除容器子坑位信息
     *
     * @param selection    条件
     * @param selectionArg 参数
     * @return 影响的行数
     *
     * Created by lancy on 2018/6/20 16:47
     */
    public int removeContainerItemInfos(String selection, String[] selectionArg) {
        SQLiteDatabase db = mDesktopDao.getWritableDatabase();
        synchronized (DESKTOP_CONTAINER_ITEM_LOCK) {
            try {
                return mDesktopDao.delete(db,
                        DesktopConstants.CONTAINER_ITEM_TABLE.TABLE_NAME,
                        selection,
                        selectionArg);
            } finally {
            }
        }
    }

    /**
     * 获取容器子坑位包名列表
     *
     * @param selection    条件
     * @param selectionArg 参数
     * @return 容器子坑位包名列表
     *
     * Created by lancy on 2018/6/20 16:50
     */
    public List<String> getContainerItemPackageNames(String selection, String[] selectionArg) {
        SQLiteDatabase db = mDesktopDao.getReadableDatabase();
        List<String> columns = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDesktopDao.query(db,
                    DesktopConstants.CONTAINER_ITEM_TABLE.TABLE_NAME,
                    new String[]{DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_PACKAGENAME},
                    selection,
                    selectionArg,
                    null,
                    null,
                    null,
                    null);

            if (cursor == null) {
                return columns;
            }

            String queryColumn;
            while (cursor.moveToNext()) {
                queryColumn = cursor.getString(cursor.getColumnIndex(DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_PACKAGENAME));
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

    /**
     * 查询容器子坑位信息
     *
     * @param selection    条件
     * @param selectionArg 参数
     * @return 容器子坑位信息
     *
     * Created by lancy on 2018/6/20 16:52
     */
    public DesktopContainerItemInfo getContainerItemInfo(String selection, String[] selectionArg) {
        SQLiteDatabase db = mDesktopDao.getReadableDatabase();

        DesktopContainerItemInfo itemInfo = null;
        Cursor cursor = null;
        try {
            cursor = mDesktopDao.query(db,//
                    DesktopConstants.CONTAINER_ITEM_TABLE.TABLE_NAME, //
                    null,//
                    selection, //
                    selectionArg,//
                    null,//
                    null,//
                    null,//
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                itemInfo = new DesktopContainerItemInfo();
                itemInfo.setId(cursor.getInt(cursor.getColumnIndex(DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_ID)));
                itemInfo.setContainerType(cursor.getInt(cursor.getColumnIndex(DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_CONTAINER_TYPE)));
                itemInfo.setPackageName(cursor.getString(cursor.getColumnIndex(DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_PACKAGENAME)));
            }
            return itemInfo;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * 查询容器子坑位信息
     *
     * @param selection    条件
     * @param selectionArg 参数
     * @return 容器子坑位信息
     *
     * Created by lancy on 2018/6/20 16:52
     */
    public List<DesktopContainerItemInfo> getContainerItemInfos(String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDesktopDao.getReadableDatabase();

        List<DesktopContainerItemInfo> list = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = mDesktopDao.query(db,
                    DesktopConstants.CONTAINER_ITEM_TABLE.TABLE_NAME,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null,
                    null);

            if (cursor == null)
                return list;

            DesktopContainerItemInfo itemInfo;
            while (cursor.moveToNext()) {
                itemInfo = new DesktopContainerItemInfo();
                itemInfo.setId(cursor.getInt(cursor.getColumnIndex(DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_ID)));
                itemInfo.setContainerType(cursor.getInt(cursor.getColumnIndex(DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_CONTAINER_TYPE)));
                itemInfo.setPackageName(cursor.getString(cursor.getColumnIndex(DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_PACKAGENAME)));
                list.add(itemInfo);
            }
            return list;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    // DockItemInfo ==============================================================================================
    public void saveDockItemInfos(List<DockItemInfo> itemInfoss) {
        SQLiteDatabase db = mDesktopDao.getWritableDatabase();

        synchronized (DOCKITEM_LOCK) {
            db.beginTransaction();
            try {
                for (DockItemInfo itemInfo : itemInfoss) {
                    ContentValues values = new ContentValues();
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_TAG, itemInfo.getTag());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_TYPE, itemInfo.getType());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_LABEL, itemInfo.getLabel());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_IMAGEURL, itemInfo.getImageUrl());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_ICONURL, itemInfo.getIconUrl());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_ICON_VISIBLE, itemInfo.isIconVisible());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_UPDATETIME, itemInfo.getUpdateTime());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_CLASSNAME, itemInfo.getClassName());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_ACTION, itemInfo.getAction());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_PACKAGENAME, itemInfo.getPackageName());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_APPVERSION, itemInfo.getAppVersion());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_APPURL, itemInfo.getAppUrl());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_APPMD5, itemInfo.getAppMd5());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_WEBSITE_URL, itemInfo.getWebsiteUrl());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_QIYI_DATA, itemInfo.getQiyiData());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_UPDATETIME, itemInfo.getUpdateTime());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_PARAM1, itemInfo.getParam1());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_PARAM2, itemInfo.getParam2());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_PARAM3, itemInfo.getParam3());
                    LogUtils.error(TAG,"values  "+ values.toString());
                    mDesktopDao.insert(db,
                            DockConstants.DOCK_ITEM_TABLE.TABLE_NAME,
                            values
                    );
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    public void removeAllDockItemInfos() {
        SQLiteDatabase db = mDesktopDao.getWritableDatabase();
        synchronized (DOCKITEM_LOCK) {
            try {
                mDesktopDao.delete(db,
                        DockConstants.DOCK_ITEM_TABLE.TABLE_NAME,
                        null,
                        null);
            } finally {
            }
        }
    }

    public void updateDockItemInfos(List<DockItemInfo> itemInfoss) {
        SQLiteDatabase db = mDesktopDao.getWritableDatabase();

        synchronized (DOCKITEM_LOCK) {
            db.beginTransaction();
            try {
                for (DockItemInfo itemInfo : itemInfoss) {
                    ContentValues values = new ContentValues();
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_TAG, itemInfo.getTag());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_TYPE, itemInfo.getType());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_LABEL, itemInfo.getLabel());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_IMAGEURL, itemInfo.getImageUrl());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_ICONURL, itemInfo.getIconUrl());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_CLASSNAME, itemInfo.getClassName());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_ACTION, itemInfo.getAction());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_PACKAGENAME, itemInfo.getPackageName());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_APPVERSION, itemInfo.getAppVersion());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_APPURL, itemInfo.getAppUrl());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_APPMD5, itemInfo.getAppMd5());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_WEBSITE_URL, itemInfo.getWebsiteUrl());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_QIYI_DATA, itemInfo.getQiyiData());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_ICON_VISIBLE, itemInfo.isIconVisible());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_UPDATETIME, itemInfo.getUpdateTime());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_PARAM1, itemInfo.getParam1());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_PARAM2, itemInfo.getParam2());
                    values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_PARAM3, itemInfo.getParam3());
                    mDesktopDao.update(db,
                            DockConstants.DOCK_ITEM_TABLE.TABLE_NAME,
                            values,
                            DockConstants.DOCK_ITEM_TABLE.COLUMN_TAG + " = ?",
                            new String[]{String.valueOf(itemInfo.getTag())});
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }



    public void updateDockItemInfo(DockItemInfo itemInfo) {
        SQLiteDatabase db = mDesktopDao.getReadableDatabase();

        synchronized (DOCKITEM_LOCK) {
            db.beginTransaction(); // 开启事务
            try {
                ContentValues values = new ContentValues();
                values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_TAG, itemInfo.getTag());
                values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_TYPE, itemInfo.getType());
                values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_LABEL, itemInfo.getLabel());
                values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_IMAGEURL, itemInfo.getImageUrl());
                values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_ICONURL, itemInfo.getIconUrl());
                values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_CLASSNAME, itemInfo.getClassName());
                values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_ACTION, itemInfo.getAction());
                values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_PACKAGENAME, itemInfo.getPackageName());
                values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_APPVERSION, itemInfo.getAppVersion());
                values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_APPURL, itemInfo.getAppUrl());
                values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_APPMD5, itemInfo.getAppMd5());
                values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_WEBSITE_URL, itemInfo.getWebsiteUrl());
                values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_QIYI_DATA, itemInfo.getQiyiData());
                values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_ICON_VISIBLE, itemInfo.isIconVisible());
                values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_UPDATETIME, itemInfo.getUpdateTime());
                values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_PARAM1, itemInfo.getParam1());
                values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_PARAM2, itemInfo.getParam2());
                values.put(DockConstants.DOCK_ITEM_TABLE.COLUMN_PARAM3, itemInfo.getParam3());
                mDesktopDao.update(db,
                        DockConstants.DOCK_ITEM_TABLE.TABLE_NAME,
                        values,
                        DockConstants.DOCK_ITEM_TABLE.COLUMN_TAG + " = ?",
                        new String[]{String.valueOf(itemInfo.getTag())}
                );
                db.setTransactionSuccessful(); // 提交
            } finally {
                db.endTransaction();
            }
        }
    }

    public List<DockItemInfo> getDockItemInfos(String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDesktopDao.getReadableDatabase();

        List<DockItemInfo> list = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = mDesktopDao.query(db,
                    DockConstants.DOCK_ITEM_TABLE.TABLE_NAME,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null,
                    null);

            if (cursor == null)
                return list;

            DockItemInfo itemInfo;
            while (cursor.moveToNext()) {
                itemInfo = new DockItemInfo();
                itemInfo.setId(cursor.getInt(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_ID)));
                itemInfo.setTag(cursor.getInt(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_TAG)));
                itemInfo.setType(cursor.getInt(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_TYPE)));
                itemInfo.setLabel(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_LABEL)));
                itemInfo.setImageUrl(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_IMAGEURL)));
                itemInfo.setIconUrl(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_ICONURL)));
                itemInfo.setIconVisible(cursor.getInt(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_ICON_VISIBLE)) != 0);
                itemInfo.setUpdateTime(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_UPDATETIME)));
                itemInfo.setClassName(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_CLASSNAME)));
                itemInfo.setPackageName(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_PACKAGENAME)));
                itemInfo.setAction(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_ACTION)));
                itemInfo.setAppVersion(cursor.getInt(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_APPVERSION)));
                itemInfo.setAppUrl(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_APPURL)));
                itemInfo.setAppMd5(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_APPMD5)));
                itemInfo.setWebsiteUrl(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_WEBSITE_URL)));
                itemInfo.setQiyiData(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_QIYI_DATA)));
                itemInfo.setParam1(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_PARAM1)));
                itemInfo.setParam2(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_PARAM2)));
                itemInfo.setParam3(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_PARAM3)));
                list.add(itemInfo);
            }
            return list;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public DockItemInfo getDockItemInfo(String selection, String[] selectionArg) {
        SQLiteDatabase db = mDesktopDao.getReadableDatabase();

        DockItemInfo itemInfo = null;
        Cursor cursor = null;
        try {
            cursor = mDesktopDao.query(db,//
                    DockConstants.DOCK_ITEM_TABLE.TABLE_NAME,
                    null,//
                    selection, //
                    selectionArg,//
                    null,//
                    null,//
                    null,//
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                itemInfo = new DockItemInfo();
                itemInfo.setId(cursor.getInt(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_ID)));
                itemInfo.setTag(cursor.getInt(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_TAG)));
                itemInfo.setType(cursor.getInt(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_TYPE)));
                itemInfo.setLabel(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_LABEL)));
                itemInfo.setImageUrl(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_IMAGEURL)));
                itemInfo.setIconUrl(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_ICONURL)));
                itemInfo.setIconVisible(cursor.getInt(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_ICON_VISIBLE)) != 0);
                itemInfo.setUpdateTime(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_UPDATETIME)));
                itemInfo.setClassName(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_CLASSNAME)));
                itemInfo.setPackageName(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_PACKAGENAME)));
                itemInfo.setAction(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_ACTION)));
                itemInfo.setAppVersion(cursor.getInt(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_APPVERSION)));
                itemInfo.setAppUrl(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_APPURL)));
                itemInfo.setAppMd5(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_APPMD5)));
                itemInfo.setWebsiteUrl(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_WEBSITE_URL)));
                itemInfo.setQiyiData(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_QIYI_DATA)));
                itemInfo.setParam1(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_PARAM1)));
                itemInfo.setParam2(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_PARAM2)));
                itemInfo.setParam3(cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_PARAM3)));
            }
            return itemInfo;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<String> getDockItemsColumn(String column) {
        SQLiteDatabase db = mDesktopDao.getReadableDatabase();

        List<String> columns = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDesktopDao.query(db,//
                    DockConstants.DOCK_ITEM_TABLE.TABLE_NAME, //
                    new String[]{column},//
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);

            if (cursor == null) {
                return columns;
            }

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

    public String getDockItemUpdateTime(String tag) {
        SQLiteDatabase db = mDesktopDao.getReadableDatabase();

        String updateTime = DockConstants.DEF_UPDATETIME;
        Cursor cursor = null;
        try {
            cursor = mDesktopDao.query(db,//
                    DockConstants.DOCK_ITEM_TABLE.TABLE_NAME, //
                    null,//
                    DockConstants.DOCK_ITEM_TABLE.COLUMN_TAG + " = ?", //
                    new String[]{tag},//
                    null,//
                    null,//
                    null,//
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                updateTime = cursor.getString(cursor.getColumnIndex(DockConstants.DOCK_ITEM_TABLE.COLUMN_UPDATETIME));
            }
            return updateTime;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}