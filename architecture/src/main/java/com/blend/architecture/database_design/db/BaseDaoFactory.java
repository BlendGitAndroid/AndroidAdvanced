package com.blend.architecture.database_design.db;

import android.database.sqlite.SQLiteDatabase;

public class BaseDaoFactory {

    private static final BaseDaoFactory sInstance = new BaseDaoFactory();

    private SQLiteDatabase mSQLiteDatabase;

    private String mSqlDataBasePath;

    public static BaseDaoFactory getInstance() {
        return sInstance;
    }

    private BaseDaoFactory() {
        mSqlDataBasePath = "";
        mSQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(mSqlDataBasePath, null);
    }

    public <T> BaseDao<T> getBaseDao(Class<T> entityClass) {
        BaseDao<T> baseDao = null;
        try {
            baseDao = BaseDao.class.newInstance(); //每次都是新new一个出来
            baseDao.init(mSQLiteDatabase, entityClass);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return baseDao;
    }

}
