package com.lycoo.commons.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CommonDao {

	private SQLiteOpenHelper mOpenHelper;

	public CommonDao(Context context) {
		mOpenHelper = new CommonDbOpenHelper(context);
	}

	public SQLiteDatabase getWritableDatabase() {
		return mOpenHelper.getWritableDatabase();
	}

	public SQLiteDatabase getReadableDatabase() {
		return mOpenHelper.getReadableDatabase();
	}

	public void closeDatabase(SQLiteDatabase db) {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}

	public long insert(SQLiteDatabase db, String table, ContentValues values) {
		return db.insert(table, null, values);
	}

	public int delete(SQLiteDatabase db, String table, String whereClause, String[] whereArgs) {
		return db.delete(table, whereClause, whereArgs);
	}

	public int update(SQLiteDatabase db, String table, ContentValues values, String whereClause, String[] whereArgs) {
		return db.update(table, values, whereClause, whereArgs);
	}

	public Cursor query(SQLiteDatabase db, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {
		return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
	}
}
