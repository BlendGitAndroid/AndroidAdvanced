package com.blend.architecture.database_design.daopackage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.blend.architecture.MyApplication;
import com.blend.architecture.R;

/**
 * GreenDao是一个开源的Android ORM(对象关系映射，Object Relational Mapping)，是一种将对象层次结构映射成关系型结构的方法。
 * <p>
 * Object：即对象，java是一门面向对象的编程语言，对开发者而言，更习惯以对象的角度看待某个问题，或者说通过面向对象的方式处理某个
 * 问题，对java程序员来说更能理解。
 * <p>
 * Relational：关系，SQLite是一种关系型数据库，其关系模型就是指二维表格模型,因而一个关系型数据库就是由二维表及其之间的联系组成
 * 的一个数据组织，它是从数学理论发展而来的，这种关系型数据库与面向对象的思想是冲突的。开发人员需要时时去面对表单和数据库的操作，特
 * 别是当表结构复杂时，会在这些数据的处理上花费大量时间。
 * <p>
 * Mapping：映射(可以通过Map来理解)，一种对应关系，用面向对象的方式来处理关系型结构的数据库。简单的理解是一张表按照统一规则映射
 * 成一个java实体类，对表的操作可以转换对成开发者更熟悉的对实体对象的操作。
 * <p>
 * 作用：
 * 更快速的操作数据库，可以使用简单的面相对象的API来存储，更新，删除和查询Java对象。
 * 优点：
 * 1：开发起来简单，ORM框架将我们的对象模型转化为SQL语句，只需要掌握一些api就能够操作数据库，不用亲自处理sql语句了
 * <p>
 * 2：当面对一个复杂的程序时，其内部较多的数据处理，sql语句大量的硬编码，会让代码显得混乱和不易维护，ORM框架能让结构更清晰。
 * <p>
 * <p>
 * 缺点：
 * 1：虽然ORM框架开发起来简单，但是我们需要掌握的东西却更多了，框架需要去学习，SQL原生操作需要去掌握。
 * 2：在一些复杂的数据库操作(如多表关联查询)时，ORM语法会变得十分复杂。直接用SQL语句会更清晰，更直接。
 * <p>
 * GreenDao的ORM内部实现：greenDAO是基于Android原生数据库的一个封装。
 * SQLiteOpenHelper:版本控制，创建数据库。
 * SQLiteDatabase:数据库类，提供直接操作数据库的API
 * <p>
 * DaoMaster：使用 greenDAO的切入点。DaoMaster保存数据库对象（SQLiteDatabase）并管理特定模式的DAO类（而不是对象）。
 * 它有静态方法来创建表或删除它们。它的内部类OpenHelper和DevOpenHelper都是SQLiteOpenHelper的实现，用来在SQLite
 * 数据库中创建和升级等操作。
 * DaoSession：管理特定模式的所有可用DAO对象，你可以使用其中一个的getter方法获取DAO对象。DaoSession还为实体提供了一
 * 些通用的持久性方法，如插入，加载，更新，刷新和删除。最后，DaoSession对象也跟踪identity scope。
 * xxDAO：数据访问对象（DAO），用于实体的持久化和查询。 对于每个实体，greenDAO会生成一个 DAO。它比DaoSession拥有更多
 * 的持久化方法，例如：count，loadAll 和 insertInTx。
 *
 *
 * <p>
 * 数据查询与缓存：
 * 如果设置了缓存，并且不是第一次查找，则直接从缓存中取出数据；从 statements中拿到数据库查询语句和查询的主键，交给 rawQuery
 * 查询，得到Cursor。通过主键查询，如果数据存在，则查到的数据是唯一的。接着会先尝试从缓存里获取数据，如果没有读取到，则从游标中
 * 取数据，然后再存储到缓存中。
 *
 *
 *
 * <p>
 * 在数据库版本更新中，DaoMaster.DevOpenHelper#onUpgrade方法中，会先删除掉所有的数据库，再重新建立，如果用户想自己控
 * 制版本升级的情况，就需要自己实现OpenHelper。因为项目每次编译运行时，DaoMaster里的内容都会恢复成默认状态，所以不要在
 * DaoMaster的DevOpenHelper里进行业务操作。所以需要自己实现数据库升级，借用一个开源解决办法MigrationHelper类，继承
 * DaoMaster.DevOpenHelper类，重写其#onUpgrade方法。MigrationHelper的migrate方法，它有三个重载方法，我们使用的
 * 那个方法有三个参数，分别为database，一个ReCreateAllTableListener回调，一个继承AbstractDao的class对象的可变参
 * 数类型，其原理主要通过创建一个临时表,将旧表的数据迁移到临时表中。
 */
public class GreenDaoMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_design_main);

        //通过DaoSession获取上面实体类对应的表单操作类，并进行简单的CURD操作
        UserDao userDao = MyApplication.getDaoSession().getUserDao();

        //创建一个对象
        User user = new User(001, "xuhai", "123");

        //增
        userDao.insert(user);

        //删
        userDao.delete(user);

        //改
        user.setName("jiangzuo");
        userDao.update(user);

        // 查
        User query = userDao.queryBuilder().where(UserDao.Properties.Name.eq("jiangzuo")).list().get(0);
    }
}