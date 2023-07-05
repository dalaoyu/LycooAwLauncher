package com.lycoo.desktop.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lycoo.desktop.config.DesktopConstants;
import com.lycoo.desktop.config.DockConstants;


/**
 * Created by lancy on 2017/12/14
 */
public class DesktopDbOpenHelper extends SQLiteOpenHelper {

    // 创建桌面坑位表SQL
    private static final String SQL_CREATE_DESKTOP_ITEM_TABLE = "CREATE TABLE "
            + DesktopConstants.DESKTOP_ITEM_TABLE.TABLE_NAME + " ("
            + DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_TAG + " INTEGER,"
            + DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_TYPE + " INTEGER,"
            + DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_LABEL + " TEXT,"
            + DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_IMAGE_URL + " TEXT,"
            + DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_ICON_URL + " TEXT,"
            + DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_ICON_VISIBLE + " INTEGER,"
            + DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_UPDATETIME + " TEXT,"
            + DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_CLASSNAME + " TEXT,"
            + DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_ACTION + " TEXT,"
            + DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PACKAGENAME + " TEXT,"
            + DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_APPVERSION + " INTEGER,"
            + DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_APPURL + " TEXT,"
            + DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_APPMD5 + " TEXT,"
            + DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_WEBSITE_URL + " TEXT,"
            + DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_QIYI_DATA + " TEXT,"
            + DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PARAM1 + " TEXT,"
            + DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PARAM2 + " TEXT,"
            + DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PARAM3 + " TEXT"

            + ");";
    // 删除桌面坑位表SQL
    private static final String SQL_DROP_DESKTOP_ITEM_TABLE
            = "DROP TABLE IF EXISTS " + DesktopConstants.DESKTOP_ITEM_TABLE.TABLE_NAME;

    private static final String SQL_CREATE_CONTAINER_ITEM_TABLE = "CREATE TABLE "
            + DesktopConstants.CONTAINER_ITEM_TABLE.TABLE_NAME + " ("
            + DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_CONTAINER_TYPE + " INTEGER,"
            + DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_PACKAGENAME + " TEXT"
            + ");";
    // 删除桌面坑位表SQL
    private static final String SQL_DROP_CONTAINER_ITEM_TABLE
            = "DROP TABLE IF EXISTS " + DesktopConstants.CONTAINER_ITEM_TABLE.TABLE_NAME;


    // SQL to create DockItem table
    private static final String SQL_CREATE_DOCK_ITEM_TABLE = "CREATE TABLE "
            + DockConstants.DOCK_ITEM_TABLE.TABLE_NAME + " ("
            + DockConstants.DOCK_ITEM_TABLE.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DockConstants.DOCK_ITEM_TABLE.COLUMN_TAG + " INTEGER,"
            + DockConstants.DOCK_ITEM_TABLE.COLUMN_TYPE + " INTEGER,"
            + DockConstants.DOCK_ITEM_TABLE.COLUMN_LABEL + " TEXT,"
            + DockConstants.DOCK_ITEM_TABLE.COLUMN_IMAGEURL + " TEXT,"
            + DockConstants.DOCK_ITEM_TABLE.COLUMN_ICONURL + " TEXT,"
            + DockConstants.DOCK_ITEM_TABLE.COLUMN_ICON_VISIBLE + " INTEGER,"
            + DockConstants.DOCK_ITEM_TABLE.COLUMN_UPDATETIME + " TEXT,"
            + DockConstants.DOCK_ITEM_TABLE.COLUMN_CLASSNAME + " TEXT,"
            + DockConstants.DOCK_ITEM_TABLE.COLUMN_ACTION + " TEXT,"
            + DockConstants.DOCK_ITEM_TABLE.COLUMN_PACKAGENAME + " TEXT,"
            + DockConstants.DOCK_ITEM_TABLE.COLUMN_APPVERSION + " INTEGER,"
            + DockConstants.DOCK_ITEM_TABLE.COLUMN_APPURL + " TEXT,"
            + DockConstants.DOCK_ITEM_TABLE.COLUMN_APPMD5 + " TEXT,"
            + DockConstants.DOCK_ITEM_TABLE.COLUMN_WEBSITE_URL + " TEXT,"
            + DockConstants.DOCK_ITEM_TABLE.COLUMN_QIYI_DATA + " TEXT,"
            + DockConstants.DOCK_ITEM_TABLE.COLUMN_PARAM1 + " TEXT,"
            + DockConstants.DOCK_ITEM_TABLE.COLUMN_PARAM2 + " TEXT,"
            + DockConstants.DOCK_ITEM_TABLE.COLUMN_PARAM3 + " TEXT"

            + ");";
    // SQL to drop DockItem table
    private static final String SQL_DROP_DOCK_ITEM_TABLE
            = "DROP TABLE IF EXISTS " + DockConstants.DOCK_ITEM_TABLE.TABLE_NAME;


    public DesktopDbOpenHelper(Context context) {
        super(context, DesktopConstants.DB_NAME, null, DesktopConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_DESKTOP_ITEM_TABLE);
        db.execSQL(SQL_CREATE_CONTAINER_ITEM_TABLE);
        db.execSQL(SQL_CREATE_DOCK_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_DESKTOP_ITEM_TABLE);
        db.execSQL(SQL_DROP_CONTAINER_ITEM_TABLE);
        db.execSQL(SQL_DROP_DOCK_ITEM_TABLE);

        onCreate(db);
    }
}
