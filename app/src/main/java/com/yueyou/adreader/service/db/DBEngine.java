package com.yueyou.adreader.service.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yueyou.adreader.service.model.BookShelfItem;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class DBEngine extends SQLiteOpenHelper {
    private static DBEngine dbEngine = null;
    private SQLiteDatabase mDB;
    public static final String PrimaryKeyStr = "primaryKeyStr";
    @Retention(RetentionPolicy.RUNTIME)
    public @interface PrimaryKey{
    }
    public static synchronized DBEngine getInstens(Context context) {
        if (dbEngine == null) {
            dbEngine = new DBEngine(context);
            dbEngine.mDB = dbEngine.getWritableDatabase();
        }
        return dbEngine;
    }

    private DBEngine(Context context) {
        super(context, "yueyou.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = ObjectToSql.getCreateTblString(BookShelfItem.class);
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String a = "";
        a = "";
    }

    public void Close() {
        this.mDB.close();
        this.mDB = null;
        this.close();
    }

    public void loadData(List<?> list, Class clazz) {
        try {
            Cursor query = this.mDB.query(clazz.getSimpleName(), (String[])null, (String)null, (String[])null, (String)null, (String)null, (String)null, (String)null);
            while (query.moveToNext()) {
                Object object = clazz.newInstance();
                ObjectToSql.fillObject(query, object);
                ((List<Object>)list).add(object);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addDObject(Object object){
        ContentValues contentValues = ObjectToSql.fillContentValues(object);
        mDB.insert(object.getClass().getSimpleName(), null, contentValues);
    }

    public void updateObject(Object object){
        try {
            ContentValues contentValues = ObjectToSql.fillContentValues(object, true);
            String primaryKey = contentValues.get(PrimaryKeyStr) + "";
            String value = contentValues.get(primaryKey) + "";
            String[] args = {value};
            contentValues.remove(PrimaryKeyStr);
            mDB.update(object.getClass().getSimpleName(), contentValues, primaryKey + "=?", args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteObject(Object object){
        try {
            ContentValues contentValues = ObjectToSql.fillContentValues(object, true, true);
            String primaryKey = contentValues.get(PrimaryKeyStr) + "";
            String value = contentValues.get(primaryKey) + "";
            String[] args = {value};
            mDB.delete(object.getClass().getSimpleName(), primaryKey + "=?", args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

