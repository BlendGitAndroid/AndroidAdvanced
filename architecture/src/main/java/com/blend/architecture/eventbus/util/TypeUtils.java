package com.blend.architecture.eventbus.util;

import android.app.Activity;
import android.app.Application;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.blend.architecture.eventbus.annotion.ClassId;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;

public class TypeUtils {

    private static final HashSet<Class<?>> CONTEXT_CLASSES = new HashSet<Class<?>>() {
        {
            add(Context.class);
            add(Activity.class);
            add(AppCompatActivity.class);
            add(Application.class);
            add(FragmentActivity.class);
            add(IntentService.class);
            add(Service.class);
        }
    };

    public static String getClassId(Class<?> clazz) {
        ClassId classId = clazz.getAnnotation(ClassId.class);
        if (classId != null) {
            return classId.value();
        } else {
            return clazz.getName();
        }
    }

    public static String getMethodId(Method method) {
        StringBuilder result = new StringBuilder(method.getName());
        result.append('(').append(getMethodParameters(method.getParameterTypes())).append(')');
        return result.toString();
    }

    //boolean, byte, char, short, int, long, float, and double void
    private static String getClassName(Class<?> clazz) {
        if (clazz == Boolean.class) {
            return "boolean";
        } else if (clazz == Byte.class) {
            return "byte";
        } else if (clazz == Character.class) {
            return "char";
        } else if (clazz == Short.class) {
            return "short";
        } else if (clazz == Integer.class) {
            return "int";
        } else if (clazz == Long.class) {
            return "long";
        } else if (clazz == Float.class) {
            return "float";
        } else if (clazz == Double.class) {
            return "double";
        } else if (clazz == Void.class) {
            return "void";
        } else {
            return clazz.getName();
        }
    }

    public static String getMethodParameters(Class<?>[] classes) {
        StringBuilder result = new StringBuilder();
        int length = classes.length;
        if (length == 0) {
            return result.toString();
        }
        result.append(getClassName(classes[0]));
        for (int i = 1; i < length; ++i) {
            result.append(",").append(getClassName(classes[i]));
        }
        return result.toString();
    }

    public static boolean primitiveMatch(Class<?> class1, Class<?> class2) {
        if (!class1.isPrimitive() && !class2.isPrimitive()) {
            return false;
        } else if (class1 == class2) {
            return true;
        } else if (class1.isPrimitive()) {
            return primitiveMatch(class2, class1);
            //class2 is primitive
            //boolean, byte, char, short, int, long, float, and double void
        } else if (class1 == Boolean.class && class2 == boolean.class) {
            return true;
        } else if (class1 == Byte.class && class2 == byte.class) {
            return true;
        } else if (class1 == Character.class && class2 == char.class) {
            return true;
        } else if (class1 == Short.class && class2 == short.class) {
            return true;
        } else if (class1 == Integer.class && class2 == int.class) {
            return true;
        } else if (class1 == Long.class && class2 == long.class) {
            return true;
        } else if (class1 == Float.class && class2 == float.class) {
            return true;
        } else if (class1 == Double.class && class2 == double.class) {
            return true;
        } else if (class1 == Void.class && class2 == void.class) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean classAssignable(Class<?>[] classes1, Class<?>[] classes2) {
        if (classes1.length != classes2.length) {
            return false;
        }
        int length = classes2.length;
        for (int i = 0; i < length; ++i) {
            if (classes2[i] == null) {
                continue;
            }
            if (primitiveMatch(classes1[i], classes2[i])) {
                continue;
            }
            if (!classes1[i].isAssignableFrom(classes2[i])) {
                return false;
            }
        }
        return true;
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
        Method result = null;
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName) && classAssignable(method.getParameterTypes(), parameterTypes)) {
                if (result == null) {
                    result = method;
                } else {

                }
            }
        }
        if (result == null) {
            return result;
        }
        return result;
    }

    public static Method getMethodForGettingInstance(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
        Method[] methods = clazz.getMethods();
        Method result = null;
        if (parameterTypes == null) {
            parameterTypes = new Class[0];
        }
        for (Method method : methods) {
            String tmpName = method.getName();
            if (tmpName.equals("getInstance")) {
                if (classAssignable(method.getParameterTypes(), parameterTypes)) {
                    result = method;
                    break;
                }
            }
        }
        if (result != null) {
            if (result.getReturnType() != clazz) {

            }
            return result;
        }
        return null;
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>[] parameterTypes) {
        Constructor<?> result = null;
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (classAssignable(constructor.getParameterTypes(), parameterTypes)) {
                if (result != null) {
                } else {
                    result = constructor;
                }
            }
        }
        if (result == null) {
        }
        return result;
    }


    public static void validateClass(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class object is null.");
        }
        if (clazz.isPrimitive() || clazz.isInterface()) {
            return;
        }
        if (clazz.isAnonymousClass()) {
            throw new IllegalArgumentException(
                    "Error occurs when registering class " + clazz.getName()
                            + ". Anonymous class cannot be accessed from outside the process.");
        }
        if (clazz.isLocalClass()) {
            throw new IllegalArgumentException(
                    "Error occurs when registering class " + clazz.getName()
                            + ". Local class cannot be accessed from outside the process.");
        }
        if (Context.class.isAssignableFrom(clazz)) {
            return;
        }
        if (Modifier.isAbstract(clazz.getModifiers())) {
            throw new IllegalArgumentException(
                    "Error occurs when registering class " + clazz.getName()
                            + ". Abstract class cannot be accessed from outside the process.");
        }
    }

    public static void validateServiceInterface(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class object is null.");
        }
        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("Only interfaces can be passed as the parameters.");
        }
    }

    public static boolean arrayContainsAnnotation(Annotation[] annotations, Class<? extends Annotation> annotationClass) {
        if (annotations == null || annotationClass == null) {
            return false;
        }
        for (Annotation annotation : annotations) {
            if (annotationClass.isInstance(annotation)) {
                return true;
            }
        }
        return false;
    }

    public static Class<?> getContextClass(Class<?> clazz) {
        for (Class<?> tmp = clazz; tmp != Object.class; tmp = tmp.getSuperclass()) {
            if (CONTEXT_CLASSES.contains(tmp)) {
                return tmp;
            }
        }
        throw new IllegalArgumentException();
    }


}
