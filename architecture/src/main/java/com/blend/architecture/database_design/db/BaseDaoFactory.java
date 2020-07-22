package com.blend.architecture.database_design.db;

import android.database.sqlite.SQLiteDatabase;

import com.blend.architecture.MyApplication;

public class BaseDaoFactory {

    private static final BaseDaoFactory sInstance = new BaseDaoFactory();

    private SQLiteDatabase mSQLiteDatabase;

    private String mSqlDataBasePath;

    public static BaseDaoFactory getInstance() {
        return sInstance;
    }

    private BaseDaoFactory() {
        mSqlDataBasePath = MyApplication.getInstance().getFilesDir().getPath() + "/basedb";
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

    /*
    <T extends BaseDao<M>, M>：申明此方法有泛型，也可以理解为泛型方法
    T：表示返回值为T
     */
    public <T extends BaseDao<M>, M> T getBaseDao(Class<T> daoClass, Class<M> entityClass) {
        BaseDao baseDao = null;
        try {
            baseDao = daoClass.newInstance();
            baseDao.init(mSQLiteDatabase, entityClass);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }

}
