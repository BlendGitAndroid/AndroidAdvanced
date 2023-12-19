package com.blend.algorithm.serialization;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;

class SingleSerialization {

    static final String CurPath = System.getProperty("user.dir");

    public static void main(String... args) throws Exception {
        Single instance = Single.getInstance();
        System.out.println(instance.hashCode());
        System.out.println(copyInstance(instance).hashCode());

        System.out.println("=================reflect======================");
        //使用反射方式直接调用私有构造器
        Class<Single> clazz = (Class<Single>) Class.forName("com.blend.algorithm.serialization.Single");
        Constructor<Single> con = clazz.getDeclaredConstructor();
        con.setAccessible(true);//绕过权限管理，即在true的情况下，可以通过构造函数新建对象
        Single instance1 = con.newInstance();
        Single instance2 = con.newInstance();
        System.out.println(instance1.hashCode());
        System.out.println(instance2.hashCode());

    }

    private static Single copyInstance(Single instance) throws Exception {
        //序列化会导致单例失效
        FileOutputStream fos = new FileOutputStream(CurPath + "/a.txt");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(instance);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CurPath + "/a.txt"));
        Single single2 = (Single) ois.readObject();
        oos.close();
        ois.close();
        return single2;
    }
}

class Single implements Serializable {
    private static final long serialVersionUID = 1L;
    private static boolean flag = false;

    private Single() {
        synchronized (Single.class) {
            if (!flag) {
               // flag = true;
            } else {
                throw new RuntimeException("single is not reflect！");
            }

        }
    }

    private static volatile Single single;

    public static Single getInstance() {
        if (single == null) {
            synchronized (Single.class) {
                if (single == null) {
                    single = new Single();
                }
            }

        }
        return single;
    }
    // 如果不重写readResolve,会导致单例模式在序列化->反序列化后失败
   private Object readResolve() {
       return single;
   }
}
