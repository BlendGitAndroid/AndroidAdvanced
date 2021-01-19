package com.blend.algorithm.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 序列化：序列化就是将对象转化字节序列的过程，相反地，当字节序列被运到相应的进程的时候，进程为了识别这些数据，就要将其反
 * 序列化，即把字节序列转化为对象。
 * 无论是在进程间通信、本地数据存储又或者是网络数据传输都离不开序列化的支持。而针对不同场景选择合适的序列化方案对于应用的
 * 性能有着极大的影响。
 * 从广义上讲，数据序列化就是将数据结构或者是对象转换成我们可以存储或者传输的数据格式的一个过程，在序列化的过程中，数据结
 * 构或者对象将其状态信息写入到临时或者持久性的存储区中，而在对应的反序列化过程中，则可以说是生成的数据被还原成数据结构或
 * 对象的过程。
 * 这样来说，数据序列化相当于是将我们原先的对象序列化概念做出了扩展，在对象序列化和反序列化中，我们熟知的有两种方法，其一
 * 是Java语言中提供的Serializable接口，其二是Android提供的Parcelable接口。而在这里，因为我们对这个概念做出了扩展，
 * 因此也需要考虑几种专门针对数据结构进行序列化的方法，如现在那些个开放API一般返回的数据都是JSON格式的，又或者是我们Android
 * 原生的SQLite数据库来实现数据的本地存储，从广义上来说，这些都可以算做是数据的序列化。
 * <p>
 * 几种常见的数据交换格式：XML，JSON，Protobuf。JSON之所以是比较轻量级的，是因为和XML相比，没有自己校验格式的正确性等。
 * 我们使用GSON，是因为它能进行将java bean转换成符合json格式的String对象，而序列化是对这个String对象进行的。
 * <p>
 * Serializable接口：是java提供的序列化接口，用Serializable标识的类可以被ObjectOutputStream序列化，以及被ObjectInputStream反序列化。
 * 1.用transient关键字标记的成员变量不参与序列化(在被反序列化后，transient 变量的值被设为初始值，如 int 型的是 0，对象型的是 null)
 * 2.静态成员变量属于类不属于对象，所以不会参与序列化(对象序列化保存的是对象的“状态”，也就是它的成员变量，因此序列化不会关注静态变量)
 * 3.反序列化一个类的过程中，它的非可序列化的属性将会调用无参构造函数重新创建，因此这个属性的无参构造函数必须可以访问，否者运行时会报错。
 * <p>
 * 注意：
 * 1.枚举类型的序列化，报出的是枚举的Name值，而不是ordinal值，这个是JVM自动保证的。
 * 2.单例模式的序列化问题。为了保证单例模式在序列化后依然是单例模式，需要重写单例模式的readResolve方法。
 * 3.但是使用反射来构造单例，即使加了flag标志位，依然不能保证单例的有效性。
 * <p>
 * <p>
 * Parcel的简介：
 * Parcel翻译过来是打包的意思,其实就是包装了我们需要传输的数据,然后在Binder中传输,也就是用于跨进程传输数据。简单来说，Parcel提供了
 * 一套机制，可以将序列化之后的数据写入到一个共享内存中，其他进程通过Parcel可以从这块共享内存中读出字节流，并反序列化成对象。
 * <p>
 * <p>
 * 比较：
 * Serializable性能分析：Serializable是Java中的序列化接口，其使用起来简单但开销较大（因为Serializable在序列化过程中使用了反射机
 * 制，故而会产生大量的临时变量，从而导致频繁的GC），并且在读写数据过程中，它是通过IO流的形式将数据写入到硬盘或者传输到网络上。
 * Parcelable性能分析：Parcelable则是以IBinder作为信息载体，在内存上开销比较小，因此在内存之间进行数据传递时，推荐使用Parcelable,
 * 而Parcelable对数据进行持久化或者网络传输时操作复杂，一般这个时候推荐使用Serializable。
 * <p>
 * <p>
 * 面试：
 * 1.Android里面为什么要设计出Bundle而不是直接用Map结构？
 * Bundle内部是由ArrayMap实现的，ArrayMap的内部实现是两个数组，一个int数组是存储对象数据对应下标，一个对象数组保存key和value，内部使
 * 用二分法对key进行排序，所以在添加、删除、查找数据的时候，都会使用二分法查找，只适合于小数据量操作，如果在数据量比较大的情况下，那么它的
 * 性能将退化。而HashMap内部则是数组+链表结构，所以在数据量较少的时候，HashMap的Entry Array比ArrayMap占用更多的内存。因为使用Bundle
 * 的场景大多数为小数据量，我没见过在两个Activity之间传递10个以上数据的场景，所以相比之下，在这种情况下使用ArrayMap保存数据，在操作速度
 * 和内存占用上都具有优势，因此使用Bundle来传递数据，可以保证更快的速度和更少的内存占用。
 * 另外一个原因，则是在Android中如果使用Intent来携带数据的话，需要数据是基本类型或者是可序列化类型，HashMap使用Serializable进行序列化，
 * 而Bundle则是使用Parcelable进行序列化。而在Android平台中，更推荐使用Parcelable实现序列化，虽然写法复杂，但是开销更小，所以为了更加
 * 快速的进行数据的序列化和反序列化，系统封装了Bundle类，方便我们进行数据的传输。
 * 2.为何Intent不能直接在组件间传递对象而要通过序列化机制？
 * Intent在启动其他组件时，会离开当前应用程序进程，进入ActivityManagerService进程（intent.prepareToLeaveProcess()），这也就意味着，
 * Intent所携带的数据要能够在不同进程间传输。首先我们知道，Android是基于Linux系统，不同进程之间的java对象是无法传输，所以我们此处要对对象
 * 进行序列化，从而实现对象在应用程序进程和ActivityManagerService进程之间传输。而Parcel或者Serializable都可以将对象序列化，其中，Serializable
 * 使用方便，但性能不如Parcel容器，后者也是Android系统专门推出的用于进程间通信等的接口。
 */
class Serialization implements Serializable {


    private static final long serialVersionUID = 667279791530738499L;
    private String name;
    private float score;

    public Serialization() {
        System.out.println("Course: empty");
    }

    public Serialization(String name, float score) {
        System.out.println("Course: " + name + " " + score);
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        System.out.println("readObject");
        inputStream.defaultReadObject();
        name = (String) inputStream.readObject();
        score = inputStream.readFloat();
    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        System.out.println("writeObject");
        outputStream.defaultWriteObject();
        outputStream.writeObject(name);
        outputStream.writeFloat(score);
    }

    private Object readResolve() {
        System.out.println("readResolve");
        return new Serialization(name, 85f);
    }

    private Object writeReplace() {
        System.out.println("writeReplace");
        return new Serialization(name + "replace", score);
    }


    @Override
    public String toString() {
        return "Course{" +
                "name='" + name + '\'' +
                ", score=" + score +
                '}';
    }

    public static void main(String... args) throws Exception {
        //TODO:
        Serialization course = new Serialization("English", 12f);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(course);
        byte[] bs = out.toByteArray();
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bs));
        Serialization course1 = (Serialization) ois.readObject();
        System.out.println("course1: " + course1);

    }

}
