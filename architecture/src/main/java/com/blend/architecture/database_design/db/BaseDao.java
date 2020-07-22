package com.blend.architecture.database_design.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.blend.architecture.database_design.annotation.DbField;
import com.blend.architecture.database_design.annotation.DbTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
        //1.获取所有字段名 limit用法：第一个参数：为查询结果的索引值（默认从0开始）;第二个参数：为查询结果返回的数量
        String sql = "select * from " + mTableName + " limit 1, 0";  //空表
        //rawQuery：用于执行select语句，第一个参数为select语句；第二个参数为select语句中占位符参数的值，
        // 如果select语句没有使用占位符，该参数可以设置为null
        Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames(); //获取到所有的列名
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
                mCacheMap.put(columnName, columnField); //数据库字段名和成员变量的名字一一匹配
            }
        }
    }

    //执行建表操作
    //create table if not exists tb_user(_id integer, name varchar(20), password varchar(20))
    //使用getCreateTableSql()生成sql语句
    private String getCreateTableSql() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("create table if not exists ");
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


    /*
    拿到数据库字段名和对应的类成员变量的值
    因为这里传入的是泛型，不知道具体的类型，所以需要使用反射根据类对象和类成员变量类型拿到具体的数值，
    再转换成String
     */
    private Map<String, String> getValues(T entity) {
        HashMap<String, String> map = new HashMap<>();
        Iterator<Map.Entry<String, Field>> iterator = mCacheMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Field> entry = iterator.next();
            String key = entry.getKey();    //字段，数据库列名
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

        //2.把数据转移到ContentValues中
        ContentValues values = getContentValues(map);

        /*
            3.开始数据库插入
            String table：表名
            String nullColumnHack：数据不允许插入所有字段值都为NULL的记录，但是如果你指定一个字段为nullColumnHack的值，则数据库允许参加所有字段值都为NULL的记录。
            ContentValues values：通过put重载添加数据，用于将表中每个列名以及相应的待插入数据传入即可
         */

        mSQLiteDatabase.insert(mTableName, null, values);
        return 0;
    }

    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String key = next.getKey();
            String value = next.getValue();
            if (value != null) {
                contentValues.put(key, value);
            }
        }
        return contentValues;
    }


    @Override
    public long update(T entity, T where) {
        int result = -1;
        Map<String, String> values = getValues(entity);
        ContentValues contentValues = getContentValues(values);

        Map<String, String> whereValue = getValues(where);
        Condition condition = new Condition(whereValue);
        /*
        String table：表名
        ContentValues values：将表中每个列名以及相应的待更新的数据传入即可
        String whereClause：对应的是SQL语句的where部分，使用占位符？
        String[] whereArgs：提供一个字符串数组为第三个参数中的每一个占位符指定相应的内容
         */
        result = mSQLiteDatabase.update(mTableName, contentValues, condition.whereClause, condition.whereArgs);
        return result;
    }

    @Override
    public int delete(T where) {
        int result = -1;
        Map<String, String> map = getValues(where);
        Condition condition = new Condition(map);
        /*
        String table:表名
        String whereClause:对应的是SQL语句的where部分，使用占位符？
        String[] whereArgs:提供一个字符串数组为第三个参数中的每一个占位符指定相应的内容
         */
        result = mSQLiteDatabase.delete(mTableName, condition.whereClause, condition.whereArgs);
        return result;
    }

    @Override
    public List<T> query(T where) {
        return query(where, null, null, null);
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        //sqLiteDatabase.query(tableName, null, "id = ?",new String[], null. null, orderBy, "1, 5");
        //1、准本好ContentValues中需要的数据
        Map<String, String> values = getValues(where);

        String limitString = "";
        if (startIndex != null && limit != null) {
            limitString = startIndex + " , " + limit;
        }

        Condition condition = new Condition(values);

        /*
            String table：指定查询的表名
            String[] columns：指定查询的列名
            String selection：指定where的约束条件
            String[] selectionArgs：为where中的占位符提供具体的值
            String groupBy：指定需要的group by的列
            String having：对group by后的结果进行进一步约束
            String orderBy：指定查询结果的排序方式
         */
        Cursor query = mSQLiteDatabase.query(mTableName, null, condition.whereClause,
                condition.whereArgs, null, orderBy, limitString);
        List<T> result = getResult(query, where);
        return result;
    }

    private List<T> getResult(Cursor query, T where) {
        ArrayList<T> list = new ArrayList<>();
        T object;
        while (query.moveToNext()) {
            try {
                object = (T) where.getClass().newInstance();
                Iterator<Map.Entry<String, Field>> iterator = mCacheMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Field> next = iterator.next();
                    String key = next.getKey(); //数据库字段名
                    //获取到某一列在表中对应的位置索引
                    int columnIndex = query.getColumnIndex(key);
                    Field field = next.getValue();  //类的成员变量的名字
                    Class<?> type = field.getType();
                    if (columnIndex != -1) {
                        if (type == String.class) {
                            //根据索引取到相应的值
                            field.set(object, query.getString(columnIndex));
                        } else if (type == Double.class) {
                            field.set(object, query.getDouble(columnIndex));
                        } else if (type == Integer.class) {
                            field.set(object, query.getInt(columnIndex));
                        } else if (type == Long.class) {
                            field.set(object, query.getLong(columnIndex));
                        } else if (type == byte[].class) {
                            field.set(object, query.getBlob(columnIndex));
                        } else {
                            continue;
                        }
                    }
                }
                list.add(object);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } finally {
                query.close();
            }
        }
        return list;
    }

    private class Condition {

        private String whereClause;
        private String[] whereArgs;

        public Condition(Map<String, String> whereCause) {
            ArrayList<String> list = new ArrayList<>();
            StringBuffer buffer = new StringBuffer();
            buffer.append("1=1");   //提供一个占位
            Iterator<Map.Entry<String, String>> iterator = whereCause.entrySet().iterator();
            if (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String key = entry.getKey();
                String value = entry.getValue();
                if (value != null) {
                    buffer.append(" and " + key + "=?");
                    list.add(value);
                }
            }
            whereClause = buffer.toString();
            whereArgs = list.toArray(new String[list.size()]);
        }

    }
}
