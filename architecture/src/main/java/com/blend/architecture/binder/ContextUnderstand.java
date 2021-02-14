package com.blend.architecture.binder;

/**
 * 理解上下文：
 * Activity，Service和Application都间接地继承自Context，因此我们可以计算出一个应用程序进程中有多少个Context，这个数量等于
 * Activity和Service的总个数加1，1指的是Application的数量。
 * 1.Context是一个抽象类，它的内部定义了很多方法以及静态常量，它的具体实现类为Contextlmpl。使用装饰者模式ContextWrapper，
 * Service，Application，ContextThemeWrapper都集成自这个装饰类。Contextlmpl和ContextWrapper继承自Context,ContextWrapper
 * 内部包含Context类型的mBase对象，mBase具体指向Contextlmpl。Contextlmpl提供了很多功能，但是外界需要使用并拓展Contextlmpl的功能，
 * 因此设计上使用了装饰模式，ContextWrapper是装饰类，它对Contextlmpl进行包装，ContextWrapper主要是起了方法传递的作用，ContextWrapper
 * 中几乎所有的方法都是调用Contextlmpl的方法来实现的。ContextThemeWrapper 、Service 和Application 都继承自ContextWrapper,这样它们
 * 都可以通过mBase来使用Context的方法，同时它们也是装饰类，在ContextWrapper的基础上又添加了不同的功能。ContextThemeWrapper中包含和主题
 * 相关的方怯（比如getTheme方法），因此，需要主题的Activity继承ContextThemeWrapper ，而不需要主题的Service继承ContextWrapper。
 * <p>
 * <p>
 * <p>
 * 2.Application Context的创建过程。由于LaunchActivity只会调用一次，所以Application只会创建一次。
 * 在ActivityThread的performLauncherActivity方法中，先创建ContextImpl对象，然后创建Application对象，在创建过程中，将Application赋值给
 * LoadedApk的Application，之后在Activity初始化方法中将Application赋值给Activity的Application。
 * 这也是为什么getApplicationContext方法获取的是LoadedApk中的Application，而在Activity中通过getApplication也能获取到Application。
 * <p>
 * <p>
 * <p>
 * 3.Activity的Context创建过程。同上，创建ContextImpl对象后，调用attach方法初始化Activity，在attachBaseContext方法中赋值ContextWrapper，
 * 并在attach方法中创建PhoneWindow，PhoneWindow在运行期间会触发很多事件，比如点击、菜单弹出等，这些事件需要转发给与PhoneWindow关联的Activity，
 * 转发操作通过Window.Callback接口实现，Activity实现这个接口。Activity获取WindowManager，这样在Activity中就能获取WindowManager了。
 * <p>
 * <p>
 * <p>
 * 3.Service Context的创建过程。与Activity类似，也是创建ContextImpl对象，调用attach初始化方法，在初始化方法中调用attachBaseContext来给
 * WrapperContext中的mBase赋值。
 */
class ContextUnderstand {
}
