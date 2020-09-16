package com.blend.androidadvanced.ioc;

import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InjectUtils {

    public static void inject(Object context) {
        injectLayout(context);
        injectView(context);
        injectClick(context);

    }

    /**
     * 事件注入
     *
     * @param context
     */
    private static void injectClick(Object context) {
        Class<?> clazz = context.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
//            OnClick onClick = method.getAnnotation(OnClick.class);
            //得到方法上的所有注解
            Annotation[] annotations = method.getAnnotations();

            for (Annotation annotation : annotations) {

//                annotation  ===OnClick  OnClick.class
                Class<?> annotionClass = annotation.annotationType();   //获取到annotation对象的类型，就是OnClick类型
                EventBase eventBase = annotionClass.getAnnotation(EventBase.class);
                //如果没有eventBase，则表示当前方法不是一个处理事件的方法
                if (eventBase == null) {
                    continue;
                }
                //开始获取事件处理的相关信息，
                // 用于确定是哪种事件(onClick还是onLongClick)以及由谁来处理
                //订阅
                String listenerSetter = eventBase.listenerSetter();
                //事件（事件监听的类型）
                Class<?> listenerType = eventBase.listenerType();
                //事件处理   事件被触发之后，执行的回调方法的名称
                String callBackMethod = eventBase.callbackMethod();

//                         textView.setOnClickListener（new View.OnClickListener() {
//                              @Override
//                              public void onClick(View v) {
//
//                              }
//                          });

//                int[] value1=OnClick.value();//这就写死了

                Method valueMethod = null;
                try {
                    //反射得到ID,再根据ID号得到对应的VIEW
                    valueMethod = annotionClass.getDeclaredMethod("value"); //获取到onClick注解的value值，R.id.app_text的值
                    int[] viewId = (int[]) valueMethod.invoke(annotation);
                    for (int id : viewId) {
                        Method findViewById = clazz.getMethod("findViewById", int.class);
                        View view = (View) findViewById.invoke(context, id);    //通过findViewById拿到相应的View
                        if (view == null) {
                            continue;
                        }
                        //得到ID对应的VIEW以后
                        //开始在这个VIEW上执行监听  (使用动态代理)
                        //需要执行activity上的onClick方法
                        //activity==context       click==method
                        ListenerInvocationHandler listenerInvocationHandler = new ListenerInvocationHandler(context, method);
                        //proxy======View.OnClickListener()对象
                        Object proxy = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[]{listenerType}, listenerInvocationHandler);

                        //执行方法                                   setOnClickListener,new View.OnClickListener()
                        Method onClickMethod = view.getClass().getMethod(listenerSetter, listenerType); //获取到View的setOnClickListener方法，参数是View.OnClickListener
                        onClickMethod.invoke(view, proxy);  //proxy就是View.OnClickListener()接口的实现

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }


    }

    /**
     * 控件注入
     *
     * @param context
     */
    private static void injectView(Object context) {
        Class<?> aClass = context.getClass();
        Field[] fields = aClass.getDeclaredFields();    //拿到所有属性对象
//        MainActivity mainActivity = (MainActivity) context;

        for (Field field : fields) {
            ViewInject viewInject = field.getAnnotation(ViewInject.class);
            if (viewInject != null) {
                int valueId = viewInject.value();
                try {
                    Method method = aClass.getMethod("findViewById", int.class);
                    View view = (View) method.invoke(context, valueId); //拿到View
//                    View view= mainActivity.findViewById(valueId);
                    field.setAccessible(true);
                    field.set(context, view);   //设置属性中，给Activity中对应的filed设置值
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 布局注入
     *
     * @param context
     */
    private static void injectLayout(Object context) {
        int layoutId = 0;
        Class<?> clazz = context.getClass();
        ContentView contentView = clazz.getAnnotation(ContentView.class);
        if (contentView != null) {

            layoutId = contentView.value();

            try {
                Method method = context.getClass().getMethod("setContentView", int.class);

                method.invoke(context, layoutId);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}
