package com.blend.algorithm.classloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class DiskClassLoader extends ClassLoader {

    private String path;

    public DiskClassLoader(String path) {
        this.path = path;
    }

    //重写findClass方法，并且defineClass中有安全性校验，类名以"java"开头的，都不能由自定义类加载器加载，大部分都是以引导类加载器加载的
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class clazz = null;
        byte[] classData = loadClassData(name);
        if (classData == null) {
            throw new ClassNotFoundException("not found class");
        } else {
            clazz = defineClass(name, classData, 0, classData.length);
        }
        return clazz;
    }

    /**
     * 打破双亲委派机制
     */
    @Override
    protected Class<?> loadClass(String s, boolean b) throws ClassNotFoundException {
        Class<?> c = findLoadedClass(s);
        if (c == null) {
            if ("com.blend.Study".equalsIgnoreCase(s)) {
                c = findClass(s);
            } else {
                //交由父加载器去加载
                c = getParent().loadClass(s);
            }
        }
        if (b) {
            resolveClass(c);
        }
        return c;
    }

    private byte[] loadClassData(String name) {
        String fileName = getFileName(name);
        File file = new File(path, fileName);
        System.out.println(file.getAbsolutePath());
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new FileInputStream(file);
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            return out.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getFileName(String name) {
        int index = name.lastIndexOf(".");
        if (index == -1) {
            return name + ".class";
        } else {
            return name.substring(index + 1) + ".class";
        }
    }

    public static void main(String[] args) {
        DiskClassLoader loader = new DiskClassLoader("E:\\ClassLoder");
        try {
            Class c = loader.loadClass("com.blend.Study");
            if (c != null) {
                try {
                    Object obj = c.newInstance();
                    Method method = c.getDeclaredMethod("say");
                    method.invoke(obj);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //这个获取到的就是Application ClassLoader
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
    }
}
