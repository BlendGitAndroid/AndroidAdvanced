package com.blend.architecture.database_design.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.blend.architecture.database_design.annotation.DbField;
import com.blend.architecture.database_design.annotation.DbTable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BaseDao<T> implements IBaseDao<T> {

    //持有数据库操作的引用
    private SQLiteDatabase mSQLiteDatabase;

    //表明
    private String mTableName;

    //
    private Class<T> mEntityClass;

    private boolean isInit = false;

    private HashMap<String, Field> mCacheMap;

    //架构内部的逻辑，最后不要提供构造方法给调用层使用
    public void init(SQLiteDatabase sqLiteDatabase, Class<T> entityClass) {
        mSQLiteDatabase = sqLiteDatabase;
        mEntityClass = entityClass;
        //自动建表
        if (!isInit) {
            //获取表名
            if (entityClass.getAnnotation(DbTable.class) == null) {
                //反射到类名
                mTableName = entityClass.getSimpleName();
            } else {
                //取得注解上的名字
                mTableName = entityClass.getAnnotation(DbTable.class).value();
            }
            String createTableSql = getCreateTableSql();
            mSQLiteDatabase.execSQL(createTableSql);

            mCacheMap = new HashMap<>();
            initCache();
            isInit = false;
        }
    }

    private void initCache() {
        //1.获取所有字段名
        String sql = "select * from " + mTableName + " limit 1, 0";  //空表
        Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        //2.取得会有的成员变量
        Field[] declaredFields = mEntityClass.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
        }
        //3.字段和成员变量一一对应
        for (String columnName : columnNames) {
            Field columnField = null;
            for (Field field : declaredFields) {
                String fieldName = "";
                if (field.getAnnotation(DbField.class) != null) {
                    fieldName = field.getAnnotation(DbField.class).value();
                } else {
                    fieldName = field.getName();
                }

                if (columnName.equals(fieldName)) {
                    columnField = field;
                    break;
                }
            }
            if (columnField != null) {
                mCacheMap.put(columnName, columnField); //字段名和成员变量的名字一一匹配
            }
        }
    }

    //执行建表操作
    //create table if not exists tb_user(_id integer, name varchar(20), password varchar(20))
    //使用getCreateTableSql()生成sql语句
    private String getCreateTableSql() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("create table is not exists ");
        stringBuffer.append(mTableName + "(");
        //反射取得所有的成员变量
        Field[] declaredFields = mEntityClass.getDeclaredFields();
        for (Field field : declaredFields) {
            Class type = field.getType();   //拿到成员的类型
            if (field.getAnnotation(DbField.class) != null) {
                //通过注解获取
                if (type == String.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " TEXT,");
                } else if (type == Integer.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " INTEGER,");
                } else if (type == Long.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " BIGINT,");
                } else if (type == Double.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " DOUBLE,");
                } else if (type == byte[].class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " BLOB,");
                } else {
                    //不支持的类型
                    continue;
                }
            } else {
                //通过反射获取
                if (type == String.class) {
                    stringBuffer.append(field.getName() + " TEXT,");
                } else if (type == Integer.class) {
                    stringBuffer.append(field.getName() + " INTEGER,");
                } else if (type == Long.class) {
                    stringBuffer.append(field.getName() + " BIGINT,");
                } else if (type == Double.class) {
                    stringBuffer.append(field.getName() + " DOUBLE,");
                } else if (type == byte[].class) {
                    stringBuffer.append(field.getName() + " BLOB,");
                } else {
                    //不支持的类型
                    continue;
                }
            }
        }

        if (stringBuffer.charAt(stringBuffer.length() - 1) == ',') {
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }

    private Map<String, String> getValues(T entity) {
        HashMap<String, String> map = new HashMap<>();
        Iterator<Map.Entry<String, Field>> iterator = mCacheMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Field> entry = iterator.next();
            String key = entry.getKey();    //字段，列名
            Field field = entry.getValue(); //成员变量
            field.setAccessible(true);
            try {
                Object object = field.get(entity);
                if (object == null) {
                    continue;
                }
                String fieldValue = object.toString();
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(fieldValue)) {
                    map.put(key, fieldValue);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /*
       ContentValues contentValues = new ContentValues();
       contentValues.put("_id", 1);  getid
       contentValues.put("name", "alan");
       contentValues.put("password", "123");
       sqLiteDatabase.insert(tableName, null, contentValues);
     */
    @Override
    public long insert(T entity) {
        //1.准备好ContentValues中需要的数据
        Map<String, String> map = getValues(entity);
        return 0;
    }


    @Override
    public long update(T entity, T where) {
        return 0;
    }

    @Override
    public int delete(T where) {
        return 0;
    }

    @Override
    public List<T> query(T where) {
        return null;
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        return null;
    }
}
