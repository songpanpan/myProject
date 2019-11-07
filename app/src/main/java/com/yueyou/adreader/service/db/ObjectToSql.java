package com.yueyou.adreader.service.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.lang.reflect.Field;
public class ObjectToSql {
    public static String getCreateTblString(Class clazz){
        final Field[] declaredFields = clazz.getDeclaredFields();
        String sql = "create table if not exists " + clazz.getSimpleName() + "(";
        for (int i = 0; i < declaredFields.length; i++) {
            if (declaredFields[i].isSynthetic())
                continue;
            String type = declaredFields[i].getGenericType().toString();
            String name = declaredFields[i].getName();
            if (name.equals("serialVersionUID"))
                continue;
            if (type.equals("int")) {
                sql += name + " integer,";
            }
            else if (type.equals("class java.lang.String")) {
                sql += name + " text,";
            }
            else {
                sql += name + " integer,";
            }
        }
        return sql.substring(0, sql.length() - 1) + ")";
    }

    public static boolean fillObject(Cursor cursor, Object object){
        try {
            final Field[] declaredFields = object.getClass().getDeclaredFields();
            for (int i = 0; i < declaredFields.length; i++) {
                String name = declaredFields[i].getName();
                String type = declaredFields[i].getGenericType().toString();
                if (cursor.getColumnIndex(name) < 0)
                    continue;
                declaredFields[i].setAccessible(true);
                if (type.equalsIgnoreCase("int")) {
                    declaredFields[i].setInt(object, cursor.getInt(cursor.getColumnIndex(name)));
                } else if (type.equalsIgnoreCase("class java.lang.String")) {
                    declaredFields[i].set(object, cursor.getString(cursor.getColumnIndex(name)));
                } else if (type.equalsIgnoreCase("boolean")) {
                    declaredFields[i].setBoolean(object, cursor.getInt(cursor.getColumnIndex(name))==1);
                } else if (type.equalsIgnoreCase("double")) {
                    declaredFields[i].setDouble(object, cursor.getDouble(cursor.getColumnIndex(name)));
                }
            }
            return true;
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static ContentValues fillContentValues(final Object object) {
        return fillContentValues(object, false, false);
    }

    public static ContentValues fillContentValues(final Object object, boolean primaryKey) {
        return fillContentValues(object, primaryKey, false);
    }

    public static ContentValues fillContentValues(final Object object, boolean primaryKey, boolean onlyPrimaryKey) {
        try {
            ContentValues contentValues = new ContentValues();
            Field[] declaredFields = object.getClass().getDeclaredFields();
            for (int i = 0; i < declaredFields.length; i++) {
                if (declaredFields[i].isSynthetic())
                    continue;
                String name = declaredFields[i].getName();
                String type = declaredFields[i].getGenericType().toString();
                if (name.equals("serialVersionUID"))
                    continue;
                declaredFields[i].setAccessible(true);
                if (primaryKey) {
                    boolean fieldIsPrimaryKey = declaredFields[i].isAnnotationPresent(DBEngine.PrimaryKey.class);
                    if (fieldIsPrimaryKey){
                        contentValues.put(DBEngine.PrimaryKeyStr, name);
                        if (onlyPrimaryKey){
                            contentValues.put(name, declaredFields[i].getInt(object));
                            return contentValues;
                        }
                    }
                }
                if (onlyPrimaryKey)
                    continue;
                if (type.equalsIgnoreCase("int")) {
                    contentValues.put(name, declaredFields[i].getInt(object));
                }else if (type.equalsIgnoreCase("boolean")) {
                    contentValues.put(name, declaredFields[i].getBoolean(object));
                }else if (type.equalsIgnoreCase("class java.lang.String")) {
                    if (declaredFields[i].get(object) != null)
                        contentValues.put(name, declaredFields[i].get(object).toString());
                }else {
                    contentValues.put(name, declaredFields[i].getDouble(object));
                }
            }
            return contentValues;
        }
        catch (Exception ex) {
            return null;
        }
    }
}
